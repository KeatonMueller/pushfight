package main.java.genetics.strategy;

import java.util.ArrayList;
import java.util.List;

import main.java.genetics.Arena;
import main.java.genetics.EvolutionUtils;
import main.java.genetics.Genome;

/**
 * Coevolution strategy that evolves a single population. Fitness evaluation is done within members
 * of the same generation.
 */
public class Coevolution {
    /**
     * Weights after 2 days of training
     */
    public static double[] weights = {0.12808120614166763, -0.9984497530047178, 0.08058234074884363,
            -0.529587564365785, 0.9784933008517469, 0.5620729724330126, 0.9262268553631087};

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
    public Coevolution(int time, int size, int num) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        numValues = num;
        evolve();
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolve() {
        // initialize population
        List<Genome> pop = new ArrayList<>();
        EvolutionUtils.initPopulation(pop, popSize, numValues);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pop);
            // select next generation
            pop = EvolutionUtils.selection(pop);
            EvolutionUtils.mutate(pop);
        }
        fitnessEval(pop);
        System.out.println(pop.get(0));
    }

    /**
     * Perform round robin evalution on members of population. Each individual plays all others both
     * as player 1 and as player 2. Fitness is total number of wins over all games, minus the
     * absolute difference in P1 wins vs P2 wins.
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
        for (Genome g : pop) {
            g.fitness = g.p1 + g.p2 - (Math.abs(g.p1 - g.p2) / 2);
        }
        pop.sort(Genome.compare);
    }
}
