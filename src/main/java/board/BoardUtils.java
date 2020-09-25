package main.java.board;

import static java.util.Map.entry;
import java.util.Map;

import main.java.game.GameUtils;

public class BoardUtils {
    /**
     * Hard-coded values of the strength of being in a certain position (very rough values at the
     * moment)
     */
    private static Map<Integer, Integer> boardValues = Map.ofEntries(entry(2, -10), entry(3, 3),
            entry(4, 5), entry(5, 3), entry(6, -10), entry(10, -10), entry(11, -10), entry(12, 1),
            entry(13, 10), entry(14, 10), entry(15, 7), entry(16, 0), entry(17, -10),
            entry(20, -10), entry(21, 0), entry(22, 7), entry(23, 10), entry(24, 10), entry(25, 1),
            entry(26, -10), entry(27, -10), entry(31, -10), entry(32, 3), entry(33, 5),
            entry(34, 3), entry(35, -10));

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
}
