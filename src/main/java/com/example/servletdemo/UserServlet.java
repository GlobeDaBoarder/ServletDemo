package com.example.servletdemo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "userServlet", value = "/users")
public class UserServlet extends HttpServlet {

    // This map will act as a very basic database
    private Map<String, User> users = new HashMap<>();

    // Handle HTTP GET requests (Get User)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        // Iterate over all users and print their information.
        for (User user : users.values()) {
            out.println("User Name: " + user.getName());
            out.println("Surname: " + user.getSurname());
            out.println("Age: " + user.getAge());
            out.println("-----------------------------");
        }

        out.close();
    }


    // Handle HTTP POST requests (Create User)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        int age = Integer.parseInt(req.getParameter("age"));

        User user = new User(name, surname, age);

        // Save the user in the map
        users.put(name, user);

        PrintWriter out = resp.getWriter();
        out.println("User created with name: " + name);
        out.close();
    }
}

