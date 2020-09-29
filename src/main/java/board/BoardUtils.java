package main.java.board;

import java.util.HashMap;
import java.util.Map;

import main.java.game.GameUtils;

public class BoardUtils {
    /**
     * Hard-coded values of the strength of being in a certain position (very rough values at the
     * moment)
     */
    private static Map<Integer, Integer> boardValues;
    static {
        boardValues = new HashMap<>();
        boardValues.put(2, -10); // a3
        boardValues.put(3, 3); // a4
        boardValues.put(4, 5); // a5
        boardValues.put(5, 3); // a6
        boardValues.put(6, -10); // a7
        boardValues.put(10, -10); // b1
        boardValues.put(11, -10); // b2
        boardValues.put(12, 1); // b3
        boardValues.put(13, 10); // b4
        boardValues.put(14, 10); // b5
        boardValues.put(15, 7); // b6
        boardValues.put(16, 0); // b7
        boardValues.put(17, -10); // b8
        boardValues.put(20, -10); // c1
        boardValues.put(21, 0); // c2
        boardValues.put(22, 7); // c3
        boardValues.put(23, 10); // c4
        boardValues.put(24, 10); // c5
        boardValues.put(25, 1); // c6
        boardValues.put(26, -10); // c7
        boardValues.put(27, -10); // c8
        boardValues.put(31, -10); // d2
        boardValues.put(32, 3); // d3
        boardValues.put(33, 5); // d4
        boardValues.put(34, 3); // d5
        boardValues.put(35, -10); // d6
    }

    private static double squareWeight = 1;
    private static double circleWeight = 2;
    private static double p1MWeight = 1;
    private static double p2MWeight = -1;
    private static double p1PWeight = 1;
    private static double p2PWeight = -1;

    /**
     * Evaluate the given board state
     * 
     * @param board The board state to evaluate
     * @return The heuristic evalution. Higher values are better for p1/worse for p2
     */
    public static double heuristic(Board board) {
        double h = 0;

        // mobility
        int p1Mobility = 0;
        int p2Mobility = 0;
        // strength of piece positions
        int p1Position = 0;
        int p2Position = 0;
        // number of pieces
        int p1Pieces = 0;
        int p2Pieces = 0;
        double weight;
        for (int row = 0; row < GameUtils.HEIGHT; row++) {
            for (int col = 0; col < GameUtils.LENGTH; col++) {
                if (!board.isValid(row, col) || board.isEmpty(row, col))
                    continue;
                weight = board.isSquare(row, col) ? squareWeight : circleWeight;
                if (board.owns(row, col, 0)) {
                    p1Position += weight * boardValues.get(row * 10 + col);
                    p1Mobility += GameUtils.findSlideDests(board, row, col).size();
                    p1Pieces++;
                } else {
                    p2Position += weight * boardValues.get(row * 10 + col);
                    p2Mobility += GameUtils.findSlideDests(board, row, col).size();
                    p2Pieces++;
                }
            }
        }
        // a player missing a piece is the ultimate bad position
        if (p1Pieces != 5) {
            return -1000000000000.0;
        }
        if (p2Pieces != 5) {
            return 1000000000000.0;
        }
        // weight the components of the heuristic
        h += p1MWeight * p1Mobility;
        h += p2MWeight * p2Mobility;
        h += p1PWeight * p1Position;
        h += p2PWeight * p2Position;

        return h;
    }

    /**
     * For convenience, skip setup step and use a default setup
     * 
     */
    public static void skipSetup(Board board) {
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
    }
}
