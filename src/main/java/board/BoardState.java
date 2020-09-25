package main.java.board;

import java.util.Arrays;

import main.java.game.GameUtils;

public class BoardState {
    /**
     * Store the board state packed into a 1-D array. The first HEIGHT * LENGTH - 4 entries
     * correspond to the values in the board. The last two entries are the row and column values of
     * the anchor
     */
    private int[] savedState;

    public BoardState(int[][] board, int anchorRow, int anchorCol) {
        savedState = new int[GameUtils.HEIGHT * GameUtils.LENGTH - 2];
        for (int i = 2; i < savedState.length; i++) {
            savedState[i - 2] = board[i / GameUtils.LENGTH][i % GameUtils.LENGTH];
        }
        savedState[savedState.length - 1] = anchorRow;
        savedState[savedState.length - 2] = anchorCol;
    }

    /**
     * Return the saved state
     * 
     * @return The saved state information
     */
    public int[] getState() {
        return savedState;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(savedState);
    }

    // adapted from: https://stackoverflow.com/a/3692441
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        BoardState other = (BoardState) obj;
        int[] otherState = other.getState();
        for (int i = 0; i < savedState.length; i++) {
            if (savedState[i] != otherState[i])
                return false;
        }

        return true;
    }
}
