package fr.picedr.bot.utils;

import fr.picedr.bot.Bot;
import fr.picedr.bot.Params;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
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

    /**
     * get user object by his name or nickname
     * @param server : server on which the request is done
     * @param name : name of the user
     * @return an User object or null if nothing found
     */
    public static User getUserByName(Guild server,String name){
        logger.debug("getUserByName - start : server=<"+server.getName()+"> - name=<"+name+">");
        User result = null;
        List<Member> members =  server.getMembersByNickname(name,true);
        if (members!=null && members.size()>0){
            result = members.get(0).getUser();
        }else{
            logger.debug("Nothing found by nickname");
            List<Member> members2 = server.getMembersByName(name,true);
            if (members2!=null && members2.size()>0){
                result = members2.get(0).getUser();
            }else {
                logger.debug("Nothing found by name");
            }
        }
        logger.debug("getUserByName - end");
        return result;
    }

}
