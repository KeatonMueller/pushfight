package main.java.genetics;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EvolutionUtils {
    /**
     * Random object to aid in evolution
     */
    private static Random rand = new Random();
    /**
     * The depth to run minimax algorithm when doing fitness function
     */
    public static int fitnessDepth = 1;

    /**
     * The number of previous generation genomes to use as reference genomes when evaluating next
     * generation
     */
    public static int referenceSize = 5;

    /**
     * Initialize the population randomly
     * 
     * @param pop       Empty list of Genomes to populate
     * @param popSize   Number of individuals to initialize
     * @param numValues Number of values to initialize per genome
     */
    public static void initPopulation(List<Genome> pop, int popSize, int numValues) {
        for (int i = 0; i < popSize; i++) {
            pop.add(new Genome(numValues));
        }
    }

    /**
     * Pretty tame mutation function. 20% chance to randomly change a single weight
     * 
     * @param pop Population to mutate
     */
    public static void mutate(List<Genome> pop) {
        int numValues = pop.get(0).values.length;
        for (Genome g : pop) {
            if (rand.nextDouble() < 0.2) {
                g.values[rand.nextInt(numValues)] = rand.nextDouble();
            }
        }
    }

    /**
     * Select next generation of genomes, using elitism, roulette wheel selection, and uniform
     * crossover
     * 
     * @param lastGen Previous generation of genomes
     * @param rand    Random object to aid selection
     * @return Next generation of genomes
     */
    public static List<Genome> selection(List<Genome> lastGen) {
        printPopFitness(lastGen);
        int popSize = lastGen.size();
        // initialize next generation
        List<Genome> nextGen = new ArrayList<>();
        // 20% elitism
        int keep = popSize / 5;
        // ensure remaining popSize to select is even
        if ((popSize - keep) % 2 != 0)
            keep++;
        EvolutionUtils.roulette(lastGen, nextGen, (popSize - keep) / 2);

        for (int i = 0; i < keep; i++) {
            nextGen.add(lastGen.get(i));
            // reset fitness for genomes that stay
            nextGen.get(popSize - keep + i).fitness = 0;
            nextGen.get(popSize - keep + i).p1 = 0;
            nextGen.get(popSize - keep + i).p2 = 0;
        }

        return nextGen;
    }

    /**
     * Perform roulette wheel selection. Based on:
     * https://github.com/dwdyer/watchmaker/blob/master/framework/src/java/main/org/uncommons/watchmaker/framework/selection/RouletteWheelSelection.java
     * 
     * @param lastGen       Previous generation of Genomes
     * @param nextGen       Next generation of Genomes
     * @param selectionSize Number of pairs of parents to select for crossover
     * @param rand          Random object to aid roulette wheel selection
     */
    public static void roulette(List<Genome> lastGen, List<Genome> nextGen, int selectionSize) {
        int i;
        // calculate cumulative fitness of genomes
        double[] cumulativeFitness = new double[lastGen.size()];
        cumulativeFitness[0] = lastGen.get(0).fitness;
        for (i = 1; i < lastGen.size(); i++) {
            cumulativeFitness[i] = cumulativeFitness[i - 1] + lastGen.get(i).fitness;
        }

        // select two parents and perform crossover
        double highestCumulative = cumulativeFitness[cumulativeFitness.length - 1];
        double randFitness;
        int idx;
        Genome g1, g2;
        for (i = 0; i < selectionSize; i++) {
            // select parent 1
            randFitness = rand.nextDouble() * highestCumulative;
            idx = Arrays.binarySearch(cumulativeFitness, randFitness);
            if (idx < 0) {
                idx = Math.abs(idx + 1);
            }
            g1 = lastGen.get(idx);
            // select parent 2
            randFitness = rand.nextDouble() * highestCumulative;
            idx = Arrays.binarySearch(cumulativeFitness, randFitness);
            if (idx < 0) {
                idx = Math.abs(idx + 1);
            }
            g2 = lastGen.get(idx);
            // crossover and add to population
            uniformCrossover(nextGen, g1, g2);
        }
    }

    /**
     * Perform uniform crossover on the two genomes, producing two offspring
     * 
     * @param pop  The population to add the offspring to
     * @param g1   Genome of parent 1
     * @param g2   Genome of parent 2
     * @param rand Random object to aid crossover
     */
    public static void uniformCrossover(List<Genome> pop, Genome g1, Genome g2) {
        int numValues = g1.values.length;
        double[] values1 = new double[numValues];
        double[] values2 = new double[numValues];
        for (int i = 0; i < numValues; i++) {
            if (rand.nextDouble() < 0.5) {
                values1[i] = g1.values[i];
                values2[i] = g2.values[i];
            } else {
                values1[i] = g2.values[i];
                values2[i] = g1.values[i];
            }
        }
        pop.add(new Genome(values1));
        pop.add(new Genome(values2));
    }

    /**
     * For testing purposes only. Print the fitness values of the given population. Prints at most
     * 10 members, showing the difference between p1 wins and p2 wins.
     * 
     * @param pop Population to print fitness values of
     */
    public static void printPopFitness(List<Genome> pop) {
        int length = Math.min(10, pop.size());
        for (int i = 0; i < length - 1; i++) {
            System.out
                    .print(pop.get(i).fitness + " (" + pop.get(i).p1 + "|" + pop.get(i).p2 + "), ");
        }
        System.out.println(pop.get(length - 1).fitness + " (" + pop.get(length - 1).p1 + "|"
                + pop.get(length - 1).p2 + ")");
    }
}
