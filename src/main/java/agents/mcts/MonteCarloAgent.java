package main.java.agents.mcts;

import main.java.agents.Agent;
import main.java.agents.AgentInterface;
import main.java.board.Bitboard;

/**
 * One-stop-shop for constructing an agent using Monte-Carlo tree search.
 */
public class MonteCarloAgent extends Agent {
    public enum MCTSType {
        VANILLA, MAST, SEEDED, WEIGHTED_SEEDED, BIASED, LGR1, FPU
    }

    private AgentInterface agent;

    /**
     * Initialize mcts agent of given type with default iterations
     * 
     * @param type Type of mcts agent to instantiate
     */
    public MonteCarloAgent(MCTSType type) {
        switch (type) {
            case VANILLA:
                this.agent = new VanillaMCTSAgent();
                break;
            case MAST:
                this.agent = new MASTAgent();
                break;
            case SEEDED:
                this.agent = new SeededMCTSAgent();
                break;
            case WEIGHTED_SEEDED:
                this.agent = new WeightedSeededMCTSAgent();
                break;
            case BIASED:
                this.agent = new BiasedMCTSAgent();
                break;
            case LGR1:
                this.agent = new LGR1Agent();
                break;
            case FPU:
                this.agent = new FPUAgent();
                break;
        }
    }

    /**
     * Initialize mcts agent of given type with given iterations
     * 
     * @param type       Type of mcts agent to instantiate
     * @param iterations Number of iterations per move
     */
    public MonteCarloAgent(MCTSType type, long iterations) {
        switch (type) {
            case VANILLA:
                this.agent = new VanillaMCTSAgent(iterations);
                break;
            case MAST:
                this.agent = new MASTAgent(iterations);
                break;
            case SEEDED:
                this.agent = new SeededMCTSAgent(iterations);
                break;
            case WEIGHTED_SEEDED:
                this.agent = new WeightedSeededMCTSAgent(iterations);
                break;
            case BIASED:
                this.agent = new BiasedMCTSAgent(iterations);
                break;
            case LGR1:
                this.agent = new LGR1Agent(iterations);
                break;
            case FPU:
                this.agent = new FPUAgent(iterations);
                break;
        }
    }

    /**
     * Initialize mcts agent agent of given type with given iterations and extra parameter
     * 
     * @param type       Type of mcts agent to instantiate
     * @param iterations Number of iterations per move
     * @param extra      Extra parameter than some agents can use
     */
    public MonteCarloAgent(MCTSType type, long iterations, double extra) {
        switch (type) {
            // weighted and FPU can take an extra param
            case WEIGHTED_SEEDED:
                this.agent = new WeightedSeededMCTSAgent(iterations, extra);
                break;
            case FPU:
                this.agent = new FPUAgent(iterations, extra);
                break;
            // the rest don't use one
            case VANILLA:
                this.agent = new VanillaMCTSAgent(iterations);
                break;
            case MAST:
                this.agent = new MASTAgent(iterations);
                break;
            case SEEDED:
                this.agent = new SeededMCTSAgent(iterations);
                break;
            case BIASED:
                this.agent = new BiasedMCTSAgent(iterations);
                break;
            case LGR1:
                this.agent = new LGR1Agent(iterations);
                break;
        }
    }

    // === when type isn't specified, use the vanilla agent ===

    /**
     * Initialize default mcts agent with default iterations
     */
    public MonteCarloAgent() {
        this.agent = new VanillaMCTSAgent();
    }

    /**
     * Initialize default mcts agent with given iterations
     * 
     * @param iterations Number of iterations per move
     */
    public MonteCarloAgent(long iterations) {
        this.agent = new VanillaMCTSAgent(iterations);
    }

    // === route function calls to agent object ===

    public Bitboard getNextState(Bitboard board) {
        return this.agent.getNextState(board);
    }

    public void agentMove(Bitboard board) {
        this.agent.agentMove(board);
    }

    public void newGame(int turn) {
        this.agent.newGame(turn);
    }

    @Override
    public String toString() {
        return this.agent.toString();
    }
}
