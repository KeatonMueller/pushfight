package main.java.agents.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.board.State;
import main.java.util.BitboardUtils;
import main.java.util.SuccessorUtils;

/**
 * Data structure for individual nodes in the game tree
 */
public class Node {
    protected List<Node> children; // (visited) children of current node
    protected List<Node> parents; // list of parents (there can be more than one)
    protected Node chosenParent; // specific parent chosen during a playout

    protected State state; // State associated with this node
    protected boolean isTerminal; // whether or not node is terminal
    protected boolean isFullyExpanded; // whether or not node is fully expanded
    protected List<State> unexplored; // list of unexplored children
    protected int totalVisits; // number of times current node has been visited
    protected Map<Node, Stats> childToStats; // map from child node to stats

    /**
     * Initialize new Node for given board state
     * 
     * @param state State associated with this node
     */
    public Node(State state) {
        this.state = state;
        this.isTerminal = BitboardUtils.checkWinner(state.board) != -1;
        this.unexplored = new ArrayList<>(SuccessorUtils.getSuccessors(state.board));
        this.isFullyExpanded = this.unexplored.size() == 0;
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.totalVisits = 0;
        this.childToStats = new HashMap<>();
    }

    @Override
    public int hashCode() {
        return state.board.hashCode();
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
        return this.state.board.equals(other.state.board);
    }
}
