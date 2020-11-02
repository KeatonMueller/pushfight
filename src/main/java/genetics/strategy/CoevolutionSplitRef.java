package main.java.genetics.strategy;

import java.util.ArrayList;
import java.util.List;

import main.java.genetics.Arena;
import main.java.genetics.EvolutionUtils;
import main.java.genetics.Genome;
import main.java.util.HeuristicUtils;

public class CoevolutionSplitRef {
    /**
     * Weights after 2 days of training for P1
     */
    public static double[] p1Weights = {0.8445876921792111, 0.8970492450753385, 0.9080945965208406,
            0.14852226490091258, -0.2540021511646249, 0.4553156647079748, 0.10447428668541514};
    /**
     * Weights after 2 days of training for P1
     */
    public static double[] p2Weights = {0.429423050881819, 0.7135061645010079, 0.3595997234686382,
            0.5104436521900898, 0.27424875411762906, 0.7611561274508133, 0.8775339091415143};

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
    public CoevolutionSplitRef(int time, int size, int num) {
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
        // initialize list of Genomes to evaluate the populations against
        List<Genome> ref1 = new ArrayList<>();
        List<Genome> ref2 = new ArrayList<>();
        ref1.add(new Genome(HeuristicUtils.defaultValues));
        ref2.add(new Genome(HeuristicUtils.defaultValues));

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pop1, pop2, ref1, ref2);
            // select next generation
            pop1 = EvolutionUtils.selection(pop1);
            pop2 = EvolutionUtils.selection(pop2);
            EvolutionUtils.mutate(pop1);
            EvolutionUtils.mutate(pop2);
        }
        fitnessEval(pop1, pop2, ref1, ref2);
        System.out.println(pop1.get(0));
        System.out.println(pop2.get(0));
    }

    /**
     * Evaluate the two populations against the two reference populations. popX is a population
     * optimizing play for player X. refX contains reference genomes to train popX against.
     * 
     * @param pop1 List of Genomes optimizing P1
     * @param pop2 List of Genomes optimizing P2
     * @param ref1 List of Genomes to evaluate pop1 against
     * @param ref2 List of Genomes to evaluate pop2 against
     */
    private void fitnessEval(List<Genome> pop1, List<Genome> pop2, List<Genome> ref1,
            List<Genome> ref2) {
        // pop1 competes against ref1
        for (Genome g1 : pop1) {
            for (Genome g2 : ref1) {
                Arena.compete(g1, g2);
            }
        }
        // pop2 competes against ref2
        for (Genome g1 : pop2) {
            for (Genome g2 : ref2) {
                Arena.compete(g2, g1);
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

        // update reference lists
        ref1.subList(1, ref1.size()).clear();
        ref2.subList(1, ref2.size()).clear();
        for (int i = 0; i < EvolutionUtils.referenceSize && i < popSize; i++) {
            ref1.add(new Genome(pop2.get(i).values));
            ref2.add(new Genome(pop1.get(i).values));
        }
    }
}
