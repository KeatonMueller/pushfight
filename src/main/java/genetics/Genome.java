package main.java.genetics;

import java.util.Random;

public class Genome {
    private static Random rand = new Random();
    protected double[] values;
    protected double fitness;

    /**
     * Initialize a Genome with random values
     * 
     * @param numValues Number of values to initialize
     */
    public Genome(int numValues) {
        values = new double[numValues];
        for (int i = 0; i < numValues; i++) {
            values[i] = rand.nextDouble();
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
}
