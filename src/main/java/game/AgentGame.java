package main.java.game;

import main.java.agents.Agent;
import main.java.board.Bitboard;
import main.java.board.BitboardUtils;

public class AgentGame {
    private Bitboard board;
    private int turn;
    private Agent a1, a2;

    /**
     * Perform a fully automated game between two agents
     * 
     * @param agent1 Agent to be player 1
     * @param agent2 Agent to be player 2
     */
    public AgentGame(Agent agent1, Agent agent2) {
        board = new Bitboard();
        turn = 0;

        a1 = agent1;
        a2 = agent2;

        BitboardUtils.skipSetup(board);
    }

    /**
     * Run the game loop until someone wins
     */
    public int getWinner() {
        int winner;
        while (true) {
            makeMove(turn);
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                return winner;
            }
            // change turns
            turn = 1 - turn;
        }
    }

    /**
     * Have the agent whose turn it is make their move
     * 
     * @param turn Turn indicator
     */
    private void makeMove(int turn) {
        if (turn == 0) {
            a1.agentMove(board, turn);
        } else {
            a2.agentMove(board, turn);
        }
    }
}
