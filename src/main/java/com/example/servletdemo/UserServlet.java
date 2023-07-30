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
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        List<User> userList = new ArrayList<>();

        // establishing connection
        try (Connection connection = DriverManager.getConnection(DEPLOY_URL, USERNAME, PASSWORD)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String sql = "SELECT * FROM users";

            // executing query
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSets = statement.executeQuery()) {

                while (resultSets.next()) {
                    userList.add(
                            new User(resultSets.getInt("id"),resultSets.getString("name"),
                                    resultSets.getString("surname"), resultSets.getInt("age"))
                    );
                }
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

        // writing response as json array
        try (PrintWriter out = resp.getWriter()) {
            out.print(MAPPER.writeValueAsString(userList));
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = new User(
                req.getParameter("name"),
                req.getParameter("surname"),
                Integer.parseInt(req.getParameter("age"))
        );

        try (Connection conn = DriverManager.getConnection(DEPLOY_URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String sql = "INSERT INTO users (name, surname, age) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getSurname());
                stmt.setInt(3, user.getAge());

                stmt.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }

        resp.sendRedirect("users/new");
    }
}

