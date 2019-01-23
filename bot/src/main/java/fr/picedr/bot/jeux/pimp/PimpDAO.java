package fr.picedr.bot.jeux.pimp;

import fr.picedr.bot.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class PimpDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(PimpDAO.class);

    /**
     * insert a user entry in database
     * @param userId : id of the user to add
     */
    void addPlayer(String serverId,String userId){
        logger.debug("addPlayer - start : serverId=<"+serverId+"> - userId=<"+userId+">");
        int result=0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO pimp (serverid,userid,score) values (?,?,100);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
			ps.setString(2,userId);
            result = ps.executeUpdate();
            logger.debug("Insert result : <"+result+">");


        }catch (SQLException sqle){
            logger.error("Error while inserting player entry",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }

        logger.debug("Final result : <"+result+">");
    }
	
	
    /**
     * Update a user entry in database
     * @param userId : id of the user to update
     * @param score : state of the user
     */
    void updatePlayer(String serverId, String userId, int score){
        logger.debug("addPlayer - start : serverId=<"+serverId+"> - userId=<"+userId+"> - score=<"+score+">");
        int result=0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE pimp SET "
				+ " score = ? "
				+ " WHERE userid = ? "
				+ " AND serverid = ?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1,score);
			ps.setString(2,userId);
			ps.setString(3,serverId);

			
            result = ps.executeUpdate();
            logger.debug("Update result : <"+result+">");


        }catch (SQLException sqle){
            logger.error("Error while updating player entry",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }

        logger.debug("Final result : <"+result+">");
    }

    /**
     * return scores filtered by servers
     * @return scores filtered by servers
     */
	Hashtable<String,Hashtable<String,Integer>> getScores(){
		logger.debug("getScores - start");
        Hashtable<String,Hashtable<String,Integer>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT serverid,userid,score FROM pimp;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
				String serverId = rs.getString("serverid");
				String userId = rs.getString("userid");
				int score = rs.getInt("score");

                logger.debug("loop : serverId=<"+serverId+"> - userId=<"+userId+"> - score=<"+score+">");

				if (!result.containsKey(serverId)){
					result.put(serverId,new Hashtable<>());
				}
				
				result.get(serverId).put(userId,score);

            }
        }catch (SQLException sqle){
            logger.error("Error getting scores",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting pimp score",sqle);
            }
        }
        logger.debug("getScores - End");
        return result;		
	}
}
