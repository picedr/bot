package fr.picedr.bot.jeux.flood;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.jeux.flood.bean.FloodUser;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import fr.picedr.bot.utils.Utils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FloodService implements BotService {

    private Logger logger = LoggerFactory.getLogger(FloodService.class);

    private static FloodService INSTANCE = null;

	private Hashtable<String,Hashtable<String, FloodUser>> scores;
	private Hashtable<Integer, Integer> totalScore;
	private Hashtable<Integer, Integer> nextScore;
	private int max;

	

    private FloodService(){
    	logger.debug("FloodService init");
    	FloodDAO floodDAO = new FloodDAO();
    	totalScore = new Hashtable<>();
    	nextScore = new Hashtable<>();
		max = floodDAO.getMax();
		fillMaps();
		scores = floodDAO.getScores();
		logger.debug("Flood service init end");
    }


    public static FloodService getInstance() {
    	if (INSTANCE == null) {
            INSTANCE = new FloodService();
        }
        return INSTANCE;
    }

    /**
     * Dispatch the command passed to the service
     *
     * @param server  : Server from which come the command
     * @param channel : Channel from which come the commande
     * @param user    : user that launch the command
     * @param cmd     : command
     * @param content : parameters of the command
     */
    public void dispatch(Guild server, TextChannel channel, Message msg, User user, String cmd, String content) {
        logger.debug("dispatch - start : cmd=<" + cmd + "> - content = <" + content + ">");
        String function = content.split(" ")[0].toLowerCase();
        Bot bot = Bot.getInstance();
        logger.debug("Jeux service : "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX));
        if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
           switch (cmd){
               case "!flood":
                   switch (function) {
                       case "score":
                           score(server,channel,user);
                           break;
					   case "classement":
                           classement(server,channel);
                           break;
					   case "help":
					   case "aide" :
							FloodService.help(server,channel);
					  		break;
                       default:
                           logger.debug("default function");
                           if (channel == null) {
                               MsgUtils.tell(user, "Je ne connais pas cette commande.");
                           } else {
                               MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help flood** pour plus de details",1);
                           }
                   }
                   break;
               default :
                   logger.debug("default cmd");
                   if (channel == null) {
                       MsgUtils.tell(user, "Je ne connais pas cette commande.");
                   } else {
                       MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help flood** pour plus de details",1);
                   }
           }

        } else {
        	logger.debug("Command not recognized");
            MsgUtils.tell(user, "Je ne connais pas cette commande. Tappe **!help** pour plus de détails");
        }

    }



    /**
     * Called to display help
     *
     * @param channel : channel to display help on
     * @param server  : server which called the help
     */
    public static void help(Guild server, TextChannel channel) {
		List<String> tell = new ArrayList<>();
		if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")){
			tell.add("AIDE POUR LA SECTION [FLOOD]");
			tell.add(" ");
			tell.add("Le service [flood] est un jeu.");
			tell.add("Les commandes disponibles sont : ");
			tell.add("-**!flood score** : pour afficher son score*");
			tell.add("-**!flood classement** : pour afficher le classement pour ce jeu*");
			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS, 2);
		} else {
			MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help** pour plus de détails");
		}
    }


	/**
	 * Initialisation of Service maps
	 */
	private void fillMaps(){
		logger.debug("fillMaps - start");
		int nbPts = 100;
		int ptsAdd = 55;
		int total = 0;
		
		this.totalScore.put(0,0);
		this.nextScore.put(0, 100);
		
		for (int i = 1;i<=max;i++){
			total = total+nbPts;			
			nbPts = nbPts+ptsAdd;
			ptsAdd = ptsAdd+10;
			
			this.nextScore.put(i, nbPts);
			this.totalScore.put(i, total);
		}
		logger.debug("fillMaps - end");
	}


	/**
	 * Manage when a user type a message on a public channel
	 * @param server : server on which message is posted
	 * @param channel : channel on which message is posted
	 * @param user : user who posted the message
	 */
	public void newMsg(Guild server,TextChannel channel,User user){
		logger.debug("newMsg - start : server=<"+server.getName()+"> - channel = <"+channel.getName()+"> - user = <"+user.getName()+">");
		String id = user.getId(); 
		long time = (new Date()).getTime();
		logger.debug("time = <"+time+">");
		long DELAY = 60000;
		Bot bot = Bot.getInstance();
		logger.debug("Jeux service : "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX));
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			if (!user.isBot()) {
				if (!scores.containsKey(server.getId())) {
					logger.debug("First server message");
					scores.put(server.getId(), new Hashtable<>());
				}

				if (scores.get(server.getId()).containsKey(id)) {
					logger.debug("Scores containes user for this server");
					FloodUser player = scores.get(server.getId()).get(id);
					long lastMsg = player.getLastMsg();
					logger.debug("lastMsg = <" + lastMsg + ">");

					if (lastMsg + DELAY < time) {
						logger.debug("Last message befor delay");
						int score = player.getScore();
						logger.debug("Player old score : <" + score + ">");
						score = score + Utils.rand(10) + 15;
						logger.debug("Player new score : <" + score + ">");
						int ns = nextScore.get(player.getNiv());
						logger.debug("ns = <" + ns + ">");
						if (score >= ns) {
							logger.debug("level up");
							score = score - ns;
							player.setScore(score);
							int n = player.getNiv();
							logger.debug("n = <" + n + ">");
							if (n == max) {
								max = max + 1;
								fillMaps();
							}
							player.setNiv(n + 1);
							MsgUtils.tellFramed(channel, "GG [" + user.getName() + "] a encore gagné un level inutile .... Merci d'avoir floodé car tu viens d'atteindre le level <" + player.getNiv() + ">  <3", MsgUtils.FT_CSS, 1);
						} else {
							logger.debug("No level up");
							player.setScore(score);
						}
						player.setLastMsg(time);
						scores.get(server.getId()).put(player.getUserId(), player);
						FloodDAO floodDAO = new FloodDAO();
						floodDAO.updatePlayer(server.getId(), user.getId(), player);
					}
				} else {
					logger.debug("First play of the user");
					FloodUser player = new FloodUser(user.getId(), 0, Utils.rand(10) + 15);
					player.setLastMsg(time);
					scores.get(server.getId()).put(player.getUserId(), player);
					FloodDAO floodDAO = new FloodDAO();
					int addP = floodDAO.addPlayer(server.getId(), user.getId(), player);
					if (addP > 0) {
						MsgUtils.tell(channel, "Bienvenue dans le jeu du Flood " + user.getName() + ".", 2);
					}
				}
			}
		}

		logger.debug("newMsg - end");
	}	
	
	
	/**
	* Display score for this game
	* @param server : server on which the command is launched
	* @param channel : channel to display the score on
	* @param user : user for which display the score
	*/
	public void score(Guild server,TextChannel channel, User user){
		logger.debug("score - start : server = <"+server.getName()+"> - channel = <"+channel.getName()+"> - user = <"+user.getName()+">");
		Bot bot = Bot.getInstance();
		logger.debug("Jeux service : "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX));
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			if (scores .containsKey(server.getId())
					&& scores.get(server.getId()).containsKey(user.getId())) {
				logger.debug("scores contains user for this server");
				FloodUser player = scores.get(server.getId()).get(user.getId());
				int total = totalScore.get(player.getNiv()) + player.getScore();
				List<String> tell = new ArrayList<>();
				tell.add(user.getName() + " : ");
				tell.add("- [Rang] : " + getRank(server, user));
				tell.add("- [Niveau] : " + player.getNiv());
				tell.add("- [Points] : " + player.getScore() + "/" + nextScore.get(player.getNiv()));
				tell.add("- [Total points] : " + total);
				MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
			} else {
				MsgUtils.tell(channel, "Tu n'as pas encore joué à ce jeu", 1);
			}
		}
		logger.debug("score - end");
	}


	/**
	 * Get the rank of a user on a server
	 * @param server : server server on which getting the rank
	 * @param user : user to get the rank of
	 * @return rank of the user
	 */
	private int getRank(Guild server, User user){
		logger.debug("getRank - start : server = <"+server.getName()+"> - user = <"+user.getName()+">");
		int rank = 0;
		Hashtable<Integer, List<FloodUser>> classement = new Hashtable<>();
		List<Integer> toOrder = new ArrayList<>();
		Set<String> keys = scores.get(server.getId()).keySet();
		for (String key :keys) {
			FloodUser pl = scores.get(server.getId()).get(key);
			int total = totalScore.get(pl.getNiv()) + pl.getScore();
			logger.debug("loop on scores : "+key+" - player <"+pl.getUserId()+"> - score <"+total+">");

			if (classement.containsKey(total)) {
				logger.debug("classement contains this score");
				classement.get(total).add(pl);
			} else {
				logger.debug("classement doesn't contain this score");
				List<FloodUser> temp = new ArrayList<>();
				temp.add(pl);
				classement.put(total, temp);
				toOrder.add(total);
			}
		}
		Collections.sort(toOrder);

		int pos = 1;
		boolean stop = false;
		for (int i = toOrder.size()-1;i>=0;i--) {
			int total = toOrder.get(i);
			logger.debug("loop on ordered : "+total);
			List<FloodUser> pls = classement.get(total);
			for (FloodUser fUser : pls) {
				logger.debug("-- player : "+fUser.getUserId()+" - pos = "+pos);
				if (fUser.getUserId().equals(user.getId())) {
					logger.debug("Current user !!");
					rank = pos;
					stop = true;
					break;
				}
				pos++;
			}
			if (stop) {
				logger.debug("stop");
				break;
			}
		}

		logger.debug("getRank - end");
		return rank;
	}

	/**
	 * Display server rank
	 * @param server : server for which display the rank
	 * @param channel : channel on which to display
	 */
	public void classement(Guild server, TextChannel channel){
		logger.debug("classement - start : server : <"+server.getName()+"> - channel : <"+channel.getName()+">");

		Bot bot = Bot.getInstance();
		logger.debug("Jeux service : "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX));
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			Hashtable<Integer, List<FloodUser>> classement = new Hashtable<>();
			List<Integer> toOrder = new ArrayList<>();
			Set<String> keys = scores.get(server.getId()).keySet();
			for (String key : keys) {
				FloodUser pl = scores.get(server.getId()).get(key);
				int total = totalScore.get(pl.getNiv()) + pl.getScore();
				logger.debug("loop on scores : "+key+" - score = "+total);
				if (classement.containsKey(total)) {
					logger.debug("classement contains score");
					classement.get(total).add(pl);
				} else {
					logger.debug("classement doesn't contain score");
					List<FloodUser> temp = new ArrayList<>();
					temp.add(pl);
					classement.put(total, temp);
					toOrder.add(total);
				}

			}

			Collections.sort(toOrder);

			int pos = 1;
			List<String> tell = new ArrayList<>();
			String tellPart;
			for (int i = toOrder.size()-1;i>=0;i--) {
				Integer total = toOrder.get(i);
				logger.debug("loop on ordered : "+total);
				List<FloodUser> pls = classement.get(total);

				tellPart = "[" + pos + "]";
				for (FloodUser user : pls) {
					logger.debug("-- player = "+user.getUserId());
					pos++;
					tellPart = tellPart.concat("#").concat(UserUtils.getUserById(server, user.getUserId()).getName())
							.concat(" : niveau ").concat(String.valueOf(user.getNiv())).concat(" - ")
							.concat(String.valueOf(user.getScore())).concat("/").concat(String.valueOf(nextScore.get(user.getNiv())))
							.concat(" pts (Total : ").concat(String.valueOf(total)).concat(")");
				}

				tell.add(tellPart);

			}
			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
		}
		logger.debug("classement - end");
	}	
	
	
	
	
	/**
	* Return a map of users' global score for this game
	 * @param server  : server for which global score table is returned
	 * @return contains global score of each user : key=user, value=score
	*/
	public Hashtable<String,Integer> globalScore(Guild server){
		logger.debug("globalScore - add : server=<"+server.getName()+">");
		Hashtable<String,Integer> result = new Hashtable<>();
		if(scores.containsKey(server.getId())) {
			logger.debug("Scores contains server");
			int max = 0;
			Set<String> skeys = scores.get(server.getId()).keySet();
			for (String userId : skeys) {
				FloodUser player = scores.get(server.getId()).get(userId);
				int score = totalScore.get(player.getNiv()) + player.getScore();
				logger.debug("loop on scores : userId = <"+userId+"> - score = <"+score+">");
				result.put(userId, score);
				if (score > max) {
					max = score;
					logger.debug("New max : "+max);
				}
			}

			Set<String> rkeys = scores.get(server.getId()).keySet();
			for (String userId : rkeys) {
				Integer globalScore = (result.get(userId) * 100) / max;
				logger.debug("loop on result : userId = <> - global score = <"+globalScore+">");
				result.put(userId, globalScore);
			}
		}
		logger.debug("globalScore - end : result size = "+result.size());
		return result;		
	}
}
