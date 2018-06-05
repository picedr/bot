package fr.picedr.bot;

import fr.picedr.bot.admin.AdminService;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HelpService implements BotService {

    private Logger logger = LoggerFactory.getLogger(HelpService.class);

    private static HelpService INSTANCE = null;

    private HelpService() {

    }

    public static HelpService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HelpService();
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

        if (content == null || content.equals("")) {
            List<String> tell = new ArrayList<>();
            tell.add("Tappez '!help [catégorie]' ou '!aide [catégorie]' pour plus de détails");
            tell.add("Les catégories disponibles sont : ");

            //End User help


            //Admin help
            if (channel.getId().equals(bot.getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
                tell.add("- admin");
            }
            MsgUtils.tellBlockFramed(channel,tell,"css");

        } else {
            switch (content.toLowerCase()) {
                case "admin":
                    AdminService.help(server, channel);
                    break;
                default:
                    MsgUtils.tell(channel, "Catégorie inconnue");
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
        MsgUtils.tell(channel, "Aucune aide pour cette fonctionnalité");
    }

}