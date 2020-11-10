package main.java.board;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Queue;

import main.java.util.GameUtils;
import main.java.util.HeuristicUtils;
import main.java.util.SearchUtils;

public class Heuristic {
    /**
     * Hard-coded values of the strength of having a piece at a certain position (very rough values
     * at the moment)
     */
    private Map<Integer, Double> boardValues = new HashMap<>();

    // data structures to facilitate connected component ownership
    private Map<Integer, Set<Integer>> posToAdjCCID = new HashMap<>();
    private Map<Integer, Integer> ccIDToOwner = new HashMap<>();
    private Map<Integer, Integer> ownerToCCs = new HashMap<>();
    // data structures to facilitate BFS
    private Queue<Integer> searchQueue = new ArrayDeque<>();
    private Map<Integer, Integer> prev = new HashMap<>();
    private Map<Integer, Integer> dist = new HashMap<>();
    // visited bitmap instance variable
    private int visited;

    // weights for each heuristic component, default values
    private double[] weights = new double[HeuristicUtils.numComponents];
    // weights[0] = square weight
    // weights[1] = circle weight
    // weights[2] = mobility weight
    // weights[3] = piece location weight
    // weights[4] = number of connected components weight
    // weights[5] = isolated circles weight
    // weights[6] = circle distance to "owned" connected component weight

    /**
     * Initialize heuristic with all defaults
     */
    public Heuristic() {
        this(HeuristicUtils.defaultValues);
    }

    /**
     * Initialize heuristic with given values
     * 
     * @param values Array of doubles for heuristic weights and board position values
     */
    public Heuristic(double[] values) {
        // validate length of input
        if (values.length != HeuristicUtils.numValues
                && values.length != HeuristicUtils.numComponents) {
            System.out.println("Invalid values given to heuristic");
            System.exit(1);
        }
        int i;
        // fill in heuristic component weights
        for (i = 0; i < HeuristicUtils.numComponents; i++) {
            weights[i] = values[i];
        }

        // allow the board weights to be optional
        if (values.length == HeuristicUtils.numComponents)
            values = HeuristicUtils.defaultValues;

        // fill in board position values
        double[] initValues = new double[values.length - HeuristicUtils.numComponents];
        for (i = HeuristicUtils.numComponents; i < values.length; i++) {
            initValues[i - HeuristicUtils.numComponents] = values[i];
        }
        HeuristicUtils.initBoardValues(boardValues, initValues);
    }

    /**
     * Initialize heuristic with given values
     * 
     * @param componentWeights Array of doubles for component weights
     * @param positionWeights  Array of doubles for position weights
     */
    public Heuristic(double[] componentWeights, double[] positionWeights) {
        weights = componentWeights;
        HeuristicUtils.initBoardValues(boardValues, positionWeights);
    }

    /**
     * Evaluate the given board state
     * 
     * @param board The board state to evaluate
     * @return The heuristic evalution. Higher values are better for p1/worse for p2
     */
    public double heuristic(Bitboard board) {
        double h = 0;

        // mobility
        int p1Mobility = 0;
        int p2Mobility = 0;
        // strength of piece positions
        double p1Position = 0;
        double p2Position = 0;
        // number of pieces
        int p1Pieces = 0;
        int p2Pieces = 0;
        // number of connected components
        int p1CC = 0;
        int p2CC = 0;
        // number of isolated circle pieces
        int p1Isolated = 0;
        int p2Isolated = 0;

        double weight;

        visited = 0;
        // check p1's pieces
        int posMasks = board.getPieces(0);
        int posMask;
        while (posMasks != 0) {
            posMask = posMasks & ~(posMasks - 1);
            posMasks ^= posMask;
            if ((visited & posMask) == 0) {
                if (exploreCC(board, posMask, 0) == 1)
                    if (!board.isSquare(posMask))
                        p1Isolated++;
                p1CC++;
            }
            weight = board.isSquare(posMask) ? weights[0] : weights[1];
            if (!boardValues.containsKey(posMask)) {
                System.out.println("I don't have " + (int) (Math.log(posMask) / Math.log(2)));
                board.show();
            }
            p1Position += weight * boardValues.get(posMask);
            p1Pieces++;
        }
        // check p2's pieces
        visited = 0;
        posMasks = board.getPieces(1);
        while (posMasks != 0) {
            posMask = posMasks & ~(posMasks - 1);
            posMasks ^= posMask;
            if ((visited & posMask) == 0) {
                if (exploreCC(board, posMask, 1) == 1)
                    if (!board.isSquare(posMask))
                        p2Isolated++;
                p2CC++;
            }
            weight = board.isSquare(posMask) ? weights[0] : weights[1];
            p2Position += weight * boardValues.get(posMask);
            p2Pieces++;
        }

        // perform connected component analysis on empty spaces
        posToAdjCCID.clear();
        ccIDToOwner.clear();
        ownerToCCs.clear();
        int toCheck = (BitMasks.valid & (~board.getPieces()));
        int check;
        int ccId = 0;
        while (toCheck != 0) {
            check = toCheck & ~(toCheck - 1);
            toCheck ^= SearchUtils.checkSpaces(board, check, ccId++, posToAdjCCID, ccIDToOwner,
                    ownerToCCs);
        }

        // check how close each player's circles are to an "owned" connected component
        int circles, circleMask, searchDistance;
        boolean adjacent;
        for (int turn = 0; turn < 2; turn++) {
            circles = board.getCircles(turn);
            while (circles != 0) {
                adjacent = false;
                circleMask = circles & ~(circles - 1);
                circles ^= circleMask;
                if (posToAdjCCID.containsKey(circleMask)) {
                    for (int id : posToAdjCCID.get(circleMask)) {
                        if (ccIDToOwner.get(id) == turn) {
                            adjacent = true;
                            break;
                        }
                    }
                }
                if (!adjacent) {
                    searchDistance = search(board, circleMask, turn);
                    if (turn == 0 && searchDistance > GameUtils.NUM_SLIDES) {
                        h += -weights[6] * searchDistance;
                    } else if (turn == 1 && searchDistance > GameUtils.NUM_SLIDES) {
                        h += weights[6] * searchDistance;
                    }
                }
            }
        }

        // a player missing a piece is the ultimate bad position
        if (p1Pieces != 5) {
            return -10000.0;
        }
        if (p2Pieces != 5) {
            return 10000.0;
        }
        // weight the components of the heuristic
        h += weights[2] * p1Mobility;
        h += -weights[2] * p2Mobility;
        h += weights[3] * p1Position;
        h += -weights[3] * p2Position;

        // make it so only > 1 connected components impacts heuristic
        h += -weights[4] * (p1CC - 1);
        h += weights[4] * (p2CC - 1);

        h += -weights[5] * p1Isolated;
        h += weights[5] * p2Isolated;

        h /= 6200; // normalize
        return h;
    }

    /**
     * Fully explore the connected component that the given position is a part of
     * 
     * @param board Board object to be used
     * @param row   Row to search from
     * @param col   Column to search from
     * @param turn  Turn indicator
     * @return Number of pieces found in the connected component
     */
    private int exploreCC(Bitboard board, int posMask, int turn) {
        // (this isn't a real queue... but we're not doing shortest path so its fine)
        int queue = posMask;
        int ccSize = 0;
        while (queue != 0) {
            posMask = queue & ~(queue - 1);
            queue ^= posMask;

            if ((visited & posMask) != 0)
                continue;

            visited |= posMask;

            if (!board.isValid(posMask))
                continue;

            if (board.owns(posMask, 1 - turn))
                continue;
            else if (board.owns(posMask, turn)) {
                ccSize++;
            }

            queue |= BitMasks.orthogonal.get(posMask);
        }
        return ccSize;
    }

    /**
     * Search for shortest path from given position to an "owned" connected component without
     * traveling through opponent's pieces
     * 
     * @param board   Board to analyze
     * @param posMask Position to search from
     * @param turn    Whose turn it is
     * @return Shortest distance to an "owned" connected component
     */
    private int search(Bitboard board, int posMask, int turn) {
        // if this player doesn't even "own" any connected components, they're in bad shape...
        if (!ownerToCCs.containsKey(turn)) {
            return 100;
        }

        // perform basic BFS to explore the connected component
        // we're finding distance to an "owned" connected component
        int target = ownerToCCs.get(turn);
        int orthogonal, nextMask, altDist;
        int myVisit = 0;

        searchQueue.clear();
        prev.clear();
        dist.clear();

        searchQueue.add(posMask);
        dist.put(posMask, 0);

        while (!searchQueue.isEmpty()) {
            // get next position off of queue
            posMask = searchQueue.poll();

            // ignore if visited, invalid location, or opponent owns
            if ((myVisit & posMask) != 0 || !board.isValid(posMask)
                    || board.owns(posMask, 1 - turn))
                continue;
            myVisit |= posMask;

            // check if it's a target
            if ((target & posMask) != 0) {
                // return distance to posMask
                return dist.get(prev.get(posMask)) + 1;
            }

            // continue BFS
            orthogonal = BitMasks.orthogonal.get(posMask);
            while (orthogonal != 0) {
                nextMask = orthogonal & ~(orthogonal - 1);
                orthogonal ^= nextMask;

                altDist = dist.get(posMask) + 1;
                if (altDist < dist.getOrDefault(nextMask, Integer.MAX_VALUE)) {
                    prev.put(nextMask, posMask);
                    dist.put(nextMask, altDist);
                }
                searchQueue.add(nextMask);
            }
        }
        // no path found to a target (the circle is isolated)
        return 100;
    }
}
