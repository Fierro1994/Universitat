package org.example.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPConfig {
    private static HikariDataSource dataSource;
    private static HikariConfig config = new HikariConfig();
    static {
        config.setJdbcUrl("jdbc:h2:~/test");
        config.setUsername("sa");
        config.setPassword("");
        config.setConnectionTimeout(50000);
        config.setMaximumPoolSize(100);
        dataSource = new HikariDataSource(config);
    }


}
