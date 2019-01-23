package fr.picedr.bot.jeux.flood;

import fr.picedr.bot.dao.DAO;
import fr.picedr.bot.jeux.flood.bean.FloodUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

class FloodDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(FloodDAO.class);

    /**
     * insert a user entry in database
     * @param serverId : id of the server the command is launched
     * @param userId : id of the user to add
     * @param score : state of the user
     * @return Number of line added
     */
    int addPlayer(String serverId,String userId, FloodUser score){
        logger.debug("addPlayer - start : serverId=<"+serverId+"> userId=<"+userId+"> - score=<"+score.toString()+">");
        int result=0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO flood (serverid,userid,score,niv) values (?,?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
			ps.setString(2,userId);
            ps.setInt(3,score.getScore());
            ps.setInt(4,score.getNiv());


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
        return result;
    }
	
	
    /**
     * Update a user entry in database
     * @param serverId : server
     * @param userId : id of the user to update
     * @param score : state of the user
     */
    void updatePlayer(String serverId,String userId, FloodUser score){
        logger.debug("updatePlayer - start : serverId=<"+serverId+"> userId=<"+userId+"> - score=<"+score.toString()+">");
        int result=0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE flood SET "
				+ " score = ? , "
				+ " niv = ?  "
				+ " WHERE userid = ? "
				+ " AND serverid = ?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1,score.getScore());
            ps.setInt(2,score.getNiv());
			ps.setString(3,userId);
			ps.setString(4,serverId);
			
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
	
	int getMax(){
		logger.debug("getMax - start");
        int result = 1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT COALESCE(MAX(niv),1) as max FROM flood;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()){
				result = rs.getInt("max");
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
        logger.debug("getMax - End : result=<"+result+">");
        return result;		
		
	}
	
	
	
	Hashtable<String,Hashtable<String,FloodUser>> getScores(){
		logger.debug("getScores - start");
        Hashtable<String,Hashtable<String,FloodUser>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT serverid,userid,score,niv FROM flood;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
				String serverId = rs.getString("serverid");
				String userId = rs.getString("userid");
				int score = rs.getInt("score");	
				int niv = rs.getInt("niv");	
                logger.debug("serverId=<"+serverId+"> - userId=<"+userId+"> - score=<"+score+"> - niv=<"+niv+">");
				if (!result.containsKey(serverId)){
					result.put(serverId,new Hashtable<>());
				}
				FloodUser user = new FloodUser(userId,niv,score);
				result.get(serverId).put(userId,user);

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
