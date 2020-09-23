package main.java.game;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

import main.java.Board;

public class GameUtils {
    private static HashSet<Integer> moves = new HashSet<>();
    private static HashSet<Integer> visited = new HashSet<>();
    private static Queue<Integer> queue = new ArrayDeque<>();

    /**
     * Find all positions reachable from given row and column
     * 
     * @param board Board object to be used
     * @param row   Row to search from
     * @param col   Column to search from
     * @return HashSet of valid moves encoded as integers of the form: row * 10 + col
     */
    public static HashSet<Integer> findSlidingActions(Board board, int row, int col) {
        // perform basic BFS to find valid moves
        moves.clear();
        visited.clear();
        queue.clear();

        queue.add((row + 1) * 10 + col);
        queue.add((row - 1) * 10 + col);
        queue.add(row * 10 + col + 1);
        queue.add(row * 10 + col - 1);
        int pos;
        while (queue.size() > 0) {
            pos = queue.poll();

            if (visited.contains(pos))
                continue;

            visited.add(pos);
            row = pos / 10;
            col = pos % 10;

            if (!board.isValid(row, col))
                continue;

            if (board.isEmpty(row, col))
                moves.add(pos);
            else
                continue;

            queue.add((row + 1) * 10 + col);
            queue.add((row - 1) * 10 + col);
            queue.add(pos + 1);
            queue.add(pos - 1);
        }
        return moves;
    }
}
