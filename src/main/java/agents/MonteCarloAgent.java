package main.java.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;

/**
 * Agent to find next move using Monte-Carlo Tree Search.
 * 
 * Adapted from my own homework from CPSC 474.
 */
public class MonteCarloAgent extends Agent {
    /**
     * Data structure to maintain the game tree that the agent explores
     */
    private class Tree {
        Node root; // root of tree
        Map<Bitboard, Node> map; // map from board positions to corresponding Nodes

        /**
         * Initialize game tree with given board position at the root
         * 
         * @param rootPos Root board position
         * @param turn    Turn indicator
         */
        public Tree(Bitboard rootPos, int turn) {
            root = new Node(rootPos, turn);
            map = new HashMap<>();
            map.put(rootPos, root);
        }

        /**
         * Get the Node corresponding to the given board state, or create a new one if none.
         * 
         * @param board The board to get the node for
         * @param turn  Turn indicator
         * @return The corresponding Node
         */
        public Node getNode(Bitboard board, int turn) {
            if (map.containsKey(board)) {
                return map.get(board);
            }
            Node node = new Node(board, turn);
            map.put(board, node);
            return node;
        }
    }

    /**
     * Data structure for individual nodes in the game tree
     */
    private class Node {
        private List<Node> children; // (visited) children of current node
        private List<Node> parents; // list of parents (there can be more than one)
        private Node chosenParent; // specific parent chosen during a playout

        private Bitboard board; // board state associated with this node
        private boolean isTerminal; // whether or not node is terminal
        private boolean isFullyExpanded; // whether or not node is fully expanded
        private List<Bitboard> unexplored; // list of unexplored children
        private int totalVisits; // number of times current node has been visited
        private Map<Node, Stats> childToStats; // map from child node to stats

        /**
         * Initialize new Node for given board state
         * 
         * @param board Board state
         * @param turn  Turn indicator
         */
        public Node(Bitboard board, int turn) {
            this.board = board;
            this.isTerminal = BitboardUtils.checkWinner(board) != -1;
            this.unexplored = new ArrayList<>(BitboardUtils.getNextStates(board, turn));
            this.isFullyExpanded = this.unexplored.size() == 0;
            this.children = new ArrayList<>();
            this.parents = new ArrayList<>();
            this.totalVisits = 0;
            this.childToStats = new HashMap<>();
        }

        @Override
        public int hashCode() {
            return board.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;

            Node other = (Node) obj;
            return this.board.equals(other.board);
        }
    }

    /**
     * Stats object to track statistics for EDGES in the game tree
     */
    private class Stats {
        private int numPlays; // number of times this action/edge has been played
        private int totalReward; // aggregate reward seen along edge

        /**
         * Initialize Stats object with zero values
         */
        public Stats() {
            this.numPlays = 0;
            this.totalReward = 0;
        }
    }

    private long timeLimit; // time allowed to explore game tree
    private Random rand; // Random object used for random playouts
    private int turn; // turn indicator

    /**
     * Initialize Monte-Carlo Tree Search agent with given time limit
     * 
     * @param timeLimit Max time (in seconds) allowed per move
     */
    public MonteCarloAgent(long timeLimit) {
        this.timeLimit = timeLimit * 1000; // convert seconds to milliseconds
        this.rand = new Random();
    }

    /**
     * Initialize Monte-Carlo Tree Search agent with default time limit of 5 seconds
     */
    public MonteCarloAgent() {
        this.timeLimit = 5000;
        this.rand = new Random();
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        this.turn = turn;
        Tree tree = new Tree(board, this.turn);
        Node leaf;
        int result;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeLimit) {
            leaf = traverse(tree);
            result = playout(leaf);
            updateStats(leaf, result);
        }
        return getBestState(tree.root);
    }

    /**
     * Traverse the game tree by following UCT algorithm
     * 
     * @param tree Tree to traverse
     * @return Leaf Node at the bottom of the traversal
     */
    private Node traverse(Tree tree) {
        Node node = tree.root;
        Node nextNode;

        // follow UCT until you find a non-fully-expanded node
        while (node.isFullyExpanded && !node.isTerminal) {
            nextNode = bestUCT(node);
            nextNode.chosenParent = node;
            node = nextNode;
            turn = 1 - turn;
        }
        if (node.isTerminal) {
            return node;
        }

        // choose first unexplored child
        Bitboard nextBoard = node.unexplored.remove(0);
        nextNode = tree.getNode(nextBoard, turn);
        node.isFullyExpanded = node.unexplored.size() == 0;

        // add child to node's children and initialize new edge Stats
        node.children.add(nextNode);
        node.childToStats.put(nextNode, new Stats());

        // add node to child's parents and set the chosen parent
        nextNode.parents.add(node);
        nextNode.chosenParent = node;

        return nextNode;
    }

    /**
     * Randomly playout from the given Node to a terminal state
     * 
     * @param node Node to playout from
     * @return Result of playout (1 if p1 win, -1 if p2 win)
     */
    private int playout(Node node) {
        Bitboard board = new Bitboard(node.board);
        int winner;
        while (true) {
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                if (winner == 0)
                    return 1;
                return -1;
            }

            RandomAgent.randomMove(board, turn, rand);
            turn = 1 - turn;
        }
    }

    /**
     * Traverse up the game tree updating node and edge statistics
     * 
     * @param node   Node to traverse from
     * @param result Game result to propagate
     */
    private void updateStats(Node node, int result) {
        // increment number of visits
        node.totalVisits += 1;

        // stop if reached root
        if (node.parents.size() == 0)
            return;

        Node parent = node.chosenParent;
        node.chosenParent = null;

        // update edge statistics
        Stats stats = parent.childToStats.get(node);
        stats.numPlays += 1;
        stats.totalReward += result;

        // recurse
        updateStats(parent, result);
    }

    /**
     * Get the next child from the given node based on UCT
     * 
     * @param node Node to perform UCT on
     * @return Next node to explore
     */
    private Node bestUCT(Node node) {
        Node bestNode = null;
        double bestUCB, ucb, avgReward;
        int totalReward, totalPlays;
        Stats stats;
        if (turn == 0) {
            bestUCB = -Double.MAX_VALUE;
            for (Node child : node.children) {
                totalReward = 0;
                totalPlays = 0;
                for (Node parent : child.parents) {
                    stats = parent.childToStats.get(child);
                    totalReward += stats.totalReward;
                    totalPlays += stats.numPlays;
                }
                avgReward = (double) totalReward / totalPlays;
                stats = node.childToStats.get(child);
                ucb = avgReward + Math.pow(2 * Math.log(node.totalVisits) / stats.numPlays, 0.5);

                if (ucb > bestUCB) {
                    bestUCB = ucb;
                    bestNode = child;
                }
            }
        } else {
            bestUCB = Double.MAX_VALUE;
            for (Node child : node.children) {
                totalReward = 0;
                totalPlays = 0;
                for (Node parent : child.parents) {
                    stats = parent.childToStats.get(child);
                    totalReward += stats.totalReward;
                    totalPlays += stats.numPlays;
                }
                avgReward = (double) totalReward / totalPlays;
                stats = node.childToStats.get(child);
                ucb = avgReward - Math.pow(2 * Math.log(node.totalVisits) / stats.numPlays, 0.5);

                if (ucb < bestUCB) {
                    bestUCB = ucb;
                    bestNode = child;
                }
            }
        }
        return bestNode;
    }

    /**
     * Get the best next state from the given node based on observed reward
     * 
     * @param node Node to get best move from
     * @return Bitboard of next state with the highest observed reward
     */
    private Bitboard getBestState(Node node) {
        Node bestNode = null;
        double bestReward, reward;
        Stats stats;
        if (turn == 0) {
            bestReward = -Double.MAX_VALUE;
            for (Node child : node.childToStats.keySet()) {
                stats = node.childToStats.get(child);
                reward = (double) stats.totalReward / stats.numPlays;
                if (reward > bestReward) {
                    bestReward = reward;
                    bestNode = child;
                }
            }
        } else {
            bestReward = Double.MAX_VALUE;
            for (Node child : node.childToStats.keySet()) {
                stats = node.childToStats.get(child);
                reward = (double) stats.totalReward / stats.numPlays;
                if (reward < bestReward) {
                    bestReward = reward;
                    bestNode = child;
                }
            }
        }
        return bestNode.board;
    }
}
