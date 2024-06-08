package org.example.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyAppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("sdsd");
        ServletContextListener.super.contextInitialized(sce);
        CreateDBAndDTO createDBAndDTO = new CreateDBAndDTO();
        createDBAndDTO.createDataBase();
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}