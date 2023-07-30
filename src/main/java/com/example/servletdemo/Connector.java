package com.example.servletdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private static final String LOCAL_DEV_URL = "jdbc:postgresql://127.0.0.5:5432/users";
    private static final String DEPLOY_URL = "jdbc:postgresql://db:5432/users";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private Connector() {
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DEPLOY_URL, USERNAME, PASSWORD);
    }

}
