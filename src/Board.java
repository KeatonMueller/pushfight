
public class Board {
    private final int LENGTH = 8;
    private final int HEIGHT = 4;
    private int[][] board;
    private int anchorRow, anchorCol;

    // Board values:
    // -1 : invalid
    // 0 : empty
    // 1 : p1 circle
    // 2 : p1 square
    // 3 : p2 circle
    // 4 : p2 square

    public Board() {
        // initialize board
        board = new int[HEIGHT][LENGTH];
        // configure irregular board shape by setting invalid spots
        board[0][0] = -1;
        board[0][1] = -1;
        board[0][LENGTH - 1] = -1;
        board[HEIGHT - 1][0] = -1;
        board[HEIGHT - 1][LENGTH - 1] = -1;
        board[HEIGHT - 1][LENGTH - 2] = -1;
        // anchor is initially not present
        anchorRow = -1;
        anchorCol = -1;
    }

    public void setPiece(int row, int col, int val) {
        if (!isValid(row, col) || board[row][col] != 0)
            return;
        board[row][col] = val;
    }

    /**
     * Check if a row and column is on the board
     * 
     * @param row The row to check
     * @param col The column to check
     * @return true if position specifies a location on the board, else false
     */
    public boolean isValid(int row, int col) {
        // check for out of bounds
        if (row < 0 || row >= HEIGHT || col < 0 || col >= LENGTH) {
            return false;
        }
        // check for the 6 irregularities in the board
        if ((row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == LENGTH - 1)
                || (row == HEIGHT - 1 && col == 0) || (row == HEIGHT - 1 && col == LENGTH - 1)
                || (row == HEIGHT - 1 && col == LENGTH - 2)) {
            return false;
        }
        return true;
    }

    public void slide(int oldRow, int oldCol, int newRow, int newCol) {
        board[newRow][newCol] = board[oldRow][oldCol];
        board[oldRow][oldCol] = 0;
    }

    public void push(int row, int col, char dir) {
        // initialize deltaRow & deltaCol based on pushing direction
        int deltaRow, deltaCol;
        switch (dir) {
            case 'r':
                deltaRow = 0;
                deltaCol = 1;
                break;
            case 'l':
                deltaRow = 0;
                deltaCol = -1;
                break;
            case 'u':
                deltaRow = -1;
                deltaCol = 0;
                break;
            case 'd':
                deltaRow = 1;
                deltaCol = 0;
                break;
            default:
                return;
        }
        // push the pieces
        anchorRow = row + deltaRow;
        anchorCol = col + deltaCol;
        int temp, prevPiece = 0;
        while (isValid(row, col)) {
            temp = board[row][col];
            board[row][col] = prevPiece;
            prevPiece = temp;
            row += deltaRow;
            col += deltaCol;
            if (prevPiece == 0) {
                return;
            }
        }
        System.out.println("Winner winner!");
    }

    public String getChar(int row, int col) {
        switch (board[row][col]) {
            case -1:
                return "  ";
            case 0:
                return ". ";
            case 1:
                return "c ";
            case 2:
                return "s ";
            case 3:
                return "C ";
            case 4:
                return "S ";
            default:
                return "? ";
        }
    }

    public void show() {
        System.out.println("      ---------");
        for (int row = 0; row < HEIGHT; row++) {
            System.out.print((char) ('a' + row) + " ");
            for (int col = 0; col < LENGTH; col++) {
                System.out.print(getChar(row, col));
            }
            System.out.println();
        }
        System.out.println("    ---------");
        System.out.print("  ");
        for (int col = 0; col < LENGTH; col++) {
            System.out.print(col + 1 + " ");
        }
        System.out.println();
    }
}
