package main.java.agents.alphaBeta;

import main.java.agents.Agent;
import main.java.board.Bitboard;

/**
 * One-stop-shop for constructing an agent using minimax with alpha beta pruning. There are many
 * different ways to construct each agent, and several different types of agents.
 */
public class AlphaBetaAgent extends Agent {
    public enum Type {
        VANILLA, STOCHASTIC, MOVE_ORDER
    }

    private AlphaBetaTemplate agent;

    /**
     * Initialize alpha beta agent of requested type
     */
    public AlphaBetaAgent(Type type) {
        switch (type) {
            case VANILLA:
                this.agent = new VanillaABAgent();
                break;
            case STOCHASTIC:
                this.agent = new StochasticABAgent();
                break;
            case MOVE_ORDER:
                this.agent = new MoveOrderABAgent();
                break;
        }
    }

    // === when type isn't specified, use the vanilla agent ===

    /**
     * Initialize Alpha Beta Agent with default heuristic
     */
    public AlphaBetaAgent() {
        this.agent = new VanillaABAgent();
    }

    /**
     * Initialize Alpha Beta Agent with custom heuristic
     * 
     * @param weights Array of doubles for heuristic
     */
    public AlphaBetaAgent(double[] weights) {
        this.agent = new VanillaABAgent(weights); // good
    }

    /**
     * Initialize Alpha Beta Agent with different heuristic depending on the player
     * 
     * @param p1Weights Array of doubles for heuristic if playing as P1
     * @param p2Weights Array of doubles for heuristic if playing as P2
     */
    public AlphaBetaAgent(double[] p1Weights, double[] p2Weights) {
        this.agent = new VanillaABAgent(p1Weights, p2Weights); // good
    }

    /**
     * Initialize Alpha Beta agent with custom heuristic and depth
     * 
     * @param values  Array of doubles for custom heuristic
     * @param depth   Depth to run minimax to
     * @param silence Boolean flag to suppress print statements
     */
    public AlphaBetaAgent(double[] values, int depth) {
        this.agent = new VanillaABAgent(values, depth); // good
    }

    /**
     * Initialize Alpha Beta agent with custom heuristic and depth
     * 
     * @param componentWeights Array of doubles for component weights of heuristic
     * @param positionWeights  Array of doubles for position weights of heuristic
     * @param depth            Depth to run minimax to
     * @param silence          Boolean flag to suppress print statements
     */
    public AlphaBetaAgent(double[] componentWeights, double[] positionWeights, int depth) {
        this.agent = new VanillaABAgent(componentWeights, positionWeights, depth); // good
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        return this.agent.getNextState(board, turn);
    }

    public void agentMove(Bitboard board, int turn) {
        this.agent.agentMove(board, turn);
    }

    public void newGame(int turn) {
        this.agent.newGame(turn);
    }
}
