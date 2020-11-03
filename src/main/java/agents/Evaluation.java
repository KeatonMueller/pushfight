package main.java.agents;

import java.util.Scanner;
import main.java.agents.mcts.MASTAgent;
import main.java.agents.mcts.MonteCarloAgent;
import main.java.game.AgentGame;
import main.java.genetics.Arena;
import main.java.genetics.strategy.Coevolution;
import main.java.genetics.strategy.CoevolutionSplit;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolution;
import main.java.genetics.strategy.CoopCoevolutionSplit;
import main.java.genetics.strategy.CoopCoevolutionSplitRef;

public class Evaluation {

    /**
     * Evaluate different agents against the stochastic alpha beta agent
     */
    public Evaluation() {
        Scanner scan = new Scanner(System.in);

        // choose agent
        System.out.println("Choose agent to evaluate:");
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

        // choose number of games
        System.out.print("Number of games: ");
        int numGames = Integer.parseInt(scan.nextLine().trim());

        // run evaluation
        switch (agentType) {
            case 1:
                evalP1(new RandomAgent(), numGames);
                evalP2(new RandomAgent(), numGames);
                break;
            case 2:
                switch (evolutionType) {
                    case 0:
                        evalP1(new AlphaBetaAgent(), numGames);
                        evalP2(new AlphaBetaAgent(), numGames);
                    case 1:
                        Arena.testP1(Coevolution.weights, numGames);
                        Arena.testP2(Coevolution.weights, numGames);
                        break;
                    case 2:
                        Arena.testP1(CoevolutionSplit.p1Weights, numGames);
                        Arena.testP2(CoevolutionSplit.p2Weights, numGames);
                        break;
                    case 3:
                        Arena.testP1(CoevolutionSplitRef.p1Weights, numGames);
                        Arena.testP2(CoevolutionSplitRef.p2Weights, numGames);
                        break;
                    case 4:
                        Arena.testP1(CoopCoevolution.weights, numGames);
                        Arena.testP2(CoopCoevolution.weights, numGames);
                        break;
                    case 5:
                        Arena.testP1(CoopCoevolutionSplit.p1Weights, numGames);
                        Arena.testP2(CoopCoevolutionSplit.p2Weights, numGames);
                        break;
                    case 6:
                        Arena.testP1(CoopCoevolutionSplitRef.p1Weights, numGames);
                        Arena.testP2(CoopCoevolutionSplitRef.p2Weights, numGames);
                        break;
                }
                break;
            case 3:
                evalP1(new StochasticABAgent(), numGames);
                evalP2(new StochasticABAgent(), numGames);
                break;
            case 4:
                evalP1(new MonteCarloAgent(), numGames);
                evalP2(new MonteCarloAgent(), numGames);
                break;
            case 5:
                evalP1(new MASTAgent(), numGames);
                evalP2(new MASTAgent(), numGames);
                break;
        }
        scan.close();
    }

    /**
     * Evaluate given agent as P1 against stochastic alpha beta agent
     * 
     * @param p1       Agent to be evaluated
     * @param numGames Number of games to play
     */
    public static void evalP1(Agent p1, int numGames) {
        Agent p2 = new StochasticABAgent();
        int winCount = 0;
        for (int i = 0; i < numGames; i++) {
            if ((new AgentGame(p1, p2)).getWinner() == 0)
                winCount++;
        }
        System.out.println("P1 won " + winCount + "/" + numGames + " games = "
                + Math.round((double) winCount / numGames * 10000) / 100.0 + "%");
    }

    /**
     * Evaluate given agent as P2 against stochastic alpha beta agent
     * 
     * @param p2       Agent to be evaluated
     * @param numGames Number of games to play
     */
    public static void evalP2(Agent p2, int numGames) {
        Agent p1 = new StochasticABAgent();
        int winCount = 0;
        for (int i = 0; i < numGames; i++) {
            if ((new AgentGame(p1, p2)).getWinner() == 1)
                winCount++;
        }
        System.out.println("P2 won " + winCount + "/" + numGames + " games = "
                + Math.round((double) winCount / numGames * 10000) / 100.0 + "%");
    }
}
