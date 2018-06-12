package fr.picedr.bot.listener;

import fr.picedr.bot.Bot;
import fr.picedr.bot.Params;
import fr.picedr.bot.admin.AdminService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscListener  implements EventListener {

    private Logger logger = LoggerFactory.getLogger(MiscListener.class);

    public MiscListener() {
    }

    public void onEvent(Event event) {
        if (event instanceof UserUpdateGameEvent) {
            logger.debug("UserUpdateGameEvent");
            //A user change game status
            UserUpdateGameEvent  e = (UserUpdateGameEvent) event;
            Guild server = e.getGuild();
            User user = e.getMember().getUser();
            Game newGame =  e.getNewGame();
            Game oldGame = e.getOldGame();
            logger.debug("server=<"+server.getName()+"> " +
                    "- user=<"+user.getName()+"> " +
                    "- newGame=<"+newGame.getName()+"> ");// +
                  //  "- oldGame=<"+oldGame.getName()+">");

        }else if (event instanceof GuildVoiceJoinEvent){
            //user join a voice channel
            logger.debug("GuildVoiceJoinEvent");
            GuildVoiceJoinEvent e = (GuildVoiceJoinEvent ) event;
            Guild server = e.getGuild();
            Channel channel = e.getChannelJoined();
            User user = e.getMember().getUser();
            logger.debug("server=<"+server.getName()+"> - channel = <"+channel.getName()+">- user=<"+user.getName()+">");

            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SHAME).equals("1")
                    && AdminService.getInstance().isShamed(server,user)
                    && !channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_VOICEROLE))){
                logger.debug("Is shamed");
                AdminService.getInstance().moveToShame(server,user);
            }



        }else if (event instanceof GuildVoiceLeaveEvent){
            //user quit voice channel
            logger.debug("GuildVoiceLeaveEvent");
            GuildVoiceLeaveEvent  e = (GuildVoiceLeaveEvent ) event;
            Guild server = e.getGuild();
            Channel channel = e.getChannelLeft();
            User user = e.getMember().getUser();
            logger.debug("server=<"+server.getName()+"> - channel = <"+channel.getName()+">- user=<"+user.getName()+">");


        }else if (event instanceof GuildVoiceMoveEvent){
            //user change of voice channel
            logger.debug("GuildVoiceMoveEvent");
            GuildVoiceMoveEvent  e = (GuildVoiceMoveEvent ) event;
            Guild server = e.getGuild();
            Channel channelL = e.getChannelLeft();
            Channel channelJ= e.getChannelJoined();
            User user = e.getMember().getUser();
            logger.debug("channelL=<"+channelL.getName()+"> - channelJ = <"+channelJ.getName()+">- user=<"+user.getName()+">");

            if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SHAME).equals("1")
                    && AdminService.getInstance().isShamed(server,user)
                    && !channelJ.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_VOICEROLE))){
                logger.debug("Is shamed");
                AdminService.getInstance().moveToShame(server,user);
            }


        } else if (event instanceof GuildMessageReactionAddEvent){
            //User add a reaction icon on a message
            logger.debug("GuildMessageReactionAddEvent");
            GuildMessageReactionAddEvent e = (GuildMessageReactionAddEvent)event;

        }else if (event instanceof GuildMessageReactionRemoveEvent){
            //user remove reaction event from message
            logger.debug("GuildMessageReactionRemoveEvent");
            GuildMessageReactionRemoveEvent e = (GuildMessageReactionRemoveEvent)event;

        }



    }


}
