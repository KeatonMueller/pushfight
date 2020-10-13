package main.java.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Evolution {
    private static Random rand = new Random();
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
        initPopulation(pop1);
        initPopulation(pop2);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pop1, pop2);
            // select next generation
            pop1 = selection(pop1);
            pop2 = selection(pop2);
            mutate(pop1);
            mutate(pop2);
        }
        fitnessEval(pop1, pop2);
        pop1.sort(Genome.compare);
        pop2.sort(Genome.compare);
        System.out.println(pop1.get(0));
        System.out.println(pop2.get(0));
    }

    /**
     * For testing purposes only. Print the fitness values of the given population
     * 
     * @param pop Population to print fitness values of
     */
    private void printPopFitness(List<Genome> pop) {
        int length = Math.min(10, pop.size());
        for (int i = 0; i < length - 1; i++) {
            System.out
                    .print(pop.get(i).fitness + " (" + pop.get(i).p1 + "|" + pop.get(i).p2 + "), ");
        }
        System.out.println(pop.get(length - 1).fitness + " (" + pop.get(length - 1).p1 + "|"
                + pop.get(length - 1).p2 + ")");
    }

    /**
     * Pretty tame mutation function. 20% chance to randomly change a single weight
     * 
     * @param pop Population to mutate
     */
    private void mutate(List<Genome> pop) {
        for (Genome g : pop) {
            if (rand.nextDouble() < 0.2) {
                g.values[rand.nextInt(g.values.length)] = rand.nextDouble();
            }
        }
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
    }

    /**
     * Select next generation of genomes, using elitism, roulette wheel selection, and uniform
     * crossover
     * 
     * @param lastGen Previous generation of genomes
     * @return Next generation of genomes
     */
    private List<Genome> selection(List<Genome> lastGen) {
        // sort based on decreasing fitness
        lastGen.sort(Genome.compare);
        printPopFitness(lastGen);
        // initialize next generation
        List<Genome> nextGen = new ArrayList<>();
        // 20% elitism
        int keep = popSize / 5;
        // ensure remaining popSize to select is even
        if ((popSize - keep) % 2 != 0)
            keep++;
        roulette(lastGen, nextGen, (popSize - keep) / 2);

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
     */
    private void roulette(List<Genome> lastGen, List<Genome> nextGen, int selectionSize) {
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
            crossover(nextGen, g1, g2);
        }
    }

    /**
     * Perform uniform crossover on the two genomes, producing two offspring
     * 
     * @param pop The population to add the offspring to
     * @param g1  Genome of parent 1
     * @param g2  Genome of parent 2
     */
    private void crossover(List<Genome> pop, Genome g1, Genome g2) {
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
     * Initialize the population randomly
     * 
     * @param pop Empty list of Genomes to populate
     */
    private void initPopulation(List<Genome> pop) {
        for (int i = 0; i < popSize; i++) {
            pop.add(new Genome(numValues));
        }
    }
}
