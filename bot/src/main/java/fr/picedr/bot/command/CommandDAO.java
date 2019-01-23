package fr.picedr.bot.command;

import fr.picedr.bot.command.bean.Command;
import fr.picedr.bot.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class CommandDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(CommandDAO.class);

    /**
     * Get alla commands
      * @return list of commands
     */
    List<Command> getCommands(){
        logger.debug("getCommands -start");
        List<Command> result = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id,serverid,name,value,admin FROM commands;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String serverId = rs.getString("serverid");
                String name = rs.getString("name");
                String value = rs.getString("value");
                boolean isAdmin = rs.getBoolean("admin");
                logger.debug("Adding : id=<"+id+"> - serverId=<"+serverId+"> - name=<"+name+"> - value=<"+value+"> - isAdmin=<"+isAdmin+">");
                Command command = new Command(id,serverId,name,value,isAdmin);
                result.add(command);
            }
        }catch (SQLException sqle){
            logger.error("Error getting commands",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting commands",sqle);
            }
        }
        logger.debug("getCommands - End");
        return result;
    }

    /**
     * Remove a command
     * @param serverId : server the command belongs to
     * @param id : id of the command
     */
    int remove(String serverId, int id){
        logger.debug("remove - start : serverId=<"+serverId+"> - id=<"+id+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM commands WHERE serverid =? AND id=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setInt(2,id);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error removing command");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("remove - end : result=<"+result+">");
        return result;
    }

    int add(String serverId, String name, String value){//}, boolean admin){
        logger.debug("add - start : serverid=<"+serverId+"> - name=<"+name+"> - value=<"+value+">");// - admin=<"+admin+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO commands (serverid,name,value,admin) values (?,?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,name);
            ps.setString(3,value);
            ps.setBoolean(4,false);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error adding command",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle){
                logger.error("Error while closing ps or rs",sqle);
            }
        }
        logger.debug("add - end : result=<"+result+">");
        return result;
    }


}
