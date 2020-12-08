package main.java.agents.mcts;

import main.java.agents.RandomAgent;
import main.java.board.Bitboard;
import main.java.board.Heuristic;
import main.java.util.BitboardUtils;

/**
 * Agent using Monte-Carlo Tree Search that weights a (normalized) heuristic on board states in
 * addition to using random playouts.
 */
public class WeightedSeededMCTSAgent extends VanillaMCTSAgent {
    private Heuristic h = new Heuristic();
    private double heuristicWeight = 0.5;
    private double playoutWeight = 1.0 - heuristicWeight;

    /**
     * Initialize Weighted Seeded Monte-Carlo Tree Search agent with given iteration limit
     * 
     * @param iterations Max number of iterations allowed per move
     */
    public WeightedSeededMCTSAgent(long iterations) {
        super(iterations);
    }

    /**
     * Initialize Weighted Seeded Monte-Carlo Tree Search agent with given iteration limit and
     * weight for heuristic value.
     * 
     * @param iterations      Max number of iterations allowed per move
     * @param heuristicWeight Weight (0 - 1) for heuristic value
     */
    public WeightedSeededMCTSAgent(long iterations, double heuristicWeight) {
        super(iterations);
        this.heuristicWeight = heuristicWeight;
        this.playoutWeight = 1.0 - this.heuristicWeight;
    }

    /**
     * Initialize Weighted Seeded Monte-Carlo Tree Search agent
     */
    public WeightedSeededMCTSAgent() {
        super();
    }

    @Override
    protected double playout(Node node) {
        double heuristic = h.heuristic(node.state.board) * heuristicWeight;
        boardToNum.clear();
        Bitboard board = new Bitboard(node.state.board);
        int winner, count;
        int turnCount = 0;
        while (true) {
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                if (winner == 0)
                    return playoutWeight + heuristic;
                return -playoutWeight + heuristic;
            }

            // add tie logic in rare case of long loop
            count = boardToNum.getOrDefault(board, 0);
            boardToNum.put(board, count + 1);
            turnCount += 1;
            if (count >= 5 || turnCount >= 100) {
                // default to returning heuristic value if tie
                return heuristic / heuristicWeight;
            }

            RandomAgent.randomMove(board, turn, rand);
            turn = 1 - turn;
        }
    }

    @Override
    public String toString() {
        return heuristicWeight + " Weighted Heuristic-Seeded MCTS Agent";
    }
}
