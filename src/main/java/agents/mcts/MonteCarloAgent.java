package main.java.agents.mcts;

import main.java.agents.Agent;
import main.java.agents.AgentInterface;
import main.java.board.Bitboard;

/**
 * One-stop-shop for constructing an agent using Monte-Carlo tree search.
 */
public class MonteCarloAgent extends Agent {
    public enum Type {
        VANILLA, MAST, SEEDED, BIASED, LGR1
    }

    private AgentInterface agent;

    /**
     * Initialize mcts agent of given type with default iterations
     * 
     * @param type Type of mcts agent to instantiate
     */
    public MonteCarloAgent(Type type) {
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
            case BIASED:
                this.agent = new BiasedMCTSAgent();
                break;
            case LGR1:
                this.agent = new LGR1Agent();
                break;
        }
    }

    /**
     * Initialize mcts agent of given type with given iterations
     * 
     * @param type       Type of mcts agent to instantiate
     * @param iterations Number of iterations per move
     */
    public MonteCarloAgent(Type type, long iterations) {
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

    public Bitboard getNextState(Bitboard board, int turn) {
        return this.agent.getNextState(board, turn);
    }

    public void agentMove(Bitboard board, int turn) {
        this.agent.agentMove(board, turn);
    }

    public void newGame(int turn) {
        this.agent.newGame(turn);
    }

    @Override
    public String toString() {
        return this.agent.toString();
    }
}
