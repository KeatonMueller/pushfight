package main.java.board;

import static java.util.Map.entry;
import java.util.Map;

import main.java.game.GameUtils;

public class BoardUtils {
    Map<Integer, Integer> boardValues = Map.ofEntries(entry(13, 5), entry(23, 5));

    /**
     * Evaluate the given board state
     * 
     * @param board The board state to evaluate
     * @return The heuristic evalution. Higher values are better for p1/worse for p2
     */
    public static double heuristic(Board board) {
        double h = 0;

        int p1Mobility = 0;
        for (int pos : board.getPieceLocs(0)) {
            p1Mobility += GameUtils.findSlideDests(board, pos / 10, pos % 10).size();
        }

        h += (1) * p1Mobility;

        int p2Mobility = 0;
        for (int pos : board.getPieceLocs(1)) {
            p2Mobility += GameUtils.findSlideDests(board, pos / 10, pos % 10).size();
        }

        h += (-1) * p2Mobility;

        return h;
    }
}
