package com.example.servletdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "userServlet", value = "/users")
public class UserServlet extends HttpServlet {
    private static final String LOCAL_DEV_URL = "jdbc:postgresql://127.0.0.5:5432/users";
    private static final String DEPLOY_URL = "jdbc:postgresql://db:5432/users";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        ObjectMapper mapper = new ObjectMapper();

        List<User> userList = new ArrayList<>();

        // Get users from the database
        try (Connection conn = DriverManager.getConnection(DEPLOY_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM users";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    User user = new User(rs.getString("name"), rs.getString("surname"),
                            rs.getInt("age"));
                    userList.add(user);
                }
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

        out.print(mapper.writeValueAsString(userList));

        out.flush();
        out.close();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        int age = Integer.parseInt(req.getParameter("age"));

        User user = new User(name, surname, age);

        // Save the user in the database
        try (Connection conn = DriverManager.getConnection(LOCAL_DEV_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO users (name, surname, age) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getSurname());
                stmt.setInt(3, user.getAge());

                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

        resp.sendRedirect("users/new");
    }
}

