package fr.picedr.bot.listener;

import fr.picedr.bot.admin.AdminService;
import fr.picedr.bot.jeux.pimp.PimpService;
import fr.picedr.bot.jeux.quizz.QuizzService;
import fr.picedr.bot.utils.MsgUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivateListener implements EventListener {

    private Logger logger = LoggerFactory.getLogger(PrivateListener.class);


    public PrivateListener(){

    }

    public void onEvent(Event event) {

        if (event instanceof PrivateMessageReceivedEvent) {
            PrivateMessageReceivedEvent e = (PrivateMessageReceivedEvent) event;
            User user = e.getAuthor();
            Message msg = e.getMessage();
            String content = e.getMessage().getContentDisplay();
            String cmd = null;

            logger.debug("PrivateMessage : user=<"+user.getName()+"> - content = <"+content+">");

            if (content.startsWith("!")){
                cmd = content.split(" ")[0];
            }

            if (cmd!=null){
                content = content.replaceFirst(cmd,"").trim();
                logger.debug("cmd=<"+cmd+"> - content=<"+content+">");
                switch(cmd){
                    case "!stop" :
                        logger.debug("!stop");
                        AdminService.getInstance().dispatch(null,null,msg,user,cmd,content);
                        break;
                    case "!pair" :
                    case "!impair" :
                        PimpService.getInstance().dispatch(null,null,msg,user,cmd,content);
                        break;
                    case "!quizz" :
                        QuizzService.getInstance().dispatch(null,null,msg,user,cmd,content);
                        break;
                    default :
                        MsgUtils.tell(user,"Tu es sûr de vouloir fair ça ici ?",2);
                        break;
                }
            }else{
                MsgUtils.tell(user,"Hum hum ...",2);
            }




        }

    }



}
