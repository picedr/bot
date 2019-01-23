package fr.picedr.bot.jeux.pimp;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
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

public class PimpService implements BotService {

    private Logger logger = LoggerFactory.getLogger(PimpService.class);

    private static PimpService INSTANCE = null;
	
	private Hashtable<String,Integer> state;
	private Hashtable<String,Hashtable<String, String>> players;
	private Hashtable<String,Hashtable<String, Integer>> scores;

    private PimpService(){
    	PimpDAO pimpDAO = new PimpDAO();
		scores = pimpDAO.getScores();
		state = new Hashtable<>();
		
		Enumeration<String> keys = scores.keys();
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			state.put(key,0);
		}
		players = new Hashtable<>();
    }


    public static PimpService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PimpService();
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
        String function = content.split(" ")[0];
        Bot bot = Bot.getInstance();

	   if (server != null){
		   if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			   switch (cmd){
				   case "!pimp":
					   switch(function){
						   case "":
							 play(server,channel, user);
						   break;
						   case "score":
							 score(server,channel,user);
						   break;
						   case "classement":
							 classement(server,channel);
						   break;
						   case "help":
						   case "aide":
						   	 PimpService.help(server,channel);
						   	break;
						   default :
							   logger.debug("default function");
							   MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help pimp** pour plus de details",1);
					   }
					   break;
				   default :
					   logger.debug("default cmd");
					   MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help pimp** pour plus de details",1);
			   }
		   } else {
			   MsgUtils.tell(user, "Je ne connais pas cette commande. Tappe **!help** pour plus de détails");
		   }
	   } else {
		  switch (cmd){
			   case "!pair":
			   case "!impair":
				   playerMove(user, cmd);
				   break;
			   default :
				   logger.debug("default cmd");
				   MsgUtils.tell(user, "Je ne connais pas cette commande.");
		   }
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
		if (Bot.getInstance().getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			tell.add("AIDE POUR LA SECTION [PIMP]");
			tell.add(" ");
			tell.add("Le service [pimp] est un jeu de pair ou impair.");
			tell.add("Les regles sont : ");
			tell.add("La partie se lance à l'aide la la commande **!pimp**.");
			tell.add("Pour rejoindre la partie, il faut tapper la commande **!pimp** dans les 30s qui suivent le début de partie.");
			tell.add("A chaque manche, les joueurs restants en jeu choisissent entre *pair* et *impair* (par MP à papy avec les commandes **!pair** et **!impair**)");
			tell.add("Une fois le délai passé, on ajoute 1 pour chaque joueur ayant choisi *impair* et 2 pour chaque joueur ayant choisi *pair*");
			tell.add("Si le résultat est pair, tous les joueurs ayant choisi impair sont éliminés, et vice versa.");
			tell.add("La manche suivante démarre avec les joueurs restants jusqu'à que tout le monde soit éliminé ou qu'il ne reste qu'un joueur qui sera alors le vainqueur.");
			tell.add("En fin de partie, si un vainqueur est désigné, il vole un point à chaque vaincu");
			tell.add("*Précision* : Si il ne reste que deux joueurs en jeu, un troisième jet sera effectué par Papy (si seulement deux joueurs jouent, seul la commande **!pair** peut gagner).");
			tell.add(" ");
			tell.add("Commandes supplémentaires : ");
			tell.add("-**!pimp score** : affiche le score du joueur");
			tell.add("-**!pimp classement** : affiche le classement du jeu");
			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS, 1);
		}
    }


	/**
	 * Update the state of a game
	 * @param server : server for which the server change state
	 * @param state : state to change to
	 */
	void setState(Guild server,int state){
		logger.debug("setState - start : server=<"+server.getName()+"> - state=<"+state+">");
		this.state.put(server.getId(),state);
		logger.debug("setState - end");
	}

	/**
	 * Getter for players hashtable
	 * @return players
	 */
	Hashtable<String,Hashtable<String, String>> getPlayers(){
		return this.players;
	}

	/**
	 * Called when playing on public channel
	 * @param server : server the game is on
	 * @param channel : channel the game is launched on
	 * @param user : user who launched the command
	 */
	private void play(Guild server, TextChannel channel, User user){
		logger.debug("play - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
		PimpDAO pimpDAO = new PimpDAO();
		if (!state.containsKey(server.getId())){
			logger.debug("states doesn't contains the server");
			state.put(server.getId(),0);
			Hashtable<String,Integer> serverScore = new Hashtable<>();
			serverScore.put("Papy",100);
			scores.put(server.getId(),serverScore);
			pimpDAO.addPlayer(server.getId(),"Papy");
		}

		int serverState = state.get(server.getId());
		logger.debug("serverState = "+serverState);
		if (serverState == 0){
			Hashtable<String, String> serverPlayer = new Hashtable<>();
			serverPlayer.put(user.getId(), "0");
			players.put(server.getId(),serverPlayer);
			players.get(server.getId()).put("Papy", "0");

			if (!scores.get(server.getId()).containsKey(user.getId())){
				logger.debug("First play on this server");
				scores.get(server.getId()).put(user.getId(),100);
				pimpDAO.addPlayer(server.getId(), user.getId());
			}
			PimpStartThread start = new PimpStartThread(server, channel);
			start.start();
		} else if (serverState == 1) {
			String id = user.getId();
			if (players.get(server.getId()).containsKey(id)){
				logger.debug("Already registred");
				MsgUtils.tellFramed(channel, "Tu es déjà inscrit(e)",MsgUtils.FT_CSS,0);
			}else {
				logger.debug("Registration to the game");
				players.get(server.getId()).put(id, "0");
				if (!scores.get(server.getId()).containsKey(id)){
					logger.debug("First play on this server");
					scores.get(server.getId()).put(id, 100);
					pimpDAO.addPlayer(server.getId(), user.getId());
				}
				MsgUtils.tellFramed(channel, user.getName()+" est maintenant inscrit(e)",MsgUtils.FT_CSS,0);
			}
		} else {
			MsgUtils.tell(channel, "`Une partie est déjà en cours",0);
		}
		logger.debug("play - end");
	}


	/**
	 * Make all the change for a new set in a game
	 * @param server : server the game is on
	 * @param channel : channel to display result on
	 */
	void newSet(Guild server, TextChannel channel){
		logger.debug("newSet - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
		Hashtable<String,String> newPlayers = new Hashtable<>();
		Set<String> keys = players.get(server.getId()).keySet();
		for (String key : keys){
			logger.debug("loop on  players : <"+key+">");
			if (!players.get(server.getId()).get(key).equals("3")){
				logger.debug("--Player is not out");
				if (key.equals("Papy")){
					int papyMove = Utils.rand(2);
					logger.debug("--Papy case : move=<"+papyMove+">");
					switch (papyMove){
						case 0 :
							newPlayers.put("Papy", "1");
							break;
						case 1 :
							newPlayers.put("Papy", "2");
							break;
					}
				}else {
					logger.debug("--Not Papy");
					newPlayers.put(key, "0");
				}
			}		
		}
		
		players.put(server.getId(),newPlayers);
		
		PimpSetThread newSet = new PimpSetThread(server, channel);
		newSet.start();
		logger.debug("newSet - end");
	}

	/**
	 * Reinitiate a game for a specific server
	 * @param server : server to reinit the game for
	 */
	private void reset(Guild server){
		logger.debug("reset - start : server=<"+server.getName()+">");
		state.put(server.getId(),0);
		Hashtable<String,String> newPlayers = new Hashtable<>();
		newPlayers.put("Papy", "0");
		players.put(server.getId(),newPlayers);
		logger.debug("reset - end");
	}

	/**
	 * To end a set of a game
	 * @param server : server to end the game for
	 * @param channel : channel to display result on
	 */
	void endSet(Guild server, TextChannel channel){
		logger.debug("endSet -start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
		List<String> pair = new ArrayList<>();
		List<String> impair = new ArrayList<>();
		List<String> noPlay = new ArrayList<>();
		PimpDAO pimpDAO = new PimpDAO();
		
		Set<String> keys = players.get(server.getId()).keySet()				;
		
		for( String key : keys){
			String coup = players.get(server.getId()).get(key);
			logger.debug("loop on players : player=<"+key+"> - coup=<"+coup+">");
			switch (coup){
			case "0" :
				noPlay.add(key);
				break;
			case "1" :
				impair.add(key);
				break;
			case "2" : 
				pair.add(key);
				break;				
			}
		}
		
		if (impair.size() + pair.size() == 2 && noPlay.size()==0){
			//A deux joueurs, seul la commande !pair gagne
			int papyMove = Utils.rand(2);
			logger.debug("Only two players : papyMove=<"+papyMove+">");
			switch (papyMove){
				case 0 :
					pair.add("Papy2");
					break;
				case 1 :
					impair.add("Papy2");
					break;
			}
		}
		
		if ((impair.size()%2)==0){
			logger.debug("Pair");
			MsgUtils.tellFramed(channel, "Le résultat est [pair]",MsgUtils.FT_CSS);
			if (impair.size()+noPlay.size()>0){
				logger.debug("Some players lose");
				List<String> tell = new ArrayList<>();
				tell.add("Les joueurs suivants sont éliminés : ");
				for (String player:impair){
					logger.debug("loop on impair : <"+player+">");
					if (!player.equals("Papy2")){
						if (player.equals("Papy")){
							tell.add("- Papy");
						}else {
							tell.add("- "+UserUtils.getUserById(server,player).getName());
						}
						 players.get(server.getId()).put(player, "3");
					}
				}
				for (String player:noPlay){
					logger.debug("loop on noPlay : <"+player+">");
					tell.add("- "+UserUtils.getUserById(server,player).getName());
					players.get(server.getId()).put(player, "3");
				}
				
				MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_CSS);
			}
			if (pair.size()>1){
				logger.debug("Several players stays");
				List<String> tell = new ArrayList<>();
				tell.add("Restent en jeu : ");
				for (String player:pair){
					logger.debug("loop on pair : <"+player+">");
					if (!player.equals("Papy2")){
						if (player.equals("Papy")){
							tell.add("- Papy");
						}else {
							tell.add("- "+UserUtils.getUserById(server,player).getName());
						}
					}
				}
				
				MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_CSS);
				this.newSet(server, channel);
			}else if (pair.size()==1){
				logger.debug("Only one player left : "+pair.get(0));
				String winner = pair.get(0);
				String winnerName;
				if (winner.equals("Papy")){
					winnerName ="Papy";
				}else {
					winnerName = UserUtils.getUserById(server,winner).getName();
				}
				
				int gain = 0;
				Hashtable<String,Integer> newScores = new Hashtable<>();
				Set<String> keys2 = players.get(server.getId()).keySet();
				
				for(String key2 : keys2){
					logger.debug("loop on players : <"+key2+">");
					if (!key2.equals(winner)){
						logger.debug("--Not the winner");
						int score = scores.get(server.getId()).get(key2);
						logger.debug("--score = <"+score+">");
						if(score>0){
							score --;
							gain ++;
							newScores.put(key2, score);
							pimpDAO.updatePlayer(server.getId(),key2,score);
						}
					}
				}
				logger.debug("gain=<"+gain+">");
				int winnerScore = scores.get(server.getId()).get(winner);
				pimpDAO.updatePlayer(server.getId(),winner,winnerScore+gain);
				newScores.put(winner, winnerScore+gain);
				scores.put(server.getId(),newScores);
								
				MsgUtils.tellFramed(channel, winnerName+" a gagné.\nFin de la partie",MsgUtils.FT_CSS,0);
				this.reset(server);
			}else {
				MsgUtils.tellFramed(channel, "Personne n'a gagné.\nFin de la partie",MsgUtils.FT_CSS,0);
				this.reset(server);
			}
				
			
		} else {
			logger.debug("Impair");
			MsgUtils.tellFramed(channel, "Le résultat est [impair]",MsgUtils.FT_CSS,0);
			if (pair.size()+noPlay.size()>0){
				logger.debug("Some players lose");
				List<String> tell = new ArrayList<>();
				tell.add("Les joueurs suivants sont éliminés : ");
				for (String player : pair){
					logger.debug("loop on pair : <"+player+">");
					if (!player.equals("Papy2")){
						if (player.equals("Papy")){
							tell.add("- Papy");
						}else {
							tell.add("- "+UserUtils.getUserById(server, player));
						}
						players.get(server.getId()).put(player, "3");
					}
				}
				for (String player : noPlay){
					logger.debug("loop on noPlay : <"+player+">");
					tell.add("- "+UserUtils.getUserById(server, player));
					players.get(server.getId()).put(player, "3");
				}
				
				MsgUtils.tellBlockFramed(channel, tell,MsgUtils.FT_CSS);
			}
			if (impair.size()>1){
				logger.debug("Several players stays");
				List<String> tell = new ArrayList<>();
				tell.add("Restent en jeu : ");
				for (String player : impair){
					logger.debug("loop on imair : <"+player+">");
					if (!player.equals("Papy2")){
						if (player.equals("Papy")){
							tell.add("- Papy");
						}else {
							tell.add("- "+UserUtils.getUserById(server, player).getName());
						}
					}
				}
				MsgUtils.tellBlockFramed(channel, tell,MsgUtils.FT_CSS);
				this.newSet(server,channel);
			}else if (impair.size()==1){
				logger.debug("Only one player left : "+pair.get(0));
				String winner = impair.get(0);
				String winnerName;
				if (winner.equals("Papy")){
					winnerName ="Papy";
				}else {
					winnerName = UserUtils.getUserById(server, winner).getName();
				}
				
				int gain = 0;
				Hashtable<String,Integer> newScores = new Hashtable<>();
				Set<String> keys2 = players.get(server.getId()).keySet();
				for(String key2 : keys2){
					logger.debug("loop on players : <"+key2+">");
					if (!key2.equals(winner)){
						logger.debug("--Not the winner");
						int score = scores.get(server.getId()).get(key2);
						logger.debug("--score = <"+score+">");
						if(score>0){
							score --;
							gain ++;
							newScores.put(key2, score);
							pimpDAO.updatePlayer(server.getId(),key2,score);
						}
					}
				}
				logger.debug("gain=<"+gain+">");
				int winnerScore = scores.get(server.getId()).get(winner);
				pimpDAO.updatePlayer(server.getId(),winner,winnerScore+gain);
				newScores.put(winner,winnerScore+gain);
				scores.put(server.getId(),newScores);
								
				MsgUtils.tellFramed(channel, winnerName+" a gagné.\nFin de la partie",MsgUtils.FT_CSS,0);
				this.reset(server);
			}else {
				logger.debug("No one won");
				MsgUtils.tellFramed(channel, "Personne n'a gagné.\nFin de la partie",MsgUtils.FT_CSS,0);
				this.reset(server);
			}			
		}
		logger.debug("endSet - end");
	}


	/**
	 * For a play of a player
	 * @param user : user who plays
	 * @param cmd : move of the player
	 */
	private void playerMove(User user, String cmd){
		logger.debug("playMove - start : user=<"+user.getName()+"> - cmd=<"+cmd+">");
		Set<String> playE =  players.keySet();
		for(String key:playE){
			Guild server = Bot.getInstance().getJDA().getGuildById(key);
			logger.debug("loop on playE=<"+server.getName()+">");
			Hashtable<String,String> playerServer = players.get(key);

			if (playerServer.containsKey(user.getId())){
				logger.debug("--playerServer contains user");
				logger.debug("Player status : <"+playerServer.get(user.getId())+">");
				if (!playerServer.get(user.getId()).equals("3")){
					if (cmd.startsWith("!pair")){
						playerServer.put(user.getId(), "2");
					}else {
						playerServer.put(user.getId(), "1");
					}
				} else {
					MsgUtils.tell(user, "Tu ne peux plus jouer, tu as été éliminé(e) de la partie sur "+server.getName());
				}
			}else {
				logger.debug("--playerServer doesn't contains user");
				MsgUtils.tell(user, "Tu ne participes pas à la partie en cours sur " + server.getName());
			}
		}
		logger.debug("playerMove - end");
	}

	/**
	 * Display levels of players for a server
	 * @param server : server to display for
	 * @param channel : channel to display on
	 */
	public void classement(Guild server, TextChannel channel){
		logger.debug("classement - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
		Hashtable<Integer, List<String>> classement = new Hashtable<>();
		List<Integer> tempScores = new ArrayList<>();
		
		Set<String> keys = scores.get(server.getId()).keySet();
		
		for (String key : keys){
			int val = scores.get(server.getId()).get(key);
			logger.debug("loop on scores : <"+key+"> - val=<"+val+">");
			if (!tempScores.contains(val)){
				logger.debug("--tempScore contains val");
				tempScores.add(val);
				List<String> temp = new ArrayList<>();
				temp.add(key);
				classement.put(val, temp);
			}else {
				logger.debug("--tempScore doesn't contains val");
				classement.get(val).add(key);
			}
		}
		
		Collections.sort(tempScores);
		
		List<String> tell = new ArrayList<>();
		tell.add("Pair ou Impair - Classement : ");
		for (int i = tempScores.size()-1;i>=0;i--){
			String score = tempScores.get(i).toString();
			logger.debug("loop on tempScores - score=<"+score+">");
			List<String> players = classement.get(tempScores.get(i));
			String tellPart = "- "+score+" pts : ";
			int j = 0;
			for(String player : players){
				logger.debug("--loop on players : <"+player+">");
				if (j>0){
					logger.debug("--first loop");
					tellPart = tellPart.concat("- ");
				}
				if (player.equals("Papy")){
					tellPart = tellPart.concat("[Papy]");
				} else {
					tellPart = tellPart.concat("[").concat(UserUtils.getUserById(server, player).getName()).concat("] ");
				}
				j++;
			}
			tell.add(tellPart);
		}
		MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS,1);
		logger.debug("classement - end");
	}	
	
	
	/**
	* Display score for this game
	* @param channel : channel to display the score on
	* @param user : user for which display the score
	*/
	public void score(Guild server,TextChannel channel, User user){
		logger.debug("score - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
		Bot bot = Bot.getInstance();
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			logger.debug("Service is available");
			if (scores.containsKey(server.getId()) && scores.get(server.getId()).containsKey(user.getId())) {
				logger.debug("scores contains user");
				int score = scores.get(server.getId()).get(user.getId());
				logger.debug("score=<"+score+">");
				String tell = "Score de [" + user.getName() + "] :" + score;
				MsgUtils.tellFramed(channel, tell, MsgUtils.FT_CSS);
			} else {
				logger.debug("scores doesn't contains user");
				MsgUtils.tell(channel, "Tu n'as pas encore joué à ce jeu.");
			}
		}
		logger.debug("score - end");
	}
	
	
	/**
	* Return a map of users' global score for this game
	 * @param server : server that call the command
	*/
	public Hashtable<String,Integer> globalScore(Guild server){
		logger.debug("globalScore - start : server=<"+server.getName()+">");
		Hashtable<String,Integer> result = new Hashtable<>();

		Bot bot = Bot.getInstance();
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			logger.debug("Service is available");
			int maxScore = -1;

			for (String userId : scores.get(server.getId()).keySet()) {
				logger.debug("loop on scores : userId=<"+userId+">");
				Integer score = scores.get(server.getId()).get(userId);
				if (score > maxScore) {
					maxScore = score;
					logger.debug("--new max=<"+maxScore+">");
				}
			}

			for (String userId:scores.get(server.getId()).keySet()) {
				int score = ((scores.get(server.getId()).get(userId))*100)/maxScore;
				logger.debug("loop on scores 2 : userId=<"+userId+"> - score final=<"+score+">");
				result.put(userId, score);
			}
		}
		logger.debug("globalScore - end : result size=<"+result.size()+">");
		return result;		
	}
	
	
 
 

}
