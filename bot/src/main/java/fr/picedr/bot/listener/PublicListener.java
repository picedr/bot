package fr.picedr.bot.listener;

import fr.picedr.bot.admin.AdminService;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class PublicListener implements EventListener {

    public PublicListener(){

    }

    public void onEvent(Event event) {

        if (event instanceof GuildMessageReceivedEvent) {
            GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

            Guild server = e.getGuild();
            TextChannel channel = e.getChannel();
            User user = e.getAuthor();
            String content = e.getMessage().getContentDisplay();
            String cmd = null;
            if (content.startsWith("!")){
                cmd = content.split(" ")[0];
            }
            String msg;
            if (cmd!=null){
                msg = content.replaceFirst(cmd+" ","");

                switch(cmd){
                    case "!stop" :
                        AdminService.getInstance().dispatch(server,channel,user,cmd,msg);
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
