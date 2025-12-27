package com.todo.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.todo.dao.TodoAppDAO;
import com.todo.model.Todo;

import java.util.List;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TodoAppGUI extends JFrame{
    private TodoAppDAO todoDao;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JComboBox filterComboBox;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton addButton;
    private JButton updateButton;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    
    public TodoAppGUI(){
        this.todoDao = new TodoAppDAO();
        initializeComponents();
        setupLayout();
        loadTodos();
        setVisible(true);
        setupEventListener();
    }

    public void initializeComponents(){
        setTitle("TodoList");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon(TodoAppGUI.class.getResource("/images/todos.png"));
        setIconImage(img.getImage());

        titleField = new JTextField(25);
        descriptionArea = new JTextArea(5,25);
        completedCheckBox = new JCheckBox("Completed");
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        refreshButton = new JButton("Refresh");
        deleteButton  = new JButton("Delete");
        
        String[] filterOptions = {"All","Completed","Pending"};
        filterComboBox = new JComboBox<>(filterOptions);
        
        String[] columnName = {"Id","Title","Description","Completed","CreatedAt","UpdatedAt"};
        tableModel = new DefaultTableModel(columnName,0){
            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        todoTable = new JTable(tableModel);
    }
    
    public void setupLayout(){
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("My Todo List", SwingConstants.CENTER);
        heading.setFont(new Font("Arial",Font.BOLD, 20));
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel titleLable = new JLabel("Title:");
        inputPanel.add(titleLable,gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField,gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"),gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descriptionArea),gbc);
        gbc.gridy = 2;
        inputPanel.add(completedCheckBox,gbc);
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(inputPanel,BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gb = new GridBagConstraints();
        gb.insets = new Insets(5, 5, 5, 5); // top, left, bottom, right

        gb.gridx = 0;
        buttonPanel.add(addButton, gb);
        gb.gridx = 1;
        buttonPanel.add(updateButton, gb);
        gb.gridx = 2;
        buttonPanel.add(refreshButton, gb);
        gb.gridx = 3;
        buttonPanel.add(deleteButton, gb);

        topPanel.add(buttonPanel,BorderLayout.SOUTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);
        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BorderLayout());
        northContainer.add(filterPanel, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);


        JScrollPane tabJScrollPane = new JScrollPane(todoTable);
        add(tabJScrollPane,BorderLayout.CENTER);
    }


    private void setupEventListener(){
        addButton.addActionListener(
            (e) -> {
                addTodo();
            });
        
        updateButton.addActionListener(
            (e) -> {
                updateTodo();
            });
        
        refreshButton.addActionListener(
            (e) -> {
                tableModel.setRowCount(0);
                loadTodos();
            });

        deleteButton.addActionListener(
            (e) -> {
                deleteTodo();
            });

        filterComboBox.addActionListener(
            (e) -> {
                String selectedFilter = (String) filterComboBox.getSelectedItem();
                tableModel.setRowCount(0);
                if(selectedFilter.equals("All")){
                    loadTodos();
                }else if(selectedFilter.equals("Completed")){
                    loadFilteredTodos(true);
                }else{
                    loadFilteredTodos(false);
                }
            });

        todoTable.getSelectionModel().addListSelectionListener(
            (e) -> {
                int selectedRow = todoTable.getSelectedRow();
                if(selectedRow >= 0){
                    String title = (String) tableModel.getValueAt(selectedRow, 1);
                    String description = (String) tableModel.getValueAt(selectedRow, 2);
                    boolean isCompleted = (boolean) tableModel.getValueAt(selectedRow, 3);

                    titleField.setText(title);
                    descriptionArea.setText(description);
                    completedCheckBox.setSelected(isCompleted);
                }
            });
    }

    private void addTodo(){
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean isCompleted = completedCheckBox.isSelected();

        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Todo newTodo = new Todo();
        newTodo.setTitle(title);
        newTodo.setDescription(description);
        newTodo.setCompleted(isCompleted);

        try{
            todoDao.addTodo(newTodo);
            JOptionPane.showMessageDialog(this, "Todo added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
            loadTodos();
            titleField.setText("");
            descriptionArea.setText("");
            completedCheckBox.setSelected(false);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error adding todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTodo(){
        Todo updatedTodo = new Todo();
        int selectedRow = todoTable.getSelectedRow();
        if(selectedRow < 0){
            JOptionPane.showMessageDialog(this, "Please select a todo to update", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int tableId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String tableTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String tableDescription = (String) tableModel.getValueAt(selectedRow, 2);
        boolean tableCompleted = (Boolean) tableModel.getValueAt(selectedRow, 3);
        LocalDateTime tableCreatedAt = (LocalDateTime) tableModel.getValueAt(selectedRow, 4);
        LocalDateTime tableUpdatedAt = LocalDateTime.now();

        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean isCompleted = completedCheckBox.isSelected();

        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title cannot be empty", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updatedTodo.setId(tableId);
        updatedTodo.setTitle(title);
        updatedTodo.setDescription(description);
        updatedTodo.setCompleted(isCompleted);
        updatedTodo.setCreatedAt(tableCreatedAt);
        updatedTodo.setUpdatedAt(tableUpdatedAt);

        try{
            todoDao.updateTodo(updatedTodo);
            JOptionPane.showMessageDialog(this, "Todo updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            tableModel.setRowCount(0);
            loadTodos();
            titleField.setText("");
            descriptionArea.setText("");
            completedCheckBox.setSelected(false);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error updating todo: " + e.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTodo(){

    }

    private void loadFilteredTodos(boolean isCompleted){

    }

    private void loadTodos(){
        List<Todo> todos;
        try{
           todos = todoDao.getAllTodos();
           for(Todo todo : todos){
               Object[] rowData = {
                   todo.getId(),
                   todo.getTitle(),
                   todo.getDescription(),
                   todo.isCompleted(),
                   todo.getCreatedAt(),
                   todo.getUpdatedAt(),
               };
               tableModel.addRow(rowData);
           }
        }catch(SQLException e){
           JOptionPane.showMessageDialog(this, "Errors fetching todos : " + e.getMessage(), "Error",
           JOptionPane.ERROR_MESSAGE);
        }
   }
}

