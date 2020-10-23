package main.java.genetics;

import java.util.Comparator;
import java.util.Random;

public class Genome {
    private static Random rand = new Random();
    public double[] values;
    public double fitness;
    public int p1, p2;

    /**
     * Initialize a Genome with random values
     * 
     * @param numValues Number of values to initialize
     */
    public Genome(int numValues) {
        values = new double[numValues];
        for (int i = 0; i < numValues; i++) {
            values[i] = rand.nextDouble() * 2 - 1;
        }
        fitness = 0;
    }

    /**
     * Initialize Genome with given values
     * 
     * @param vals Values to use for initialization
     */
    public Genome(double[] vals) {
        values = vals;
        fitness = 0;
    }

    /**
     * Comparator to sort Genomes in descending order
     */
    public static Comparator<Genome> compare = new Comparator<Genome>() {
        @Override
        public int compare(Genome g1, Genome g2) {
            if (g1.fitness < g2.fitness)
                return 1;
            else if (g1.fitness == g2.fitness)
                return 0;
            else
                return -1;
        }
    };

    @Override
    public String toString() {
        StringBuilder repr = new StringBuilder();
        for (double val : values) {
            repr.append(val + ",");
        }
        repr.deleteCharAt(repr.length() - 1);
        return repr.toString();
    }
}
