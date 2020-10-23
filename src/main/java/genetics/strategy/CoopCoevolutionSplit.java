package main.java.genetics.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.board.heuristic.HeuristicUtils;
import main.java.genetics.Arena;
import main.java.genetics.EvolutionUtils;
import main.java.genetics.Genome;

/**
 * Cooperative coevolutionary strategy that evolves four populations. One for P1 components, one for
 * P1 positions, one for P2 components, and one for P2 positions. Fitness evaluation is done within
 * members of the same generation, creating pairs using the best member from the previous
 * generation.
 */
public class CoopCoevolutionSplit {
    private long timeLimit;
    private int popSize;

    /**
     * Begin genetic evolution of heuristic parameters
     * 
     * @param time Time limit (in seconds) for evolution to run
     * @param size Size of population
     */
    public CoopCoevolutionSplit(int time, int size) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        evolve();
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolve() {
        // popNa evolves component weights for player N
        // popNb evolves position weights for player N
        List<Genome> pop1a = new ArrayList<>();
        List<Genome> pop1b = new ArrayList<>();
        List<Genome> pop2a = new ArrayList<>();
        List<Genome> pop2b = new ArrayList<>();
        EvolutionUtils.initPopulation(pop1a, popSize, HeuristicUtils.numComponents);
        EvolutionUtils.initPopulation(pop1b, popSize, HeuristicUtils.numPositions);
        EvolutionUtils.initPopulation(pop2a, popSize, HeuristicUtils.numComponents);
        EvolutionUtils.initPopulation(pop2b, popSize, HeuristicUtils.numPositions);
        // initialize best Genomes for each population
        double[] defaultComponents =
                Arrays.copyOfRange(HeuristicUtils.defaultValues, 0, HeuristicUtils.numComponents);
        double[] defaultPositions = Arrays.copyOfRange(HeuristicUtils.defaultValues,
                HeuristicUtils.numComponents, HeuristicUtils.numValues);
        List<Genome> best = new ArrayList<>();
        best.add(new Genome(defaultComponents));
        best.add(new Genome(defaultPositions));
        best.add(new Genome(defaultComponents));
        best.add(new Genome(defaultPositions));

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pop1a, pop1b, pop2a, pop2b, best);
            // select next generation
            pop1a = EvolutionUtils.selection(pop1a);
            pop1b = EvolutionUtils.selection(pop1b);
            pop2a = EvolutionUtils.selection(pop2a);
            pop2b = EvolutionUtils.selection(pop2b);
            EvolutionUtils.mutate(pop1a);
            EvolutionUtils.mutate(pop1b);
            EvolutionUtils.mutate(pop2a);
            EvolutionUtils.mutate(pop2b);
        }
        fitnessEval(pop1a, pop1b, pop2a, pop2b, best);
        System.out.println(pop1a.get(0));
        System.out.println(pop1b.get(0));
        System.out.println(pop2a.get(0));
        System.out.println(pop2b.get(0));
    }

    /**
     * Evaluate the four independent populations with a cooperative, subjective measure
     * 
     * @param pop1a List of Genomes for p1 component weights
     * @param pop1b List of Genomes for p1 position weights
     * @param pop2a List of Genomes for p2 component weights
     * @param pop2b List of Genomes for p2 position weights
     * @param best  List of Genomes for previous best from pop1a, pop1b, pop2a, pop2b, in that order
     */
    private void fitnessEval(List<Genome> pop1a, List<Genome> pop1b, List<Genome> pop2a,
            List<Genome> pop2b, List<Genome> best) {
        for (Genome g1a : pop1a) {
            for (Genome g2a : pop2a) {
                Arena.compete(g1a, best.get(1), g2a, best.get(3));
            }
            for (Genome g2b : pop2b) {
                Arena.compete(g1a, best.get(1), best.get(2), g2b);
            }
        }
        for (Genome g1b : pop1b) {
            for (Genome g2a : pop2a) {
                Arena.compete(best.get(0), g1b, g2a, best.get(3));
            }
            for (Genome g2b : pop2b) {
                Arena.compete(best.get(0), g1b, best.get(2), g2b);
            }
        }

        for (Genome g : pop1a) {
            g.fitness = g.p1;
        }
        for (Genome g : pop1b) {
            g.fitness = g.p1;
        }
        for (Genome g : pop2a) {
            g.fitness = g.p2;
        }
        for (Genome g : pop2b) {
            g.fitness = g.p2;
        }
        pop1a.sort(Genome.compare);
        pop1b.sort(Genome.compare);
        pop2a.sort(Genome.compare);
        pop2b.sort(Genome.compare);

        best.clear();
        best.add(new Genome(pop1a.get(0).values));
        best.add(new Genome(pop1b.get(0).values));
        best.add(new Genome(pop2a.get(0).values));
        best.add(new Genome(pop2b.get(0).values));
    }
}
