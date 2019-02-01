package fr.picedr.bot.papy;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.Utils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PapyService implements BotService {

    private Logger logger = LoggerFactory.getLogger(PapyService.class);

    private static PapyService INSTANCE = null;

    private List<String> hello;

    private PapyService(){
        logger.debug("PapyService init");
        hello = new ArrayList<>();
    }

    public static PapyService getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new PapyService();
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
        logger.debug("dispatch - start : server=<" + server.getName() + "> - channel=<" + channel.getName() + "> - user=<" + user.getName() + "> - cmd=<" + cmd + "> - content = <" + content + ">");

        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_PAPY).equals("1")) {
            logger.debug("Service papy is up for this server");
            switch (cmd) {
                case "!boule8":
                    boule8(channel);
                    break;
                case "!gif":
                    tellGif(channel,content);
                    break;
                case "!journee" :
                    journee(server,channel);
                    break;
                default:

            }
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
            if (bot.getServices().get(server.getId()).get(Params.SRV_CMD).equals("1")) {
                tell.add("AIDE POUR LA SECTION [PAPY]");
                tell.add("Le service [compapymand] permet d'utiliser diverses commandes. ");
                tell.add("Les commandes disponibles sont : ");
                tell.add("**!boule8** : tout est dans le nom");
                tell.add("**!journee** : affiche la ou les journée(s) du jour");

                /*if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {

                }*/
            }
            MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS,0);
        }


    /**
     * Bot answe by yes or not when someone call him
     * @param server : server to check if functionnality is available
     * @param channel : channel to answer on
     */
    public void papy(Guild server, TextChannel channel){
        logger.debug("papy - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_PAPY).equals("1")) {
            int rand = Utils.rand(2);
            logger.debug("rand = <"+rand+">");
            switch (rand) {
                case 0:
                    MsgUtils.tell(channel, "Oui",Params.DEFAUL_TYPING_DELAY);
                    break;
                case 1:
                    MsgUtils.tell(channel, "Non",Params.DEFAUL_TYPING_DELAY);
                    break;
            }
        }
        logger.debug("papy - end");
    }

    /**
     * 8 ball functionnality
     * @param channel : channel to display the answer
     */
    private void boule8(TextChannel channel){
        logger.debug("boule8 - start : channel=<"+channel.getName()+">");
        int rand = Utils.rand(20);
        logger.debug("rand = <"+rand+">");
        switch (rand) {
            case 0:
                MsgUtils.tell(channel, "Essaye plus tard");
                break;
            case 1:
                MsgUtils.tell(channel, "Essaye encore");
                break;
            case 2:
                MsgUtils.tell(channel, "Pas d'avis");
                break;
            case 3:
                MsgUtils.tell(channel, "C'est ton destin");
                break;
            case 4:
                MsgUtils.tell(channel, "Le sort en est jeté");
                break;
            case 5:
                MsgUtils.tell(channel, "Une chance sur deux");
                break;
            case 6:
                MsgUtils.tell(channel, "Repose ta question");
                break;
            case 7:
                MsgUtils.tell(channel, "D'après moi oui");
                break;
            case 8:
                MsgUtils.tell(channel, "C'est certain");
                break;
            case 9:
                MsgUtils.tell(channel, "Oui absolument");
                break;
            case 10:
                MsgUtils.tell(channel, "Tu peux compter dessus");
                break;
            case 11:
                MsgUtils.tell(channel, "Sans aucun doute");
                break;
            case 12:
                MsgUtils.tell(channel, "Très probable");
                break;
            case 13:
                MsgUtils.tell(channel, "C'est bien parti");
                break;
            case 14:
                MsgUtils.tell(channel, "C'est non");
                break;
            case 15:
                MsgUtils.tell(channel, "Peu probable");
                break;
            case 16:
                MsgUtils.tell(channel, "Oui");
                break;
            case 17:
                MsgUtils.tell(channel, "Faut pas r�ver");
                break;
            case 18:
                MsgUtils.tell(channel, "N'y compte pas");
                break;
            case 19:
                MsgUtils.tell(channel, "Impossible");
                break;
        }
        logger.debug("boule8 - end");

    }

    /**
     * Bot say hello when user speak for the first time
     * @param server : server user speak on
     * @param channel : channel user speak on
     * @param user : user who speak
     */
    public void hello(Guild server,TextChannel channel, User user){
        logger.debug("hello - start : server=<"+server.getName()+"> - channel=<"+channel+"> - user=<"+user.getName()+">");
        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_PAPY).equals("1")) {
            if (!user.isBot()){
                String[] msg = {"Coucou","Hello","Salut","Plop","Yop",
                        "Bonjour","Bien le bonjour","Salutation"};

                int rand = Utils.rand(8);

                if (!hello.contains(user.getId())){
                    hello.add(user.getId());
                    MsgUtils.tell(channel, msg[rand]+" "+user.getName());
                }
            }
        }
        logger.debug("hello - end");
    }

    /**
     * Eerease users table for a new day
     */
    public void newDay(){
        logger.debug("newDay");
        hello = new ArrayList<>();
    }

    /**
     * Get a phrase and randomly post a gif
     * @param server : for service right
     * @param channel : channel to post on
     * @param user : user who post the message
     * @param content : content of the message
     */
    public void papyGif(Guild server,TextChannel channel,User user, String content) {
        logger.debug("papyGif -start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - content=<"+content+">");
        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_PAPY).equals("1")) {
            if (!user.isBot()) {

                String[] allWords = content.replaceAll("[^ A-Za-z0-9éèàùêô]", " ").split(" ");

                for (String word : allWords) {
                    logger.debug("Loop on words : <"+word+">");
                    if (word.length() >= 4 && Utils.rand(100) == 0) {
                        tellGif(channel, word);
                    }
                }
            }
        }
        logger.debug("papyGif - end");
    }

    /**
     * Post a gif
     * @param channel : channel to post the gif on
     * @param message : worg for the gif search
     */
    private void tellGif(TextChannel channel, String message) {
        logger.debug("tellGif - start : channel=<"+channel.getName()+"> - message=<"+message+">");
        List<String> gifs = getGifs(message);
        String gif = gifs.get(Utils.rand(gifs.size()));
        MsgUtils.tell(channel, gif);
        logger.debug("tellGif - end");
    }


    /**
     * Get a gifs
     * @param search : keyword for the search
     * @return gif address
     */
    private List<String> getGifs(String search){
        logger.debug("getGifs - start : search=<"+search+">");
        List<String> result = new ArrayList<>();

        try {
            URL url = new URL("https://tenor.com/search/"+search.replace(" ", "-")+"-gifs");
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("img src=") && inputLine.contains(".gif")) {
                    String[] splited = inputLine.split("img src=\"");
                    for (int i = 1;i<splited.length;i++) {
                        String img = splited[i].split("\"")[0];
                        if (img.startsWith("http")) {
                            result.add(img);
                        }
                    }
                }

            }
            in.close();

        } catch (MalformedURLException e) {
            logger.error("Bad url",e);
        } catch (IOException e) {
            logger.error("Error",e);
        }
        logger.debug("getGifs - end : result size = "+result.size());
        return result;
    }

    /**
     * Tell what is the day (internationnal days)
     * @param server : server on which display it
     * @param channel : channel on which display it
     */
    public void journee(Guild server,TextChannel channel){
        logger.debug("journee - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_PAPY).equals("1")) {
            Calendar cal = Calendar.getInstance();
            String suffix = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            logger.debug("suffix after day : "+suffix);
            if (suffix.equals("1")) {
                suffix = suffix + "er";
            }

            String mois = "";
            switch (cal.get(Calendar.MONTH)) {
                case 0:
                    mois = "janvier";
                    break;
                case 1:
                    mois = "février";
                    break;
                case 2:
                    mois = "mars";
                    break;
                case 3:
                    mois = "avril";
                    break;
                case 4:
                    mois = "mai";
                    break;
                case 5:
                    mois = "juin";
                    break;
                case 6:
                    mois = "juillet";
                    break;
                case 7:
                    mois = "aoüt";
                    break;
                case 8:
                    mois = "septembre";
                    break;
                case 9:
                    mois = "octobre";
                    break;
                case 10:
                    mois = "novembre";
                    break;
                case 11:
                    mois = "décembre";
                    break;
            }
            suffix = suffix + "_" + mois;
            logger.debug("suffix after month : "+suffix);
            List<String> journee = new ArrayList<>();
            String s;
            String url = "https://fr.wikipedia.org/wiki/" + suffix;
            logger.debug("url : "+url);
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
                while ((s = r.readLine()) != null) {
                    if (s.toLowerCase().contains("journée mondiale")) {
                        journee.add("la " + s.split("</a>")[0].split("\">")[1]);
                    } else if (s.toLowerCase().contains("journée internationale")) {
                        journee.add("la " + s.split("</a>")[0].split("\">")[1]);
                    }
                }
            } catch (MalformedURLException e) {
                logger.error("Bad url", e);
            } catch (IOException e) {
                logger.error("Error", e);
            }

            if (journee.size() == 0) {
                MsgUtils.tell(channel,"Pas de journée aujourd'hui");
            } else if (journee.size()==1){
                MsgUtils.tell(channel,"Aujourd'hui, c'est "+journee.get(0));
            } else {
                List<String> tell =  new ArrayList<>();
                tell.add("Ajourd'hui c'est : ");
                tell.addAll(journee);
                MsgUtils.tellBlock(channel,tell);
            }
        }
        logger.debug("journee - fin");

    }



}
