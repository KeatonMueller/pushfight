package main.java.genetics;

import java.util.Scanner;

import main.java.genetics.strategy.Coevolution;
import main.java.genetics.strategy.CoevolutionSplit;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolution;
import main.java.genetics.strategy.CoopCoevolutionSplit;
import main.java.genetics.strategy.CoopCoevolutionSplitRef;

public class Evaluation {

    /**
     * Evaluate different evolutionary strategies
     */
    public Evaluation() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Choose evolution strategy to evaluate:");
        System.out.println("\t1. Coevolution");
        System.out.println("\t2. Coevolution with Split Populations");
        System.out.println("\t3. Coevolution with Split and Reference Populations");
        System.out.println("\t4. Cooperative Coevolution");
        System.out.println("\t5. Cooperative Coevolution with Split Populations");
        System.out.println("\t6. Cooperative Coevolution with Split and Reference Populations");
        int type = Integer.parseInt(scan.nextLine().trim());

        System.out.print("Number of games: ");
        int numGames = Integer.parseInt(scan.nextLine().trim());

        switch (type) {
            case 1:
                // no weights currently
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
        scan.close();
    }
}
