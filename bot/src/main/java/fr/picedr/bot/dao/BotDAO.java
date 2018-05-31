package fr.picedr.bot.dao;


import fr.picedr.bot.exception.DataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BotDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(BotDAO.class);

    public BotDAO(){
        super();
    }

    public String getBotKey() throws DataNotFoundException{
        String result = "";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT value FROM confBot WHERE name='botId';";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()){
                result = rs.getString("value");
            }else{
                throw new DataNotFoundException();
            }
        }catch (SQLException sqle){
            logger.error("Error getting bot Key",sqle);
            throw new DataNotFoundException();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting bot Key",sqle);
            }
        }
        return result;
    }

    public Hashtable<String,List<String>> getRoles(){
        Hashtable<String,List<String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            String query = "SELECT userid,role FROM botroles;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                String role = rs.getString("role");
                String userId = rs.getString("userid");

                if(result.containsKey(role)){
                    result.get(role).add(userId);
                }else {
                    List<String> users = new ArrayList<>();
                    users.add(userId);
                    result.put(role,users);
                }
            }
        }catch (SQLException sqle){
            logger.error("Error getting roles",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting bot Key",sqle);
            }
        }

        return result;
    }

    public Hashtable<String,Hashtable<String,String>> getServersConf(){
        Hashtable<String,Hashtable<String,String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            String query = "SELECT serverid,name,value FROM confserver;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                String serverId = rs.getString("serverid");
                String name = rs.getString("name");
                String value = rs.getString("value");

                if(result.containsKey(serverId)){
                    result.get(serverId).put(name,value);
                }else {
                    Hashtable<String,String> conf = new Hashtable<>();
                    conf.put(name,value);
                    result.put(serverId,conf);
                }
            }
        }catch (SQLException sqle){
            logger.error("Error getting serverConf",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting server conf",sqle);
            }
        }

        return result;
    }

    public int addConf(String serverId, String name, String value){
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO confserver (serverid,name,value) values (?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,name);
            ps.setString(3,value);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error adding a server conf");
        }
        return result;
    }

    public Hashtable<String,Hashtable<String,String>> getServersServices(){
        Hashtable<String,Hashtable<String,String>> result = new Hashtable<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            String query = "SELECT serverid,servicename,state FROM services;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                String serverId = rs.getString("serverid");
                String serviceName = rs.getString("servicename");
                String state = rs.getString("state");

                if(result.containsKey(serverId)){
                    result.get(serverId).put(serviceName,state);
                }else {
                    Hashtable<String,String> conf = new Hashtable<>();
                    conf.put(serviceName,state);
                    result.put(serverId,conf);
                }
            }
        }catch (SQLException sqle){
            logger.error("Error getting services",sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle){
                logger.error("Error getting services",sqle);
            }
        }

        return result;
    }

    public int addService(String serverId, String name, String state){
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO services (serverid,servicename,state) values (?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1,serverId);
            ps.setString(2,name);
            ps.setString(3,state);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error adding a server service");
        }
        return result;
    }

    public int updateService(String serverId, String name, String state){
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE services SET state = ? WHERE serverid = ? AND servicename = ?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,state);
            ps.setString(2,serverId);
            ps.setString(3,name);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error updating a server service");
        }
        return result;
    }

}
