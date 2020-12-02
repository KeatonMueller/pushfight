package main.java.evaluation;

import java.util.Scanner;

import main.java.agents.Agent;
import main.java.agents.RandomAgent;
import main.java.agents.alphaBeta.AlphaBetaAgent;
import main.java.agents.mcts.MonteCarloAgent;
import main.java.game.AgentGame;
import main.java.genetics.strategy.Coevolution;
import main.java.genetics.strategy.CoevolutionSplit;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolution;
import main.java.genetics.strategy.CoopCoevolutionSplit;
import main.java.genetics.strategy.CoopCoevolutionSplitRef;
import main.java.util.NumberUtils;

import static main.java.agents.alphaBeta.AlphaBetaAgent.ABType;
import static main.java.agents.mcts.MonteCarloAgent.MCTSType;

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
        System.out.println("\t3. MCTS");
        System.out.print("Choice: ");
        int agentType = Integer.parseInt(scan.nextLine().trim());

        // if alpha beta, choose variant and weights
        int abType = 0;
        int evolutionType = 0;
        if (agentType == 2) {
            System.out.println("Choose Alpha Beta Variant:");
            System.out.println("\t1. Vanilla");
            System.out.println("\t2. Move Ordered");
            System.out.println("\t3. Stochastic");
            System.out.println("\t4. Scout");
            System.out.print("Choice: ");
            abType = Integer.parseInt(scan.nextLine().trim());

            if (abType == 1) {
                System.out.println("Choose evolution strategy to evaluate:");
                System.out.println("\t0. Default Weights");
                System.out.println("\t1. Coevolution");
                System.out.println("\t2. Coevolution with Split Populations");
                System.out.println("\t3. Coevolution with Split and Reference Populations");
                System.out.println("\t4. Cooperative Coevolution");
                System.out.println("\t5. Cooperative Coevolution with Split Populations");
                System.out.println(
                        "\t6. Cooperative Coevolution with Split and Reference Populations");
                System.out.print("Choice: ");
                evolutionType = Integer.parseInt(scan.nextLine().trim());
            }
        }

        // if mcts, choose variant and iterations per move
        int mctsType = 0;
        int iterations = 5000;
        if (agentType == 3) {
            System.out.println("Choose MCTS Variant:");
            System.out.println("\t1. Vanilla");
            System.out.println("\t2. MAST");
            System.out.println("\t3. Heuristic-Seeded");
            System.out.println("\t4. Biased");
            System.out.println("\t5. Last Good Reply");
            System.out.println("\t6. Weighted Heuristic-Seeded");
            System.out.print("Choice: ");
            mctsType = Integer.parseInt(scan.nextLine().trim());
            System.out.print("Choose number of iterations per move: ");
            iterations = Integer.parseInt(scan.nextLine().trim());
        }

        switch (agentType) {
            // random
            case 1:
                return new RandomAgent();
            // alpha beta
            case 2:
                switch (abType) {
                    // vanilla
                    case 1:
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
                        return null;
                    // move-ordered
                    case 2:
                        return new AlphaBetaAgent(ABType.MOVE_ORDER);
                    // stochastic
                    case 3:
                        return new AlphaBetaAgent(ABType.STOCHASTIC);
                    // scout
                    case 4:
                        return new AlphaBetaAgent(ABType.SCOUT);

                }
                // mcts
            case 3:
                switch (mctsType) {
                    case 1:
                        return new MonteCarloAgent(MCTSType.VANILLA, iterations);
                    case 2:
                        return new MonteCarloAgent(MCTSType.MAST, iterations);
                    case 3:
                        return new MonteCarloAgent(MCTSType.SEEDED, iterations);
                    case 4:
                        return new MonteCarloAgent(MCTSType.BIASED, iterations);
                    case 5:
                        return new MonteCarloAgent(MCTSType.LGR1, iterations);
                    case 6:
                        return new MonteCarloAgent(MCTSType.WEIGHTED_SEEDED, iterations);
                }
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
        int winner, p1Wins = 0, p2Wins = 0, ties = 0;
        int totalTurns = 0;
        AgentGame ag;
        for (int i = 0; i < numGames; i++) {
            System.out.print("                                                   \r");
            System.out.print("Running game " + i + "\r");
            ag = new AgentGame(p1, p2);
            winner = ag.getWinner();
            totalTurns += ag.numTurns;
            if (winner == 0)
                p1Wins++;
            else if (winner == 1)
                p2Wins++;
            else
                ties++;
        }
        System.out.println("Results after " + numGames + " games:");
        System.out.println("\tPlayer 1: " + p1 + " won " + p1Wins + "/" + numGames + " games ("
                + NumberUtils.round((double) p1Wins / numGames * 100, 2) + "%)");

        System.out.println("\tPlayer 2: " + p2 + " won " + p2Wins + "/" + numGames + " games ("
                + NumberUtils.round((double) p2Wins / numGames * 100, 2) + "%)");
        System.out.println("\tTies: " + ties);
        System.out.println("\tAverage length: " + NumberUtils.round(totalTurns / numGames, 2));
    }
}
