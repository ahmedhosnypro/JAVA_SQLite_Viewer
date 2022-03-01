package viewer;

import javax.swing.*;
import java.awt.*;

public class SQLiteViewer extends JFrame {
    final Dimension iconDimension = new Dimension(32, 32);
    //open file panel vars
    final JPanel filePanel = new JPanel();
    final FlowLayout filePanelFlowLayout = new FlowLayout();
    final ImageIcon openImageIcon = new ImageIcon("SQLite Viewer/task/resources/open.png");
    final JTextField fileNameTextField = new JTextField();
    final JButton openFileButton = new JButton(openImageIcon);


    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setTitle("SQLite Viewer");
        setResizable(false);
        setLocationRelativeTo(null);
        addOpenFilePanel();

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
        add(filePanel);
    }
}
