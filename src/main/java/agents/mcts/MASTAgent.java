package main.java.agents.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import main.java.board.Bitboard;
import main.java.board.Move;
import main.java.board.State;
import main.java.util.BitboardUtils;
import main.java.util.SuccessorUtils;

/**
 * Agent using Monte-Carlo Tree Search along with the Move Averaged Sampling Technique enhancement
 * to the default policy.
 */
public class MASTAgent extends VanillaMCTSAgent {
    private final double TAU = 1.0; // tunable parameter for MAST exploration
    private long iterations = 5000; // iterations allowed to explore game tree
    private Random rand = new Random(); // Random object used for random playouts
    private int turn; // turn indicator
    private Map<Move, Stats> moveMap = new HashMap<>();;

    /**
     * Initialize Monte-Carlo Tree Search agent using MAST with given iteration limit
     * 
     * @param iterations Max iterations allowed per move
     */
    public MASTAgent(long iterations) {
        this.iterations = iterations;
    }

    /**
     * Initialize Monte-Carlo Tree Search agent using MAST
     */
    public MASTAgent() {
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        this.turn = turn;
        Tree tree = new Tree(board, this.turn);
        Node leaf;
        int result;
        List<Move> path = new ArrayList<>();
        int i = 0;
        while (i < this.iterations) {
            System.out.print(turn + " " + toString() + " " + i + " traversing  \r");
            leaf = traverse(tree, path);
            System.out.print(turn + " " + toString() + " " + i + " playing out \r");
            result = playout(leaf, path);
            System.out.print(turn + " " + toString() + " " + i + " updating    \r");
            updateStats(leaf, result);
            updateStats(path, result);
            System.out.print(turn + " " + toString() + " " + i + " done        \r");
            i++;
        }
        return getBestState(tree.root);
    }

    /**
     * Traverse the game tree by following UCT algorithm
     * 
     * @param tree Tree to traverse
     * @param path List of Moves used on current traversal
     * @return Leaf Node at the bottom of the traversal
     */
    private Node traverse(Tree tree, List<Move> path) {
        path.clear();
        Node node = tree.root;
        Node nextNode;

        // follow UCT until you find a non-fully-expanded node
        while (node.isFullyExpanded && !node.isTerminal) {
            nextNode = bestUCT(node);
            path.add(nextNode.state.move);
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

        // add node's move to the path
        path.add(state.move);

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
     * @param path List of Moves used on current traversal
     * @return Result of playout (1 if p1 win, -1 if p2 win)
     */
    private int playout(Node node, List<Move> path) {
        Bitboard board = node.state.board;
        int winner, count;
        int turnCount = 0;
        Set<State> nextStates;
        Map<Move, Double> qMap = new HashMap<>();
        double value, randChoice, totalValue = 0.0;
        Stats stats;
        State choice = null;
        Iterator<State> iter;
        boolean found;
        Map<Bitboard, Integer> boardToNum = new HashMap<>();
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
            turnCount += 1;
            if (count >= 5 || turnCount >= 100) {
                return 0;
            }

            nextStates = SuccessorUtils.getSuccessors(board, turn);
            // calculate total value, as well as value for each move
            qMap.clear();
            found = false;
            int sign = turn == 0 ? 1 : -1;
            for (State state : nextStates) {
                if (moveMap.containsKey(state.move)) {
                    stats = moveMap.get(state.move);
                    value = Math.exp(sign * stats.totalReward / stats.numPlays / TAU);
                    totalValue += value;
                    qMap.put(state.move, value);
                } else {
                    // always choose an unexplored move
                    board = state.board;
                    turn = 1 - turn;
                    found = true;
                    path.add(state.move);
                    break;
                }
            }

            if (found) {
                continue;
            }

            randChoice = rand.nextDouble() * totalValue;
            value = 0;
            iter = nextStates.iterator();
            while (iter.hasNext() && value <= randChoice) {
                choice = iter.next();
                value += qMap.get(choice.move);
            }
            board = choice.board;
            path.add(choice.move);
            turn = 1 - turn;
        }
    }

    /**
     * Update stats for the moves taken along the traversal and simulation
     * 
     * @param path   List<Move> of moves taken
     * @param result Result of simulation
     */
    private void updateStats(List<Move> path, int result) {
        Stats stats;
        for (Move move : path) {
            stats = moveMap.getOrDefault(move, new Stats());
            stats.numPlays += 1;
            stats.totalReward += result;
        }
    }

    @Override
    public String toString() {
        return "MAST Agent";
    }
}
