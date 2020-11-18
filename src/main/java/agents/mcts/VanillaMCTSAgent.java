package main.java.agents.mcts;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import main.java.agents.Agent;
import main.java.agents.AgentInterface;
import main.java.agents.RandomAgent;
import main.java.board.Bitboard;
import main.java.board.State;
import main.java.util.BitboardUtils;
// import main.java.util.SuccessorUtils;

/**
 * Agent using vanilla Monte-Carlo Tree Search.
 * 
 * Adapted from my own homework from CPSC 474.
 */
public class VanillaMCTSAgent extends Agent implements AgentInterface {
    protected Map<Bitboard, Integer> boardToNum = new HashMap<>(); // facilitate tie checking
    private long iterations = 5000; // iterations allowed to explore game tree
    protected Random rand = new Random();; // Random object used for random playouts
    protected int turn; // turn indicator

    /**
     * Initialize Monte-Carlo Tree Search agent with given iteration limit
     * 
     * @param iterations Max number of iterations allowed per move
     */
    public VanillaMCTSAgent(long iterations) {
        this.iterations = iterations;
    }

    /**
     * Initialize Monte-Carlo Tree Search agent
     */
    public VanillaMCTSAgent() {
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        this.turn = turn;
        Tree tree = new Tree(board, this.turn);
        Node leaf;
        double result;
        int i = 0;
        while (i < this.iterations) {
            leaf = traverse(tree);
            result = playout(leaf);
            updateStats(leaf, result);
            i++;
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
        State state = node.unexplored.remove(0);
        nextNode = tree.getNode(state, turn);
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
    protected double playout(Node node) {
        boardToNum.clear();
        Bitboard board = new Bitboard(node.state.board);
        int winner, count;
        while (true) {
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                if (winner == 0)
                    return 1;
                return -1;
            }

            // add tie logic in rare case of long loop
            count = boardToNum.getOrDefault(board, 0);
            boardToNum.put(board, count + 1);
            if (count >= 5) {
                return 0;
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
    private void updateStats(Node node, double result) {
        // increment number of visits
        node.totalVisits += 1;

        Node parent;
        Stats stats;
        while (node.chosenParent != null) {
            parent = node.chosenParent;
            node.chosenParent = null;
            parent.totalVisits += 1;

            // update edge statistics
            stats = parent.childToStats.get(node);
            stats.numPlays += 1;
            stats.totalReward += result;

            node = parent;
        }
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
        int totalPlays;
        double totalReward;
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
                avgReward = totalReward / totalPlays;
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
                avgReward = totalReward / totalPlays;
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
                reward = stats.totalReward / stats.numPlays;
                if (reward > bestReward) {
                    bestReward = reward;
                    bestNode = child;
                }
            }
        } else {
            bestReward = Double.MAX_VALUE;
            for (Node child : node.childToStats.keySet()) {
                stats = node.childToStats.get(child);
                reward = stats.totalReward / stats.numPlays;
                if (reward < bestReward) {
                    bestReward = reward;
                    bestNode = child;
                }
            }
        }
        return bestNode.state.board;
    }

    @Override
    public String toString() {
        return "Vanilla MCTS Agent";
    }
}
