package fr.picedr.bot.jeux;


import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.jeux.flood.FloodService;
import fr.picedr.bot.jeux.pfc.PfcService;
import fr.picedr.bot.jeux.pimp.PimpService;
import fr.picedr.bot.jeux.quizz.QuizzService;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JeuxService implements BotService {

    private Logger logger = LoggerFactory.getLogger(JeuxService.class);
	
    private static JeuxService INSTANCE = null;

    private JeuxService(){
    }


    public static JeuxService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JeuxService();
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
        if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
		   switch (cmd){
			   case "!classement":
				   switch (function) {
					   case "pimp":
						   PimpService.getInstance().classement(server,channel);
						   break;
					   case "pfc":
						   PfcService.getInstance().classement(server,channel);
						   break;
					   case "flood":
						   FloodService.getInstance().classement(server,channel);
						   break;
					   case "quizz":
						   QuizzService.getInstance().classement(server,channel);
						   break;
					   case "":
						   classement(server,channel);
						   break;						   
					   default:
						   logger.debug("default function");
						   if (channel == null) {
							   MsgUtils.tell(user, "Je ne connais pas cette commande.");
						   } else {
							   MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help jeux** pour plus de details");
						   }
				   }
				   break;
			   case "!score":
				   switch (function) {
					   case "pimp":
						   PimpService.getInstance().score(server,channel,user);
						   break;
					   case "pfc":
						   PfcService.getInstance().score(server,channel,user);
						   break;
					   case "flood":
						   FloodService.getInstance().score(server,channel,user);
						   break;
					   case "quizz":
						   QuizzService.getInstance().score(server,channel,user);
						   break;
					   case "":
						   score(server,channel,user);
						   break;						   
					   default:
						   logger.debug("default function");
						   if (channel == null) {
							   MsgUtils.tell(user, "Je ne connais pas cette commande.");
						   } else {
							   MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help jeux** pour plus de details",0);
						   }
				   }
				   break;			
			   default :
				   logger.debug("default cmd");
				   if (channel == null) {
					   MsgUtils.tell(user, "Je ne connais pas cette commande.");
				   } else {
					   MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help jeux** pour plus de details",0);
				   }
		   }
        } else {
            MsgUtils.tell(user, "Je ne connais pas cette commande. Tappe **!help** pour plus de d�tails");
        }
		

    }	
	
	
	
    public static void help(Guild server, TextChannel channel) {
        List<String> tell = new ArrayList<>();

		Bot bot = Bot.getInstance();
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			tell.add("AIDE POUR LA SECTION [JEUX]");
			tell.add(" ");
			tell.add("Le service [jeux] permet la gestion globale des diff�rents jeux.");
			tell.add("Les commandes disponibles sont : ");
			tell.add("-**!classement** : affiche le classement global pour l'ensemble des jeux");
			tell.add("-**!classement <jeu>** : affiche le classement du jeu sp�cifiquement");
			tell.add("-**!score** : affiche l'ensemble des scores");
			tell.add("-**!score <jeu>** : affiche le score du jeu sp�cifiquement");
			tell.add(" ");
			tell.add("Les jeux disponibles sont : ");
			tell.add("-[pimp] : jeu de pair ou impair (**!help pimp** pour plus de details)");
			tell.add("-[pfc] : jeu de pierre feuille ciseaux (**!help pfc** pour plus de details)");
			tell.add("-[flood] : classement de participation aux chans publics (**!help flood** pour plus de details)");
			tell.add("-[quizz] : un quizz (**!help quizz** pour plus de details)");

			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
		}
    }

	/**
	 * Display score for all games
	 * @param server : server for which score are displayed
	 * @param channel : channel on which scores are displayed
	 * @param user : user for which scores are displayed
	 */
	private void score(Guild server, TextChannel channel, User user){
		//http://patorjk.com/software/taag/#p=display&f=Big&t=PFC
		logger.debug("score - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");

		logger.debug("Display for pimp");
		List<String> tell = new ArrayList<>();
		tell.add(" _____ _____ __  __ _____  ");
		tell.add("|  __ \\_   _|  \\/  |  __ \\ ");
		tell.add("| |__) || | | \\  / | |__) |");
		tell.add("|  ___/ | | | |\\/| |  ___/ ");
		tell.add("| |    _| |_| |  | | |     ");
		tell.add("|_|   |_____|_|  |_|_|     ");
		MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_FIX);
		PimpService.getInstance().score(server,channel,user);

		logger.debug("Display for PFC");
		tell = new ArrayList<>();
 		tell.add(" _____  ______ _____ ");
 		tell.add("|  __ \\|  ____/ ____|");
 		tell.add("| |__) | |__ | |     ");
 		tell.add("|  ___/|  __|| |     ");
 		tell.add("| |    | |   | |____ ");
 		tell.add("|_|    |_|    \\_____|");
        MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_FIX);
		PfcService.getInstance().score(server,channel,user);

		logger.debug("Display for flood");
 		tell = new ArrayList<>();
 		tell.add(" ______ _      ____   ____  _____  ");
  		tell.add("|  ____| |    / __ \\ / __ \\|  __ \\ ");
  		tell.add("| |__  | |   | |  | | |  | | |  | |");
  		tell.add("|  __| | |   | |  | | |  | | |  | |");
  		tell.add("| |    | |___| |__| | |__| | |__| |");
  		tell.add("|_|    |______\\____/ \\____/|_____/ ");
        MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_FIX);
		FloodService.getInstance().score(server,channel,user);

		logger.debug("Display for quizz");
		tell = new ArrayList<>();
  		tell.add("   ____  _    _ _____ ____________");
  		tell.add("  / __ \\| |  | |_   _|___  /___  /");
  		tell.add(" | |  | | |  | | | |    / /   / / ");
  		tell.add(" | |  | | |  | | | |   / /   / /  ");
  		tell.add(" | |__| | |__| |_| |_ / /__ / /__ ");
  		tell.add("  \\___\\_\\____/|_____/_____/_____|");
        MsgUtils.tellBlockFramed(channel,tell,MsgUtils.FT_FIX);
		QuizzService.getInstance().score(server,channel,user);

		logger.debug("score - end");
	}

	/**
	 * Display global level
	 * @param server : server for which to display
	 * @param channel : channel to display on
	 */
	private void classement(Guild server, TextChannel channel){
		logger.debug("Classement - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
		List<User> serverUsers = UserUtils.getUsers(server);
		Hashtable<String,Integer> pimpGS =  PimpService.getInstance().globalScore(server);
		Hashtable<String,Integer> pfcGS =  PfcService.getInstance().globalScore();
		Hashtable<String,Integer> floodGS =  FloodService.getInstance().globalScore(server);
		Hashtable<String,Integer> quizzGS =  QuizzService.getInstance().globalScore(server);
		
		Hashtable<String,List<Integer>> usersScores = new Hashtable<>();
		
		for( User user:serverUsers ){
			logger.debug("loop on user : <"+user.getName()+">");
			if (pimpGS.containsKey(user.getId())){
				logger.debug("--Has score in PIMP");
				if (usersScores.containsKey(user.getId())){
					logger.debug("--Not first score : <"+pimpGS.get(user.getId())+">");
					usersScores.get(user.getId()).add(pimpGS.get(user.getId()));
				}else {
					logger.debug("--First score : <"+pimpGS.get(user.getId())+">");
					List<Integer> temp = new ArrayList<>();
					temp.add(pimpGS.get(user.getId()));
					usersScores.put(user.getId(),temp);
				}
			}
			
			if (pfcGS.containsKey(user.getId())){
				logger.debug("--Has score in PFC");
				if (usersScores.containsKey(user.getId())){
					logger.debug("--Not first score : <"+pfcGS.get(user.getId())+">");
					usersScores.get(user.getId()).add(pfcGS.get(user.getId()));
				}else {
					logger.debug("--First score : <"+pfcGS.get(user.getId())+">");
					List<Integer> temp = new ArrayList<>();
					temp.add(pfcGS.get(user.getId()));
					usersScores.put(user.getId(),temp);
				}
			}

			if (floodGS.containsKey(user.getId())){
				logger.debug("--Has score in FLOOD");
				if (usersScores.containsKey(user.getId())){
					logger.debug("--Not first score : <"+floodGS.get(user.getId())+">");
					usersScores.get(user.getId()).add(floodGS.get(user.getId()));
				}else {
					logger.debug("--First score : <"+floodGS.get(user.getId())+">");
					List<Integer> temp = new ArrayList<>();
					temp.add(floodGS.get(user.getId()));
					usersScores.put(user.getId(),temp);
				}
			}

			if (quizzGS.containsKey(user.getId())){
				logger.debug("--Has score in QUIZZ");
				if (usersScores.containsKey(user.getId())){
					logger.debug("--Not first score : <"+quizzGS.get(user.getId())+">");
					usersScores.get(user.getId()).add(quizzGS.get(user.getId()));
				}else {
					logger.debug("--First score : <"+quizzGS.get(user.getId())+">");
					List<Integer> temp = new ArrayList<>();
					temp.add(quizzGS.get(user.getId()));
					usersScores.put(user.getId(),temp);
				}
			}				
		}
		
		Hashtable<Integer, List<String>> classement = new Hashtable<>();
		List<Integer> tempScores = new ArrayList<>();
		
		for(String userId : usersScores.keySet() ){
			int score = getScore(usersScores.get(userId));
			logger.debug("loop on usersScores : user=<"+userId+"> - score=<"+score+">");
			if (!tempScores.contains(score)){
				logger.debug("--New tempscore");
				tempScores.add(score);
				List<String> temp = new ArrayList<>();
				temp.add(userId);
				classement.put(score, temp);
			}else {
				logger.debug("--Existing tempscore");
				classement.get(score).add(userId);
			}			
		}
		
		Collections.sort(tempScores);
		int pos = 1;
		List<String> tell = new ArrayList<>();
		tell.add("Classement Global : ");
		for (int i = tempScores.size()-1;i>=0;i--){
			Integer score = tempScores.get(i);
			logger.debug("final loop : score=<"+score+">");
			List<String> players = classement.get(score);
			String tellPart = "["+pos+"] "+score+" pts : ";
			int j = 0;
			for(String player : players){
				logger.debug("--loop on players : <"+player+">");
				pos ++;
				if (j>0){
					tellPart = tellPart.concat("- ");
				}
				tellPart = tellPart.concat("[").concat(UserUtils.getUserById(server, player).getName()).concat("] ");
				j++;
			}
			tell.add(tellPart);
		}
		MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS,0);
		logger.debug("classement - end");
	}

	/**
	 * Calculate score
	 * @param scores : list of scores
	 * @return calculated score
	 */
	private int getScore(List<Integer> scores){
		logger.debug("getScore - start : scores size : <"+scores.size()+">");
		int result = 0;		
		for (int score:scores){
			logger.debug("loop on score : <"+score+">");
			result = result + score;
		}		
		result = result / scores.size();
		logger.debug("getScore - end : result=<"+result+">");
		return result;
	}
	
	
}