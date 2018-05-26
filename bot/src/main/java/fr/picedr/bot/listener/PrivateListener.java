package fr.picedr.bot.listener;

import fr.picedr.bot.Bot;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class PrivateListener implements EventListener {



    public PrivateListener(){

    }

    public void onEvent(Event event) {

        if (event instanceof PrivateMessageReceivedEvent) {
            PrivateMessageReceivedEvent e = (PrivateMessageReceivedEvent) event;



            String author = e.getAuthor().getId();
            String content = e.getMessage().getContentDisplay();
            String cmd = null;
            if (content.startsWith("!")){
                cmd = content.split(" ")[0];
            }

            String msg = "";
            if (cmd!=null){
                msg = content.replaceFirst(cmd+" ","");

                switch(cmd){
                    case "!stop" :
                        Bot.getInstance().stop();
                        break;
                    default :

                        break;
                }
            }else{
                msg = content;

            }




        }

    }



}
