package main.java.board;

import java.util.ArrayList;
import java.util.List;
import main.java.util.BitboardUtils;

/**
 * Class to uniquely represent a move in Push Fight to facilitate MAST. All unit actions are
 * represented using two integers. Sliding action have a source bitmap followed by a destination
 * bitmap. Pushing actions have a source bitmap followed by the direction character (r|l|u|d) casted
 * to an int.
 */
public class Move {
    public List<Integer>actions;
    public double reward;

    public Move() {
        actions = new ArrayList<>();
    }

    public Move(Move m) {
        actions = new ArrayList<>(m.actions);
    }

    /**
     * Attempt to perform move on the given bitboard. If move is valid, update the board to reflect
     * the move and return true. If the move is invalid on the given board, return false and don't
     * change the board state.
     * 
     * @param board Bitboard to attempt move on
     * @param turn  Turn indicator
     * @return true if move was performed, else false
     */
    public boolean attempt(Bitboard board, int turn) {
        if (actions.size() < 2) {
            System.out.println("Why are you attempting a move of size " + actions.size() + "????");
        }
        Bitboard initState = board.getState();
        int numSlides = (actions.size() / 2) - 1;
        int src, dst;
        for (int i = 0; i < numSlides; i++) {
            src = actions.get(i * 2);
            dst = actions.get((i * 2) + 1);
            if (board.owns(src, turn) && board.isEmpty(dst)) {
                board.slide(src, dst);
            } else {
                board.restoreState(initState);
                return false;
            }
        }
        src = actions.get(actions.size() - 2);
        dst = actions.get(actions.size() - 1);
        if (!BitboardUtils.isValidPush(board, src, (char) dst)) {
            board.restoreState(initState);
            return false;
        }
        board.push(src, (char) dst);
        return true;
    }

    public void add(Integer action) {
        actions.add(action);
    }

    public void pop() {
        actions.remove(actions.size() - 1);
    }

    public void clear() {
        actions.clear();
    }

    @Override
    public int hashCode() {
        return actions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Move other = (Move) obj;
        return this.actions.equals(other.actions);
    }
}
