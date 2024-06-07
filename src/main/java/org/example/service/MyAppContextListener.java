package org.example.service;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyAppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        CreateDBAndDTO createDBAndDTO = new CreateDBAndDTO();
        createDBAndDTO.createDataBase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}