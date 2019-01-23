package fr.picedr.bot.jeux.quizz;


import fr.picedr.bot.Bot;
import fr.picedr.bot.BotService;
import fr.picedr.bot.Params;
import fr.picedr.bot.jeux.quizz.bean.QuestionQuizz;
import fr.picedr.bot.utils.MsgUtils;
import fr.picedr.bot.utils.UserUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QuizzService implements BotService {

    private Logger logger = LoggerFactory.getLogger(QuizzService.class);
	
	private Hashtable<String,List<String>> cles;

    private static QuizzService INSTANCE = null;

    private QuizzService(){
    	QuizzDAO quizzDAO = new QuizzDAO();
    	cles = quizzDAO.getCles();
    }


    public static QuizzService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QuizzService();
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

		if (server != null){
			if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
				switch (cmd){
					case "!quizz":
						switch (function) {
							case "list":
								list(server,channel,msg,user);
								break;
							case "add":
								add(server,channel,msg,user,content.replace("add","").trim());
								break;
							case "rem":
								rem(server,channel,msg,user,content.replace("rem","").trim());
								break;
							case "aff":
								aff(server,channel,msg,user,content);
								break;
							case "classement":
								classement(server,channel);
								break;
							case "score":
								score(server,channel,user);
								break;
							case "help":
							case "aide":
								QuizzService.help(server,channel);
								break;
							default:
								logger.debug("default function");
								if (channel == null) {
									MsgUtils.tell(user, "Je ne connais pas cette commande.");
								} else {
									MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help quizz** pour plus de details",0);
								}
						}
						break;
					default :
						logger.debug("default cmd");
						if (channel == null) {
							MsgUtils.tell(user, "Je ne connais pas cette commande.");
						} else {
							MsgUtils.tell(channel, "Je ne connais pas cette commande. Tappe **!help quizz** pour plus de details",0);
						}
				}
			} else {
				MsgUtils.tell(user, "Je ne connais pas cette commande. Tappe **!help** pour plus de d�tails");
			}
		}else {
			switch (cmd){
				case "!quizz":
					switch (function) {
					case "list":
						rappel(user);
						break;
					default:
						answer(user,content);
					}
				break;
			}
		}
		logger.debug("dispatch - end");
    }
	
    public static void help(Guild server, TextChannel channel) {
        List<String> tell = new ArrayList<>();
		Bot bot = Bot.getInstance();

		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			tell.add("AIDE POUR LA SECTION [QUIZZ]");
			tell.add(" ");
			tell.add("Le service [quizz] est un jeu de quizz (comme son nom l'indique).");
			tell.add("Les questions sont découvertes à l'aide de mots clés. Il suffit que le mot clé soit présent dans une phrase quand vous parlez et la question vous est envoyée par Papy en privée.");
			tell.add("Les commandes disponibles sont : ");
			tell.add("-**!quizz classement** : Affiche le classement du quizz.");
			tell.add("-**!quizz score** : Affiche le score du quizz.");
			tell.add("-**!quizz list** [en privée à Papy] : Affiche le liste des questions en attente.");

			if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
				tell.add("-**!quizz list** : Génère un pdf contenant la liste des questions du quizz.");
				tell.add("-**!quizz add** *<clé>*@@*<question>*@@*<réponse>* : Ajoute la question au quizz.");
				tell.add("-**!quizz add** *<clé>*@@*<question>*@@*<réponse>*@@*<retour>* : Ajoute la question au quizz et personnalise le retour de Papy si le joueur trouve.");
				tell.add("-**!quizz aff** *<id>* : Affiche la question liée à l'*id*.");
			}

			MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS, 0);
		}
    }

	/**
	 * Add a question to the game
	 * @param server : server to add the question to
	 * @param channel : channel to display return on
	 * @param msg : input message of user
	 * @param user : user requirering add
	 * @param content : content of the request
	 */
	private void add(Guild server,TextChannel channel,Message msg,User user,String content){
		logger.debug("add - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> -content=<"+content+">");
		if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
			String[] spLine = content.split("@@");
			QuizzDAO quizzDAO = new QuizzDAO();
			logger.debug("spLine length=<"+spLine.length+">");
			if (spLine.length==3){		
				String key = spLine[0].toLowerCase().trim();
				String question = spLine[1].trim().replace("\n","##");
				String answer = spLine[2].trim();
				logger.debug("key=<"+key+"> - question=<"+question+"> - answer=<"+answer+">");
				if (!cles.containsKey(server.getId()) || !cles.get(server.getId()).contains(key)){
					logger.debug("New key");
					int ret = quizzDAO.addQuestion(server.getId(),key,question,answer,"");
					logger.debug("Add return=<"+ret+">");
					if (ret > 0){
						MsgUtils.tell(channel,"La question a été ajoutée");
						if (!cles.containsKey(server.getId())){
							cles.put(server.getId(),new ArrayList<>());
						}
						cles.get(server.getId()).add(key);
					} else {
						MsgUtils.tell(channel,"J'ai eu un soucis, je n'ai pas réussi à ajouter la question");
					}
				}else {
					logger.debug("Key already used");
					MsgUtils.tell(channel,"Une question avec cette clé existe déjà");
				}
			}else if (spLine.length==4){
				String key = spLine[0].toLowerCase().trim();
				String question = spLine[1].trim().replace("\n","##");
				String answer = spLine[2].trim();
				String retour = spLine[3]==null ? "" : spLine[3].trim();
				logger.debug("key=<"+key+"> - question=<"+question+"> - answer=<"+answer+"> - retour=<"+retour+">");
				if (!cles.containsKey(server.getId()) || !cles.get(server.getId()).contains(key)){
					logger.debug("New Key");
					int ret=quizzDAO.addQuestion(server.getId(),key,question,answer,retour);
					logger.debug("Adding return : <"+ret+">");
					if (ret > 0){
						MsgUtils.tell(channel,"La question a été ajoutée");
						if (!cles.containsKey(server.getId())){
							cles.put(server.getId(),new ArrayList<>());
						}
						cles.get(server.getId()).add(key);
					} else {
						MsgUtils.tell(channel,"J'ai eu un soucis, je n'ai pas réussi à ajouter la question");
					}
				}else {
					logger.debug("Existing key");
					MsgUtils.tell(channel,"Une question avec cette clé existe déjà");
				}
			}else {
				logger.debug("Bad format input message");
				MsgUtils.tell(channel, "Le format de la commande est : **!quizz add *clé*@@*question*@@*réponse* ** ou **!quizz add *clé*@@*question*@@*réponse*@@*retour* **",0);
			}
		} else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!quizz add**) est limitée au chan admin.");
        }
        logger.debug("add - end");
	}


	/**
	 * remove a message
	 * @param server : server to remove the message for
	 * @param channel : channel to display
	 * @param msg : inpu message
	 * @param user : user who tries to remove the message
	 * @param id : id of the question to remove
	 */
	private void rem(Guild server, TextChannel channel,Message msg,User user,String id){
		logger.debug("rem - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+"> - id=<"+id+">");
		if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
			QuizzDAO quizzDAO = new QuizzDAO();
			int result = quizzDAO.removeQuestion(server.getId(),Integer.parseInt(id));
			logger.debug("Remove result=<"+result+">");
			if (result>0){
				MsgUtils.tell(channel,"La question a été supprimée.");
				cles = quizzDAO.getCles();
			}else {
				MsgUtils.tell(channel,"La question avec l'id "+id+" n'a pas été trouvée pour ce serveur");
			}	
		} else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!quizz rem**) est limitée au chan admin.");
        }
        logger.debug("rem - end");
	}

	/**
	 * Display a question details
	 * @param server : server for which display the question
	 * @param channel : channel to display on
	 * @param msg : request message
	 * @param user : user requestiong the display
	 * @param id : id of the question
	 */
	private void aff(Guild server,TextChannel channel,Message msg,User user,String id){
		logger.debug("aff - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+
				"> - user=<"+user.getName()+"> - id=<"+id+">");
		if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
			QuizzDAO quizzDAO = new QuizzDAO();
			QuestionQuizz question = quizzDAO.getQuestion(server.getId(),id);
			
			if (question != null){
				logger.debug("Question : cle=<"+question.getQuestion()+"> - question=<"+question.getQuestion()
						+"> - reponse=<"+question.getAswer()+"> - retour=<"+question.getRetour()+">");
				List<String> tell=new ArrayList<>();
				tell.add("La question ["+id+"] est : ");
				tell.add("#CLE : "+question.getKey());
				tell.add("#QUESTION : "+question.getQuestion().replace("##", "\n"));
				tell.add("#REPONSE : "+question.getAswer());
				tell.add("#RETOUR : "+question.getRetour());

				MsgUtils.tellBlockFramed(channel, tell,MsgUtils.FT_CSS,0);
			}else {
				logger.debug("No question for this id and server");
				MsgUtils.tell(channel, "Aucune question avec l'id **"+id+"** pour ce serveur.",0);
			}
		} else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Cette commande (**!quizz aff**) est limitée au chan admin.");
        }			
		logger.debug("aff - end");
	}

	/**
	 * Check if question is discovered in public messages
	 * @param server : server on which message was posted
	 * @param msg : message
	 * @param user : user who posted the message
	 * @param content : content of the message
	 */
	public void publicMsg(Guild server,Message msg, User user, String content){
		logger.debug("publicMsg - start : server=<"+server.getName()+"> - user=<"+user.getName()+"> - content=<"+content+">");
		if (!user.isBot()){
			String filteredContent = content.replace("!", " ").replace(",", " ").replace(";", " ")
					.replace(".", " ").replace("?", " ").replace(":", " ")
					.replace("(", " ").replace(")", " ").toLowerCase();
			String[] splitMsg = filteredContent.split(" ");

			if (!UserUtils.isAdmin(server,user)){
				logger.debug("User is not admin");
				QuizzDAO quizzDAO = new QuizzDAO();
				for (String msgPart : splitMsg){
					logger.debug("loop on parts : <"+msgPart+">");
					if (cles.containsKey(server.getId()) && cles.get(server.getId()).contains(msgPart)){
						logger.debug("cles contains text");
						boolean isDisc = quizzDAO.isQuestionDiscovered(server.getId(),msgPart);
						logger.debug("isDisc=<"+isDisc+">");
						if (!isDisc){
							quizzDAO.discovered(server.getId(), user.getId(), msgPart);
							int qId = quizzDAO.getIdFromKey(server.getId(), msgPart);
							logger.debug("Id of the question=<"+qId+">");
							QuestionQuizz question = quizzDAO.getQuestion(String.valueOf(qId));
							
							List<String> tell = new ArrayList<>();
							tell.add("Félicitation, tu as trouvé une nouvelle question du quizz : ");
							tell.add("*"+question.getQuestion().replace("##", "\n")+"*");
							tell.add("Pour répondre à cette question tappe : **!quizz "+question.getId()+" taReponse**");
							MsgUtils.tellBlock(user,tell);
						
							TextChannel adminChannel = server.getTextChannelById(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL));
							MsgUtils.tell(adminChannel, user.getName()+" a découvert la question "+qId);
							aff(server,adminChannel,msg,user,String.valueOf(qId));
						}
					}
				}
			}
		}
		logger.debug("publicMsg - end");
	}


	/**
	 * Send a pdf containing a list of all the server questions
	 * @param server : server to send the questions for
	 * @param channel : channel to send the questions on
	 * @param msg : original message
	 * @param user : user requiring questions
	 */
	private void list(Guild server, TextChannel channel,Message msg, User user){
		logger.debug("list - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
		if (channel.getId().equals(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL))) {
		    GenerateListThread thread = new GenerateListThread(server);
			thread.run();
		} else {
            logger.debug("is not admin channel");
            msg.delete().complete();
            logger.debug("message is deleted");
            MsgUtils.tell(user, "Pas sur ce chan.");
        }
        logger.debug("list - end");
	}


	/**
	 * Display levels
	 * @param server : server to display for
	 * @param channel : channel to display on
	 */
	public void classement(Guild server,TextChannel channel){
		logger.debug("classement - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+">");
		Bot bot = Bot.getInstance();
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			Hashtable<Integer, List<String>> classement = new Hashtable<>();
			int max = 0;
			QuizzDAO quizzDAO = new QuizzDAO();
			Hashtable<String, Integer> scores = quizzDAO.getScores(server.getId());
			Set<String> keys = scores.keySet();
			for (String userId : keys) {
				int score = scores.get(userId);
				logger.debug("loop on scores : userId=<"+userId+"> - score=<"+score+">");
				if (score > max) {
					max = score;
					logger.debug("--New max : <"+max+">");
				}
				if (classement.containsKey(score)) {
					logger.debug("--Existing score");
					classement.get(score).add(userId);
				} else {
					logger.debug("--New score");
					List<String> users = new ArrayList<>();
					users.add(userId);
					classement.put(score, users);
				}
			}
			int position = 1;
			List<String> tell = new ArrayList<>();
			String tellPart;
			for (int i = max; i >= 0; i--) {
				logger.debug("final loop : i=<"+i+">");
				tellPart = "";
				if (classement.containsKey(i)) {
					logger.debug("--classement contains i");
					List<String> users = classement.get(i);
					int score = scores.get(users.get(0));
					tellPart = "[" + position + "] " + score + " pt";
					if (i > 1) {
						tellPart = tellPart + "s";
					}
					tellPart = tellPart + " : ";
					int j = 0;
					for (String userId:users) {
						logger.debug("--loop on users : <"+userId+">");
						if (j > 0) {
							tellPart = tellPart.concat(" # ");
						}
						tellPart = tellPart.concat(UserUtils.getUserById(server, userId).getName());
						position++;
						j++;
					}
				}
				if (!tellPart.equals("")) {
					tell.add(tellPart);
				}
			}
			logger.debug("tell size : <"+tell.size()+">");
			if (tell.size()>0) {
				MsgUtils.tellBlockFramed(channel, tell, MsgUtils.FT_CSS);
			} else {
				MsgUtils.tell(channel, "Aucun classement à afficher");
			}
		}
		logger.debug("classement - end");
	}

	/**
	 * Display score of the user on the server
	 * @param server : server for the score
	 * @param channel : channel to display on
	 * @param user : user to display for
	 */
	public void score(Guild server, TextChannel channel, User user){
		logger.debug("score - start : server=<"+server.getName()+"> - channel=<"+channel.getName()+"> - user=<"+user.getName()+">");
		Bot bot = Bot.getInstance();
		if (bot.getServices().get(server.getId()).get(Params.SRV_JEUX).equals("1")) {
			if (UserUtils.isAdmin(server, user)) {
				MsgUtils.tellFramed(channel, "n/a", MsgUtils.FT_CSS);
			} else {
				QuizzDAO quizzDAO = new QuizzDAO();
				String[] score = quizzDAO.getRankValue(server, user.getId());
				MsgUtils.tellFramed(channel, "- Score : " + score[0] + "\n- Rang : " + score[1], MsgUtils.FT_CSS);
			}
		}
	}

	/**
	 * Tell a user what are his pending questions
	 * @param user : user to tell to
	 */
	private void rappel(User user){
		logger.debug("rappel - start : user=<"+user.getName()+">");
		List<String> tell = new ArrayList<>();
		QuizzDAO quizzDAO = new QuizzDAO();
		List<QuestionQuizz> pq = quizzDAO.getAllUserQuestions(user.getId());
		logger.debug("Number of pending questions : "+pq.size());
		if (pq.size()>0){
			tell.add("Pour répondre à une question la commande est : **!quizz <id> <<réponse>**");
			tell.add("Tes questions en cours sont : ");
			for (QuestionQuizz qq:pq){
				logger.debug("loop on pq : id=<"+qq.getId()+"> - question=<"+qq.getQuestion()+">");
				tell.add("- "+qq.getId()+" : "+qq.getQuestion());
			}
			MsgUtils.tellBlock(user,tell);
		}else{
			MsgUtils.tell(user, "Il n'y a aucune question en attente pour toi");
		}
		logger.debug("rappel - end");
	}

	/**
	 * When a user tries to answer a question
	 * @param user : user who tries to answer
	 * @param content : content of the answer
	 */
	private void answer(User user, String content){
		logger.debug("answer - start : user=<"+user.getName()+"> - content=<"+content+">");
		if (content.length()>0){
			String qId = content.split(" ")[0];
			String answer = content.replace(qId+" ", "");
			logger.debug("qId=<"+qId+"> - answer=<"+answer+">");

			QuizzDAO quizzDAO = new QuizzDAO();
			int check = quizzDAO.checkAnswer(user.getId(),qId,answer);
			logger.debug("check=<"+check+">");
			if (check==0){
				MsgUtils.tell(user,"La question **"+qId+"** ne t'est pas disponible.");
			} else if (check == -1){
				MsgUtils.tell(user,"Nop",2);
			} else if (check ==1){				
				QuestionQuizz qq = quizzDAO.getQuestion(qId);
				quizzDAO.answer(user.getId(),qId);
				String serverId = quizzDAO.getQuestionServer(qId);
				logger.debug("serverId=<"+serverId+">");
				Guild server = Bot.getInstance().getJDA().getGuildById(serverId);
				TextChannel generalChannel = server.getTextChannelById(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_GENERALCHANNEL));
				TextChannel adminChannel = server.getTextChannelById(Bot.getInstance().getServersConf().get(server.getId()).get(Params.CONF_ADMINCHANNEL));
				MsgUtils.tell(generalChannel, user.getName()+" marque un point.",2);
				if (qq.hasRetour()){
					logger.debug("retour=<"+qq.getRetour()+">");
					MsgUtils.tell(user, qq.getRetour(),2);
				} else {
					MsgUtils.tell(user, "Félicitation, tu marques un point",2);
				}
				MsgUtils.tell(adminChannel, user.getName()+" a répondu à la question "+qId,0);
				aff(server,adminChannel,null,user,qId);
				classement(server,generalChannel);				
			}		
		} else {
			logger.debug("Bad format message");
			MsgUtils.tell(user,"Je ne comprend pas ce que tu veux. Tappe **!help quizz** sur un chan public.");
		}
		logger.debug("answer - end");
	}	
	
	
	/**
	* Return a map of users' global score for this game
	 * @param server : server for which return the result
	 * @return Map of scores : key=userId - value=score
	*/
	public Hashtable<String,Integer> globalScore(Guild server){
		logger.debug("globalScore - start : server=<"+server.getName()+">");
		Hashtable<String,Integer> result = new Hashtable<>();
		int maxScore=-1;
		QuizzDAO quizzDAO = new QuizzDAO();
		Hashtable<String,Integer> scores =  quizzDAO.getScores(server.getId());
		
		for(String userId : scores.keySet()){
			Integer score = scores.get(userId);
			logger.debug("loop on scores : user=<"+userId+"> - score=<"+score+">");
			if (score>maxScore){
				maxScore = score;
				logger.debug("--new max : "+maxScore);
			}			
		}
		
		for( String userId : scores.keySet()){
			int score = (scores.get(userId)*100)/maxScore;
			logger.debug("final loop : user=<"+userId+"> - score=<"+score+">");
			result.put(userId,score);
		}

		logger.debug("globaalScore - end : result size="+result.size());
		return result;		
	}	
	
	
}