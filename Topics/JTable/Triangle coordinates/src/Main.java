import javax.swing.table.DefaultTableModel;
import java.util.Scanner;

class TriangleTable {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Object[][] data = new Object[3][2];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                data[i][j] = scanner.nextInt();
            }
        }

        String[] columns = {"X", "Y"};

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);
        for (Object[] row : data) {
            model.addRow(row);
        }

        // do not remove the code below
//        TableModelTest.test(model);
    }
}