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
    private static final String UPDATE_TODO = "UPDATE todos SET title=?,description=?,completed=?,updated_At=? WHERE id=?";
    private static final String DELETE_TODO = "DELETE FROM todos WHERE id=?";
    private static final String GET_TODOS_BY_COMPLETION_STATUS = "SELECT * FROM todos WHERE completed=?";

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
    public void updateTodo(Todo todo) throws SQLException {
        int id = todo.getId();
        String title = todo.getTitle();
        String description = todo.getDescription();
        boolean isCompleted = todo.isCompleted();
        try (Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setBoolean(3, isCompleted);
            stmt.setObject(4, todo.getUpdatedAt());
            stmt.setInt(5, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Updating todo failed, no rows affected.");
            }
        }
    }

    public void deleteTodo(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_TODO)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Deleting todo failed, no rows affected.");
            }
        }
    }

    public List<Todo> getTodosByCompletionStatus(boolean b) throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_TODOS_BY_COMPLETION_STATUS)) {
            stmt.setBoolean(1, b);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Todo todo = new Todo();
                todo.setId(rs.getInt("id"));
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
}
