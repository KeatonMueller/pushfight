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
     */
    public Tree(Bitboard rootPos) {
        root = new Node(new State(rootPos));
        map = new HashMap<>();
        map.put(rootPos, root);
    }

    /**
     * Get the Node corresponding to the given board state, or create a new one if none.
     * 
     * @param state The state to get the node for
     * @return The corresponding Node
     */
    public Node getNode(State state) {
        if (map.containsKey(state.board)) {
            return map.get(state.board);
        }
        Node node = new Node(state);
        map.put(state.board, node);
        return node;
    }
}
