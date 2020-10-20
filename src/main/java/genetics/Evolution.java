package main.java.genetics;

import java.util.ArrayList;
import java.util.List;

import main.java.board.heuristic.HeuristicUtils;

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
            g.fitness = g.p1 + g.p2 - (Math.abs(g.p1 - g.p2));
        }
        pop.sort(Genome.compare);
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

    /**
     * Evaluate the two populations against the two reference populations. popX is a population
     * optimizing play for player X. refX contains reference genomes to train popX against.
     * 
     * @param pop1 List of Genomes optimizing P1
     * @param pop2 List of Genomes optimizing P2
     * @param ref1 List of Genomes to evalute pop1 against
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
