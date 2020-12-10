package main.java.agents;

import main.java.board.Bitboard;

/**
 * Interface that all agents must implement
 */
public interface AgentInterface {
    public Bitboard getNextState(Bitboard board);

    public void agentMove(Bitboard board);

    public void newGame(int turn);
}
