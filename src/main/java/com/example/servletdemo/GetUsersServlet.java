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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "getUserServlet", value = "/users-json")
public class GetUsersServlet extends HttpServlet {
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
        try (Connection connection = Connector.getConnection()) {
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
}

