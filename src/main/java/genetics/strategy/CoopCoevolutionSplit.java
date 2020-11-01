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
    /**
     * Weights after 2 days of training for P1
     */
    public static double[] p1Weights = {0.395147772998111, 0.7307726284557222, 0.2688364668960008,
            0.829423678719572, -0.156052979963627, 0.22254061215543464, 0.7010022528454649,
            -0.34542812426741976, 0.6898757623200715, 0.3005710044591088, 0.4820513958259449,
            0.13925473881035355, -0.6734281536428415, 0.12371090566103882, -0.04757918433770958,
            0.5182167962247988, 0.6776095794903543, 0.19208523488496398, 0.9157004253519216,
            0.8719888717307112};
    /**
     * Weights after 2 days of training for P2
     */
    public static double[] p2Weights = {0.08058785120565082, 0.08642034443518098,
            0.5062659099510358, -0.14167730210982854, 0.33359484535449635, 0.23075346084406712,
            0.8013225337623995, 0.5708228259825511, 0.633784095768479, 0.1794408062328794,
            0.6193099087193182, 0.837836332901041, 0.3656329909669698, 0.8486559694522847,
            0.38385621373662504, -0.6809709378242104, -0.7952076555786651, -0.4430367631903014,
            0.08900749634995586, -0.32557736324222364};

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
