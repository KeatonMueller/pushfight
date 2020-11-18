package main.java.agents;

import main.java.board.Bitboard;

/**
 * Interface that all agents must implement
 */
public interface AgentInterface {
    public Bitboard getNextState(Bitboard board, int turn);

    public void agentMove(Bitboard board, int turn);

    public void newGame(int turn);
}
