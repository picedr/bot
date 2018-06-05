package fr.picedr.bot.utils;

import fr.picedr.bot.Bot;
import fr.picedr.bot.Params;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;

public class UserUtils {

    private static Logger logger = LoggerFactory.getLogger(UserUtils.class);

    /**
     * To know if user is admin on a server or not
     * @param server
     * @param user
     * @return true if is admin, false else.
     */
    public static  boolean isAdmin(Guild server, User user){
        logger.debug("isAdmin - start : server=<"+server.getName()+"> - user=<"+user.getName()+">");
        boolean result = false;
        Bot bot = Bot.getInstance();
        List<Role> roles = server.getMember(user).getRoles();
        for (Role role : roles){
            logger.debug("Check role "+role.getName());
            Hashtable<String,String> conf = bot.getServersConf().get(server.getId());
            if (conf.containsKey(Params.CONF_ADMINROLE) && conf.get(Params.CONF_ADMINROLE).equals(role.getId())){
                logger.debug("is admin role");
                result = true;
            }
        }
        logger.debug("isAdmin - end : result=<"+result+">");
        return result;
    }

}
