package fr.picedr.bot.admin;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.dao.BotDAO;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class AdminService implements BotService {



    private static AdminService INSTANCE = null;

    private AdminService(){

    }

    public static AdminService getInstance(){
        if (INSTANCE == null){
            INSTANCE = new AdminService();
        }
        return INSTANCE;
    }

    /**
     * Dispatch the command passed to the service
     * @param server : Server from which come the command
     * @param channel : Channel from which come the commande
     * @param user : user that launch the command
     * @param cmd : command
     * @param content : parameters of the command
     */
    public void dispatch(Guild server, TextChannel channel, User user, String cmd, String content){
        Bot bot = Bot.getInstance();
        switch (cmd){
            case "!addServer" :
                if(Bot.getInstance().getRoles().get(Params.SUPERADMIN).contains(user.getId())){
                    String[] params = content.split(" ");
                    if (params.length==2){
                        addServer(params[0],params[1]);
                    }else{
                        MsgUtils.tell(user,"Commande incorrecte : !addServer <serverId> <adminChannelId>");
                    }
                }
                break;
            case "!stop" :
                if(bot.getRoles().get(Params.SUPERADMIN).contains(user.getId())){
                    bot.stop();
                }
                break;
            default:
                if(channel == null) {
                    MsgUtils.tell(user, "Je ne connais pas cette commande.");
                } else {
                    MsgUtils.tell(channel, "Je ne connais pas cette commande.");
                }
        }
    }

    /**
     * Called to display help
     * @param channel : channel to display help on
     * @param server : server which called the help
     */
    public static void help(Guild server, TextChannel channel){
        MsgUtils.tell(channel,"Aucune aide pour cette fonctionnalité");
    }

    private void addServer(String serverId, String adminChannelId){
        BotDAO botDAO = new BotDAO();
        botDAO.addConf(serverId,Params.ADMINCHANNEL,adminChannelId);
    }

}
