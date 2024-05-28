package org.example.controllers;

import org.example.Dao.StudentDao;
import org.example.models.Student;
import org.example.service.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@WebServlet(urlPatterns = {"/index", ""})
public class MainPageController extends HttpServlet {
    private StudentDao studentDao;
    private DBConnector dbConnector;

    @Override
    public void init() {
        try {
            studentDao = new StudentDao(dbConnector.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<Student> studentList = studentDao.getAll();
        req.setAttribute("students", studentList);

        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);

    }
}