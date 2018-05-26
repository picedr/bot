package fr.picedr.bot.listener;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class PublicListener implements EventListener {

    public PublicListener(){

    }

    public void onEvent(Event event) {

        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

            String server = e.getGuild().getId();
            String channel = e.getChannel().getId();
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
                    case "" :

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
