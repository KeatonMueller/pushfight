package main.java.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import main.java.board.Board;
import main.java.board.BoardState;

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
    public static HashSet<Integer> findSlideDests(Board board, int row, int col) {
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
     * Find all the pieces that the given position can push
     * 
     * @param board The board to examine
     * @param row   The row position of the piece
     * @param col   The column position of the piece
     * @return A HashSet of integer positions of pieces that can be pushed by the given piece
     */
    public static HashSet<Integer> findPushablePieces(Board board, int row, int col) {
        HashSet<Integer> pushable = new HashSet<>();
        if (!board.isSquare(row, col))
            return pushable;

        int pushRow, pushCol;
        for (int dir = 0; dir < 4; dir++) {
            if (isValidPush(board, row, col, dirIntToChar(dir))) {
                pushRow = row + GameUtils.getDeltas(dirIntToChar(dir))[0];
                pushCol = col + GameUtils.getDeltas(dirIntToChar(dir))[1];
                pushable.add(pushRow * 10 + pushCol);
            }
        }
        return pushable;
    }

    /**
     * Generate all legal sliding actions from current position for given player
     * 
     * @param board The board position to be examined
     * @param turn  Turn indicator
     * @return List<Integer> of sliding actions encoded as startPos * 100 + endPos where positions
     *         are encoded as row * 10 + col
     */
    public static List<Integer> getSlideActions(Board board, int turn) {
        List<Integer> slides = new ArrayList<>();
        for (int startPos : board.getPieceLocs(turn)) {
            for (int endPos : findSlideDests(board, startPos / 10, startPos % 10)) {
                slides.add(startPos * 100 + endPos);
            }
        }
        // add in optional empty slide
        slides.add(0);
        return slides;
    }

    /**
     * Generate all legal pushing actions from current position for given player
     * 
     * @param board The board position to be examined
     * @param turn  Turn indicator
     * @return List<Integer> of pushing actions encoded as pos * 10 + dir where pos is encoded as
     *         row * 10 + col and dir is encoded as 0, 1, 2, 3 for r, l, u, d, respectively
     */
    public static List<Integer> getPushActions(Board board, int turn) {
        List<Integer> pushes = new ArrayList<>();
        for (int pos : board.getPieceLocs(turn)) {
            if (!board.isSquare(pos / 10, pos % 10))
                continue;
            for (int dir = 0; dir < 4; dir++) {
                if (isValidPush(board, pos / 10, pos % 10, dirIntToChar(dir))) {
                    pushes.add(pos * 10 + dir);
                }
            }
        }
        return pushes;
    }

    /**
     * Generate all legal moves from current position for given player
     * 
     * @param board The board position to be examined
     * @param turn  Turn indicator
     * @return List<Long> of complete moves. Moves are encoded as 11112222333 where the 1's
     *         correspond to the first sliding action, the 2's are the second sliding action, and
     *         the 3's are the pushing action
     */
    public static List<Long> getMoves(Board board, int turn) {
        List<Long> moves = new ArrayList<>();
        int oldPos1 = 0, newPos1 = 0, oldPos2 = 0, newPos2 = 0;
        int[] result;
        BoardState state;
        HashSet<BoardState> seenStates = new HashSet<>();
        // for all first slides
        for (int slide1 : getSlideActions(board, turn)) {
            if (slide1 != 0) {
                oldPos1 = slide1 / 100;
                newPos1 = slide1 % 100;
                // perform slide
                board.slide(oldPos1 / 10, oldPos1 % 10, newPos1 / 10, newPos1 % 10);
            }
            // for all second slides
            for (int slide2 : getSlideActions(board, turn)) {
                if (slide2 != 0) {
                    oldPos2 = slide2 / 100;
                    newPos2 = slide2 % 100;
                    // perform slide
                    board.slide(oldPos2 / 10, oldPos2 % 10, newPos2 / 10, newPos2 % 10);
                }
                // for all pushes
                for (int push : getPushActions(board, turn)) {
                    int pushPos = push / 10;
                    // perform push
                    result = board.push(pushPos / 10, pushPos % 10,
                            GameUtils.dirIntToChar(push % 10));
                    state = board.generateState();
                    // if you end up in a new board state
                    if (!seenStates.contains(state)) {
                        seenStates.add(state);
                        // record the move
                        moves.add((long) slide1 * 10000000 + slide2 * 1000 + push);
                    }
                    // undo push
                    board.undoPush(result, GameUtils.dirIntToChar(push % 10));
                }
                // undo slide
                if (slide2 != 0) {
                    board.slide(newPos2 / 10, newPos2 % 10, oldPos2 / 10, oldPos2 % 10);
                }
            }
            // undo slide
            if (slide1 != 0) {
                board.slide(newPos1 / 10, newPos1 % 10, oldPos1 / 10, oldPos1 % 10);
            }
        }
        return moves;
    }

    public static char dirIntToChar(int dir) {
        switch (dir) {
            case 0:
                return 'r';
            case 1:
                return 'l';
            case 2:
                return 'u';
            case 3:
                return 'd';
            default:
                return ' ';
        }
    }

    public static int dirCharToInt(char dir) {
        switch (dir) {
            case 'r':
                return 0;
            case 'l':
                return 1;
            case 'u':
                return 2;
            case 'd':
                return 3;
            default:
                return -1;
        }
    }

    /**
     * Decodes an encoded sliding action
     * 
     * @param slide A slide action encoded as startPos * 100 + endPos
     * @return int array of form [oldRow, oldCol, newRow, newCol]
     */
    public static int[] decodeSlideAction(int slide) {
        int[] decoded = new int[] {0, 0, 0, 0};
        int startPos = slide / 100;
        int endPos = slide % 100;
        decoded[0] = startPos / 10;
        decoded[1] = startPos % 10;
        decoded[2] = endPos / 10;
        decoded[3] = endPos % 10;
        return decoded;
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
        // only squares can push
        if (!board.isSquare(row, col))
            return false;
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
        // if push is off the top or bottom rails, it's invalid
        if (nextRow < 0 || nextRow >= HEIGHT)
            return false;
        return true;
    }

    /**
     * Converts a change in position into a direction of that change. The two given positions must
     * be orthogonally connected
     * 
     * @param oldRow Initial row
     * @param oldCol Initial column
     * @param newRow Final row
     * @param newCol Final column
     * @return A char (r|l|u|d) corresponding to the change in direction
     */
    public static char posChangeToDir(int oldRow, int oldCol, int newRow, int newCol) {
        if (oldRow - newRow == 1)
            return 'u';
        else if (oldRow - newRow == -1)
            return 'd';
        else if (oldCol - newCol == 1)
            return 'l';
        else if (oldCol - newCol == -1)
            return 'r';
        return ' ';
    }

    /**
     * Calculate the change in row and column index based on a pushing direction
     * 
     * @param dir The direction (r|l|u|d)
     * @return int array of form {deltaRow, deltaCol}
     */
    public static int[] getDeltas(char dir) {
        int[] delta = new int[] {0, 0};
        switch (dir) {
            case 'r':
                delta[1] = 1;
                break;
            case 'l':
                delta[1] = -1;
                break;
            case 'u':
                delta[0] = -1;
                break;
            case 'd':
                delta[0] = 1;
                break;
        }
        return delta;
    }
}
