package main.java.agents.mcts;

import java.util.HashMap;
import java.util.Map;

import main.java.board.Bitboard;
import main.java.board.State;

/**
 * Data structure to maintain the game tree that the agent explores
 */
public class Tree {
    public Node root; // root of tree
    public Map<Bitboard, Node> map; // map from board positions to corresponding Nodes

    /**
     * Initialize game tree with given board position at the root
     * 
     * @param rootPos Root board position
     * @param turn    Turn indicator
     */
    public Tree(Bitboard rootPos, int turn) {
        root = new Node(new State(rootPos), turn);
        map = new HashMap<>();
        map.put(rootPos, root);
    }

    /**
     * Get the Node corresponding to the given board state, or create a new one if none.
     * 
     * @param state The state to get the node for
     * @param turn  Turn indicator
     * @return The corresponding Node
     */
    public Node getNode(State state, int turn) {
        if (map.containsKey(state.board)) {
            return map.get(state.board);
        }
        Node node = new Node(state, turn);
        map.put(state.board, node);
        return node;
    }
}
