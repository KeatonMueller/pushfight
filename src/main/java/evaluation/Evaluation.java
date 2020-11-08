package main.java.evaluation;

import java.util.Scanner;
import main.java.agents.Agent;
import main.java.agents.AlphaBetaAgent;
import main.java.agents.RandomAgent;
import main.java.agents.StochasticABAgent;
import main.java.agents.mcts.MASTAgent;
import main.java.agents.mcts.MonteCarloAgent;
import main.java.game.AgentGame;
import main.java.genetics.strategy.Coevolution;
import main.java.genetics.strategy.CoevolutionSplit;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolution;
import main.java.genetics.strategy.CoopCoevolutionSplit;
import main.java.genetics.strategy.CoopCoevolutionSplitRef;

/**
 * Evaluation tool to compare any two different agents against one another
 */
public class Evaluation {
    public Evaluation() {
        Scanner scan = new Scanner(System.in);

        Agent p1 = chooseAgent(0, scan);
        Agent p2 = chooseAgent(1, scan);

        // choose number of games
        System.out.print("Number of games: ");
        int numGames = Integer.parseInt(scan.nextLine().trim());

        evalAgents(p1, p2, numGames);
        evalAgents(p2, p1, numGames);

        scan.close();
    }

    public Agent chooseAgent(int turn, Scanner scan) {
        // choose agent
        System.out.println("Choose agent for Player " + (turn + 1) + ":");
        System.out.println("\t1. Random");
        System.out.println("\t2. Alpha Beta");
        System.out.println("\t3. Stochastic Alpha Beta");
        System.out.println("\t4. MCTS");
        System.out.println("\t5. MCTS with MAST");
        System.out.print("Choice: ");
        int agentType = Integer.parseInt(scan.nextLine().trim());

        // if alpha beta, choose weights
        int evolutionType = 0;
        if (agentType == 2) {
            System.out.println("Choose evolution strategy to evaluate:");
            System.out.println("\t0. Default Weights");
            System.out.println("\t1. Coevolution");
            System.out.println("\t2. Coevolution with Split Populations");
            System.out.println("\t3. Coevolution with Split and Reference Populations");
            System.out.println("\t4. Cooperative Coevolution");
            System.out.println("\t5. Cooperative Coevolution with Split Populations");
            System.out.println("\t6. Cooperative Coevolution with Split and Reference Populations");
            System.out.print("Choice: ");
            evolutionType = Integer.parseInt(scan.nextLine().trim());
        }

        // if mcts, choose iterations per move
        int iterations = 5000;
        if (agentType == 4 || agentType == 5) {
            System.out.print("Choose number of iterations per move:");
            iterations = Integer.parseInt(scan.nextLine().trim());
        }

        switch (agentType) {
            case 1:
                return new RandomAgent();
            case 2:
                switch (evolutionType) {
                    case 0:
                        return new AlphaBetaAgent();
                    case 1:
                        return new AlphaBetaAgent(Coevolution.weights);
                    case 2:
                        return new AlphaBetaAgent(CoevolutionSplit.p1Weights,
                                CoevolutionSplit.p2Weights);
                    case 3:
                        return new AlphaBetaAgent(CoevolutionSplitRef.p1Weights,
                                CoevolutionSplitRef.p2Weights);
                    case 4:
                        return new AlphaBetaAgent(CoopCoevolution.weights);
                    case 5:
                        return new AlphaBetaAgent(CoopCoevolutionSplit.p1Weights,
                                CoopCoevolutionSplit.p2Weights);
                    case 6:
                        return new AlphaBetaAgent(CoopCoevolutionSplitRef.p1Weights,
                                CoopCoevolutionSplitRef.p2Weights);
                }
                break;
            case 3:
                return new StochasticABAgent();
            case 4:
                return new MonteCarloAgent(iterations);
            case 5:
                return new MASTAgent(iterations);
        }
        return null;
    }

    /**
     * Evaluate the two agents against one another
     * 
     * @param p1       Agent to play P1
     * @param p2       Agent to play P2
     * @param numGames Number of games to play
     */
    public static void evalAgents(Agent p1, Agent p2, int numGames) {
        int winner, p1Wins = 0, p2Wins = 0;
        for (int i = 0; i < numGames; i++) {
            winner = (new AgentGame(p1, p2)).getWinner();
            if (winner == 0)
                p1Wins++;
            else if (winner == 1)
                p2Wins++;
        }
        System.out.println("Results after " + numGames + " games:");
        System.out.println("\tPlayer 1: " + p1 + " won " + p1Wins + "/" + numGames + " games ("
                + Math.round((double) p1Wins / numGames * 10000) / 100.0 + "%)");

        System.out.println("\tPlayer 2: " + p2 + " won " + p2Wins + "/" + numGames + " games ("
                + Math.round((double) p2Wins / numGames * 10000) / 100.0 + "%)");
    }
}