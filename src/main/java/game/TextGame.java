package main.java.game;

import java.util.HashSet;
import java.util.Scanner;

import main.java.Board;

public class TextGame {
    private Scanner scan;
    private Board board;
    private int turn;

    public TextGame() {
        scan = new Scanner(System.in);
        board = new Board();
        board.show();

        turn = 0;
        setup();
        gameLoop();
    }

    /**
     * Run the game loop, prompting for input and performing updates
     */
    public void gameLoop() {
        int i;
        while (true) {
            board.show();
            // perform two sliding actions
            for (i = 0; i < 2; i++) {
                slideAction(turn, i + 1);
            }
            // perform one pushing action
            if (pushAction(turn)) {
                board.show();
                System.out.println("Player " + (turn + 1) + " wins!");
                scan.close();
                return;
            }
            // change turns
            turn = 1 - turn;
        }
    }

    /**
     * Get input and validate a sliding action
     * 
     * @param turn Turn indicator
     * @param num  Which sliding action (1|2) is occuring
     */
    public void slideAction(int turn, int num) {
        // get sliding action start
        System.out.println(
                "Player " + (turn + 1) + ", choose piece to slide (optional " + (num) + "/2)");
        int startPos = getInput(turn);

        // allow sliding action to be skipped
        if (startPos == -1) {
            board.show();
            return;
        }

        // compute valid destinations given this starting position
        HashSet<Integer> dests = GameUtils.findSlidingDests(board, startPos / 10, startPos % 10);

        // get sliding action destination
        int endPos = -1;
        System.out.println("Player " + (turn + 1) + ", choose destination");
        while (endPos == -1 || !dests.contains(endPos)) {
            endPos = getInput();

            // allow for an empty action
            if (startPos == endPos) {
                board.show();
                return;
            }
        }

        board.slide(startPos / 10, startPos % 10, endPos / 10, endPos % 10);
        board.show();
    }

    /**
     * Get input and validate a push action
     * 
     * @param turn Turn indicator
     * @return true if push resulted in a win, else false
     */
    public boolean pushAction(int turn) {
        int pos = -1;
        int row = -1, col = -1;
        char dir = ' ';
        while (true) {
            // prompt for piece to push
            System.out.println("Player " + (turn + 1) + ", choose a piece to push (required)");
            while (pos == -1)
                pos = getInput(turn);
            row = pos / 10;
            col = pos % 10;
            // prompt for push direction
            System.out.println("Player " + (turn + 1) + ", choose pushing direction (r|l|u|d)");
            dir = scan.nextLine().charAt(0);

            // validate push, and loop if invalid
            if (!GameUtils.isValidPush(board, row, col, dir)) {
                board.show();
                pos = -1;
                continue;
            }

            // perform push
            return board.push(row, col, dir);
        }
    }

    /**
     * Prompt user for a location on the board
     * 
     * @return Board position encoded as row * 10 + col
     */
    public int getInput() {
        String line;
        int row = -1, col = -1;
        while (!board.isValid(row, col)) {
            line = scan.nextLine();
            if (line.equals("")) {
                return -1;
            }
            try {
                row = line.charAt(0) - 'a';
                col = Integer.parseInt(line.substring(1)) - 1;
            } catch (Exception e) {
                row = -1;
                col = -1;
            }
        }
        return row * 10 + col;

    }

    /**
     * Prompt user for a location that they own
     * 
     * @param turn Turn indicator
     * @return Board position encoded as row * 10 + col
     */
    public int getInput(int turn) {
        String line;
        int row = -1, col = -1;
        while (!board.isValid(row, col) || !board.owns(row, col, turn)) {
            line = scan.nextLine();
            if (line.equals("")) {
                return -1;
            }
            try {
                row = line.charAt(0) - 'a';
                col = Integer.parseInt(line.substring(1)) - 1;
            } catch (Exception e) {
                row = -1;
                col = -1;
            }
        }
        return row * 10 + col;
    }

    /**
     * For convenience, skip setup step and use a default setup
     * 
     * @return true if setup was skipped, else false
     */
    public boolean skipSetup() {
        System.out.println("Use default setup? (y|n)");
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
        int pos = -1;
        int[] iter = new int[] {1, 1, 2, 2};
        for (idx = 0; idx < 4; idx++) {
            num = idx % 2 == 0 ? 2 : 3;
            piece = idx % 2 == 0 ? "circle" : "square";
            for (i = 0; i < num; i++) {
                System.out.println("Player " + iter[idx] + ", place " + piece + " pieces (" + i
                        + "/" + num + " placed)");
                while (pos == -1)
                    pos = getInput();
                board.setPiece(pos / 10, pos % 10, idx + 1);
                board.show();
            }
        }
    }
}
