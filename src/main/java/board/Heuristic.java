package main.java.board;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Queue;

import main.java.game.GameUtils;

public class Heuristic {
    /**
     * Hard-coded values of the strength of being in a certain position (very rough values at the
     * moment)
     */
    private static Map<Integer, Integer> boardValues;
    static {
        boardValues = new HashMap<>();
        boardValues.put((1 << 2), -10); // a3
        boardValues.put((1 << 3), 3); // a4
        boardValues.put((1 << 4), 5); // a5
        boardValues.put((1 << 5), 3); // a6
        boardValues.put((1 << 6), -10); // a7
        boardValues.put((1 << 8), -10); // b1
        boardValues.put((1 << 9), -10); // b2
        boardValues.put((1 << 10), 1); // b3
        boardValues.put((1 << 11), 10); // b4
        boardValues.put((1 << 12), 10); // b5
        boardValues.put((1 << 13), 7); // b6
        boardValues.put((1 << 14), 0); // b7
        boardValues.put((1 << 15), -10); // b8
        boardValues.put((1 << 16), -10); // c1
        boardValues.put((1 << 17), 0); // c2
        boardValues.put((1 << 18), 7); // c3
        boardValues.put((1 << 19), 10); // c4
        boardValues.put((1 << 20), 10); // c5
        boardValues.put((1 << 21), 1); // c6
        boardValues.put((1 << 22), -10); // c7
        boardValues.put((1 << 23), -10); // c8
        boardValues.put((1 << 25), -10); // d2
        boardValues.put((1 << 26), 3); // d3
        boardValues.put((1 << 27), 5); // d4
        boardValues.put((1 << 28), 3); // d5
        boardValues.put((1 << 29), -10); // d6
    }
    // data structures to facilitate connected component ownership
    private static Map<Integer, Set<Integer>> posToAdjCCID = new HashMap<>();
    private static Map<Integer, Integer> ccIDToOwner = new HashMap<>();
    private static Map<Integer, Integer> ownerToCCs = new HashMap<>();
    // data structures to facilitate BFS
    private static Queue<Integer> searchQueue = new ArrayDeque<>();
    private static Map<Integer, Integer> prev = new HashMap<>();
    private static Map<Integer, Integer> dist = new HashMap<>();

    private static double squareWeight = 1;
    private static double circleWeight = 2;
    private static double p1MWeight = 1;
    private static double p2MWeight = -1;
    private static double p1PWeight = 1;
    private static double p2PWeight = -1;
    private static double p1CCWeight = -200;
    private static double p2CCWeight = 200;
    private static double p1IWeight = -1000000;
    private static double p2IWeight = 1000000;
    private static double p1UnownedWeight = -100;
    private static double p2UnownedWeight = 100;

    private static int visited;

    /**
     * Evaluate the given board state
     * 
     * @param board The board state to evaluate
     * @return The heuristic evalution. Higher values are better for p1/worse for p2
     */
    public static double heuristic(Bitboard board) {
        double h = 0;

        // mobility
        int p1Mobility = 0;
        int p2Mobility = 0;
        // strength of piece positions
        int p1Position = 0;
        int p2Position = 0;
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
                if (bfs(board, posMask, 0) == 1)
                    if (!board.isSquare(posMask))
                        p1Isolated++;
                p1CC++;
            }
            weight = board.isSquare(posMask) ? squareWeight : circleWeight;
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
                if (bfs(board, posMask, 1) == 1)
                    if (!board.isSquare(posMask))
                        p2Isolated++;
                p2CC++;
            }
            weight = board.isSquare(posMask) ? squareWeight : circleWeight;
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
                        h += p1UnownedWeight * searchDistance;
                    } else if (turn == 1 && searchDistance > GameUtils.NUM_SLIDES) {
                        h += p2UnownedWeight * searchDistance;
                    }
                }
            }
        }


        // a player missing a piece is the ultimate bad position
        if (p1Pieces != 5) {
            return -1000000000000.0;
        }
        if (p2Pieces != 5) {
            return 1000000000000.0;
        }
        // weight the components of the heuristic
        h += p1MWeight * p1Mobility;
        h += p2MWeight * p2Mobility;
        h += p1PWeight * p1Position;
        h += p2PWeight * p2Position;

        // make it so only > 1 connected components impacts heuristic
        h += p1CCWeight * (p1CC - 1);
        h += p2CCWeight * (p2CC - 1);

        h += p1IWeight * p1Isolated;
        h += p2IWeight * p2Isolated;
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
    public static int bfs(Bitboard board, int posMask, int turn) {
        // perform basic BFS to explore the connected component
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
    public static int search(Bitboard board, int posMask, int turn) {
        // if this player doesn't even "own" any connected components, they're in bad shape...
        if (!ownerToCCs.containsKey(turn)) {
            return 1000000;
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
        return 10000;
    }
}
