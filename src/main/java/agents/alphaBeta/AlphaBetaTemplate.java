package main.java.agents.alphaBeta;

import main.java.board.Bitboard;

public interface AlphaBetaTemplate {
    public Bitboard getNextState(Bitboard board, int turn);

    public void agentMove(Bitboard board, int turn);

    public void newGame(int turn);
}
