package main.java.genetics.strategy;

import java.util.ArrayList;
import java.util.List;

import main.java.genetics.Arena;
import main.java.genetics.EvolutionUtils;
import main.java.genetics.Genome;

/**
 * Coevolution strategy that evolves two populations, one optimizing P1, one optimizing P2. Fitness
 * evaluation is done within members of the same generation.
 */
public class CoevolutionSplit {
    /**
     * Weights after 2 days of training for P1
     */
    public static double[] p1Weights = {0.4470917254952085, -0.9204980411673434, 0.7132774089338966,
            0.17521488568014731, 0.844944073700155, 0.6030821040676043, 0.8642230339081908};
    /**
     * Weights after 2 days of training for P2
     */
    public static double[] p2Weights = {0.6975941149848557, 0.6631396149044878, 0.12527247607651526,
            0.15857850844552657, 0.4010626840263476, 0.9751960444413503, 0.16317777110633014};


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
    public CoevolutionSplit(int time, int size, int num) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        numValues = num;
        evolve();
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolve() {
        // initialize two populations, one to optimize P1 and one for P2
        List<Genome> pop1 = new ArrayList<>();
        List<Genome> pop2 = new ArrayList<>();
        EvolutionUtils.initPopulation(pop1, popSize, numValues);
        EvolutionUtils.initPopulation(pop2, popSize, numValues);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pop1, pop2);
            // select next generation
            pop1 = EvolutionUtils.selection(pop1);
            pop2 = EvolutionUtils.selection(pop2);
            EvolutionUtils.mutate(pop1);
            EvolutionUtils.mutate(pop2);
        }
        fitnessEval(pop1, pop2);
        System.out.println(pop1.get(0));
        System.out.println(pop2.get(0));
    }

    /**
     * Perform round robin evalaution between the two populations. Each member of one population
     * plays all members of the other.
     * 
     * @param pop1 List of Genomes optimizing P1
     * @param pop2 List of Genomes optimizing P2
     */
    private void fitnessEval(List<Genome> pop1, List<Genome> pop2) {
        for (Genome g1 : pop1) {
            for (Genome g2 : pop2) {
                Arena.compete(g1, g2);
            }
        }
        // pop1's fitness is P1 wins, pop2's fitness is P2 wins
        for (Genome g : pop1) {
            g.fitness = g.p1;
        }
        for (Genome g : pop2) {
            g.fitness = g.p2;
        }
        pop1.sort(Genome.compare);
        pop2.sort(Genome.compare);
    }
}
