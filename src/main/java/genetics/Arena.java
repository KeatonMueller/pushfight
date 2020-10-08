package main.java.genetics;

import main.java.agents.Agent;
import main.java.agents.AlphaBetaAgent;
import main.java.game.AgentGame;

public class Arena {
    /**
     * Have two genomes compete head to head using depth-1 Minimax
     * 
     * @param g1 Genome to be player 1
     * @param g2 Genome to be player 2
     */
    public static void compete(Genome g1, Genome g2) {
        // make alpha beta agents with depth of 1 and silent execution
        Agent a1 = new AlphaBetaAgent(g1.values, 1, true);
        Agent a2 = new AlphaBetaAgent(g2.values, 1, true);

        if (playout(a1, a2) == 0) {
            // System.out.println(0);
            // g1.fitness++;
            g1.p1++;
        } else {
            // System.out.println(1);
            // g2.fitness++;
            g2.p2++;
        }
    }

    /**
     * Playout the full game between two agents
     * 
     * @param a1 Agent to be player 1
     * @param a2 Agent to be player 2
     * @return 0 if player 1 won, else 1
     */
    public static int playout(Agent a1, Agent a2) {
        return new AgentGame(a1, a2).getWinner();
    }
}
