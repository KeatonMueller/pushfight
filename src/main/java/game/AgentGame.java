package main.java.game;

import java.util.HashMap;
import java.util.Map;

import main.java.agents.Agent;
import main.java.board.Bitboard;
import main.java.util.BitboardUtils;

public class AgentGame {
    private Bitboard board;
    private int turn;
    private Agent a1, a2;
    public int numTurns;

    private Map<Bitboard, Integer> stateToNum;

    /**
     * Perform a fully automated game between two agents
     * 
     * @param agent1 Agent to be player 1
     * @param agent2 Agent to be player 2
     */
    public AgentGame(Agent agent1, Agent agent2) {
        board = new Bitboard();
        turn = 0;
        numTurns = 0;

        a1 = agent1;
        a2 = agent2;

        a1.newGame(0);
        a2.newGame(1);

        BitboardUtils.skipSetup(board);
        stateToNum = new HashMap<>();
    }

    /**
     * Run the game loop until someone wins
     */
    public int getWinner() {
        int winner, count;
        while (true) {
            makeMove(turn);
            numTurns++;
            count = stateToNum.getOrDefault(board, 0);
            stateToNum.put(board, count + 1);

            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                return winner;
            }
            // detect fifth time repeating a board state, call it a tie
            if (count >= 5) {
                return -1;
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
