package main.java.genetics;

import main.java.agents.Agent;
import main.java.agents.alphaBeta.AlphaBetaAgent;
import main.java.game.AgentGame;
import main.java.util.NumberUtils;

public class Arena {
    /**
     * Have two genomes compete head to head using an alpha beta agent
     * 
     * @param g1 Genome to be player 1
     * @param g2 Genome to be player 2
     */
    public static void compete(Genome g1, Genome g2) {
        // make alpha beta agents with each genome
        Agent a1 = new AlphaBetaAgent(g1.values, EvolutionUtils.fitnessDepth);
        Agent a2 = new AlphaBetaAgent(g2.values, EvolutionUtils.fitnessDepth);

        int winner = playout(a1, a2);
        if (winner == 0) {
            // System.out.println(0);
            // g1.fitness++;
            g1.p1++;
        } else if (winner == 1) {
            // System.out.println(1);
            // g2.fitness++;
            g2.p2++;
        } else {
            // tie, no fitness update
        }
    }

    /**
     * Have two cooperative genomes compete against two other cooperative genomes using an alpha
     * beta agent
     * 
     * @param g1a Genome responsible for p1's component weights
     * @param g1b Genome repsonsible for p1's position weights
     * @param g2a Genome responsible for p2's component weights
     * @param g2b Genome responsible for p2's position weights
     */
    public static void compete(Genome g1a, Genome g1b, Genome g2a, Genome g2b) {
        Agent a1 = new AlphaBetaAgent(g1a.values, g1b.values, EvolutionUtils.fitnessDepth);
        Agent a2 = new AlphaBetaAgent(g2a.values, g2b.values, EvolutionUtils.fitnessDepth);

        int winner = playout(a1, a2);
        if (winner == 0) {
            // p1 win, g1 genomes get +2 fitness
            g1a.p1 += 2;
            g1b.p1 += 2;
        } else if (winner == 1) {
            // p2 win, g2 genomes get +2 fitness
            g2a.p2 += 2;
            g2b.p2 += 2;
        } else {
            // tie, all genomes get +1 fitness
            g1a.p1 += 1;
            g1b.p1 += 1;
            g2a.p2 += 1;
            g2b.p2 += 1;
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

    /**
     * Test an agent as P1 with given heuristic weights against the stochastic alpha beta agent
     * 
     * @param values   Weight values for heuristic
     * @param numGames Number of games to play
     */
    public static void testP1(double[] values, int numGames) {
        Agent p1 = new AlphaBetaAgent(values, 2);
        Agent p2 = new AlphaBetaAgent(AlphaBetaAgent.ABType.STOCHASTIC);
        int winCount = 0;
        for (int i = 0; i < numGames; i++) {
            if (playout(p1, p2) == 0)
                winCount++;
        }
        System.out.println("P1 won " + winCount + "/" + numGames + " games = "
                + NumberUtils.round((double) winCount / numGames * 100, 2) + "%");
    }

    /**
     * Test an agent as P2 with given heuristic weights against the stochastic alpha beta agent
     * 
     * @param values   Weight values for heuristic
     * @param numGames Number of games to play
     */
    public static void testP2(double[] values, int numGames) {
        Agent p1 = new AlphaBetaAgent(AlphaBetaAgent.ABType.STOCHASTIC);
        Agent p2 = new AlphaBetaAgent(values, 2);
        int winCount = 0;
        for (int i = 0; i < numGames; i++) {
            if (playout(p1, p2) == 1)
                winCount++;
        }
        System.out.println("P2 won " + winCount + "/" + numGames + " games = "
                + NumberUtils.round((double) winCount / numGames * 100, 2) + "%");
    }
}
