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

    /**
     * Getting bot Key to connect to discord
     * @return the Key
     * @throws DataNotFoundException
     */
    public String getBotKey() throws DataNotFoundException{
        logger.debug("getBotKey - start");
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
        logger.debug("getBot key - end : result = <"+result+">");
        return result;
    }

    /**
     * Getting roles linked to the bot administration
     * @return A map of users and list of roles they have
     */
    public Hashtable<String,List<String>> getRoles(){
        logger.debug("getRoles - start");
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
        logger.debug("getRoles - end  : result = <"+result.toString()+">");
        return result;
    }

    /**
     * Getting servers configuration
     * @return a map of servers and configuration linked to each server
     */
    public Hashtable<String,Hashtable<String,String>> getServersConf(){
        logger.debug("getServerConf - start");
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
        logger.debug("getServerConf - end : result = <"+result.toString()+">");
        return result;
    }

    /**
     * Add a new conf item on a server
     * @param serverId : server to add the conf item to
     * @param name : name of the conf item
     * @param value :value of the conf item
     * @return : number of line inserted
     */
    public int addConf(String serverId, String name, String value){
        logger.debug("addConf - start : serverId = <"+serverId+"> - name = <"+name+"> - value = <"+value+">");
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
        logger.debug("addConf - end : result=<"+result+">");
        return result;
    }

    /**
     * update a conf item for a server
     * @param serverId : server to update the conf item for
     * @param name : name of the conf item
     * @param value : value of the conf item
     * @return number of lines updated
     */
    public int updateConf(String serverId, String name, String value){
        logger.debug("updateConf - start : serverId=<"+serverId+"> - name=<"+name+"> - value=<"+value+">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE confserver SET value = ? WHERE serverid = ? AND name = ?;";
            ps = conn.prepareStatement(query);
            ps.setString(1,value);
            ps.setString(2,serverId);
            ps.setString(3,name);

            result = ps.executeUpdate();

        }catch (SQLException sqle){
            logger.error("Error updating a server service");
        }
        logger.debug("updateService - end : result = <"+result+">");
        return result;
    }

    /**
     * getting services and their state for all servers
     * @return a map of servers and their services states
     */
    public Hashtable<String,Hashtable<String,String>> getServersServices(){
        logger.debug("getServerServices - start");
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
        logger.debug("getServersServices - end : result=<"+result.toString()+">");
        return result;
    }

    /**
     * Adding a new service for a server
     * @param serverId : server to add the service for
     * @param name : name of the service
     * @param state : state of the service
     * @return number of lines inserted
     */
    public int addService(String serverId, String name, String state){
        logger.debug("addService - start : serverId=<"+serverId+"> - name=<"+name+"> - state=<"+state+">");
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
        logger.debug("addService - end : result=<"+result+">");
        return result;
    }

    /**
     * Update a service state for a server
     * @param serverId : server to update the service for
     * @param name : name of the service
     * @param state : state of the service
     * @return number of lines updated
     */
    public int updateService(String serverId, String name, String state){
        logger.debug("UpdateService - start : serverId=<"+serverId+"> - name=<"+name+"> - state=<"+state+">");
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
        logger.debug("updateService - end : result = <"+result+">");
        return result;
    }


}
