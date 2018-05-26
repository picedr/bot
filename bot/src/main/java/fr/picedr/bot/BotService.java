package fr.picedr.bot;

import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public interface BotService {


    /**
     * Dispatch the command passed to the service
     * @param server : Server from which come the command
     * @param channnel : Channel from wich come the commande
     * @param user : user that launch the command
     * @param cmd : command
     * @param content : parameters of the command
     */
    public void dispatch(Guild server, Channel channnel, User user, String cmd, String content);

    /**
     * Called to display help
     * @param channel : channel to display help on
     * @param server : server which called the help
     */
    public static void help(Guild server, TextChannel channel){
        MsgUtils.tell(channel,"Aucune aide pour cette fonctionnalité");
    }


}
