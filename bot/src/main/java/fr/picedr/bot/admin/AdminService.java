package fr.picedr.bot.admin;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.dao.BotDAO;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AdminService implements BotService {

    private Logger logger = LoggerFactory.getLogger(AdminService.class);

    private static AdminService INSTANCE = null;

    private Hashtable<String,List<String>> muted;
    private Hashtable<String,List<String>> shamed;
    private Hashtable<String,Date> slowLastMsg;
    private Hashtable<String, Integer> slowDelays;
    private Hashtable<String, List<String>> storedMessages;

    private static int DEFAULT_SLOW_DELAY = 2;
    public static int MAX_MSG_HISTORY=100;

    private AdminService() {
        AdminDAO adminDAO = new AdminDAO();
        muted = adminDAO.getMuted();
        shamed = adminDAO.getShamed();
        slowDelays = new Hashtable<>();
        slowLastMsg = new Hashtable<>();
        storedMessages = new Hashtable<>();

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
            case "!mute":
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_MUTE).equals("1")){
                    mute(server,channel,user,content);
                }
                break;
            case "!unmute" :
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_MUTE).equals("1")){
                    unmute(server,channel,user,content);
                }
                break;
            case "!shame":
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SHAME).equals("1")){
                    shame(server,channel,user,content);
                }
                break;
            case "!unshame" :
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SHAME).equals("1")){
                    unshame(server,channel,user,content);
                }
                break;
            case "!slow":
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SLOW).equals("1")){
                    slow(server,channel,user,content);
                }
                break;
            case "!unslow" :
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SLOW).equals("1")){
                    unslow(server,channel,user);
                }
                break;
            case "!clear" :
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_CLEAR).equals("1")){
                    clear(server,channel,user,content);
                }
                break;
            case "!tellG" :
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_TELL).equals("1")){
                    tellG(server,channel,user,msg,content);
                }
                break;
            case "!tell" :
                if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_TELL).equals("1")){
                    tell(server,channel,user,msg,content);
                }
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
                    MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help admin** pour plus de details");
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
            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_TELL).equals("1")){
                tell.add(" ");
                tell.add("#TELL : ");
                tell.add("Deux commandes disponibles");
                tell.add("- !tellG [message] : message à écrire sur le canal général par le bot.");
                tell.add("- !tell [pseudo] [message] : message à envoyé en privé à l'utilisateur par le bot.");
            }
            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_MUTE).equals("1")){
                tell.add(" ");
                tell.add("#MUTE : ");
                tell.add("Deux commandes disponibles : ");
                tell.add("- !mute [pseudo] : pour empecher un utilisateur d'écrire sur l'ensemble des canaux textuels.");
                tell.add("- !unmute[pseudo] : pour autoriser à nouveau l'utilisateur à écrire.");
            }
            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SHAME).equals("1")){
                tell.add(" ");
                tell.add("#SHAME : ");
                tell.add("Retire le droit **VOICE** à l'utilisateur (l'empechant d'accéder aux voice channel nécessitant ce droit) et le déplace sur le **shame chan**.");
                tell.add("Deux commandes disponibles");
                tell.add("- !shame [pseudo] : Active le shame sur un utilisateur.");
                tell.add("- !unshame [pseudo] [message] : désactive le shame sur un utilisateur.");
            }
            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SLOW).equals("1")){
                tell.add(" ");
                tell.add("#SLOW : ");
                tell.add("Deux commandes disponibles");
                tell.add("- !slow [delay]: passe le chan en mode slow : pas plus d'un message toute les [delay] secondes.");
                tell.add("La commande peut être passée sans paramètre, la valeur par defaut appliquée sera alors de ["+DEFAULT_SLOW_DELAY+"] secondes");
                tell.add("- !unslow : désactive le slow mode sur le chan.");
            }
            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_CLEAR).equals("1")){
                tell.add(" ");
                tell.add("#CLEAR : ");
                tell.add("Une commande disponible");
                tell.add("- !clear [nb]: supprime les [nb] dernières ligne du chan (en cas de reboot du bot, l'historique est perdu).");
            }

        }
        MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
    }

    /**
     * Check everything is set (conf mainly) to activate a service
     * @param server : server on which the service is enabaling
     * @param service : name of the service;
     * @return : true if can be enabled, false if not.
     */
    private boolean isServiceSetable(Guild server,TextChannel channel,String service){
        boolean result = true;
        logger.debug("isServiceSetable - start : server = <"+server.getName()+"> - channel=<"+channel.getName()+"> - service=<"+service+">");
        if (service.equals(Params.SRV_MUTE)){
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_ADMINROLE)){
                logger.debug("No "+Params.CONF_ADMINROLE);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'aucun **rôle d'administrateur** n'est défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf "+Params.CONF_ADMINROLE+" **idDuRole**");
                MsgUtils.tellBlock(channel,tell);
                result = false;
            }
        }else if (service.equals(Params.SRV_TELL)){
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_GENERALCHANNEL)){
                logger.debug("No "+Params.CONF_GENERALCHANNEL);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'un **chan général** n'est pas défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf "+Params.CONF_GENERALCHANNEL+" **idDuChan**");
                MsgUtils.tellBlock(channel,tell);
                result = false;
            }
        }  else if (service.equals(Params.SRV_SHAME)) {
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_SHAMECHANNEL)) {
                logger.debug("No "+Params.CONF_SHAMECHANNEL);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'un **chan shame** n'est pas défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf " + Params.CONF_SHAMECHANNEL + " **idDuChan**");
                MsgUtils.tellBlock(channel, tell);
                result = false;
            }
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_VOICEROLE)) {
                logger.debug("No "+Params.CONF_VOICEROLE);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'aucun **rôle VOICE** n'est défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf " + Params.CONF_VOICEROLE + " **idDuRole**");
                MsgUtils.tellBlock(channel, tell);
                result = false;
            }
        }else if (service.equals(Params.SRV_SLOW)){
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_ADMINROLE)){
                logger.debug("No "+Params.CONF_ADMINROLE);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'aucun **rôle d'administrateur** n'est défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf "+Params.CONF_ADMINROLE+" **idDuRole**");
                MsgUtils.tellBlock(channel,tell);
                result = false;
            }
        }else if (service.equals(Params.SRV_CLEAR)){
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_ADMINROLE)){
                logger.debug("No "+Params.CONF_ADMINROLE);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'aucun **rôle d'administrateur** n'est défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf "+Params.CONF_ADMINROLE+" **idDuRole**");
                MsgUtils.tellBlock(channel,tell);
                result = false;
            }
        }else if (service.equals(Params.SRV_AGENDA)){
            if (!Bot.getInstance().getServersConf().get(server.getId()).containsKey(Params.CONF_GENERALCHANNEL)){
                logger.debug("No "+Params.CONF_GENERALCHANNEL);
                List<String> tell = new ArrayList<>();
                tell.add("Ce service ne peut être activé tant qu'un **chan général** n'est pas défini.");
                tell.add("Pour cela, il suffit de lancer la commande :");
                tell.add("- !conf "+Params.CONF_GENERALCHANNEL+" **idDuChan**");
                MsgUtils.tellBlock(channel,tell);
                result = false;
            }
        }
        return result;
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
                MsgUtils.tellBlockFramed(chan, tell, MsgUtils.FT_CSS);
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
                            if (state.equals("0") || isServiceSetable(server,chan,params[0])) {
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
                MsgUtils.tellBlockFramed(chan, tell, MsgUtils.FT_CSS);
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
                    if ( bot.getServersConf().get(server.getId()).containsKey(confName)){
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

    /**
     * Put a user into muted List. Until he is removed from this list, his public messages will automatically be removed.
     * Only admins can mute and they can't be muted
     * @param server : Server on which the user will be muted
     * @param channel : channel from which come the command
     * @param author : user who launchned the command
     * @param user : nickname of the user
     */
    private void mute(Guild server,TextChannel channel, User author, String user){
        logger.debug("AdminCmd - mute : debut : server=<"+server.getName()+"> - author=<"+author.getName()+"> - user=<"+user+">");
        if (UserUtils.isAdmin(server,author)){
            User mutedUser = UserUtils.getUserByName(server,user);
            if (mutedUser==null){
                MsgUtils.tell(channel,"Je ne connais pas "+user);
            } else {

                if ((!muted.containsKey(server.getId()) ||
                        !muted.get(server.getId()).contains(mutedUser.getId()))
                        && !UserUtils.isAdmin(server,mutedUser)) {
                    AdminDAO adminDAO = new AdminDAO();
                    int nb = adminDAO.mute(server.getId(),mutedUser.getId());
                    if (nb>0){
                        if (muted.containsKey(server.getId())){
                            muted.get(server.getId()).add(mutedUser.getId());
                        }else {
                            List<String> users = new ArrayList<>();
                            users.add(mutedUser.getId());
                            muted.put(server.getId(),users);
                        }
                        MsgUtils.tellFramed(channel,user+" ne peut plus parler",MsgUtils.FT_CSS);
                    }
                }
            }
        }else {
            MsgUtils.tell(channel, "Bien essayé, mais nan.");
        }
        logger.debug("mute - end");
    }

    /**
     * Remove a user from the muted list. His message will not be removed automatically
     * @param server : Server on which the user will be muted
     * @param channel : channel from which come the command
     * @param author : user who launchned the command
     * @param user : nickname of the user
     */
    private void unmute(Guild server,TextChannel channel, User author, String user){
        logger.debug("AdminCmd - mute : debut : server=<"+server.getName()+"> - author=<"+author.getName()+"> - user=<"+user+">");
        if (UserUtils.isAdmin(server,author)){
            User mutedUser = UserUtils.getUserByName(server,user);
            if (mutedUser==null){
                MsgUtils.tell(channel,"Je ne connais pas "+user);
            } else {
                if (muted.get(server.getId()).contains(mutedUser.getId())) {
                    AdminDAO adminDAO = new AdminDAO();
                    int nb = adminDAO.unmute(server.getId(),mutedUser.getId());
                    if (nb>0) {
                        muted.get(server.getId()).remove(mutedUser.getId());
                        MsgUtils.tellFramed(channel,user+" peut de nouveau parler",MsgUtils.FT_CSS);
                    }
                } else {
                    MsgUtils.tell(channel,user+" n'est pas bloqué.");
                }
            }
        }else {
            MsgUtils.tell(channel, "Bien essayé, mais nan.");
        }
        logger.debug("unmute - end");
    }

    /**
     * Check if a user is muted
     * @param id : id of the user to check
     * @return true if muted false if not
     */
    public boolean isMuted(Guild server,String id){
        logger.debug("isMuted - start : server=<"+server.getId()+"> - id=<"+id+">");
        boolean result = false;
        if (muted.containsKey(server.getId()) && muted.get(server.getId()).contains(id)){
            result = true;
        }
        logger.debug("isMuted - end : result=<"+result+">");
        return result;

    }


    /**
     * Make the bot tell something on General channel
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     * @param message : message that contains the command
     * @param content : message to tell
     */
    private void tellG(Guild server, TextChannel channel,User user, Message message,String content){
        logger.debug("tellG - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - message=<"+content+">");
        if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))){
            logger.debug("Is admin channel");
            TextChannel general = server.getTextChannelById(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_GENERALCHANNEL));
            MsgUtils.tell(general,content);
        }else {
         logger.debug("Not admin channel");
         message.delete().complete();
         MsgUtils.tell(user,"La commande **!tellG** doit être lancée sur le chan admin");
        }
        logger.debug("tellG - end");
    }

    /**
     * Bot send a private message to a user
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     * @param message : message that contains the command
     * @param content : message parameter
     */
    private void tell( Guild server, TextChannel channel,User user, Message message,String content){
        logger.debug("tellG - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - message=<"+content+">");

        String target="";
        String msg="";

        if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))){

            if (content.startsWith("\"")){
                //If message contains quotes, to be able to manage user name with spaces
                String[] spContent = content.split("\"");
                if (spContent.length>2){
                    target = spContent[1];
                    msg = content.replaceFirst("\""+target+"\" ", "");
                }
            }else {
                //if no quotes in message
                String[] splContent = content.split(" ");
                if (splContent.length>1){
                    target = splContent[0];
                    msg = content.replaceFirst(""+target+" ", "");
                }
            }
            logger.debug("tell : target=<"+target+">, msg=<"+msg+">");
            User destUser = UserUtils.getUserByName(server,target);
            if (destUser==null){
                MsgUtils.tell(channel,"Je ne connais pas cet utilisateur");
            }else {
                MsgUtils.tell(destUser,msg);
            }
        }else {
            logger.debug("Not admin channel");
            message.delete().complete();
            MsgUtils.tell(user,"La commande **!tell** doit être lancée sur le chan admin");
        }
        logger.debug("tell : fin");
    }

    /**
     * Audio channel functionnality.
     * Move a user into a specific channel and dont let him to move from it.
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     * @param content : message parameter
     */
    private void shame(Guild server,TextChannel channel, User user, String content){
        logger.debug("shame - start : server=<"+server.getName()+"> - channel=<"+channel.getName()
                +"> - user=<"+user.getName()+"> - content = <"+content+">");

        if (UserUtils.isAdmin(server,user)){
            logger.debug("Is admin");
            User target = UserUtils.getUserByName(server,content);
            if (target !=null) {
                logger.debug("Target found");

                if ((!shamed.containsKey(server.getId()) || !shamed.get(server.getId()).contains(user.getId()))
                        && !UserUtils.isAdmin(server,target)) {
                    AdminDAO adminDAO = new AdminDAO();
                    int nb = adminDAO.shame(server.getId(),target.getId());
                    if (nb>0){
                        if (!shamed.containsKey(server.getId())){
                            shamed.put(server.getId(),new ArrayList<>());
                        }
                        shamed.get(server.getId()).add(target.getId());
                        Member member = server.getMember(target);
                        GuildController controller = server.getController();
                        Hashtable<String,String> serverConf = Bot.getInstance().getServersConf().get(server.getId());
                        VoiceChannel shameCh = server.getJDA().getVoiceChannelById(serverConf.get(Params.CONF_SHAMECHANNEL));
                        controller.moveVoiceMember(member, shameCh).complete();
                        controller.removeRolesFromMember(member,server.getRoleById(serverConf.get(Params.CONF_VOICEROLE))).complete();
                        MsgUtils.tellFramed(channel, "Honte à toi [" + target.getName() + "]",MsgUtils.FT_CSS);
                    }
                }
            }else {
                logger.debug("Target not found");
                MsgUtils.tell(channel,content+" : utilisateur inconnu");
            }
        }else {
            logger.debug("Is not admin");
            MsgUtils.tell(channel,"Bien essayé, mais nan.");
        }
        logger.debug("shame - end");
    }

    /**
     * Reauthorize user to move to other voice channels
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     * @param content : message parameter
     */
    private void unshame(Guild server,TextChannel channel, User user, String content){
        logger.debug("unshame - start : server=<"+server.getName()+"> - channel=<"+channel.getName()
                +"> - user=<"+user.getName()+"> - content = <"+content+">");

        if (UserUtils.isAdmin(server,user)){
            logger.debug("Is admin");
            User target = UserUtils.getUserByName(server,content);
            if (target !=null) {
                logger.debug("Target found");
                logger.debug("Contains server : "+shamed.contains(server.getId()));
                logger.debug("Contains target : "+shamed.get(server.getId()).contains(target.getId()));
                if (shamed.containsKey(server.getId()) && shamed.get(server.getId()).contains(target.getId())){
                    AdminDAO adminDAO = new AdminDAO();
                    int nb = adminDAO.unshame(server.getId(),target.getId());
                    if (nb>0) {
                        shamed.get(server.getId()).remove(target.getId());
                        Member member = server.getMember(target);
                        GuildController controller = server.getController();
                        controller.addRolesToMember(member,server.getRoleById(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_VOICEROLE))).complete();

                    }

                    MsgUtils.tellFramed(channel, "C'est bon ["+target.getName()+"] tu peux revenir.",MsgUtils.FT_CSS);
                }
            }else {
                logger.debug("Target not found");
                MsgUtils.tell(channel,content+" : utilisateur inconnu");
            }
        }else {
            MsgUtils.tell(channel, "Bien essayé, mais nan.");
        }
        logger.debug("unshame - end");
    }

    /**
     * Check if a user is shamed
     * @param server : server to check
     * @param user : user to check
     * @return true if shamed false if not
     */
    public boolean isShamed(Guild server, User user){
        logger.debug("isShamed - start : server=<"+server.getName()+"> - user=<"+user.getName()+">");
        boolean result = false;
        if (shamed.containsKey(server.getId()) && shamed.get(server.getId()).contains(user.getId())){
            result = true;
        }
        logger.debug("isshamed - fin : result=<"+result+">");
        return result;
    }

    /**
     * Move a user to the shame channel
     * @param server on which the action is done
     * @param user to move
     */
    public void moveToShame(Guild server, User user){
        Member member = server.getMember(user);
        GuildController controller = server.getController();
        Hashtable<String,String> serverConf = Bot.getInstance().getServersConf().get(server.getId());
        VoiceChannel shameCh = server.getJDA().getVoiceChannelById(serverConf.get(Params.CONF_SHAMECHANNEL));
        controller.moveVoiceMember(member, shameCh).complete();
    }




    /**
     * Activate slow mode
     * Use slowDelay map : contains for each channel in slow mode the delay between two messages
     * Use slowTime map : contains last message timestamp
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     * @param content : message parameter
     */
    private void slow(Guild server, TextChannel channel, User user, String content){
        logger.debug("slow - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - content=<"+content+">");
        if (UserUtils.isAdmin(server,user)){
            int delay = DEFAULT_SLOW_DELAY;

            if (content!=null && !content.equals("")){
                try{
                    delay = Integer.valueOf(content).intValue();
                }catch (NumberFormatException nfe){
                    MsgUtils.tell(channel,"Le paramètre de la fonction **!slow** doit être un nombre.");
                    return;
                }
            }

            int nb=0;
            AdminDAO adminDAO = new AdminDAO();
            if(slowDelays.containsKey(channel.getId())){
                nb = adminDAO.slowUpdate(channel.getId(),delay);
            }else {
                nb = adminDAO.slowInsert(channel.getId(),delay);
            }

            if (nb>0){
                slowDelays.put(channel.getId(), delay);
            }

            MsgUtils.tellFramed(channel, "Le chan passe en slow mode : max 1 message toutes les "+delay+" secondes.",MsgUtils.FT_CSS);
        }else {
            MsgUtils.tell(channel, "Bien essayé, mais nan.");
        }
        logger.debug("slow - end");
    }

    /**
     * Remove slow mode from a channel
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     */
    private void unslow(Guild server, TextChannel channel, User user){
        logger.debug("unslow - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
        if (UserUtils.isAdmin(server,user)){
            if (slowDelays.containsKey(channel.getId())){
                AdminDAO adminDAO = new AdminDAO();
                int nb = adminDAO.slowDelete(channel.getId());
                if (nb>0){
                    slowDelays.remove(channel.getId());
                    if (slowLastMsg.containsKey(channel.getId())){
                        slowLastMsg.remove(channel.getId());
                    }
                    MsgUtils.tellFramed(channel,"Slow mode désactivé",MsgUtils.FT_CSS);
                }
            }else {
                MsgUtils.tellFramed(channel,"Pas de slow mode activé sur ce chan.",MsgUtils.FT_CSS);
            }
        }else {
            MsgUtils.tell(channel, "Bien essayé, mais nan.");
        }
        logger.debug("unslow - end");
    }

    /**
     * For slow mode : check if a message has to be removed.
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     * @return : true if the message can be displayed, false if it has to be removed
     */
    public boolean canSpeak(Guild server,TextChannel channel,User user){
        boolean result = false;
        logger.debug("unslow - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
        if (UserUtils.isAdmin(server,user)){
            result=true;
        } else {
            if (slowDelays.containsKey(channel.getId())){
                if (slowLastMsg.containsKey(channel.getId())){
                    Date lastDate = slowLastMsg.get(channel.getId());
                    int delay = slowDelays.get(channel.getId());
                    Calendar calcCal =  Calendar.getInstance();
                    calcCal.add(Calendar.SECOND,-delay);
                    Date calcDate = calcCal.getTime();
                    if(calcDate.after(lastDate)){
                        result = true;
                        slowLastMsg.put(channel.getId(),Calendar.getInstance().getTime());
                    }
                }else {
                    result = true;
                    slowLastMsg.put(channel.getId(),Calendar.getInstance().getTime());
                }
            }else {
                result=true;
            }

        }
        logger.debug("canSpeak - end : result=<"+result+">");
        return result;
    }



    /**
     * Remove all messages from the history.
     * For this functionnality, messages are put into storedMsg map that store every message per chan.
     * @param server : server on which the command is launched
     * @param channel : channel on which the command is launched
     * @param user : user who launch the command
     */
    private void clear(Guild server, TextChannel channel, User user, String content){
        logger.debug("clear - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
        if (UserUtils.isAdmin(server,user)){

            int nbClear = new Integer(content);
            List<String> msgs = storedMessages.get(channel.getId());
            int msgSize = msgs.size();
            logger.debug("clear : nbStoredMsg=<"+msgSize+">");
            Collection<String> col = new ArrayList<>();
            if (nbClear>=msgs.size()){
                channel.deleteMessagesByIds(msgs).complete();
                storedMessages.put(channel.getId(), new ArrayList<>());
            }else {
                for (int i = msgs.size()-1;i>msgSize-nbClear-1;i--){
                    col.add(msgs.get(i));
                    msgs.remove(i);
                }
                if (col.size()==1){
                    channel.deleteMessageById(((List<String>)col).get(0)).complete();
                }else {
                    channel.deleteMessagesByIds(col).complete();
                }
                storedMessages.put(channel.getId(), msgs);
            }
        }

        logger.debug("AdminCmd - clear : fin");
    }

    /**
     * Called for storing message every time one is written on public channel.
     * @param channel : channel of the message stored
     * @param msg : message to store
     */
    public void storeMsg(TextChannel channel, Message msg){
        logger.debug("AdminCmd - storeMsg : debut : channel = <"+channel.getName()+"> - msg=<"+msg.getContentDisplay()+">");
        if (storedMessages.containsKey(channel.getId())){
            storedMessages.get(channel.getId()).add(msg.getId());
            if (storedMessages.get(channel.getId()).size()>MAX_MSG_HISTORY){
                storedMessages.get(channel.getId()).remove(0);
            }
        } else {
            List<String> list = new ArrayList<String>();
            list.add(msg.getId());
            storedMessages.put(channel.getId(), list);
        }
        logger.debug("storeMsg - end");
    }

}
