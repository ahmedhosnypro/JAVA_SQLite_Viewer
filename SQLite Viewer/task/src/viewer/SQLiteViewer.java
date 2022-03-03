package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteViewer extends JFrame {
    final Dimension iconDimension = new Dimension(32, 32);

    //open file panel vars
    final JPanel filePanel = new JPanel();
    final FlowLayout filePanelFlowLayout = new FlowLayout();
    final ImageIcon openImageIcon = new ImageIcon("SQLite Viewer/task/resources/open.png");
    final JTextField fileNameTextField = new JTextField();
    final JButton openFileButton = new JButton(openImageIcon);

    //table vars
    ArrayList<String> queryTableColumns;
    ArrayList<ArrayList<Object>> queryTableData;
    TableModel dataBaseTableModel;
    JTable dataBaseTable;
    JScrollPane dbTableScrollPane;

    //data base viewer panel
    final JPanel dataBasePanel = new JPanel();

    ArrayList<String> tableNames;
    final JComboBox<String> tablesComboBox = new JComboBox<>();
    final JTextArea queryTextArea = new JTextArea();
    final JButton executeQueryButton = new JButton("Execute");

    final ActionListener openFileActionListener = e -> {
        getDataBaseTableNames();
        addTableNamesToComboBox();
    };
    final ActionListener tablesComboBoxActionListener = e -> generateSelectQueryForTable();
    final ActionListener executeQueryButtonActionListener = e -> {
        getQueryColumnsNames();
        createTableModelFromQuery();
        updateTableViewer();
    };


    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setTitle("SQLite Viewer");
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        addOpenFilePanel();
        addDataBasePanel();
        addTableViewer();

        setVisible(true);
    }

    private void addOpenFilePanel() {
        filePanel.setLayout(filePanelFlowLayout);
        openFileButton.setName("OpenFileButton");
        openFileButton.setMinimumSize(iconDimension);
        openFileButton.setMaximumSize(iconDimension);
        openFileButton.setPreferredSize(iconDimension);

        fileNameTextField.setName("FileNameTextField");
        final Dimension d = new Dimension(640, 32);
        fileNameTextField.setMinimumSize(d);
        fileNameTextField.setMaximumSize(d);
        fileNameTextField.setPreferredSize(d);

        filePanel.add(fileNameTextField);
        filePanel.add(openFileButton);

        openFileButton.addActionListener(openFileActionListener);

        add(filePanel, BorderLayout.NORTH);
    }

    private void addDataBasePanel() {
        tablesComboBox.setName("TablesComboBox");
        Dimension tablesComboBoxDimension = new Dimension(650, 32);
        tablesComboBox.setMinimumSize(tablesComboBoxDimension);
        tablesComboBox.setMaximumSize(tablesComboBoxDimension);
        tablesComboBox.setPreferredSize(tablesComboBoxDimension);

        queryTextArea.setName("QueryTextArea");
        Dimension queryTextAreaDimension = new Dimension(550, 200);
        queryTextArea.setMinimumSize(queryTextAreaDimension);
        queryTextArea.setMaximumSize(queryTextAreaDimension);
        queryTextArea.setPreferredSize(queryTextAreaDimension);
        queryTextArea.setEnabled(false);

        executeQueryButton.setName("ExecuteQueryButton");
        executeQueryButton.setEnabled(false);

        dataBasePanel.add(tablesComboBox);
        dataBasePanel.add(queryTextArea);
        dataBasePanel.add(executeQueryButton);

        executeQueryButton.addActionListener(executeQueryButtonActionListener);
        tablesComboBox.addActionListener(tablesComboBoxActionListener);

        add(dataBasePanel, BorderLayout.CENTER);
    }

    private void addTableViewer() {
        dataBaseTable = new JTable();
        dataBaseTable.setName("Table");
        dataBaseTable.setAutoCreateRowSorter(true);

        dbTableScrollPane = new JScrollPane(dataBaseTable);
        add(dbTableScrollPane, BorderLayout.SOUTH);
    }

    private void updateTableViewer() {
        dataBaseTable.setModel(dataBaseTableModel);
        dataBaseTable.updateUI();
        dbTableScrollPane.updateUI();
    }

    private void getDataBaseTableNames() {
        String url = "jdbc:sqlite:" + fileNameTextField.getText();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        queryTextArea.setEnabled(false);
        executeQueryButton.setEnabled(false);
        tableNames = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';")) {
                        while (resultSet.next()) {
                            tableNames.add(resultSet.getString("name"));
                        }
                        if (tableNames.size() == 0) {
                            JOptionPane.showMessageDialog(new Frame(), "File doesn't exist or empty database");
                        } else {
                            queryTextArea.setEnabled(true);
                            executeQueryButton.setEnabled(true);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getQueryColumnsNames() {
        String url = "jdbc:sqlite:" + fileNameTextField.getText();

        //get query from comboBox item
        String tableName = (String) tablesComboBox.getSelectedItem();
        String query = "PRAGMA table_info(" + tableName + ");";

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        queryTableColumns = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        queryTableColumns.add(resultSet.getString("name"));
                    }
                    if (queryTableColumns.size() == 0) {
                        JOptionPane.showMessageDialog(new Frame(), "ERROR Query");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTableNamesToComboBox() {
        tablesComboBox.removeAllItems();
        for (var str : tableNames) {
            tablesComboBox.addItem(str);
        }
        tablesComboBox.updateUI();
        generateSelectQueryForTable();
    }

    private void generateSelectQueryForTable() {
        if (tablesComboBox.getItemCount() > 0) {
            queryTextArea.setText("SELECT * FROM " + tablesComboBox.getSelectedItem() + ";");
        }
    }

    private void createTableModelFromQuery() {
        String url = "jdbc:sqlite:" + fileNameTextField.getText(); //database path
        String query = queryTextArea.getText();

        //set data base source
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        //process the query
        queryTableData = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    int colsNumber = queryTableColumns.size();
                    while (resultSet.next()) {
                        ArrayList<Object> tmp = new ArrayList<>();
                        for (int colsIndex = 1; colsIndex <= colsNumber; colsIndex++) {
                            tmp.add(resultSet.getString(colsIndex));
                        }
                        queryTableData.add(tmp);
                    }
                    if (queryTableData.size() == 0 && queryTableColumns.size() == 0) {
                        JOptionPane.showMessageDialog(new Frame(), "ERROR Query");
                    }
                    dataBaseTableModel = new DataBaseTableModel(queryTableColumns, queryTableData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
