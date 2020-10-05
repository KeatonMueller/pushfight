package main.java.agents;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;
import main.java.board.BitboardState;
import main.java.board.Heuristic;

public class AlphaBetaAgent extends Agent {
    /**
     * Private class to store return value of alpha beta function
     */
    private class AlphaReturn {
        public double value;
        public BitboardState state;

        public AlphaReturn(double v, BitboardState s) {
            value = v;
            state = s;
        }
    }

    private int DEPTH = 2;
    private int explored;

    public BitboardState getNextState(Bitboard board, int turn) {
        System.out.print("Alpha Beta searching for a move for player " + (turn + 1) + "... ");

        explored = 0;
        BitboardState nextState =
                alphaBeta(board, DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE, turn).state;
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
            return new AlphaReturn(Heuristic.heuristic(board), null);
        }

        AlphaReturn globalBest = new AlphaReturn(0, null);
        AlphaReturn localBest;
        if (turn == 0) {
            globalBest.value = -Double.MAX_VALUE;
            for (BitboardState state : BitboardUtils.getNextStates(board, turn)) {
                board.restoreState(state);
                localBest = alphaBeta(board, depth - 1, alpha, beta, 1 - turn);

                if (localBest.value > globalBest.value) {
                    globalBest.value = localBest.value;
                    globalBest.state = state;
                }

                alpha = Math.max(alpha, globalBest.value);
                if (alpha >= beta)
                    break;
            }
            return globalBest;
        } else {
            globalBest.value = Double.MAX_VALUE;
            for (BitboardState state : BitboardUtils.getNextStates(board, turn)) {
                board.restoreState(state);
                localBest = alphaBeta(board, depth - 1, alpha, beta, 1 - turn);

                if (localBest.value < globalBest.value) {
                    globalBest.value = localBest.value;
                    globalBest.state = state;
                }

                beta = Math.min(beta, globalBest.value);
                if (beta <= alpha)
                    break;
            }
            return globalBest;
        }
    }
}
