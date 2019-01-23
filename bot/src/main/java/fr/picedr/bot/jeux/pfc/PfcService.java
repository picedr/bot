package fr.picedr.bot.jeux.pfc;

import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.jeux.pfc.bean.PfcScore;
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

public class PfcService implements BotService {

    private Logger logger = LoggerFactory.getLogger(PfcService.class);

    private static PfcService INSTANCE = null;
	
	private Hashtable<String, PfcScore> scores;

    private PfcService(){
    	PfcDAO pfcDAO = new PfcDAO();
    	scores = pfcDAO.getScores();
    }


    public static PfcService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PfcService();
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
        logger.debug("Service jeux = "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1"));
        if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
           switch (cmd){
               case "!pierre":
			   case "!feuille":
			   case "!ciseaux":
				   play(channel, user, cmd.replace("!","").toLowerCase());
                   break;
               case "!pfc":
                   switch (function) {
                       case "score":
                           score(server,channel,user);
                           break;
					   case "classement":
                           classement(server,channel);
                           break;
					   case "help":
					   case "aide":
					   	PfcService.help(server,channel);
					   	break;
                       default:
                           logger.debug("default function");
                           if (channel == null) {
                               MsgUtils.tell(user, "Je ne connais pas cette commande.");
                           } else {
                               MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help pfc** pour plus de details",1);
                           }
                   }
                   break;
               default :
                   logger.debug("default cmd");
                   if (channel == null) {
                       MsgUtils.tell(user, "Je ne connais pas cette commande.");
                   } else {
                       MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help pfc** pour plus de details",1);
                   }
           }

        } else {
            MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help** pour plus de détails",0);
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
			tell.add("AIDE POUR LA SECTION [PFC]");
			tell.add(" ");
			tell.add("Le service [pfc] est un jeu de pierre feuille ciseaux .");
			tell.add("Les commandes disponibles sont : ");
			tell.add("-**!pierre** : pour jouer *pierre*");
			tell.add("-**!feuille** : pour jouer *feuille*");
			tell.add("-**!ciseaux** : pour jouer *ciseaux*");
			tell.add("-**!pfc score** : pour afficher son score*");
			tell.add("-**!pfc classement** : pour afficher le classement pour ce jeu*");


			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
		}
    }

	/**
	 * Called when a user play pfc
	 *
     * @param channel : Channel from which come the command
     * @param user    : user that launch the command
	 * @param move : what player plays
	 *
	 */
	private void play(TextChannel channel, User user, String move){
		/*
		 * pierre = 0
		 * feuille = 1
		 * ciseaux = 2
		 * */
		logger.debug("play - start : channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - move=<"+move+">");
		PfcScore score;
		boolean newPlayer = false;
		
		if (scores.containsKey(user.getId())){
			score = scores.get(user.getId());
			logger.debug("existing user : score = <"+score.toString()+">");
		}else {
			logger.debug("New user");
			score = new PfcScore();
			scores.put(user.getId(), score);
			newPlayer = true;
		}
		
		int playerChoice = -1;
		int botChoice = Utils.rand(3);
		String botChoiceStr = "";

		switch (botChoice){
		case 0 :
			botChoiceStr = "Pierre";
			break;
		case 1 :
			botChoiceStr = "Feuille";
			break;
		case 2 :
			botChoiceStr = "Ciseaux";
			break;
		}

		logger.debug("bot choice = <"+botChoice+"><"+botChoiceStr+">");

		switch (move){
			case "pierre" :
				playerChoice = 0;
				break;
			case"feuille" :
				playerChoice = 1;
				break;
			case "ciseaux" :
				playerChoice = 2;
				break;
		}
		
		String test = (new Integer(playerChoice)).toString()+ "," + (new Integer(botChoice)).toString();
		logger.debug("test = <"+test+">");
		// 0 : egalité, 1 : player win, 2 : bot win
		int result = 0;
		switch (test){
		case "0,1" : 
			result = 2;			
			break;
		case "0,2" : 
			result = 1;
			break;
		case "1,0" : 
			result = 1;
			break;			
		case "1,2" : 
			result = 2;
			break;	
		case "2,0" : 
			result = 2;
			break;
		case "2,1" : 
			result = 1;
			break;	
		}

		logger.debug("result = <"+result+">");
		
		MsgUtils.tell(channel, botChoiceStr,1);
		
		switch (result){
			case 0 :
				MsgUtils.tellFramed(channel, "Match nul entre [Papy] et ["+user.getName()+"]", MsgUtils.FT_CSS,1);
				score.setDraw(score.getDraw()+1);
				break;
			case 1 :
				MsgUtils.tellFramed(channel, "["+user.getName()+"] bat [Papy]", MsgUtils.FT_CSS,1);
				score.setWin(score.getWin()+1);
				break;
			case 2 :
				MsgUtils.tellFramed(channel, "[Papy] bat ["+user.getName()+"] ", MsgUtils.FT_CSS,1);
				score.setLose(score.getLose()+1);
				break;
		}
		PfcDAO pfcDAO = new PfcDAO();
		if (newPlayer){
			pfcDAO.addPlayer(user.getId(),score);
		}else{
			pfcDAO.updatePlayer(user.getId(),score);
		}
		logger.debug("play - end");
		
	}
	
	/**
	* Calculate a score value
	* @param score : win, draw and lose of the user
	* @return score of the user
	*/	
	private int getScoreValue(PfcScore score){
		logger.debug("getScoreValue - start : score=<"+score.toString()+">");
		int result = (3*score.getWin()) + score.getDraw() - (2*score.getLose());
		logger.debug("getScoreValue - end : result=<"+result+">");
		return result;
	}
	
	/**
	* Display score for this game
	* @param channel : channel to display the score on
	* @param user : user for which display the score
	*/
	public void score(Guild server,TextChannel channel, User user){
		logger.debug("score - start :server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
		Bot bot = Bot.getInstance();
		logger.debug("Service jeux = "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1"));
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			if (scores.containsKey(user.getId())) {
				List<String> tell = new ArrayList<>();
				PfcScore score = scores.get(user.getId());
				logger.debug("scores contains user : "+score.toString());
				tell.add("Score de [" + user.getName() + "] :");
				tell.add("- Victoires : " + score.getWin());
				tell.add("- Matchs nuls : " + score.getDraw());
				tell.add("- Défaites : " + score.getLose());
				MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS, 1);
			} else {
				MsgUtils.tell(channel, "Nous n'avons encore joué aucun match ensemble.", 1);
			}
		}
		logger.debug("score - end");
	}


	/**
	 * Display levels of users on the server
	 * @param server : server users belong to
	 * @param channel : channel to display on
	 */
	public void classement(Guild server, TextChannel channel){
		logger.debug("classement - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
		Hashtable<Integer, List<String>> classement = new Hashtable<>();
		List<Integer> tempScores = new ArrayList<>();


		Bot bot = Bot.getInstance();
		logger.debug("Service jeux = "+bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1"));
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			List<User> serverUsers = UserUtils.getUsers(server);
			logger.debug(serverUsers.size()+" users on the server");
			for (User user : serverUsers) {
				logger.debug("loop on users : name=<"+user.getName()+"> - id=<"+user.getId()+">");
				String key = user.getId();
				if (scores.containsKey(key)) {
					logger.debug("--scores contains user");
					PfcScore val = scores.get(key);
					int score = getScoreValue(val);
					logger.debug("--score of the user : <"+val.toString()+">-<"+score+">");
					if (!tempScores.contains(score)) {
						logger.debug("--No user with this score");
						tempScores.add(score);
						List<String> temp = new ArrayList<>();
						temp.add(key);
						classement.put(score, temp);
					} else {
						logger.debug("--Score already exists");
						classement.get(score).add(key);
					}
				}
			}

			Collections.sort(tempScores);

			List<String> tell = new ArrayList<>();
			tell.add("Pair ou Impair - Classement : ");
			for (int i = tempScores.size()-1;i>=0;i--) {
				Integer score = tempScores.get(i);
				logger.debug("loop in tempScore : "+score);
				List<String> players = classement.get(score);
				String tellPart = "- " + score + " pts : ";
				int j = 0;
				for (String player : players) {
					logger.debug("--loop on player : "+player);
					if (j > 0) {
						tellPart = tellPart.concat("- ");
					}
					tellPart = tellPart.concat("[").concat(UserUtils.getUserById(server, player).getName()).concat("] ");
					j++;
				}
				tell.add(tellPart);
			}
			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS, 1);
		}
		logger.debug("classement - end");
	}	
	
	
	
	
	/**
	* Return a map of users' global score for this game
	*/
	public Hashtable<String,Integer> globalScore(){
		logger.debug("globalScore - start");
		Hashtable<String,Integer> result = new Hashtable<>();
		int max = 0;

		Set<String> skeys = scores.keySet();
		for(String userId:skeys ){
			logger.debug("loop on scores : "+userId);
			PfcScore score = scores.get(userId);
			int scoreValue = getScoreValue(score);
			logger.debug("--score = <"+score.toString()+">-<"+scoreValue+">");
			result.put(userId,scoreValue);
			if (scoreValue>max){
				max = getScoreValue(score);
				logger.debug("--new max : "+max);
			}
		}

		Set<String> rkeys = result.keySet();
		for( String userId : rkeys ){
			int finalScore = (result.get(userId)*100)/max;
			logger.debug("loop on result : user=<"+userId+"> - final score=<"+finalScore+">");
			result.put(userId,finalScore);
		}
		logger.debug("globalScore - end : result size = <"+result.size()+">");
		return result;		
	}
	
	
 
 

}
