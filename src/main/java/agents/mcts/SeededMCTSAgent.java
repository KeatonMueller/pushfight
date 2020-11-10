package main.java.agents.mcts;

import main.java.board.Heuristic;

public class SeededMCTSAgent extends MonteCarloAgent {
    private Heuristic h = new Heuristic();
    
    /**
     * Initialize Heuristic-Seeded Monte-Carlo Tree Search agent with given iteration limit
     * 
     * @param iterations Max number of iterations allowed per move
     */
    public SeededMCTSAgent(long iterations) {
        super(iterations);
    }

    /**
     * Initialize Heuristic-Seeded Monte-Carlo Tree Search agent
     */
    public SeededMCTSAgent() {
        super();
    }

    @Override
    protected double playout(Node node){
        return h.heuristic(node.state.board);
    }

    @Override
    public String toString(){
        return "Heuristic-Seeded MCTS Agent";
    }
}
