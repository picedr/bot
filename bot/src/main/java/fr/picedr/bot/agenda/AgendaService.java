package fr.picedr.bot.agenda;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.agenda.beans.Event;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AgendaService implements BotService {

    private Logger logger = LoggerFactory.getLogger(AgendaService.class);

    private static AgendaService INSTANCE = null;
    static String TYPE_AGENDA = "agenda";
    static String TYPE_AGENDAD = "agendad";
    private static String TYPE_ANNIV = "anniv";

    private AgendaService(){

    }


    public static AgendaService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AgendaService();
        }
        return INSTANCE;
    }

    /**
     * Dispatch the command passed to the service
     *
     * @param server  : Server from which come the command
     * @param channel : Channel from which come the commande
     * @param user    : user that launch the command
     * @param cmd     : command
     * @param content : parameters of the command
     */
    public void dispatch(Guild server, TextChannel channel, Message msg, User user, String cmd, String content) {
        logger.debug("dispatch - start : cmd=<" + cmd + "> - content = <" + content + ">");
        String function = content.split(" ")[0];
        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_AGENDA).equals("1")) {

           switch (cmd){
               case "!rappel":
                   switch (function){
                       case "rem" :
                           remRappel(server,channel, msg, user, content);
                           break;
                       default :
                           rappel(server,channel,user,msg,content);
                   }

                   break;
               case "!agenda":
                   switch (function) {
                       case "add":
                           add(server, channel, msg,user,cmd, content);
                           break;
                       case "rem":
                           rem(server,channel,msg,user,content);
                           break;
                       case "list" :
                            list(server,channel,user,msg);
                           break;
                       case "" :
                           today(server,channel);
                           break;
                       default:
                           logger.debug("default function");
                           if (channel == null) {
                               MsgUtils.tell(user, "Je ne connais pas cette commande.");
                           } else {
                               MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help agenda** pour plus de details",0);
                           }
                   }
                   break;
               case "!anniv" :
                   switch (function) {
                       case "add":
                           add(server, channel, msg,user,cmd, content);
                           break;
                       case "rem":
                           rem(server,channel,msg,user,content);
                           break;
                       case "" :
                           wishAnnivs(server,channel);
                           break;
                       default :
                           logger.debug("default function");
                           if (channel == null) {
                               MsgUtils.tell(user, "Je ne connais pas cette commande.");
                           } else {
                               MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help agenda** pour plus de details",0);
                           }

                   }
                   break;
               default :
                   logger.debug("default cmd");
                   if (channel == null) {
                       MsgUtils.tell(user, "Je ne connais pas cette commande.");
                   } else {
                       MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help agenda** pour plus de details",0);
                   }

           }

        } else {
            MsgUtils.tell(user, "Je ne connais pas cette commande. Tappe **!help** pour plus de détails");
        }

    }



    /**
     * Called to display help
     *
     * @param channel : channel to display help on
     * @param server  : server which called the help
     */
    public static void help(Guild server, TextChannel channel) {
        List<String> tell = new ArrayList<>();

        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_AGENDA).equals("1")) {

            tell.add("AIDE POUR LA SECTION [AGENDA]");
            tell.add(" ");
            tell.add("#EVENEMENT");
            tell.add("Permet de gérer et d'afficher des évenements avec des rappels.");
            tell.add("Les commandes disponibles sont : ");

            tell.add("- !agenda : Liste les évenements de la journée.");
            if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
                tell.add("- !agenda add jj/mm/aaaa hh/mm <titre> : ajoute un évenement.");
                tell.add("- !agenda add jj/mm/aaaa <titre> : ajoute un évenement pour une journée entière.");
                tell.add("- !agenda rem <id> : supprime l'évenement.");
                tell.add("- !agenda list : Liste tous les évenement.");
                tell.add("- !rappel <id> liste de rappels : ajoute un ou des rappels à l'évenement <id>.");
                tell.add("- !rappel rem <id> : supprime le rappel.");
                tell.add("Les rappels peuvent être de la forme **Xm** (minutes), **Xh** (heures) ou **Xj** (jours)");
            }

            tell.add(" ");
            tell.add("#ANNIV");
            tell.add("Permet de gérer et d'afficher anniversaires.");
            tell.add("Les commandes disponibles sont : ");

            tell.add("- !anniv : Souhaite l'anniversaire de la ou des personnes dont c'est l'anniversaire.");
            if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
                tell.add("- !anniv add jj/mm <Nom> : ajoute l'anniversaire pour *Nom*");
                tell.add("- !agenda list : un anniversaire est un evenement et donc peut être vu dans la liste des évenements");
                tell.add("- !agenda rem <id> : Il est supprimé aussi comme un évenement.");
            }

            MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
        }
    }


    /**
     * Add a new event in the agenda
     * @param server server for which the event is add
     * @param chan chan on which the command has been launched
     * @param user tha launch the command
     * @param msg message that launched the command
     * @param cmd  to know if is agenda or anniv
     * @param content content of the command
     */
    private void add(Guild server, TextChannel chan, Message msg,User user,String cmd, String content){
        logger.debug("add - start : server=<"+server.getName()+"> - chan=<"+chan.getName()+"> - cmd=<"+cmd+"> - content=<"+content+">");

        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            String[] params = content.split(" ");
            String title="";
            String dateE="";
            String time="";

            logger.debug("Params legnth = "+params.length);
            if (params.length<3 ){
                List<String> tell = new ArrayList<>();
                tell.add("Le format de la commande est : ");
                if (cmd.equals("!anniv")){
                    tell.add("**!anniv add jj/mm <nom>**");
                } else {
                    tell.add("**!agenda add jj/mm/aaaa hh:mm <titre>**");
                    tell.add("ou **!agenda add jj/mm/aaaa <titre>** pour un évenement sur toute une journée.");
                }
                MsgUtils.tellBlock(chan,tell);
            }else if (params.length==3){
                dateE = params[1];
                title = content.replace("add "+params[1],"").trim();
            }else{
                if (cmd.equals("!anniv")){
                    List<String> tell = new ArrayList<>();
                    tell.add("Le format de la commande est : ");
                    tell.add("**!anniv add jj/mm <nom>**");
                    MsgUtils.tellBlock(chan,tell);
                }else {
                    if (params[2].contains(":") && params[2].length() <= 5) {
                        dateE = params[1];
                        time = params[2];
                        title = content.replace("add " + params[1] + " " + params[2], "").trim();

                    } else {
                        dateE = params[1];
                        title = content.replace("add " + params[1], "").trim();
                    }
                }
            }
            logger.debug("title=<"+title+"> - dateE=<"+dateE+"> - time=<"+time+">");
            Date d = Calendar.getInstance().getTime();
            int year = 0;
            int month = 0;
            int day = 0;
            int hour = 0;
            int min = 0;

            boolean dateOK = true;
            String[] spDate = dateE.trim().split("/");


            logger.debug("spDate length = "+spDate.length);

            if (cmd.equals("!anniv")){
                if (spDate.length != 2) {
                    MsgUtils.tell(chan, "La date doit être au format jj/mm");
                } else {
                    try {
                        month = new Integer(spDate[1]);
                        day = new Integer(spDate[0]);
                    } catch (NumberFormatException nfe) {
                        MsgUtils.tell(chan, "La date doit être au format jj/mm");
                        dateOK = false;
                        logger.debug("Bad number format in date");
                    }

                    logger.debug("dateOK : " + dateOK);
                    if (dateOK) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String s = "";
                        if (day < 10) {
                            s = s.concat("0");
                        }
                        s = s.concat(day + "/");
                        if (month < 10) {
                            s = s.concat("0");
                        }
                        s = s.concat(String.valueOf(month)+"/1999");
                        logger.debug("s=<" + s + ">");
                        try {
                            d = sdf.parse(s);
                            String t = sdf.format(d);
                            if (t.compareTo(s) != 0) {
                                MsgUtils.tell(chan, "Date non valide");
                                dateOK = false;
                                logger.debug("s!=t");
                            }
                        } catch (Exception e) {
                            MsgUtils.tell(chan, "Date non valide");
                            dateOK = false;
                            logger.debug("Error in date check");
                        }
                    }

                }
            } else {
                if (spDate.length != 3) {
                    MsgUtils.tell(chan, "La date doit être au format jj/mm/aaaa");
                    dateOK = false;
                } else {
                    try {
                        year = new Integer(spDate[2]);
                        month = new Integer(spDate[1]);
                        day = new Integer(spDate[0]);
                    } catch (NumberFormatException nfe) {
                        MsgUtils.tell(chan, "La date doit être au format jj/mm/aaaa");
                        dateOK = false;
                        logger.debug("Bad number format in date");
                    }

                    try {
                        if (!time.equals("")) {
                            String[] spTime = time.trim().split(":");
                            hour = new Integer(spTime[0]);
                            min = new Integer(spTime[1]);
                        }
                    } catch (NumberFormatException nfe) {
                        MsgUtils.tell(chan, "L'heure doit être au format hh:mm", 0);
                        dateOK = false;
                        logger.debug("Bad number format in time");
                    }

                    logger.debug("dateOK : " + dateOK);
                    if (dateOK) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String s = "";
                        if (day < 10) {
                            s = s.concat("0");
                        }
                        s = s.concat(day + "/");
                        if (month < 10) {
                            s = s.concat("0");
                        }
                        s = s.concat(month + "/" + year + " ");
                        if (hour < 10) {
                            s = s.concat("0");
                        }
                        s = s.concat(hour + ":");
                        if (min < 10) {
                            s = s.concat("0");
                        }
                        s = s.concat(String.valueOf(min));
                        logger.debug("s=<" + s + ">");
                        try {
                            d = sdf.parse(s);
                            String t = sdf.format(d);
                            if (t.compareTo(s) != 0) {
                                MsgUtils.tell(chan, "Date/heure non valide", 0);
                                dateOK = false;
                                logger.debug("s!=t");
                            }
                        } catch (Exception e) {
                            MsgUtils.tell(chan, "Date/heure non valide", 0);
                            dateOK = false;
                            logger.debug("Error in date check");
                        }
                    }
                }
            }
            logger.debug("dateOK="+dateOK);
            if (dateOK){

                String type= TYPE_AGENDA;
                if (time.equals("")) {
                    type= TYPE_AGENDAD;
                }

                if (cmd.equals("!anniv")){
                    type = TYPE_ANNIV;
                }

                AgendaDAO agendaDAO = new AgendaDAO();
                int nb = agendaDAO.insertEntry(title,d,type,server.getId());
                logger.debug("Id of the event : "+nb);

                if (nb > 0) {
                    List<String> tell = new ArrayList<>();

                    tell.add("L'évenement suivant a été ajouté : ");
                    tell.add("<id> : " + nb );
                    tell.add("<date> : " + dateE.split(" ")[0]);
                    if (!time.equals("")) {
                        tell.add("<heure> : " + time);
                    }
                    tell.add("<titre> : " + title);
                    MsgUtils.tellBlockFramed(chan, tell, MsgUtils.FT_CSS);
                }

            }

        }else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!agenda add**) est limitée au chan admin.");
        }
        logger.debug("add - end");
    }

    /**
     * To remove an event
     * @param server server for which the event is add
     * @param chan chan on which the command has been launched
     * @param user tha launch the command
     * @param msg message that launched the command
     * @param content content of the command
     */
    private void rem(Guild server,TextChannel chan,Message msg,User user,String content){
        logger.debug("rem - start : server=<"+server.getName()+"> - chan=<"+chan.getName()+"> - content=<"+content+">");

        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            AgendaDAO agendaDAO = new AgendaDAO();
            int id = 0;
            try {
                id = Integer.valueOf(content.replace("rem ","").trim());
            } catch (NumberFormatException nfe){
                logger.debug("The parameter is not a number");
                MsgUtils.tell(chan,"L'id doit être un nombre",0);
            }
            if (id>0) {
                int nb = agendaDAO.deleteEntry(id,server.getId());
                if (nb>0){
                    MsgUtils.tell(chan,"L'évenement **"+id+"** a été supprimé",0);
                }

            }

        }else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!agenda add**) est limitée au chan admin.");
        }
        logger.debug("rem - end");
    }

    /**
     * To add a reminder
     * @param server server for which the event is add
     * @param chan chan on which the command has been launched
     * @param user tha launch the command
     * @param msg message that launched the command
     * @param content content of the command
     */
    private void rappel(Guild server, TextChannel chan,User user, Message msg,String content){
        logger.debug("rappel - start : server=<"+server.getName()+"> - chan=<"+chan.getName()+"> - content=<"+content+">");

        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            int id = 0;
            String[] params = content.split(" ");
            try {
                id = Integer.valueOf(params[0]);

            }catch (NumberFormatException nfe){
                logger.debug("bad ID format");
                MsgUtils.tell(chan,"L'id doit être un nombre : <"+params[1]+">. **!help agenda** pour plus d'information.",0);
            }

            if (id>0){
                AgendaDAO agendaDAO = new AgendaDAO();
                Event event = agendaDAO.getEvent(id);

                if ( event !=null && event.getServerId().equals(server.getId())){
                    for (int i = 1;i<params.length;i++){
                        String rap = params[i].trim();
                        if (rap.endsWith("h") || rap.endsWith("j")|| rap.endsWith("m")){
                            int val = 0;
                            try{
                                val=new Integer(rap.substring(0,rap.length()-1));
                            }catch (NumberFormatException nfe){
                                MsgUtils.tell(chan, "Rappel non valide **"+rap+"**. Formats acceptés : **Xm**, **Xh** ou **Xj**.",0);
                            }

                            if (val>0){
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(event.getDate());
                                if (rap.endsWith("h")){
                                    cal.add(Calendar.HOUR,-val);
                                }else if(rap.endsWith("j")){
                                    cal.add(Calendar.DAY_OF_YEAR,-val);
                                }else if(rap.endsWith("m")){
                                    cal.add(Calendar.MINUTE,-val);
                                }
                                int nb = agendaDAO.insertRappel(event.getId(),cal.getTime());
                                if (nb>0){
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    if (event.getType().equals(TYPE_AGENDAD)){
                                        sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    }
                                    MsgUtils.tell(chan,"Un rappel a été ajouté pour l'évenement **"+event.getContent()+"** le **"+sdf.format(cal.getTime())+"**",0);
                                }

                            }
                        }else {
                            MsgUtils.tell(chan, "Rappel non valide **"+rap+"**. Formats acceptés : **Xm**, **Xh** ou **Xj**.",0);
                        }
                    }
                }else {
                    MsgUtils.tell(chan,"Cet id n'existe pas pour ce serveur.",0);
                }
            }
        }else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!agenda add**) est limitée au chan admin.");
        }
        logger.debug("rappel - end");

    }

    /**
     * To remove an event
     * @param server server for which the event is add
     * @param chan chan on which the command has been launched
     * @param user tha launch the command
     * @param msg message that launched the command
     * @param content content of the command
     */
    private void remRappel(Guild server,TextChannel chan,Message msg,User user,String content){
        logger.debug("remRappel - start : server=<"+server.getName()+"> - chan=<"+chan.getName()+"> - content=<"+content+">");

        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            AgendaDAO agendaDAO = new AgendaDAO();
            int id = 0;
            try {
                id = Integer.valueOf(content.replace("rem ","").trim());
            } catch (NumberFormatException nfe){
                logger.debug("The parameter is not a number");
                MsgUtils.tell(chan,"L'id doit être un nombre",0);
            }
            if (id>0) {
                int nb = agendaDAO.deleteRappel(id,server.getId());
                if (nb>0){
                    MsgUtils.tell(chan,"Le rappel **"+id+"** a été supprimé",0);
                }

            }

        }else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!rappel rem**) est limitée au chan admin.");
        }
        logger.debug("remRappel - end");
    }

    /**
     * List all the events
     * @param server server for which the event is add
     * @param chan chan on which the command has been launched
     * @param user tha launch the command
     * @param msg message that launched the command
     */
    private void list(Guild server, TextChannel chan,User user, Message msg){
        logger.debug("list - start : server=<"+server.getName()+"> - chan=<"+chan.getName()+">");

        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");

            AgendaDAO agendaDAO = new AgendaDAO();
            List<Event> events = agendaDAO.getAllEvents(server.getId());

            List<String> tell = new ArrayList<>();
            tell.add("Les évenement suivant sont listés :");
            for (Event event : events){
                if (event.getType().equals(TYPE_ANNIV)){
                    tell.add("[Anniversaire de " + event.getContent() + "]");
                }else {
                    tell.add("[" + event.getContent() + "]");
                }
                tell.add("<id> : "+event.getId());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                if (event.getType().equals(TYPE_AGENDAD)){
                    sdf = new SimpleDateFormat("dd/MM/yyyy");
                } else if(event.getType().equals(TYPE_ANNIV)){
                    sdf = new SimpleDateFormat("dd/MM");
                }
                tell.add("<date> : "+sdf.format(event.getDate()));
                tell.add("<Rappels> :");
                List<Event> rappels = event.getRappels();
                for (Event rappel : rappels){
                    tell.add("- id="+rappel.getId()+" - date = "+sdf.format(rappel.getDate()));
                }
                tell.add(" ");
            }

            MsgUtils.tellBlockFramed(chan,tell,MsgUtils.FT_CSS,0);


        }else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!agenda add**) est limitée au chan admin.");
        }
        logger.debug("list - end");

    }

    /**
     * Display all day's events and day's reminders
     * @param server : server to display on
     * @param channel : channel to display on
     */
    public void today(Guild server, TextChannel channel){
        logger.debug("today - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
        AgendaDAO agendaDAO = new AgendaDAO();
        List<Event> events = agendaDAO.getTodayEvents(server.getId());
        logger.debug("Event size="+events.size());
        List<String> tell = new ArrayList<>();
        if (events.size()>0) {
            List<String> tellAD = new ArrayList<>();
            List<String> tellNaD = new ArrayList<>();
            for (Event event : events) {
                String line = "- **" + event.getContent() + "** ";
                if (event.getType().equals(TYPE_AGENDAD)) {
                    line = line.concat("toute la journée.");
                    tellAD.add(line);
                    logger.debug("Adding "+event.getContent()+" - all day");
                } else if (event.getType().equals(TYPE_AGENDA)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    line = line + "à " + sdf.format(event.getDate()) + ".";
                    tellNaD.add(line);
                    logger.debug("Adding "+event.getContent()+" - specific time");
                }

            }

            tell.add("Les évenements programmés aujourd'hui sont : ");
            tell.addAll(tellAD);
            tell.addAll(tellNaD);
        }

        List<Event> rappels = agendaDAO.getTodayRappels(server.getId());
        logger.debug("Rappels sire="+rappels.size());
        if (rappels.size()>0) {
            List<String> tellAD = new ArrayList<>();
            for (Event event : rappels) {
                String line = "- **" + event.getContent() + "** ";
                line = line.concat("toute la journée.");
                tellAD.add(line);
                logger.debug("Adding "+event.getContent());
            }
            tell.add("Les évenements à venir sont : ");
            tell.addAll(tellAD);
        }
        logger.debug("tell size="+tell.size());
        if (tell.size()>0){
            MsgUtils.tellBlock(channel, tell,0);
        }else{
            MsgUtils.tell(channel,"Aucun évenement de programmé.",0);
        }

        logger.debug("today - end");
    }

    /**
     * Display event and reminder at current time
     * @param date : current time
     */
    public void now(Date date){
        logger.debug("now - start");
        AgendaDAO agendaDAO = new AgendaDAO();
        List<Event> events = agendaDAO.getNow(date);
        Bot bot = Bot.getInstance();
        logger.debug("event size="+ events.size());
        for (Event event : events){
            logger.debug("event=<"+event.getId()+">");
            TextChannel channel = bot.getJDA().getTextChannelById(bot.getServersConf().get(event.getServerId()).get(Params.CONF_GENERALCHANNEL));
            MsgUtils.tell(channel,"L'évenement **"+event.getContent()+"** débute maintenant",0);
        }

        List<Event> rappels = agendaDAO.getRappelsNow(date);
        logger.debug("rappels size="+rappels.size());
        for (Event event : rappels){
            logger.debug("rappel=<"+event.getId()+">");
            TextChannel channel = bot.getJDA().getTextChannelById(bot.getServersConf().get(event.getServerId()).get(Params.CONF_GENERALCHANNEL));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            MsgUtils.tell(channel,"L'évenement **"+event.getContent()+"** aura lieu le "+sdf.format(event.getDate())+" à "+sdf2.format(event.getDate()+"."),0);
        }

        logger.debug("now - stop");
    }

    /**
     *  Display birthday of the day
     */
    public void wishAnnivs(Guild server, TextChannel channel){
        logger.debug("wishAnnivs - start : server=<"+server.getName()+"> - channel = <"+channel.getName()+">");
        AgendaDAO agendaDAO = new AgendaDAO();
        List<Event> bds = agendaDAO.getAllEvents(server.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        Date date = Calendar.getInstance().getTime();
        bds.stream()
                .filter(e -> e.getType().equals(TYPE_ANNIV))
                .filter(e-> sdf.format(e.getDate()).equals(sdf.format(date)))
                .forEach(e -> wishAnniv(e.getContent(),channel));

        logger.debug("wishAnnivs - end");
    }

    /**
     * Sing happy birthday
     * @param userName : user for who to sing
     * @param channel : channel to sing on
     */
    private void wishAnniv(String userName, TextChannel channel){
        MsgUtils.tell(channel,"♫♪♪ Joyeux aaanniiiiveeeraiiiiire ♫♪♪",1);
        MsgUtils.tell(channel,"♫♪♪ Joyeux aaanniiiiveeeraiiiiire ♫♪♪",1);
        MsgUtils.tell(channel,"♫♪♪ Joyeux aaanniiiiveeeraiiiiire "+userName+" ♫♪♪",1);
        MsgUtils.tell(channel,"♫♪♪ Joyeux aaaaaaaaaaaaaanniiiiiiiiiiiiveeeeeeeeeeeeraiiiiiiiiiiiiiire ♫♪♪",1);
    }

    /**
     * Clear all old events
     * @param date : delete all before this date
     */
    public void clear(Date date){
        logger.debug("clear - start");
        AgendaDAO agendaDAO = new AgendaDAO();
        int nb = agendaDAO.clear(date);
        logger.debug("clear - end : nb cleared="+nb);

    }

}
