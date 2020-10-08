package main.java.genetics;

import java.util.ArrayList;
import java.util.List;

public class Evolution {
    private long timeLimit;
    private int popSize;
    private int numValues;

    /**
     * Begin genetic evolution of heuristic parameters
     * 
     * @param time Time limit (in seconds) for evolution to run
     * @param size Size of population
     * @param num  Number of heuristic values to evolve
     */
    public Evolution(int time, int size, int num) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        numValues = num;
        evolve();
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolve() {
        List<Genome> pop = new ArrayList<>();
        initPopulation(pop);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            fitnessEval(pop);
        }
    }

    /**
     * Perform round robin evalution on members of population. Each individual plays all others both
     * as player 1 and player 2. Fitness is total number of wins over all games.
     * 
     * @param pop List of Genomes to evolve
     */
    private void fitnessEval(List<Genome> pop) {
        for (Genome g1 : pop) {
            for (Genome g2 : pop) {
                if (g1 != g2) {
                    Arena.compete(g1, g2);
                }
            }
        }
    }

    /**
     * Initialize the population randomly
     * 
     * @param pop Empty list of Genomes to populate
     */
    private void initPopulation(List<Genome> pop) {
        for (int i = 0; i < popSize; i++) {
            pop.add(new Genome(numValues));
        }
    }
}
