package main.java.genetics.strategy;

import java.util.ArrayList;
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
public class CoopCoevolutionSplitRef {
    /**
     * Weights after 2 days of training for P1
     */
    public static double[] p1Weights = {0.08702999916194687, 0.38117182901877755,
            0.9410495698473001, 0.23465403382390326, -0.21448059743199832, 0.9324404862458893,
            0.9444419247407683, 0.3672292994561883, -0.13613327789988228, 0.48829843279302954,
            -0.8704764531023508, 0.33159356883743496, -0.21273849473536677, -0.22853814810594097,
            -0.6980073481666975, 0.8517581358356076, 0.8120886190289867, -0.4760964743304479,
            0.8771285066628006, 0.4628568544468201};
    /**
     * Weights after 2 days of training for P2
     */
    public static double[] p2Weights = {-0.054066086333050434, -0.32144665750632906,
            0.8677396897215788, -0.5457141576521409, 0.7623371837957481, 0.0833230984637463,
            0.9876907027520831, -0.9283471201065139, 0.9554798162757604, -0.2751366034350671,
            -0.24062142066379, 0.17987810979263474, 0.39865684189201, -0.6764106188481789,
            -0.7635185179184032, 0.3736084203359762, 0.7200297557234197, -0.7686058309308321,
            -0.7530362830628732, 0.03114125382786348};

    private long timeLimit;
    private int popSize;

    /**
     * Begin genetic evolution of heuristic parameters
     * 
     * @param time Time limit (in seconds) for evolution to run
     * @param size Size of population
     */
    public CoopCoevolutionSplitRef(int time, int size) {
        timeLimit = time * 1000; // convert seconds to milliseconds
        popSize = size;
        evolve();
    }

    /**
     * Main loop for genetic evolution
     */
    private void evolve() {
        // list of four different populations
        // pops[0] evolves component weights for P1
        // pops[1] evolves position weights for P1
        // pops[2] evolves component weights for P2
        // pops[3] evolves position weights for P2
        List<List<Genome>> pops = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pops.add(new ArrayList<>());
            if (i % 2 == 0)
                EvolutionUtils.initPopulation(pops.get(i), popSize, HeuristicUtils.numComponents);
            else
                EvolutionUtils.initPopulation(pops.get(i), popSize, HeuristicUtils.numPositions);
        }

        // list of reference populations for each population being evolved
        List<List<Genome>> refs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            refs.add(new ArrayList<>());
            if (i % 2 == 0)
                refs.get(i).add(new Genome(Genome.DefaultType.COMPONENTS));
            else
                refs.get(i).add(new Genome(Genome.DefaultType.POSITIONS));
        }

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            // run fitness function
            fitnessEval(pops, refs);
            // select next generation
            for (int i = 0; i < 4; i++) {
                pops.set(i, EvolutionUtils.selection(pops.get(i)));
                EvolutionUtils.mutate(pops.get(i));
            }
        }
        fitnessEval(pops, refs);
        for (List<Genome> pop : pops) {
            System.out.println(pop.get(0));
        }
    }

    /**
     * Evaluate the four independent populations with a cooperative, subjective measure
     * 
     * @param pops List of populations being evolved
     * @param refs List of reference populations
     */
    private void fitnessEval(List<List<Genome>> pops, List<List<Genome>> refs) {
        int i, j;
        Genome g1a, g1b, g2a, g2b;
        // P1 populations play against P2 reference populations
        for (i = 0; i < popSize; i++) {
            g1a = pops.get(0).get(i);
            g1b = pops.get(1).get(i);
            for (Genome g2 : refs.get(2)) {
                Arena.compete(g1a, refs.get(1).get(0), g2, refs.get(3).get(0));
                Arena.compete(refs.get(0).get(0), g1b, g2, refs.get(3).get(0));
            }
            for (Genome g2 : refs.get(3)) {
                Arena.compete(g1a, refs.get(1).get(0), refs.get(2).get(0), g2);
                Arena.compete(refs.get(0).get(0), g1b, refs.get(2).get(0), g2);
            }
        }
        // P2 populations play against P1 reference populations
        for (i = 0; i < popSize; i++) {
            g2a = pops.get(2).get(i);
            g2b = pops.get(3).get(i);
            for (Genome g1 : refs.get(0)) {
                Arena.compete(g1, refs.get(1).get(0), g2a, refs.get(3).get(0));
                Arena.compete(g1, refs.get(1).get(0), refs.get(2).get(0), g2b);
            }
            for (Genome g1 : refs.get(1)) {
                Arena.compete(refs.get(0).get(0), g1, g2a, refs.get(3).get(0));
                Arena.compete(refs.get(0).get(0), g1, refs.get(2).get(0), g2b);
            }
        }
        // for each population
        for (i = 0; i < 4; i++) {
            // assign fitness values
            if (i < 2)
                for (Genome g : pops.get(i))
                    g.fitness = g.p1;
            else
                for (Genome g : pops.get(i))
                    g.fitness = g.p2;

            // sort based on fitness
            pops.get(i).sort(Genome.compare);

            // update reference lists
            refs.get(i).subList(1, refs.get(i).size()).clear();
            for (j = 0; j < EvolutionUtils.referenceSize && j < popSize; j++) {
                refs.get(i).add(new Genome(pops.get(i).get(j).values));
            }
        }
    }
}
