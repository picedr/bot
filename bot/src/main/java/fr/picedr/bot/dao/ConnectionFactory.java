package fr.picedr.bot.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static Connection conn = null;

    public static Connection getConnection() throws ClassNotFoundException,SQLException {

        if (conn == null || conn.isClosed()) {

            Class.forName("org.postgresql.Driver");

            String url = System.getProperty("db.url");
            String user = System.getProperty("db.user");
            String pwd = System.getProperty("db.pwd");

            conn = DriverManager.getConnection(url, user, pwd);

        }


        return conn;
    }

}
