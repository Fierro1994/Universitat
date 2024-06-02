package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private static final Logger logger = LoggerFactory.getLogger(DBConnector.class);
    private final String driver;
    private final String url;
    private final String username;
    private final String password;

    public DBConnector() {
        this.driver = getParematersFromFile().getProperty("db.driver");
        this.url = getParematersFromFile().getProperty("db.url");
        this.username = getParematersFromFile().getProperty("db.username");
        this.password = getParematersFromFile().getProperty("db.password");
    }

    public DBConnector(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() {

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return connection;
    }

    private Properties getParematersFromFile() {
        Properties properties = new Properties();
        InputStream inputStream = DBConnector.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return properties;
    }
}