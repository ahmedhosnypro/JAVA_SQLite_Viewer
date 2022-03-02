package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
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
    final ActionListener executeQueryActionListener = e -> addTableNamesToComboBox();
    final ActionListener tablesComboBoxActionListener = e -> generateSelectQueryForTable();


    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setTitle("SQLite Viewer");
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        addOpenFilePanel();
        addDataBasePanel();

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

        executeQueryButton.setName("ExecuteQueryButton");

        dataBasePanel.add(tablesComboBox);
        dataBasePanel.add(queryTextArea);
        dataBasePanel.add(executeQueryButton);

        executeQueryButton.addActionListener(executeQueryActionListener);
        tablesComboBox.addActionListener(tablesComboBoxActionListener);

        add(dataBasePanel, BorderLayout.CENTER);
    }

    private void getDataBaseTableNames() {
        String url = "jdbc:sqlite:" + fileNameTextField.getText();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        tableNames = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';")) {
                    while (resultSet.next()) {
                        tableNames.add(resultSet.getString("name"));
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
}
