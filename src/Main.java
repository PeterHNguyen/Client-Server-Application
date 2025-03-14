import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Main {
    private JFrame frame;
    private JPanel resultPanel;
    private JTextField usernameField, passwordField;
    private JTextArea sqlCommandArea, resultArea;
    private JPasswordField passwordfield;
    private JComboBox<String> dbPropertiesBox, userPropertiesBox;
    private JLabel connectionStatusLabel;
    private Connection connection;

    public Main() {
        // **Setup Main Frame**
        frame = new JFrame("SQL CLIENT APPLICATION - (MJL - CNT 4714 - SPRING 2025 - PROJECT 3)");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // **TOP PANEL - Holds Connection Details & SQL Command**
        JPanel topPanel = new JPanel(new BorderLayout());

        // **LEFT PANEL - Connection Details**
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Connection Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        dbPropertiesBox = new JComboBox<>(new String[]{
                "All project3.properties",
                "project3.properties",
                "bikedb.properties",
                "operationslog.properties"
        });
        userPropertiesBox = new JComboBox<>(new String[]{
                "root.properties",
                "client1.properties",
                "client2.properties",
                "theaccountant.properties"});
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        usernameField = new JTextField();
        passwordField =  new JPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(new JLabel("DB URL Properties:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(dbPropertiesBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        leftPanel.add(new JLabel("User Properties:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(userPropertiesBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        leftPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        leftPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        leftPanel.add(passwordField, gbc);

        // **NEW: Connection Status Label (Now Properly Positioned)**
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        connectionStatusLabel = new JLabel("Status: Not Connected", SwingConstants.CENTER);
        connectionStatusLabel.setOpaque(true);
        connectionStatusLabel.setBackground(Color.LIGHT_GRAY);
        connectionStatusLabel.setForeground(Color.RED);
        connectionStatusLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        connectionStatusLabel.setPreferredSize(new Dimension(500, 30)); // **FIXED SIZE**
        leftPanel.add(connectionStatusLabel, gbc);

        // **Buttons Below Connection Status**
        gbc.gridx = 0;
        gbc.gridy = 5;
        JPanel connectionButtons = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton connectButton = new JButton("Connect to Database");
        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.WHITE);
        JButton disconnectButton = new JButton("Disconnect From Database");

        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.WHITE);
        disconnectButton.setBackground(Color.RED);

        connectionButtons.add(connectButton);
        connectionButtons.add(disconnectButton);
        leftPanel.add(connectionButtons, gbc);

        // **RIGHT PANEL - SQL Command Area**
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Enter An SQL Command"));

        sqlCommandArea = new JTextArea(5, 30);
        rightPanel.add(new JScrollPane(sqlCommandArea), BorderLayout.CENTER);

        JPanel sqlCommandButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearSQLButton = new JButton("Clear SQL Command");
        JButton executeSQLButton = new JButton("Execute SQL Command");

        clearSQLButton.setBackground(Color.YELLOW);
        executeSQLButton.setBackground(Color.GREEN);

        sqlCommandButtonPanel.add(clearSQLButton);
        sqlCommandButtonPanel.add(executeSQLButton);
        rightPanel.add(sqlCommandButtonPanel, BorderLayout.SOUTH);

        // **Add Left & Right Panels to Top Panel**
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        // **BOTTOM PANEL - SQL Execution Results (Fixed Alignment)**
        resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("SQL Execution Result Window"));
        JTable emptyTable = new JTable();
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        frame.add(resultPanel, BorderLayout.CENTER);

        // **BOTTOM PANEL - Main Buttons**
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 1));
        JButton clearResultButton = new JButton("Clear Result Window");
        JButton closeButton = new JButton("Close Application");

        clearResultButton.setPreferredSize(new Dimension(220, 30));
        clearResultButton.setBackground(Color.YELLOW);
        clearResultButton.setForeground(Color.BLACK);
        closeButton.setPreferredSize(new Dimension(220, 30));
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.BLACK);

        bottomPanel.add(clearResultButton);
        bottomPanel.add(closeButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // **Button Actions**
        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectDatabase());
        executeSQLButton.addActionListener(e -> executeSQLCommand());
        clearSQLButton.addActionListener(e -> sqlCommandArea.setText(""));
        clearResultButton.addActionListener(e -> resultArea.setText(""));
        closeButton.addActionListener(e -> System.exit(0));
        

        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            Properties properties = new Properties();
            String selectedProperties = (String) userPropertiesBox.getSelectedItem();
            InputStream input = getClass().getClassLoader().getResourceAsStream(selectedProperties);

            if (input == null) {
                JOptionPane.showMessageDialog(frame, "Properties file not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            properties.load(input);
            String expectedUsername = properties.getProperty("username");
            String expectedPassword = properties.getProperty("password");

            // **Get User-Entered Credentials**
            String enteredUsername = usernameField.getText().trim();
            String enteredPassword = passwordField.getText().trim();

            // **Compare Entered Credentials with Properties File**
            if (!enteredUsername.equals(expectedUsername) || !enteredPassword.equals(expectedPassword)) {
                connectionStatusLabel.setText("NOT CONNECTED - User Credentials Do Not Match Properties File!");
                connectionStatusLabel.setBackground(Color.RED);
                connectionStatusLabel.setForeground(Color.WHITE);
                JOptionPane.showMessageDialog(frame, "User credentials do not match the selected properties file!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // **If Credentials Match, Connect to Database**
            String url = properties.getProperty("url");
            connection = DriverManager.getConnection(url, enteredUsername, enteredPassword);

            connectionStatusLabel.setText("CONNECTED TO: " + url);
            connectionStatusLabel.setBackground(Color.GREEN);
            connectionStatusLabel.setForeground(Color.BLACK);

            JOptionPane.showMessageDialog(frame, "Connected to database: " + url);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void disconnectDatabase() {
        try {
            if (connection != null) {
                connection.close();
                usernameField.setText("");
                passwordField.setText("");

                connectionStatusLabel.setText("Status: Not Connected");
                connectionStatusLabel.setBackground(Color.LIGHT_GRAY);
                connectionStatusLabel.setForeground(Color.RED);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error disconnecting: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeSQLCommand() {
        if (connection == null) {
            JOptionPane.showMessageDialog(frame, "Not connected to a database!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = sqlCommandArea.getText().trim();
            if (sql.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "SQL Command cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Statement stmt = connection.createStatement();
            boolean isQuery = stmt.execute(sql);

            if (isQuery) {
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // **Get Column Names**
                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }

                // **Get Data from Result Set**
                java.util.List<String[]> rowData = new java.util.ArrayList<>();
                while (rs.next()) {
                    String[] row = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getString(i);
                    }
                    rowData.add(row);
                }

                // **Convert List to 2D Array**
                String[][] data = rowData.toArray(new String[0][]);

                // **Create JTable & Update UI**
                JTable resultTable = new JTable(data, columnNames);
                resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                resultTable.setFillsViewportHeight(true);

                JScrollPane scrollPane = new JScrollPane(resultTable);

                // **Safely update the UI**
                SwingUtilities.invokeLater(() -> {
                    resultPanel.removeAll();
                    resultPanel.add(scrollPane, BorderLayout.CENTER);
                    resultPanel.revalidate();
                    resultPanel.repaint();
                });

            } else {
                int updateCount = stmt.getUpdateCount();
                JOptionPane.showMessageDialog(frame, "Update successful. Rows affected: " + updateCount);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error executing SQL: " + e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }






    public static void main(String[] args) {
        new Main();
    }
}
