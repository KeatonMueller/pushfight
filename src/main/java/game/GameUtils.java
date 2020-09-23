package main.java.game;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

import main.java.Board;

public class GameUtils {
    public static final int LENGTH = 8;
    public static final int HEIGHT = 4;

    private static HashSet<Integer> dests = new HashSet<>();
    private static HashSet<Integer> visited = new HashSet<>();
    private static Queue<Integer> queue = new ArrayDeque<>();

    /**
     * Find all positions reachable from given row and column
     * 
     * @param board Board object to be used
     * @param row   Row to search from
     * @param col   Column to search from
     * @return HashSet of valid destinations encoded as integers of the form: row * 10 + col
     */
    public static HashSet<Integer> findSlidingDests(Board board, int row, int col) {
        // perform basic BFS to find valid destinations
        dests.clear();
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
                dests.add(pos);
            else
                continue;

            queue.add((row + 1) * 10 + col);
            queue.add((row - 1) * 10 + col);
            queue.add(pos + 1);
            queue.add(pos - 1);
        }
        return dests;
    }

    /**
     * Check if given push on the given board is valid
     * 
     * @param board Board to check the push on
     * @param row   Row of start of push
     * @param col   Column of start of push
     * @param dir   Direction of push (r|l|u|d)
     * @return true if push is valid, else false
     */
    public static boolean isValidPush(Board board, int row, int col, char dir) {
        int[] delta = getDeltas(dir);
        int nextRow = row + delta[0];
        int nextCol = col + delta[1];
        // the immediate next piece must exist and be non-empty
        if (!board.isValid(nextRow, nextCol) || board.isEmpty(nextRow, nextCol))
            return false;

        // line of consecutive pieces must not contain anchor
        while (board.isValid(nextRow, nextCol)) {
            if (board.isEmpty(nextRow, nextCol))
                return true;
            if (board.isAnchored(nextRow, nextCol))
                return false;
            nextRow += delta[0];
            nextCol += delta[1];
        }
        if (nextRow < 0 || nextRow >= HEIGHT)
            return false;
        return true;
    }

    /**
     * Calculate the change in row and column index based on a pushing direction
     * 
     * @param dir The direction (r|l|u|d)
     * @return int array of form {deltaRow, deltaCol}
     */
    public static int[] getDeltas(char dir) {
        int[] delta = new int[2];
        switch (dir) {
            case 'r':
                delta[0] = 0;
                delta[1] = 1;
                return delta;
            case 'l':
                delta[0] = 0;
                delta[1] = -1;
                return delta;
            case 'u':
                delta[0] = -1;
                delta[1] = 0;
                return delta;
            case 'd':
                delta[0] = 1;
                delta[1] = 0;
                return delta;
            default:
                return delta;
        }
    }
}
