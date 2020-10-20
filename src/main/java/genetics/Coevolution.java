package main.java.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.board.heuristic.HeuristicUtils;

public class Coevolution {
    private long timeLimit;
    private int popSize;

    /**
     * Begin genetic evolution of heuristic parameters
     * 
     * @param time Time limit (in seconds) for evolution to run
     * @param size Size of population
     * @param type Indicator for what type of coevolution to run
     */
    public Coevolution(int time, int size, int type) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        if (type == 1)
            evolve();
        else
            evolveTwo();
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolve() {
        // initialize two populations, one to evolve component weights, one for position weights
        List<Genome> pop1 = new ArrayList<>();
        List<Genome> pop2 = new ArrayList<>();
        EvolutionUtils.initPopulation(pop1, popSize, HeuristicUtils.numComponents);
        EvolutionUtils.initPopulation(pop2, popSize, HeuristicUtils.numPositions);
        // initialize best Genomes for each population
        double[] defaultComponents =
                Arrays.copyOfRange(HeuristicUtils.defaultValues, 0, HeuristicUtils.numComponents);
        double[] defaultPositions = Arrays.copyOfRange(HeuristicUtils.defaultValues,
                HeuristicUtils.numComponents, HeuristicUtils.numValues);
        Genome best1 = new Genome(defaultComponents);
        Genome best2 = new Genome(defaultPositions);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pop1, pop2, best1, best2);
            // select next generation
            pop1 = EvolutionUtils.selection(pop1);
            pop2 = EvolutionUtils.selection(pop2);
            EvolutionUtils.mutate(pop1);
            EvolutionUtils.mutate(pop2);
        }
        fitnessEval(pop1, pop2, best1, best2);
        System.out.println(pop1.get(0));
        System.out.println(pop2.get(0));
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolveTwo() {
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
     * Evaluate the two independent populations with a cooperative, subjective measure
     * 
     * @param pop1  List of Genomes for component weights
     * @param pop2  List of Genomes for position weights
     * @param best1 Genome of previous best from pop1
     * @param best2 Genome of previous best from pop2
     */
    private void fitnessEval(List<Genome> pop1, List<Genome> pop2, Genome best1, Genome best2) {

        for (Genome g1 : pop1) {
            for (Genome g2 : pop2) {
                Arena.compete(g1, best2, best1, g2);
                Arena.compete(best1, g2, g1, best2);
            }
        }

        for (Genome g : pop1) {
            g.fitness = g.p1 + g.p2 - Math.abs(g.p1 - g.p2) / 2;
        }
        for (Genome g : pop2) {
            g.fitness = g.p1 + g.p2 - Math.abs(g.p1 - g.p2) / 2;
        }
        pop1.sort(Genome.compare);
        pop2.sort(Genome.compare);

        best1.values = Arrays.copyOf(pop1.get(0).values, best1.values.length);
        best2.values = Arrays.copyOf(pop2.get(0).values, best2.values.length);
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
