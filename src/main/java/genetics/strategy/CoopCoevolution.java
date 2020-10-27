package main.java.genetics.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.board.heuristic.HeuristicUtils;
import main.java.genetics.Arena;
import main.java.genetics.EvolutionUtils;
import main.java.genetics.Genome;

/**
 * Cooperative coevolutionary strategy that evolves two populations. One for the components, and one
 * for the positions. Fitness evaluation is done within members of the same generation, creating
 * pairs using the best member from the previous generation.
 */
public class CoopCoevolution {
    /**
     * Weights after 8 hours of training
     */
    public static double[] weights = {-0.776162659738739, -0.3549946163174271, -0.4945135714318394,
            -0.5126436728426429, 0.7837807365029039, 0.8181361806915345, 0.1561907410439045,
            0.24423959457745026, 0.6046054197371902, 0.8095602181130259, 0.8536854380381231,
            -0.4172114452237199, 0.38559101796554085, -0.5411274337275029, -0.8922206904646692,
            0.5883683042692811, 0.8463679305068612, -0.5924699580833095, -0.6283127361884846,
            0.08733782182401773};

    private long timeLimit;
    private int popSize;

    /**
     * Begin genetic evolution of heuristic parameters
     * 
     * @param time Time limit (in seconds) for evolution to run
     * @param size Size of population
     */
    public CoopCoevolution(int time, int size) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        evolve();
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
}
