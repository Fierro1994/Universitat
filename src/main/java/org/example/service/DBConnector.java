package org.example.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private static final Logger logger = LoggerFactory.getLogger(DBConnector.class);
    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private final Integer maximumPoolSize;
    private static HikariDataSource dataSource;



    public DBConnector() {
        this.driver = getParematersFromFile().getProperty("db.driver");
        this.url = getParematersFromFile().getProperty("db.url");
        this.username = getParematersFromFile().getProperty("db.username");
        this.password = getParematersFromFile().getProperty("db.password");
        this.maximumPoolSize = Integer.valueOf(getParematersFromFile().getProperty("db.maximumPoolSize"));
    }

    public DBConnector(String driver, String url, String username, String password, Integer maximumPoolSize) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.maximumPoolSize = maximumPoolSize;
    }

    private HikariConfig getConfig() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maximumPoolSize);
        return config;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            HikariConfig config = getConfig();
            dataSource = new HikariDataSource(config);
        }
        return dataSource.getConnection();
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