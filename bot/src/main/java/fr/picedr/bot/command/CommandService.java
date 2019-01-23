package fr.picedr.bot.command;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.command.bean.Command;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommandService implements BotService {

    private Logger logger = LoggerFactory.getLogger(CommandService.class);

    private static CommandService INSTANCE = null;

    private CommandService() {

    }


    public static CommandService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommandService();
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
        logger.debug("dispatch - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - cmd=<" + cmd + "> - content = <" + content + ">");

        Bot bot = Bot.getInstance();
        if (bot.getServices().get(server.getId()).get(Params.SRV_CMD).equals("1")) {
            logger.debug("Service commance is up for this server");
            String function = content.split(" ")[0].toLowerCase();
            switch (cmd){
                case "!cmd":
                    switch (function){
                        case "add":
                            add(server,channel,user,msg,content.replaceFirst("add ","").trim());
                            break;
                        case "rem":
                            rem(server,channel,user,msg,content.replaceFirst("rem ","").trim());
                            break;
                        case "list":
                            list(server,channel);
                            break;
                        case "help" :
                            CommandService.help(server,channel);
                            break;
                        default:
                            MsgUtils.tell(channel,"Je ne connais pas cette option.");
                    }
                    break;
                default:
                    runCommand(server,channel,user,cmd.replaceFirst("!",""),content);
            }



        }

        logger.debug("dispatch - end");
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
            tell.add("AIDE POUR LA SECTION [COMMAND]");
            tell.add("Le service [command] permet d'utiliser des command customisées. ");
            tell.add("Les commandes disponibles sont : ");
            tell.add("**!cmd list** : liste les commandes disponibles ");

            if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
                tell.add("**!cmd add <name> <value>** : ajoute une commande. tapper *!name* affichera *value*.");
                tell.add("  Des paramètres peuvent être ajoutés sous la forme *<nb>*. Par exemple bla bla <1>, <2> blibli permettra lors du lancement de la commande !cmd val1 val2 d'afficher : bla bla val1, val2 blibli");
                tell.add("  *<user>* sera remplacé par le nom de l'utilisateur qui lance la commande.");
                tell.add("  *<chan>* sera remplacé par le chan sur lequel est lancé la commande");

                tell.add("!cmd rem <id> : supprime la commande liée à l'*id* ");

            }
        }
        MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS,0);
    }

    /**
     * Add a command
     * @param server : server to add the command on
     * @param channel : channel to speak on (for the return)
     * @param user : user who request the add
     * @param message : message object that launch the add
     * @param content : content of the call
     */
    private void add(Guild server, TextChannel channel,User user, Message message, String content){
        logger.debug("add - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - content=<"+content+">");

        if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            String[] spContent = content.trim().split(" ");
            if (spContent.length > 1) {
                String name = spContent[0];
                String value = content.replaceFirst(name, "").trim();
                //boolean isAdmin = false;
                logger.debug("name=<"+name+"> - value=<"+value+">");

                CommandDAO commandDAO = new CommandDAO();
                int inserted = commandDAO.add(server.getId(), name, value.replaceAll("\n","##"));//, isAdmin);
                if (inserted > 0) {
                    MsgUtils.tell(channel, "La commande **" + name + "** a été ajoutée");
                } else {
                    MsgUtils.tell(channel, "Oups ... petit problème technique. Je n'ai pas réussi à ajouter la commande");
                }

            } else {
                MsgUtils.tell(channel, "Pour ajouter la commande est : **!cmd add <nom> <valeur>**");
            }
        } else {
            message.delete().complete();
            if (UserUtils.isAdmin(server,user)) {
                MsgUtils.tell(user, "L'ajout de commande se fait sur le chan admin");
            }
        }
        logger.debug("add - end");

    }

    /**
     * Remove a command
     * @param server : server to remove the command for
     * @param channel : channel to speak on (for the return)
     * @param user : user who request the removal
     * @param message : message object that launch the removal
     * @param content : content of the call
     */
    private void rem(Guild server, TextChannel channel, User user, Message message, String content){
        logger.debug("rem - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - content=<"+content+">");
        if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            CommandDAO commandDAO = new CommandDAO();

            try {
                int id = Integer.parseInt(content);
                int deleted = commandDAO.remove(server.getId(), id);
                if (deleted>0){
                    MsgUtils.tell(channel,"La commande **"+id+"** a été supprimée");
                } else {
                    MsgUtils.tell(channel,"Je n'ai trouvé aucune commande avec l'id **"+id+"**");
                }
            }catch (NumberFormatException nfe){
                MsgUtils.tell(channel,"L'id doit être un nombre, ce qui n'est pas le cas de **"+content+"**.");
            }

        } else {
            message.delete().complete();
            if (UserUtils.isAdmin(server,user)) {
                MsgUtils.tell(user, "La suppression de commande se fait sur le chan admin");
            }
        }
        logger.debug("rem - end");
    }


    /**
     * List all available commands for the server
     * @param server : server for which the command are available
     * @param channel : channel to displayy the result on
     */
    private void list(Guild server, TextChannel channel){
        logger.debug("list - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
        boolean isAdminChannel = channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL));

        CommandDAO commandDAO = new CommandDAO();
        List<Command> cmds = commandDAO.getCommands();
        List<String> tell = new ArrayList<>();
        cmds.stream()
                .filter( e -> e.getServerId().equals(server.getId()))
                .forEach(e -> tell.add(displayCommand(e,isAdminChannel)));

        if (tell.size() > 0) {
            MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_CSS);
        } else {
            MsgUtils.tell(channel, "Aucune commande n'est disponibe");
        }

    }


    /**
     * Build a line of list command
     * @param cmd : command to display
     * @param isAdminChannel : to know if id has to be displayed (only on channel admin)
     * @return : line to display
     */
    private String displayCommand(Command cmd,boolean isAdminChannel){
        String result = "- !"+cmd.getName();
        if (isAdminChannel) {
            result = result +" (" + cmd.getId() + ")";
        }
        result = result + " " + cmd.getValue().replaceAll("##","\n");
        return result;
    }

    /**
     * Display the result of a custom command
     * @param server : server launched on
     * @param channel : channel to display on
     * @param user : user who launched the command
     * @param cmd : command launched
     * @param content : parameters
     */
    private void runCommand(Guild server, TextChannel channel,User user, String cmd, String content ){
        logger.debug("runCommand - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - cmd=<"+cmd+"> - content=<"+content+">");
        CommandDAO commandDAO = new CommandDAO();

        List<Command> cmds = commandDAO.getCommands();
        cmds.stream()
                .filter(e -> e.getServerId().equals(server.getId()))
                .filter(e -> e.getName().equals(cmd))
                .forEach(e -> MsgUtils.tell(channel,buildCommand(channel,user,e.getValue(),content)));
    }

    /**
     * Build the result of the command
     * @param channel : in case of <chan> tag
     * @param user : in case of <user> tag
     * @param value : initial output
     * @param params : params of the command
     * @return the final display of the command
     */
    private String buildCommand(TextChannel channel,User user,String value, String params){
        String result = value.replaceAll("<user>",user.getName()).replaceAll("<chan>",channel.getName());

        String[] spParams = params.trim().split(" ");
        for (int i = 0;i<spParams.length;i++){
           result =  result.replaceFirst("<"+(i+1)+">",spParams[i]);
        }
        return result;
    }



}