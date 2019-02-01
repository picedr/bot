package fr.picedr.bot.rite;

import fr.picedr.bot.dao.DAO;
import fr.picedr.bot.rite.bean.Ritualist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class RiteDAO extends DAO {
    private Logger logger = LoggerFactory.getLogger(RiteDAO.class);


    /**
     * get all Rite participants
     * @return : list of ritualists
     */
    List<Ritualist> getRitualists(){
        List<Ritualist> result = new ArrayList<>();
        logger.debug("getRitualists -start");

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT userid, state, lasthelp,help FROM rite;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                int state = rs.getInt("state");
                String userId = rs.getString("userid");
                Date lastHelp = rs.getTimestamp("lasthelp");
                String help = rs.getString("help");
                logger.debug("Adding : userId=<"+userId+"> - state=<"+state+">");
                Ritualist r = new Ritualist(userId,state);
                if (lastHelp != null){
                    r.setLastHelp(lastHelp);
                }
                if (help!=null){
                    r.setHelp(help);
                }


            }
        }catch (SQLException sqle){
            logger.error("Error getting ritualists",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting ritualists",sqle);
            }
        }
        logger.debug("getRitualists - End");
        return result;
    }


    /**
     * get a Rite participant
     * @param id : user to get
     * @return : list of ritualists
     */
    Ritualist getRitualist(String id){
        Ritualist result = null;
        logger.debug("getRitualists -start");

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT userid, state, lasthelp, help FROM rite WHERE userif=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,id);
            rs = ps.executeQuery();
            if (rs.next()){
                int state = rs.getInt("state");
                String userId = rs.getString("userid");
                Date lastHelp = rs.getTimestamp("lasthelp");
                String help = rs.getString("help");
                logger.debug("Adding : userId=<"+userId+"> - state=<"+state+">");
                Ritualist r = new Ritualist(userId,state);
                if (lastHelp != null){
                    r.setLastHelp(lastHelp);
                }
                if (help!=null){
                    r.setHelp(help);
                }

            }
        }catch (SQLException sqle){
            logger.error("Error getting ritualist",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting ritualist",sqle);
            }
        }
        logger.debug("getRitualist - End");
        return result;
    }

    /**
     * Change user state
     * @param userId  : id of the user to update
     * @param state : stateto put
     */
    void setState(String userId, int state){
        logger.debug("setState - start : userId=<"+userId+"> - state=<"+state+">");

        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE rite SET state=? WHERE userid=?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1,state);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error setState");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("setState - end : result=<"+result+">");

    }


    void helped (String userId){
        logger.debug("setState - start : userId=<"+userId+">");

        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE rite SET lasthelp=current_timestamp WHERE userid=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error setState");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("setState - end : result=<"+result+">");
    }

    void setHelp (String userId,String help){
        logger.debug("setHelp - start : userId=<"+userId+">");

        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE rite SET help=? WHERE userid=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,help);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error setHelp");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("setHelp - end : result=<"+result+">");
    }


    void addRitualist (String userId){
        logger.debug("addRitualist - start : userId=<"+userId+">");

        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO rite (userid,state) values (?,1);";
            ps = conn.prepareStatement(query);
            ps.setString(1,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error addRitualist");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("addRitualist - end : result=<"+result+">");
    }

}
