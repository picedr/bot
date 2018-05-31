package fr.picedr.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public abstract class DAO {

    private Logger logger = LoggerFactory.getLogger(DAO.class);

    protected Connection conn;

    public DAO(){
        try {
            this.conn = ConnectionFactory.getConnection();
            logger.debug("Connected to database");
        } catch (Exception e){
            logger.error("Init DAO",e);
        }
    }



}
