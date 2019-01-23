package fr.picedr.bot.jeux.quizz;


import fr.picedr.bot.dao.DAO;
import fr.picedr.bot.jeux.quizz.bean.QuestionQuizz;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

class QuizzDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(QuizzDAO.class);

	/**
	* Add question to the quizz
	* @param serverId : server on which the question is added
	* @param key : key to unlock question
	* @param question : question value
	* @param answer : answer value
	* @param retour : to customize answer of the bot (not mandatory)
	* @return number of line added
	*/
    int addQuestion(String serverId, String key, String question, String answer, String retour){
        logger.debug("addQuestion - start : serverid=<"+serverId+"> - key=<>question=<"+question+"> - answer=<"+answer+"> - retour=<"+retour+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO questionQuizz (serverid,key,question,answer,retour) values (?,?,?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,key);
			ps.setString(3,question);
			ps.setString(4,answer);
			ps.setString(5,retour);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error addin question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("addQuestion - end : result=<"+result+">");
        return result;
    }
	
    /**
     * Remove a question from the quizz
     * @param serverId : server on which unmute the user
     * @param questionId : questionId to remove
     * @return : number of lines deleted
     */
    int removeQuestion(String serverId, int questionId){
        logger.debug("removeQuestion - start : serverId=<"+serverId+"> - questionId=<"+questionId+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM questionQuizz WHERE serverid =? AND id=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setInt(2,questionId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error removing question");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("removeQuestion - end : result=<"+result+">");
        return result;
    }	
	
	
   /**
     * Getting all keys of quizz
     * @return a list of keys per server
     */
    Hashtable<String,List<String>> getCles(){
        logger.debug("getCles - start");
        Hashtable<String,List<String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT serverid, key FROM questionQuizz;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()){
                String serverId = rs.getString("serverid");
                String key = rs.getString("key");
                logger.debug("adding key : "+key+" for server "+serverId);
                if (result.containsKey(serverId)){
                    result.get(serverId).add(key);
                }else{
                    List<String> keys = new ArrayList<>();
                    keys.add(key);
                    result.put(serverId,keys);
                }
            }

        }catch (SQLException sqle){
            logger.error("Error while getting keys",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getCles - end : result size = "+result.size());
        return result;
    }	
	
	
   /**
     * Getting all keys of quizz
     * @return a list of keys per server
     */
    QuestionQuizz getQuestion(String serverId, String questionId){
        logger.debug("getQuestion - start : serverId=<"+serverId+"> - questionId=<"+questionId+">");
        QuestionQuizz result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT key, question, answer, retour "
								+" FROM questionQuizz "
								+" WHERE serverid = ? "
								+" AND id = ? ;";
            ps = conn.prepareStatement(query);
			ps.setString(1,serverId);
			ps.setInt(2,Integer.valueOf(questionId));
            rs = ps.executeQuery();

            if (rs.next()){
                String key = rs.getString("key");
                String question = rs.getString("question");
				String answer = rs.getString("answer");
				String retour = rs.getString("retour");
				
				result = new QuestionQuizz(questionId, key, question, answer, retour);
            }

        }catch (SQLException sqle){
            logger.error("Error while getting question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getCles - end : result  = "+(result==null ? "null":result.toString()));
        return result;
    }		
	
  /**
     * Getting all keys of quizz
     * @return a list of keys per server
     */
    QuestionQuizz getQuestion(String questionId){
        logger.debug("getQuestion - start : questionId=<"+questionId+">");
        QuestionQuizz result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT key, question, answer, retour "
								+" FROM questionQuizz "
								+" WHERE id = ? ;";
            ps = conn.prepareStatement(query);
			ps.setInt(1,Integer.valueOf(questionId));
            rs = ps.executeQuery();

            if (rs.next()){
                String key = rs.getString("key");
                String question = rs.getString("question");
				String answer = rs.getString("answer");
				String retour = rs.getString("retour");
				
				result = new QuestionQuizz(questionId, key, question, answer, retour);
            }

        }catch (SQLException sqle){
            logger.error("Error while getting question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getCles - end : result  = "+(result==null ? "null":result.toString()));
        return result;
    }		

	
	 /**
     * Check if user has a question by key
      * @param serverId : server on which to check
      * @param key : key of the question to check
     * @return true if already dicovered, false else
     */
    boolean isQuestionDiscovered(String serverId,String key){
        logger.debug("isQuestionDiscovered - start - serverId=<"+serverId+"> - key=<"+key+">");
        boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT count(uq.questionid) FROM userQuizz uq, questionQuizz qq"
								+" WHERE uq.questionid = qq.id "
								+" AND qq.key = ? "
								+" AND qq.serverid = ? ;";
            ps = conn.prepareStatement(query);
			ps.setString(1,key);
			ps.setString(2,serverId);
            rs = ps.executeQuery();

            if (rs.next()){
                int count = rs.getInt(1);
				if (count>0){
					result = true;
				}
            }

        }catch (SQLException sqle){
            logger.error("Error while checking question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getCles - end : result  = "+result);
        return result;
    }		
	
	/**
	* Add a question to a user
	* @param serverId : server on which the question is added
     * @param userId : user to add the question to
	* @param key : key of the question to add
	*/
    void discovered(String serverId, String userId, String key){

        int result = 0;
        PreparedStatement ps = null;
		int id = getIdFromKey(serverId,key);
        try {
            String query = "INSERT INTO userQuizz (questionid,userid,state) values (?,?,1);";
            ps = conn.prepareStatement(query);
            ps.setInt(1,id);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error adding question to user",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("discovered - end : result=<"+result+">");
    }
	
		/**
	* Mark as completed a question to a user
	* @param userId : user to mark question answerd to
	* @param questionId : id of the question answered
	*/
    void answer(String userId, String questionId){
        logger.debug("answer - start : userId=<"+userId+"> - questionId=<"+questionId+">");
        int result = 0;
        PreparedStatement ps = null;
        try {
            String query = "UPDATE userQuizz SET state = 2 "
							+" WHERE questionid = ? "
							+" AND userid = ? ;";
            ps = conn.prepareStatement(query);
            ps.setInt(1,Integer.valueOf(questionId));
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error answering question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("answer - end : result=<"+result+">");
    }


    /**
     * Get question id from its key
     * @param serverId : server on which the question is
     * @param key : key of the question
     * @return id of the question
     */
	int getIdFromKey(String serverId, String key){
        logger.debug("getIdFromKey - start : serverId=<"+serverId+"> - key=<"+key+">");
        int result = -1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id "
								+" FROM questionQuizz "
								+" WHERE serverid = ? "
								+" AND key = ? ;";
            ps = conn.prepareStatement(query);
			ps.setString(1,serverId);
			ps.setString(2,key);
            rs = ps.executeQuery();

            if (rs.next()){
                result = rs.getInt("id");
            }

        }catch (SQLException sqle){
            logger.error("Error while getting id from key",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getCles - end : result  = "+result);
        return result;				
	}


    /**
     * Get all question of all users of a server in a specific state
     * @param serverId : server to retrieve information for
     * @param state : state to get
     * @return Map : key = question id - value = list of user ids
     */
	Hashtable<Integer,List<String>> getState(String serverId, int state){
		logger.debug("getState - start : serverId=<"+serverId+"> - state=<"+state+">");
        Hashtable<Integer,List<String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT uq.userid, uq.questionid "
								+" FROM userQuizz uq, questionQuizz qq "
								+" WHERE uq.questionId = qq.id "
								+" AND qq.serverid = ? "
								+" AND uq.state = ?;";
            ps = conn.prepareStatement(query);
			ps.setString(1,serverId);
			ps.setInt(2,state);
            rs = ps.executeQuery();

            while (rs.next()){
                String userId = rs.getString(1);
                int questionId = rs.getInt(2);
                logger.debug("adding userId : "+userId+" for questionId "+questionId);
                if (result.containsKey(questionId)){
                    result.get(questionId).add(userId);
                }else{
                    List<String> keys = new ArrayList<>();
                    keys.add(userId);
                    result.put(questionId,keys);
                }
            }

        }catch (SQLException sqle){
            logger.error("Error while getting keys",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getState - end : result size = "+result.size());
        return result;
		
	}

    /**
     * Get all questions for a server
     * @param serverId :server to get the questions for
     * @return A list of QuestionQuizz
     */
    List<QuestionQuizz> getAllQuestions(String serverId){
        logger.debug("getAllQuestions - start : serverId=<"+serverId+">");
        List<QuestionQuizz> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id, key, question, answer, retour "
								+" FROM questionQuizz "
								+" WHERE serverid = ? "
								+" ORDER BY id;";
            ps = conn.prepareStatement(query);
			ps.setString(1,serverId);
            rs = ps.executeQuery();

            while (rs.next()){
                String id = rs.getString("id");
				String key = rs.getString("key");
                String question = rs.getString("question");
				String answer = rs.getString("answer");
				String retour = rs.getString("retour");
				
				result.add(new QuestionQuizz(id, key, question, answer, retour));
            }

        }catch (SQLException sqle){
            logger.error("Error while getting question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getAllQuestions - end : result  = "+result.toString());
        return result;
    }


    /**
     * Get players' score for a server
     * @param serverId : server to get the score for
     * @return A map of score : key=user id - value=score
     */
	Hashtable<String,Integer> getScores(String serverId){
        logger.debug("getScores - start : serverId=<"+serverId+">");
        Hashtable<String,Integer> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT uq.userId, COUNT(uq.questionId) score "
								+" FROM userQuizz uq, questionQuizz qq "
								+" WHERE uq.questionId = qq.id " 
								+" AND serverid = ? "
								+" AND state = 2 "
								+" GROUP BY uq.userId "
								+" ORDER BY score;";
            ps = conn.prepareStatement(query);
			ps.setString(1,serverId);
            rs = ps.executeQuery();

            while (rs.next()){
                String userId = rs.getString(1);
				int score = rs.getInt(2);
				logger.debug("Adding : user=<"+userId+"> - score=<"+score+">");
				result.put(userId,score);
            }

        }catch (SQLException sqle){
            logger.error("Error while getting scores",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getScores - end : result  = "+result.toString());
        return result;
	}


    /**
     * Get rank of a specific user
     * @param server : server to get the rank for
     * @param userId : user to get the rank for
     * @return rank of the user on this server
     */
    String[] getRankValue(Guild server, String userId){
        logger.debug("getRankValue - start : serverId=<"+server.getName()+"> - userId=<"+userId+">");
        String[] result = new String[2];
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            int score = 0;
            String query = "SELECT COUNT(uq.questionId)"
                    +" FROM userQuizz uq, questionQuizz qq "
                    +" WHERE uq.questionId = qq.id "
                    +" AND qq.serverid = ? "
                    +" AND uq.userid = ? "
                    +" AND state = 2 ;";
            ps = conn.prepareStatement(query);
            ps.setString(1,server.getId());
            ps.setString(2,userId);
            rs = ps.executeQuery();

            if (rs.next()){
                score = rs.getInt(1);
                result[0] = String.valueOf(score);
            }
            logger.debug("score=<"+score+">");

            query = "SELECT COUNT(id) "
                    +"FROM (SELECT uq.userId id, COUNT(uq.questionId) score "
                    +" FROM userQuizz uq, questionQuizz qq "
                    +" WHERE uq.questionId = qq.id "
                    +" AND serverid = ? "
                    +" AND state = 2 "
                    +" GROUP BY uq.userId ) as rank"
                    +" WHERE score > ? ;";

            ps = conn.prepareStatement(query);
            ps.setString(1,server.getId());
            ps.setInt(2,score);
            rs = ps.executeQuery();

            if (rs.next()){
                result[1] = String.valueOf(rs.getInt(1)+1);
            }

        }catch (SQLException sqle){
            logger.error("Error while getting rank",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getRankValue - end : result  = "+result[0]+" - "+result[1]);
        return result;
    }


    /**
     * Get all pending questions of a user
     * @param userId : user to get the questions for
     * @return a list of questions
     */
   List<QuestionQuizz> getAllUserQuestions(String userId){
        logger.debug("getAllUserQuestions - start : userId=<"+userId+">");
        List<QuestionQuizz> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id, key, question, answer, retour "
								+" FROM questionQuizz "
								+" WHERE id in (SELECT questionId FROM userQuizz WHERE userid = ? AND state = 1 ) "
								+" ORDER BY id;";
            ps = conn.prepareStatement(query);
			ps.setString(1,userId);
            rs = ps.executeQuery();

            while (rs.next()){
                String id = rs.getString("id");
				String key = rs.getString("key");
                String question = rs.getString("question");
				String answer = rs.getString("answer");
				String retour = rs.getString("retour");
				logger.debug("Adding : id=<"+id+"> - key=<"+key+"> - question=<"+question
                        +"> - answer=<"+answer+"> - retour=<"+retour+">");
				result.add(new QuestionQuizz(id, key, question, answer, retour));
            }

        }catch (SQLException sqle){
            logger.error("Error while getting question",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getAllUserQuestions - end : result  = "+result.toString());
        return result;
    }


    /**
     * Check an answer for a question
     * @param userId : user who tries to answer
     * @param questionId : id of the question
     * @param answer : answer given
     * @return : 0 if user has not this question in his list, 1 if OK, -1 if KO
     */
	int checkAnswer(String userId, String questionId, String answer){
	    logger.debug("checkAnswer - start : userId=<"+userId+"> - questionId=<"+questionId+"> - answer=<"+answer+">");
        int result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT qq.answer"
								+" FROM userQuizz uq, questionQuizz qq "
								+" WHERE uq.questionId = qq.id " 
								+" AND uq.userid = ? "
								+" AND uq.questionId = ? "
								+" AND state = 1 ;";
            ps = conn.prepareStatement(query);
			ps.setString(1,userId);
			ps.setInt(2,Integer.valueOf(questionId));
            rs = ps.executeQuery();

            if (rs.next()){
                String answerBase = rs.getString(1);
                logger.debug("answerBase=<"+answerBase+">");
				if (answerBase.toLowerCase().equals(answer.toLowerCase())){
					result = 1;
				} else {
					result = -1;
				}
            }

        }catch (SQLException sqle){
            logger.error("Error while checking answer",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("checkAnswer - end : result  = "+result);
        return result;
	}


    /**
     * Get the serverId the question belongs to
     * @param questionId : id of the question
     * @return id of the server
     */
	String getQuestionServer(String questionId){
	    logger.debug("getQuestionServer : questionId=<"+questionId+">");
        String result = "";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT serverid FROM questionQuizz WHERE id = ? ;";
            ps = conn.prepareStatement(query);
			ps.setInt(1,Integer.valueOf(questionId));
            rs = ps.executeQuery();

            if (rs.next()){
                result = rs.getString(1);
			}

        }catch (SQLException sqle){
            logger.error("Error while getting server",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("getQuestionServer - end : result  = "+result);
        return result;
	}	
	
}