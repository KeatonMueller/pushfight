package main.java.agents;

import main.java.board.Board;
import main.java.game.GameUtils;

public class AgentUtils {
    /**
     * Get, decode, and perform move from Agent
     * 
     * @param agent The agent to make the move
     * @param board The board for the agent to make the move on
     * @param turn  Turn indicator
     * @return The player who just lost (0|1) or -1 if no loser
     */
    public static int agentMove(Agent agent, Board board, int turn) {
        // get the move from the agent
        int[] move = agent.getMove(board, turn);

        // decode and perform two sliding actions
        int startPos, endPos;
        for (int i = 0; i < 2; i++) {
            // can skip sliding action
            if (move[i] == 0)
                continue;
            startPos = move[i] / 100;
            endPos = move[i] % 100;
            System.out.println("Player " + (turn + 1) + ": " + posToLabel(startPos) + " => "
                    + posToLabel(endPos));
            board.slide(startPos / 10, startPos % 10, endPos / 10, endPos % 10);
        }
        // decode and perform push action
        startPos = move[2] / 10;
        char dir = GameUtils.dirIntToChar(move[2] % 10);
        System.out.println("Player " + (turn + 1) + ": " + posToLabel(startPos) + " going " + dir);
        return board.push(startPos / 10, startPos % 10, dir)[5];
    }

    /**
     * Return label representation of board position
     * 
     * @param pos Board position to label in form row * 10 + col
     * @return The board label (e.g. a3) corresponding to the position
     */
    private static String posToLabel(int pos) {
        return (char) ('a' + (pos / 10)) + "" + (pos % 10 + 1);
    }
}
