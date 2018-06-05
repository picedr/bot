package fr.picedr.bot.listener;

import fr.picedr.bot.HelpService;
import fr.picedr.bot.admin.AdminService;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicListener implements EventListener {

    private Logger logger = LoggerFactory.getLogger(PublicListener.class);

    public PublicListener(){

    }

    public void onEvent(Event event) {

        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

            Guild server = e.getGuild();
            TextChannel channel = e.getChannel();
            Message msg = e.getMessage();
            User user = e.getAuthor();
            String content = e.getMessage().getContentDisplay();
            String cmd = null;
            logger.debug("Public message : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - content=<"+content+">");

            if (content.startsWith("!")){
                cmd = content.split(" ")[0];
            }
            if (cmd!=null){
                content = content.replaceFirst(cmd,"").trim();
                logger.debug("cmd=<"+cmd+"> - content=<"+content+">");
                switch(cmd){
                    case "!services" :
                    case "!service" :
                    case "!confs" :
                    case "!conf" :
                        AdminService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                        break;
                    case "!aide" :
                    case "!help" :
                        HelpService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                        break;
                    default :
                        MsgUtils.tell(channel,"Je ne connais pas cette comamnde");
                        break;
                }
            }else{

            }




        }

    }



}
