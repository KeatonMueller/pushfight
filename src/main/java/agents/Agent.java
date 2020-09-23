package main.java.agents;

import main.java.board.Board;

public interface Agent {
    /**
     * Get the agent's next move given the board state
     * 
     * @param board The board to get the next move for
     * @param turn  Turn indicator
     * @return An int list of the three actions encoded as follows: Positions are row * 10 + col.
     *         Sliding actions are oldPos * 100 + newPos. Directions r, l, u, and d are encoded by
     *         0, 1, 2, and 3, respectively. Pushing actions are encoded as pos * 10 + direction. A
     *         0 for any sliding action indicates that action was skipped. Return format is
     *         [slideAction1, slideAction2, pushAction]
     */
    public int[] getMove(Board board, int turn);
}
