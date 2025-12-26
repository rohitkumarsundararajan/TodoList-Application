package com.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;

public class TodoAppDAO {
    private static final String GET_TODOS = "SELECT * FROM todos";
    private static final String ADD_TODO = "INSERT INTO todos (title,description,completed,created_At,updated_At) VALUES (?,?,?,?,?)";

    public List<Todo> getAllTodos() throws SQLException{
        List<Todo> todos = new ArrayList<>();
        
        try(Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_TODOS);)
            {

                ResultSet rs = stmt.executeQuery();
                while(rs.next())
                {
                    Todo todo = new Todo();
                    
                    todo.setId(rs.getInt(1));
                    todo.setTitle(rs.getString("title"));
                    todo.setDescription(rs.getString("description"));
                    todo.setCompleted(rs.getBoolean("completed"));
                    todo.setCreatedAt(rs.getTimestamp("created_At").toLocalDateTime());
                    todo.setUpdatedAt(rs.getTimestamp("updated_At").toLocalDateTime());
                    todos.add(todo);
                }
            }
            return todos;
        }

    public void addTodo(Todo todo) throws SQLException {
    try (Connection conn = DatabaseConnection.getDBConnection();
         PreparedStatement stmt =
            conn.prepareStatement(ADD_TODO, PreparedStatement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, todo.getTitle());
        stmt.setString(2, todo.getDescription());
        stmt.setBoolean(3, todo.isCompleted());
        stmt.setObject(4, todo.getCreatedAt());
        stmt.setObject(5, todo.getUpdatedAt());

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected == 0) {
            throw new SQLException("Adding todo failed, no rows affected.");
        }

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                todo.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Adding todo failed, no ID obtained.");
            }
        }
        }
    }


}
