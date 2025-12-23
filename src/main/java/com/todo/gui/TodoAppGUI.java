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

