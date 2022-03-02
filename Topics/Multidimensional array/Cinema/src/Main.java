import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int rows = scanner.nextInt();
        int cols = scanner.nextInt();
        int[][] seats = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                seats[i][j] = scanner.nextInt();
            }
        }

        int kSeats = scanner.nextInt();
        int fstRowWithSeat = 0;
        Outer:
        for (int i = 0; i < rows; i++) {
            int tmpCounter = 0;
            for (int j = 0; j < cols; j++) {
                if (seats[i][j] == 0) {
                    tmpCounter++;
                } else if (seats[i][j] == 1 && tmpCounter > 0) {
                    tmpCounter = 0;
                }
                if (tmpCounter == kSeats) {
                    fstRowWithSeat = i + 1;
                    break Outer;
                }
            }
        }

        System.out.println(fstRowWithSeat);
    }
}