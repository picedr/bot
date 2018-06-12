package fr.picedr.bot.admin;
import fr.picedr.bot.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AdminDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(AdminDAO.class);

    /**
     * Mute a user
     * @param serverId : server on which the user is muted
     * @param userId : user muted
     * @return number of line added
     */
    public int mute(String serverId, String userId){
        logger.debug("mute - start : serverId=<"+serverId+"> - userId=<"+userId+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO mute (serverid,userid) values (?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error muting user");
        }
        logger.debug("mute - end : result=<"+result+">");
        return result;
    }

    /**
     * Unmute a user
     * @param serverId : server on which unmute the user
     * @param userId : user to unmute
     * @return : number of lines deleted
     */
    public int unmute(String serverId, String userId){
        logger.debug("unmute - start : serverId=<"+serverId+"> - userId=<"+userId+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM mute WHERE serverid =? AND userid=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error unmuting user");
        }
        logger.debug("mute - end : result=<"+result+">");
        return result;
    }

    /**
     * Getting all muted users
     * @return a list of userId per server
     */
    public Hashtable<String,List<String>> getMuted(){
        logger.debug("getMuted - start");
        Hashtable<String,List<String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT serverid, userid FROM mute;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()){
                String serverId = rs.getString("serverid");
                String userId = rs.getString("userid");
                logger.debug("adding user : "+userId+" for server "+serverId);
                if (result.containsKey(serverId)){
                    result.get(serverId).add(userId);
                }else{
                    List<String> users = new ArrayList<>();
                    users.add(userId);
                    result.put(serverId,users);
                }
            }

        }catch (SQLException sqle){
            logger.error("Error while getting mutted users",sqle);
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
        logger.debug("getMuted - end : result size = "+result.size());
        return result;
    }

    /**
     * Shame a user
     * @param serverId : server on which the user is shamed
     * @param userId : user shamed
     * @return number of line added
     */
    public int shame(String serverId, String userId){
        logger.debug("shame - start : serverId=<"+serverId+"> - userId=<"+userId+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO shame (serverid,userid) values (?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error shaming user");
        }
        logger.debug("shame - end : result=<"+result+">");
        return result;
    }

    /**
     * Unshame a user
     * @param serverId : server on which unshame the user
     * @param userId : user to unshame
     * @return : number of lines deleted
     */
    public int unshame(String serverId, String userId){
        logger.debug("unshame - start : serverId=<"+serverId+"> - userId=<"+userId+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM shame WHERE serverid =? AND userid=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,userId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error unshaming user");
        }
        logger.debug("shame - end : result=<"+result+">");
        return result;
    }

    /**
     * Getting all muted users
     * @return a list of userId per server
     */
    public Hashtable<String,List<String>> getShamed(){
        logger.debug("getShamed - start");
        Hashtable<String,List<String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT serverid, userid FROM shame;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()){
                String serverId = rs.getString("serverid");
                String userId = rs.getString("userid");
                logger.debug("adding user : "+userId+" for server "+serverId);
                if (result.containsKey(serverId)){
                    result.get(serverId).add(userId);
                }else{
                    List<String> users = new ArrayList<>();
                    users.add(userId);
                    result.put(serverId,users);
                }
            }

        }catch (SQLException sqle){
            logger.error("Error while getting mutted users",sqle);
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
        logger.debug("getShamed - end : result size = "+result.size());
        return result;
    }



    /**
     * Slow a channel
     * @param channelId : channel slowed
     * @param delay : delay between two messages
     * @return number of line added
     */
    public int slowInsert(String channelId, int delay){
        logger.debug("slow - start : channelId=<"+channelId+"> - delau=<"+delay+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO slow (channelid,delay) values (?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,channelId);
            ps.setInt(2,delay);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error slow");
        }
        logger.debug("slow - end : result=<"+result+">");
        return result;
    }

    /**
     * Slow a channel
     * @param channelId : channel slowed
     * @param delay : dely between two messages
     * @return number of line added
     */
    public int slowUpdate(String channelId, int delay){
        logger.debug("slowUpdate - start : channelId=<"+channelId+"> - delau=<"+delay+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE slow SET delay=? WHERE channelid=?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1,delay);
            ps.setString(2,channelId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error slowUpdate");
        }
        logger.debug("slowUpdate - end : result=<"+result+">");
        return result;
    }

    /**
     * Unslow a channel
     * @param channelId : channel to unslow
     * @return number of line added
     */
    public int slowDelete(String channelId){
        logger.debug("slowDelete - start : channelId=<"+channelId+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM slow SET WHERE channelid=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,channelId);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error slowDelete");
        }
        logger.debug("slowDelete - end : result=<"+result+">");
        return result;
    }


    /**
     * Getting slow delays
     * @return a list channel and their slow delay
     */
    public Hashtable<String,Integer> getSlowDelays(){
        logger.debug("getSlowDelays - start");
        Hashtable<String,Integer> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT channelid, dely FROM slow;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()){
                String channelId = rs.getString("channelId");
                Integer delay = new Integer(rs.getInt("delay"));
                logger.debug("adding channel : "+channelId+" with delay "+delay.intValue());
                result.put(channelId,delay);
            }
        }catch (SQLException sqle){
            logger.error("Error while getting slow delays",sqle);
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
        logger.debug("getSlowDelays - end : result size = "+result.size());
        return result;
    }

}
