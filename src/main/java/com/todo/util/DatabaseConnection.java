package com.todo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import com.todo.model.Todo;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/todolist";
    private static final String USERNAME = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASS");


    public static Connection getDBConnection() throws SQLException{
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }
}
