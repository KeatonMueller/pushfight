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
     * Weights after 8 hours of training for P1
     */
    public static double[] p1Weights = {0.5948264151331693, 0.5281978608286046, 0.9198849691855848,
            0.7341619221350603, 0.45965805980322383, -0.966993257287341, 0.2838820310736929,
            -0.317054602272258, 0.36681487898337073, 0.7451214510606163, -0.4720574004882079,
            0.9583151548878497, 0.8174803160301085, -0.05987966977442505, 0.038251170966599024,
            -0.8255595998007486, 0.8676272824117315, -0.6637404248715781, 0.7791294201936991,
            0.8734961364970302};
    /**
     * Weights after 8 hours of training for P2
     */
    public static double[] p2Weights = {0.5380158267272979, 0.987820830723563, 0.844076453814476,
            0.045195988945210264, 0.057650189060422186, 0.6271467823198229, 0.8683121611452331,
            0.4235337478583914, 0.4418245332594408, -0.8086455369024759, 0.025863726709760337,
            -0.372808003875714, 0.9726408949962617, 0.5598255358234272, -0.9299163766483127,
            0.44098961133117975, 0.6996761804761772, 0.7194769712270498, -0.02239087160044062,
            0.9453451139177038};

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
