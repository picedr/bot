package fr.picedr.bot.listener;

import fr.picedr.bot.Bot;
import fr.picedr.bot.HelpService;
import fr.picedr.bot.Params;
import fr.picedr.bot.admin.AdminService;
import fr.picedr.bot.agenda.AgendaService;
import fr.picedr.bot.command.CommandService;
import fr.picedr.bot.jeux.JeuxService;
import fr.picedr.bot.jeux.flood.FloodService;
import fr.picedr.bot.jeux.pfc.PfcService;
import fr.picedr.bot.jeux.pimp.PimpService;
import fr.picedr.bot.jeux.quizz.QuizzService;
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
            logger.debug("Public message : server=<" + server.getName() + "> - channel=<" + channel.getName() + "> - user=<" + user.getName() + "> - content=<" + content + ">");

            if (AdminService.getInstance().isMuted(server, user.getId())
                    && Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_MUTE).equals("1")) {
                logger.debug("User is muted");
                msg.delete().complete();
            }else if(!AdminService.getInstance().canSpeak(server,channel,user)
                    && Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_SLOW).equals("1")){
                logger.debug("msg revoved due to slow channel");
                msg.delete().complete();
            }else {

                if (content.startsWith("!")) {
                    cmd = content.split(" ")[0];
                }
                if (cmd != null) {
                    content = content.replaceFirst(cmd, "").trim();
                    logger.debug("cmd=<" + cmd + "> - content=<" + content + ">");
                    switch (cmd) {
                        case "!services":
                        case "!service":
                        case "!confs":
                        case "!conf":
                        case "!mute":
                        case "!unmute":
                        case "!tellG" :
                        case "!tell" :
                        case "!shame" :
                        case "!unshame" :
                        case "!slow":
                        case "!unslow" :
                        case "!clear" :
                            AdminService.getInstance().dispatch(server, channel, msg, user, cmd, content);
                            break;
                        case "!aide":
                        case "!help":
                            HelpService.getInstance().dispatch(server, channel, msg, user, cmd, content);
                            break;
                        case "!agenda" :
                        case "!rappel":
                        case "!anniv" :
                            AgendaService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                            break;
                        case "!flood" :
                            FloodService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                            break;
                        case "!pierre" :
                        case "!feuille" :
                        case "!ciseaux" :
                        case "!pfc" :
                            PfcService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                            break;
                        case "!pimp" :
                            PimpService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                            break;
                        case "!quizz" :
                            QuizzService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                            break;
                        case "!score":
                        case "!classement":
                            JeuxService.getInstance().dispatch(server,channel,msg,user,cmd,content);
                            break;
                        case "!cmd" :
                            CommandService.getInstance().dispatch(server, channel, msg, user, cmd, content);
                            break;
                        default:
                            CommandService.getInstance().dispatch(server, channel, msg, user, cmd, content);
                            //MsgUtils.tell(channel, "Je ne connais pas cette commande");
                            break;
                    }
                } else {
                    QuizzService.getInstance().publicMsg(server,msg,user,content);
                }
            }

            AdminService.getInstance().storeMsg(channel,msg);
            FloodService.getInstance().newMsg(server, channel,user);

        }

    }



}
