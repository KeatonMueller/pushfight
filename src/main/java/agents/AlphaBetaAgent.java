package main.java.agents;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;
import main.java.board.heuristic.Heuristic;

public class AlphaBetaAgent extends Agent {
    /**
     * Private class to store return value of alpha beta function
     */
    private class AlphaReturn {
        public double value;
        public Bitboard state;

        public AlphaReturn(double v, Bitboard s) {
            value = v;
            state = s;
        }
    }

    private Heuristic h;
    private int DEPTH = 2;
    private int explored;
    private boolean silent = false;

    /**
     * Initialize Alpha Beta Agent with default heuristic
     */
    public AlphaBetaAgent() {
        h = new Heuristic();
    }

    /**
     * Initialize Alpha Beta Agent with default heuristic and custom depth
     * 
     * @param depth Depth to run minimax to
     */
    public AlphaBetaAgent(int depth) {
        h = new Heuristic();
        DEPTH = depth;
    }

    /**
     * Initialize Alpha Beta agent with custom heuristic and depth
     * 
     * @param values  Array of doubles for custom heuristic
     * @param depth   Depth to run minimax to
     * @param silence Boolean flag to suppress print statements
     */
    public AlphaBetaAgent(double[] values, int depth, boolean silence) {
        h = new Heuristic(values);
        DEPTH = depth;
        silent = silence;
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        if (!silent)
            System.out.print("Alpha Beta searching for a move for player " + (turn + 1) + "... ");

        explored = 0;
        Bitboard nextState =
                alphaBeta(board, DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE, turn).state;

        if (!silent)
            System.out.println(explored + " nodes explored");

        return nextState;
    }

    /**
     * Perform the Minimax algorithm with Alpha-Beta pruning to find the next move
     * 
     * @param board The board to perform the tree search on
     * @param depth The depth to search to
     * @param alpha The alpha value
     * @param beta  The beta value
     * @param turn  Turn indicator
     * @return AlphaReturn object containing best value and next state
     */
    private AlphaReturn alphaBeta(Bitboard board, int depth, double alpha, double beta, int turn) {
        explored++;
        if (depth == 0 || BitboardUtils.checkWinner(board) != -1) {
            return new AlphaReturn(h.heuristic(board), null);
        }

        AlphaReturn globalBest = new AlphaReturn(0, null);
        AlphaReturn localBest;
        if (turn == 0) {
            globalBest.value = -Double.MAX_VALUE;
            for (Bitboard child : BitboardUtils.getNextStates(board, turn)) {
                localBest = alphaBeta(child, depth - 1, alpha, beta, 1 - turn);

                if (localBest.value > globalBest.value) {
                    globalBest.value = localBest.value;
                    globalBest.state = child;
                }

                alpha = Math.max(alpha, globalBest.value);
                if (alpha >= beta)
                    break;
            }
            return globalBest;
        } else {
            globalBest.value = Double.MAX_VALUE;
            for (Bitboard child : BitboardUtils.getNextStates(board, turn)) {
                localBest = alphaBeta(child, depth - 1, alpha, beta, 1 - turn);

                if (localBest.value < globalBest.value) {
                    globalBest.value = localBest.value;
                    globalBest.state = child;
                }

                beta = Math.min(beta, globalBest.value);
                if (beta <= alpha)
                    break;
            }
            return globalBest;
        }
    }
}
