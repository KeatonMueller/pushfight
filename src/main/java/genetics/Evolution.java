package main.java.genetics;

import main.java.genetics.strategy.Coevolution;
import main.java.genetics.strategy.CoevolutionSplit;
import main.java.genetics.strategy.CoevolutionSplitRef;
import main.java.genetics.strategy.CoopCoevolution;
import main.java.genetics.strategy.CoopCoevolutionSplit;

public class Evolution {

    /**
     * Begin genetic evolution of heuristic parameters
     * 
     * @param time Time limit (in seconds) for evolution to run
     * @param size Size of population
     * @param num  Number of heuristic values to evolve
     * @param type Type of evolutionary strategy to take
     */
    public Evolution(int time, int size, int num, int type) {
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
        }
    }
}
