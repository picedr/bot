package fr.picedr.bot.admin;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.dao.BotDAO;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class AdminService implements BotService {

    private Logger logger = LoggerFactory.getLogger(AdminService.class);

    private static AdminService INSTANCE = null;

    private AdminService() {

    }

    public static AdminService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AdminService();
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

        Bot bot = Bot.getInstance();
        switch (cmd) {
            case "!addServer":
                if (Bot.getInstance().getRoles().get(Params.SUPERADMIN).contains(user.getId())) {
                    logger.debug("Is superAdmin");
                    String[] params = content.split(" ");
                    logger.debug("Param length = " + params.length);
                    if (params.length == 2) {
                        addServer(params[0], params[1]);
                    } else {
                        MsgUtils.tell(user, "Commande incorrecte : !addServer <serverId> <adminChannelId>");
                    }
                } else {
                    logger.debug("Is not superAdmin");
                }
                break;
            case "!services":
                displayServices(server, channel, msg, user, content);
                break;
            case "!service":
                updateService(server, channel, msg, user, content);
                break;
            case "!confs" :
                displayConfs(server,channel,msg,user,content);
                break;
            case "!conf" :
                updateOrInsertConf(server,channel,msg,user,content);
                break;
            case "!stop":
                if (bot.getRoles().get(Params.SUPERADMIN).contains(user.getId())) {
                    logger.debug("Is superAdmin");
                    bot.stop();
                } else {
                    logger.debug("Is not superAdmin");
                }
                break;
            default:
                logger.debug("default");
                if (channel == null) {
                    MsgUtils.tell(user, "Je ne connais pas cette commande.");
                } else {
                    MsgUtils.tell(channel, "Je ne connais pas cette commande.");
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

        if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            tell.add("AIDE POUR LA SECTION [ADMIN]");
            tell.add(" ");
            tell.add("#SERVICE :");
            tell.add("Deux commandes disonibles : ");
            tell.add("- !services : liste l'ensemble des services disponibles pour le bot et l'état (on/off) dans lequel ils sont");
            tell.add("- !service [nom du service] [état] : change l'état du service (pour activer ou désactiver un service)");
            tell.add(" ");
            tell.add("#CONF : ");
            tell.add("Deux commandes disponibles : ");
            tell.add("- !confs : liste l'ensemble des confs pour le serveur");
            tell.add("- !conf [nom de la conf] [valeur] : ajoute ou met à jour une configuration pour le serveur");

        }


        MsgUtils.tellBlockFramed(channel, tell, "css");
    }

    private void addServer(String serverId, String adminChannelId) {
        logger.debug("addServer - Start : server = <" + serverId + "> - adminchannel = <" + adminChannelId + ">");
        BotDAO botDAO = new BotDAO();
        logger.debug("Add conf");
        botDAO.addConf(serverId, Params.CONF_ADMINCHANNEL, adminChannelId);
        logger.debug("Add services");
        for (Map.Entry<String, String> entry : Params.SRV_DESC.entrySet()) {
            botDAO.addService(serverId, entry.getKey(), "0");
        }
        logger.debug("addServer - end");

    }

    private void displayServices(Guild server, TextChannel chan, Message msg, User user, String content) {
        logger.debug("displayService - start : server=<" + server.getName() + "> - chan=<" + chan.getName() + "> - user=<" + user.getName() + "> - content=<" + content + ">");
        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            if (content == null || content.equals("")) {
                Hashtable servicesState = Bot.getInstance().getServices().get(server.getId());
                List<String> tell = new ArrayList<>();
                servicesState.forEach((key, value) -> {
                    logger.debug("key = <" + key + "> - value = <" + value + ">");
                    String line = "-" + key + " ";
                    if (value.equals("0")) {
                        line = line + "[OFF]";
                    } else {
                        line = line + "(ON)";
                    }
                    line = line + " : " + Params.SRV_DESC.get(key) + ".";
                    logger.debug("line = <" + line + ">");
                    tell.add(line);
                });
                MsgUtils.tellBlockFramed(chan, tell, "css");
            } else {
                logger.debug("No parameter needed for this service");
                MsgUtils.tell(chan, "La commande **!services** ne prend pas de paramètre");
            }
        } else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!services**) est limitée au chan admin.");
        }
        logger.debug("displayServices - end");
    }

    private void updateService(Guild server, TextChannel chan, Message msg, User user, String content) {
        logger.debug("updateService - start : server = <" + server.getName() + "> - channel = <" + chan.getName() + "> - user = <" + user.getName() + "> - content = <" + content + ">");
        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            String[] params = content.split(" ");
            if (params.length == 2) {
                logger.debug("Correct number of parameters : 1 =<" + params[0] + "> - 2=<" + params[1] + ">");
                if (Params.SRV_DESC.containsKey(params[0])) {
                    String state = "-1";
                    if (params[1].toLowerCase().equals("on")) {
                        state = "1";
                    } else if (params[1].toLowerCase().equals("off")) {
                        state = "0";
                    }
                    logger.debug("state = <" + state + ">");
                    if (state.equals("-1")) {
                        logger.debug("State KO");
                        MsgUtils.tell(chan, "Le deuxième paramètre doit être **on** ou **off**.");
                    } else {
                        logger.debug("State OK");
                        if (Bot.getInstance().getServices().get(server.getId()).get(params[0]).equals(state)) {
                            logger.debug("No change on state");
                            MsgUtils.tell(chan, "Le service **" + params[0] + "** est déjà à l'état " + params[1]);
                        } else {
                            if (state.equals("0") || isServiceSetable(server,params[0])) {
                                logger.debug("Change the state");
                                BotDAO botDAO = new BotDAO();
                                int res = botDAO.updateService(server.getId(), params[0], state);
                                logger.debug("Number of lines updated = <" + res + ">");
                                if (res > 0) {
                                    Bot.getInstance().getServices().get(server.getId()).put(params[0], state);
                                    String tell = "Le service **" + params[0] + "** a été  ";
                                    if (params[1].toLowerCase().equals("on")) {
                                        tell = tell + "**activé**.";
                                    } else {
                                        tell = tell + "**desactivé.**";
                                    }
                                    MsgUtils.tell(chan, tell);
                                }
                            } else {
                                logger.debug("The service is not setable");
                            }
                        }
                    }

                } else {
                    logger.debug("Service unknown");
                    List<String> tell = new ArrayList<>();
                    tell.add("Nom de service inconnu. Les services disponibles sont : ");
                    for (Map.Entry<String, String> entry : Params.SRV_DESC.entrySet()) {
                        tell.add("- " + entry.getKey());
                    }
                    MsgUtils.tellBlock(chan, tell);
                }
            } else {
                MsgUtils.tell(chan, "Commande incorrecte : !service <service name> <ON/OFF>");
            }
        } else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!service**) est limitée au chan admin.");
        }
        logger.debug("updateService - end");
    }

    /**
     * Check everything is set (conf mainly) to activate a service
     * @param server : server on which the service is enabaling
     * @param service : name of the service;
     * @return : true if can be enabled, false if not.
     */
    private boolean isServiceSetable(Guild server,String service){
        boolean result = true;

        return result;
    }


    private void displayConfs(Guild server, TextChannel chan, Message msg, User user, String content) {
        logger.debug("displayConfs - start : server=<" + server.getName() + "> - chan=<" + chan.getName() + "> - user=<" + user.getName() + "> - content=<" + content + ">");
        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            if (content == null || content.equals("")) {
                Hashtable confs = Bot.getInstance().getServersConf().get(server.getId());
                List<String> tell = new ArrayList<>();
                confs.forEach((key, value) -> {
                    logger.debug("key = <" + key + "> - value = <" + value + ">");
                    String line = "-" + key + " : " + value;
                    logger.debug("line = <" + line + ">");
                    tell.add(line);
                });
                MsgUtils.tellBlockFramed(chan, tell, "css");
            } else {
                logger.debug("No parameter needed for this service");
                MsgUtils.tell(chan, "La commande **!confs** ne prend pas de paramètre");
            }
        } else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!confs**) est limitée au chan admin.");
        }
        logger.debug("displayConfs - end");
    }




    private void updateOrInsertConf(Guild server, TextChannel chan, Message msg, User user,String content) {
        logger.debug("updateOrInsertConf - start : server = <"+server.getName()+"> - channel = <"+chan.getName()+"> - user = <"+user.getName()+"> - content = <"+content+">");
        if (chan.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
            logger.debug("is admin channel");
            String[] params = content.split(" ");
            if (params.length == 2) {
                logger.debug("Correct number of parameters : 1 =<"+params[0]+"> - 2=<"+params[1]+">");
                if (Params.SERVER_CONF.contains(params[0])) {
                    String confName = params[0];
                    String value = params[1];
                    Bot bot = Bot.getInstance();
                    BotDAO botDAO = new BotDAO();
                    String tell="";
                    int res = 0;
                    if ( bot.getServersConf().containsKey(confName)){
                        logger.debug("Update conf");
                        res = botDAO.updateConf(server.getId(),confName,value);
                        tell="La conf "+confName+" a été mise à jour avec la valeur "+value+"**.";
                    }else {
                        logger.debug("Insert conf");
                        res = botDAO.addConf(server.getId(),confName,value);
                        tell="La conf **"+confName+"** a été ajoutée avec la valeur **"+value+"**.";
                    }
                    if (res>0){
                        bot.getServersConf().get(server.getId()).put(confName,value);
                        MsgUtils.tell(chan,tell);
                    }
                } else {
                    logger.debug("Conf unknown");
                    List<String> tell = new ArrayList<>();
                    tell.add("Nom de config inconnu. Les confs disponibles sont : ");
                    for (String value : Params.SERVER_CONF) {
                        tell.add("- "+ value);
                    }
                    MsgUtils.tellBlock(chan,tell);
                }
            } else {
                MsgUtils.tell(chan, "Commande incorrecte : !service <service name> <ON/OFF>");
            }
        } else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!service**) est limitée au chan admin.");
        }
        logger.debug("updateOrInsertConf - end");
    }



}
