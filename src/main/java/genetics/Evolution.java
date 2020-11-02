package main.java.genetics;

import java.util.Scanner;

import main.java.genetics.strategy.Coevolution;
import main.java.genetics.strategy.CoevolutionSplit;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolution;
import main.java.genetics.strategy.CoopCoevolutionSplit;
import main.java.genetics.strategy.CoopCoevolutionSplitRef;
import main.java.util.HeuristicUtils;

public class Evolution {

    /**
     * Begin genetic evolution of heuristic parameters
     */
    public Evolution() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Choose evolution strategy:");
        System.out.println("\t1. Coevolution");
        System.out.println("\t2. Coevolution with Split Populations");
        System.out.println("\t3. Coevolution with Split and Reference Populations");
        System.out.println("\t4. Cooperative Coevolution");
        System.out.println("\t5. Cooperative Coevolution with Split Populations");
        System.out.println("\t6. Cooperative Coevolution with Split and Reference Populations");
        int type = Integer.parseInt(scan.nextLine().trim());

        int num = 0;
        if (type < 4) {
            System.out.println("Choose what to evolve:");
            System.out.println("\t1. Component weights");
            System.out.println("\t2. Component and position weights");
            num = Integer.parseInt(scan.nextLine().trim());
            if (num == 1)
                num = HeuristicUtils.numComponents;
            else
                num = HeuristicUtils.numValues;
        }

        System.out.print("Fitness minimax depth: ");
        EvolutionUtils.setFitnessDepth(Integer.parseInt(scan.nextLine()));

        System.out.print("Time limit (seconds): ");
        int time = Integer.parseInt(scan.nextLine());

        System.out.print("Population size: ");
        int size = Integer.parseInt(scan.nextLine());

        switch (type) {
            case 1:
                new Coevolution(time, size, num);
                break;
            case 2:
                new CoevolutionSplit(time, size, num);
                break;
            case 3:
                new CoevolutionSplitRef(time, size, num);
                break;
            case 4:
                new CoopCoevolution(time, size);
                break;
            case 5:
                new CoopCoevolutionSplit(time, size);
                break;
            case 6:
                new CoopCoevolutionSplitRef(time, size);
                break;
        }
        scan.close();
    }
}
