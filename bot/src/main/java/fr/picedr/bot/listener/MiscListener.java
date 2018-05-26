package fr.picedr.bot.listener;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class MiscListener  implements EventListener {

    public MiscListener() {
    }

    public void onEvent(Event event) {
        if (event instanceof UserUpdateGameEvent) {
            //A user change game status
            UserUpdateGameEvent  e = (UserUpdateGameEvent ) event;
            String server = e.getGuild().getId();
            User user = e.getMember().getUser();
            Game newGame =  e.getNewGame();
            Game oldGame = e.getOldGame();

        }else if (event instanceof GuildVoiceJoinEvent){
            //user join a voice channel
            GuildVoiceJoinEvent e = (GuildVoiceJoinEvent ) event;
            String server = e.getGuild().getId();
            Channel channel = e.getChannelJoined();
            User user = e.getMember().getUser();




        }else if (event instanceof GuildVoiceLeaveEvent){
            //user quit voice channel
            GuildVoiceLeaveEvent  e = (GuildVoiceLeaveEvent ) event;
            Guild server = e.getGuild();
            Channel channel = e.getChannelLeft();
            User user = e.getMember().getUser();


        }else if (event instanceof GuildVoiceMoveEvent){
            //user change of voice channel
            GuildVoiceMoveEvent  e = (GuildVoiceMoveEvent ) event;
            String channelL = e.getChannelLeft().getName();
            String channelN = e.getChannelJoined().getName();
            String user = e.getMember().getUser().getName();


        } else if (event instanceof GuildMessageReactionAddEvent){
            //User add a reaction icon on a message
            GuildMessageReactionAddEvent e = (GuildMessageReactionAddEvent)event;

        }else if (event instanceof GuildMessageReactionRemoveEvent){
            //user remove reaction event from message
            GuildMessageReactionRemoveEvent e = (GuildMessageReactionRemoveEvent)event;

        }



    }


}
