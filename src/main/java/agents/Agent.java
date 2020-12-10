package main.java.agents;

import main.java.board.Bitboard;

/**
 * Abstract parent class for agents to extend
 */
public abstract class Agent {
    /**
     * Get the agent's next move given the board state
     * 
     * @param board The board to get the next move for
     * @return A BitboardState of the new board state after the agent has made its move
     */
    protected abstract Bitboard getNextState(Bitboard board);

    /**
     * Get, decode, and perform move from Agent
     * 
     * @param board The board for the agent to make the move on
     */
    public void agentMove(Bitboard board) {
        Bitboard state = getNextState(board);
        board.restoreState(state);
    }

    /**
     * Signal to the agent a new game is starting so it can perform any necessary cleanup/resetting
     * of data structures
     * 
     * @param turn Turn indicator for what player the agent will be
     */
    public void newGame(int turn) {
        return;
    }
}
