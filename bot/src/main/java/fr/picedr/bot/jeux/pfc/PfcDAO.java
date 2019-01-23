package fr.picedr.bot.jeux.pfc;

import fr.picedr.bot.dao.DAO;
import fr.picedr.bot.jeux.pfc.bean.PfcScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

class PfcDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(PfcDAO.class);

    /**
     * insert a user entry in database
     * @param userId : id of the user to add
     * @param score : state of the user
     */
    void addPlayer(String userId, PfcScore score){
        logger.debug("addPlayer - start : userId=<"+userId+"> - score=<"+score.toString()+">");
        int result=0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO pfc (userid,win,draw,lose) values (?,?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,userId);
            ps.setInt(2,score.getWin());
            ps.setInt(3,score.getDraw());
            ps.setInt(4,score.getLose());

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

        logger.debug("addPlayer - Final result : <"+result+">");
    }
	
	
    /**
     * Update a user entry in database
     * @param userId : id of the user to update
     * @param score : state of the user
     */
    void updatePlayer(String userId, PfcScore score){
        logger.debug("updatePlayer - start : userId=<"+userId+"> - score=<"+score.toString()+">");
        int result=0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE pfc SET "
				+ " win = ? , "
				+ " draw = ? , "
				+ " lose = ? "
				+ " WHERE userid = ?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1,score.getWin());
            ps.setInt(2,score.getDraw());
            ps.setInt(3,score.getLose());
			ps.setString(4,userId);
			
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

        logger.debug("updatePlayer - Final result : <"+result+">");
    }


    /**
     * Get all scores for the game
     * @return key=user id, value=PFCScore
     */
	Hashtable<String,PfcScore> getScores(){
		logger.debug("getScores - start");
        Hashtable<String,PfcScore> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT userid,win,draw,lose FROM pfc;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()){
				String userId = rs.getString("userid");
				int win = rs.getInt("win");
				int draw = rs.getInt("draw");
				int lose = rs.getInt("lose");
			
				PfcScore score = new PfcScore(win,draw,lose);
			
				result.put(userId,score);

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
                logger.error("Error getting pfc score",sqle);
            }
        }
        logger.debug("getScores - End : result size = "+result.size());
        return result;		
	}
	
}
