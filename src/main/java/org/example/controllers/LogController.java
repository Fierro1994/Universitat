package org.example.controllers;
import org.example.dto.StudentDto;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@WebServlet(name = "LogsServlet",
        loadOnStartup = 1,
        urlPatterns = {
                "/logCatalina",
                "/logApp",
        })
public class LogController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String pathParam = request.getServletPath();
        switch (pathParam) {
            case "/logCatalina":
                displayLogsCatalina(response);
                break;
            case "/logApp":
                displayLogsApp(response);
                break;
        }
    }

    private void displayLogsCatalina(HttpServletResponse response) throws IOException {
        String logDirectoryPath = "/opt/tomcat/logs/";
        String logFileName = "catalina." + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
        String logFilePath = logDirectoryPath + logFileName;
        StringBuilder logContent = new StringBuilder();
        File logFile = new File(logFilePath);
        if (logFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    logContent.append(line).append("\n");
                }
            }
        } else {
            logContent.append("Log file not found for today.");
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.print("<html><body><pre>" + logContent.toString() + "</pre></body></html>");
        out.write("<html><body><pre>" + logContent.toString() + "</pre></body></html>");
        out.flush();
        out.close();
    }

    private void displayLogsApp(HttpServletResponse response) throws IOException {
        String logDirectoryPath = "/opt/tomcat/logs/";
        String logFileName = "localhost." + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
        String logFilePath = logDirectoryPath + logFileName;
        StringBuilder logContent = new StringBuilder();
        File logFile = new File(logFilePath);
        if (logFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    logContent.append(line).append("\n");
                }
            }
        } else {
            logContent.append("Log file not found for today.");
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.print("<html><body><pre>" + logContent.toString() + "</pre></body></html>");
        out.write("<html><body><pre>" + logContent.toString() + "</pre></body></html>");
        out.flush();
        out.close();
    }
}
