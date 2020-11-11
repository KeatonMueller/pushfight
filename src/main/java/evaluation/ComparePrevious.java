package main.java.evaluation;

import java.util.Scanner;

import main.java.agents.Agent;
import main.java.agents.AlphaBetaAgent;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolutionSplit;
import main.java.genetics.strategy.CoopCoevolutionSplitRef;

/**
 * Used to compare the old weights of a particular evolution strategy to new ones
 */
public class ComparePrevious extends Evaluation {
    public ComparePrevious() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Choose evolution strategy to compare to its previous values:");
        System.out.println("\t1. Coevolution with Split and Reference Populations");
        System.out.println("\t2. Cooperative Coevolution with Split Populations");
        System.out.println("\t3. Cooperative Coevolution with Split and Reference Populations");
        System.out.print("Choice: ");
        int evolutionType = Integer.parseInt(scan.nextLine().trim());

        Agent a1, a2;
        switch (evolutionType) {
            case 1:
                a1 = new AlphaBetaAgent(CoevolutionSplitRef.p1Weights,
                        CoevolutionSplitRef.p2Weights);
                a2 = new AlphaBetaAgent(CoevolutionSplitRef.p1WeightsOld,
                        CoevolutionSplitRef.p2WeightsOld);
                break;
            case 2:
                a1 = new AlphaBetaAgent(CoopCoevolutionSplit.p1Weights,
                        CoopCoevolutionSplit.p2Weights);
                a2 = new AlphaBetaAgent(CoopCoevolutionSplit.p1WeightsOld,
                        CoopCoevolutionSplit.p2WeightsOld);
                break;
            case 3:
                a1 = new AlphaBetaAgent(CoopCoevolutionSplitRef.p1Weights,
                        CoopCoevolutionSplitRef.p2Weights);
                a2 = new AlphaBetaAgent(CoopCoevolutionSplitRef.p1WeightsOld,
                        CoopCoevolutionSplitRef.p2WeightsOld);
                break;
            default:
                scan.close();
                return;
        }

        System.out.print("Number of games: ");
        int numGames = Integer.parseInt(scan.nextLine().trim());

        System.out.println("New values start as P1 with old values starting as P2");
        evalAgents(a1, a2, numGames);

        scan.close();
    }

}
