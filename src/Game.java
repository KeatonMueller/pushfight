
import java.util.Scanner;

public class Game {
    private Scanner scan;
    private Board board;
    private int turn;

    public Game() {
        scan = new Scanner(System.in);
        board = new Board();
        board.show();

        turn = 0;
        setup();
        board.show();
        board.push(1, 5, 'l');
        board.show();
    }

    public int[] getInput() {
        String line;
        int row = -1, col = -1;
        while (!board.isValid(row, col)) {
            line = scan.nextLine();
            try {
                row = line.charAt(0) - 'a';
                col = Integer.parseInt(line.substring(1)) - 1;
            } catch (Exception e) {
                row = -1;
                col = -1;
            }
        }
        return new int[] {row, col};
    }

    /**
     * For convenience, skip setup step and use a default setup
     * 
     * @return true if setup was skipped, else false
     */
    public boolean skipSetup() {
        System.out.println("Use default? (y|n)");
        String line = scan.nextLine();
        if (line.equalsIgnoreCase("y")) {
            // set player 1's pieces
            board.setPiece(0, 3, 2);
            board.setPiece(1, 3, 1);
            board.setPiece(2, 3, 1);
            board.setPiece(3, 3, 2);
            board.setPiece(2, 2, 2);
            // set player 2's pieces
            board.setPiece(0, 4, 4);
            board.setPiece(1, 4, 3);
            board.setPiece(2, 4, 3);
            board.setPiece(3, 4, 4);
            board.setPiece(1, 5, 4);
            return true;
        }
        return false;
    }

    /**
     * Prompt user to layout their pieces
     */
    public void setup() {
        if (skipSetup())
            return;
        String piece;
        int i, idx, num;
        int[] pos;
        int[] iter = new int[] {1, 1, 2, 2};
        for (idx = 0; idx < 4; idx++) {
            num = idx % 2 == 0 ? 2 : 3;
            piece = idx % 2 == 0 ? "circle" : "square";
            for (i = 0; i < num; i++) {
                System.out.println("Player " + iter[idx] + ", place " + piece + " pieces (" + i
                        + "/" + num + " placed)");
                pos = getInput();
                board.setPiece(pos[0], pos[1], idx + 1);
                board.show();
            }
        }
    }
}
