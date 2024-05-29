package org.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private Properties databaseProperties = getDatabaseProperties();
    private String driver = databaseProperties.getProperty("db.driver");
    private String url = databaseProperties.getProperty("db.url");
    private String username = databaseProperties.getProperty("db.username");
    private String password = databaseProperties.getProperty("db.password");


    public Connection getConnection() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private static Properties getDatabaseProperties() {
        Properties properties = new Properties();
        InputStream inputStream = DBConnector.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}