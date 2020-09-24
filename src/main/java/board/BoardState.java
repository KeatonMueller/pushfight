package main.java.board;

import main.java.game.GameUtils;

public class BoardState {
    private int[] savedState;

    public BoardState(int[][] board, int anchorRow, int anchorCol) {
        savedState = new int[GameUtils.HEIGHT * GameUtils.LENGTH + 2];
        for (int row = 0; row < GameUtils.HEIGHT; row++) {
            for (int col = 0; col < GameUtils.LENGTH; col++) {
                savedState[row * GameUtils.LENGTH + col] = board[row][col];
            }
        }
        savedState[GameUtils.HEIGHT * GameUtils.LENGTH] = anchorRow;
        savedState[GameUtils.HEIGHT * GameUtils.LENGTH + 1] = anchorCol;
    }

    /**
     * Return the saved state
     * 
     * @return The saved state information
     */
    public int[] getState() {
        return savedState;
    }
}
