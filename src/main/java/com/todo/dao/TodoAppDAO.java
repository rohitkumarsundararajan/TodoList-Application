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


}
