package main.java.agents;

import main.java.board.Bitboard;
import main.java.board.BitboardState;

public abstract class Agent {
    /**
     * Get the agent's next move given the board state
     * 
     * @param board The board to get the next move for
     * @param turn  Turn indicator
     * @return A BitboardState of the new board state after the agent has made its move
     */
    protected abstract BitboardState getNextState(Bitboard board, int turn);

    /**
     * Get, decode, and perform move from Agent
     * 
     * @param board The board for the agent to make the move on
     * @param turn  Turn indicator
     */
    public void agentMove(Bitboard board, int turn) {
        BitboardState state = getNextState(board, turn);
        board.restoreState(state);
    }
}
