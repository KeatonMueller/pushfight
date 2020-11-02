package main.java.agents.oep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import main.java.agents.Agent;
import main.java.board.Bitboard;
import main.java.board.Heuristic;
import main.java.util.BitboardUtils;
import main.java.util.GameUtils;

public class OEPAgent extends Agent {
    private static final long TIME_BUDGET = 5 * 1000L; // 5 seconds per move
    private static final int POPULATION_SIZE = 100; // this number should always be even

    private static Heuristic h = new Heuristic();

    private static Comparator<Genome> compareP1 = new Comparator<>() {
        @Override
        public int compare(Genome g1, Genome g2) {
            if (g1.value < g2.value)
                return 1;
            else if (g1.value == g2.value)
                return 0;
            else
                return -1;
        }
    };

    private static Comparator<Genome> compareP2 = new Comparator<>() {
        @Override
        public int compare(Genome g1, Genome g2) {
            if (g1.value < g2.value)
                return -1;
            else if (g1.value == g2.value)
                return 0;
            else
                return 1;
        }
    };

    private Random rand;

    public OEPAgent() {
        rand = new Random();
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        List<Genome> population = new ArrayList<>();
        initPopulation(population, board, turn);
        Bitboard state = board.getState();

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < TIME_BUDGET) {
            for (Genome gen : population) {
                BitboardUtils.decodeActions(board, gen.actions, turn);
                if (gen.visits == 0) {
                    try {
                        gen.value = h.heuristic(board);
                    } catch (Exception e) {
                        System.out.println("Failed");
                        for (int a : gen.actions) {
                            System.out.println(a);
                        }
                        board.show();
                        System.exit(1);

                    }
                }
                gen.visits++;
                board.restoreState(state);
            }
            // sort based on heuristic value
            if (turn == 0)
                population.sort(compareP1);
            else
                population.sort(compareP2);
            // 50% elitism
            population.subList(population.size() / 2, population.size()).clear();
            // mutation and crossover
            population = procreate(population, board, turn);
        }

        BitboardUtils.decodeActions(state, population.get(0).actions, turn);
        return state;
    }

    public void initPopulation(List<Genome> population, Bitboard board, int turn) {
        Genome gen;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            gen = new Genome();
            gen.actions = randomActions(board, turn);
            population.add(gen);
        }
    }

    public int[] randomActions(Bitboard board, int turn) {
        int[] actions = new int[GameUtils.NUM_SLIDES + 1];
        Set<Bitboard> seenStates = new HashSet<>();

        Bitboard initState = board.getState();
        int i, push = -1;
        while (push == -1) {
            for (i = 0; i < GameUtils.NUM_SLIDES; i++) {
                actions[i] = randomSlide(board, turn);
                BitboardUtils.decodeSlide(board, actions[i], turn);
            }

            push = randomPush(board, turn);
            if (push == -1) {
                board.restoreState(initState);
                continue;
            }

            BitboardUtils.decodePush(board, push, turn);
            if (seenStates.contains(board)) {
                board.restoreState(initState);
                continue;
            }

            actions[actions.length - 1] = push;
            seenStates.add(board.getState());
            board.restoreState(initState);
            break;
        }
        return actions;
    }

    public int randomSlide(Bitboard board, int turn) {
        List<Integer> actions = BitboardUtils.getSlideActions(board, turn);
        return actions.get(rand.nextInt(actions.size()));
    }

    public int randomPush(Bitboard board, int turn) {
        List<Integer> actions = BitboardUtils.getPushActions(board, turn);

        if (actions.size() == 0)
            return -1;

        return actions.get(rand.nextInt(actions.size()));
    }

    public List<Genome> procreate(List<Genome> prevGen, Bitboard board, int turn) {
        List<Genome> nextGen = new ArrayList<>();

        Genome parent1, parent2;
        while (!prevGen.isEmpty()) {
            // select two random parents from the previous generation
            parent1 = prevGen.remove(rand.nextInt(prevGen.size()));
            parent2 = prevGen.remove(rand.nextInt(prevGen.size()));
            // (elitism) keep them for the next generation
            nextGen.add(parent1);
            nextGen.add(parent2);
            // (crossover) have each set of parents product two offspring
            nextGen.add(crossover(parent1, parent2, board, turn));
            nextGen.add(crossover(parent1, parent2, board, turn));
        }

        return nextGen;
    }

    public Genome crossover(Genome p1, Genome p2, Bitboard board, int turn) {
        Genome child = new Genome();
        Bitboard state = board.getState();
        int numActions = GameUtils.NUM_SLIDES + 1;
        child.actions = new int[numActions];
        for (int i = 0; i < numActions - 1; i++) {
            if (rand.nextInt(2) == 0) {
                if (BitboardUtils.isValidSlide(state, p1.actions[i], turn)) {
                    child.actions[i] = p1.actions[i];
                } else if (BitboardUtils.isValidSlide(state, p2.actions[i], turn)) {
                    child.actions[i] = p2.actions[i];
                } else {
                    child.actions[i] = randomSlide(state, turn);
                }
            } else {
                if (BitboardUtils.isValidSlide(state, p2.actions[i], turn)) {
                    child.actions[i] = p2.actions[i];
                } else if (BitboardUtils.isValidSlide(state, p1.actions[i], turn)) {
                    child.actions[i] = p1.actions[i];
                } else {
                    child.actions[i] = randomSlide(state, turn);
                }
            }
            BitboardUtils.decodeSlide(state, child.actions[i], turn);
        }
        if (rand.nextInt(2) == 0) {
            if (BitboardUtils.isValidPush(state, p1.actions[numActions - 1], turn)) {
                child.actions[numActions - 1] = p1.actions[numActions - 1];
            } else if (BitboardUtils.isValidPush(state, p2.actions[numActions - 1], turn)) {
                child.actions[numActions - 1] = p2.actions[numActions - 1];
            } else {
                child.actions[numActions - 1] = randomPush(state, turn);
            }
        } else {
            if (BitboardUtils.isValidPush(state, p2.actions[numActions - 1], turn)) {
                child.actions[numActions - 1] = p2.actions[numActions - 1];
            } else if (BitboardUtils.isValidPush(state, p1.actions[numActions - 1], turn)) {
                child.actions[numActions - 1] = p1.actions[numActions - 1];
            } else {
                child.actions[numActions - 1] = randomPush(state, turn);
            }
        }
        if (child.actions[numActions - 1] == -1) {
            child.actions = randomActions(board, turn);
        }
        return child;
    }
}
