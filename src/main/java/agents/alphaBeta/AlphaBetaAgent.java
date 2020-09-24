package main.java.agents.alphaBeta;

import main.java.agents.Agent;
import main.java.board.Board;
import main.java.board.BoardUtils;
import main.java.game.GameUtils;

public class AlphaBetaAgent implements Agent {
    private int[] bestMove = new int[] {0, 0, 0};

    public int[] getMove(Board board, int turn) {
        alphaBeta(board, 1, Double.MIN_VALUE, Double.MAX_VALUE, turn);
        return bestMove;
    }

    /**
     * Perform the Minimax algorithm with Alpha-Beta pruning to find the next move
     * 
     * @param board The board to perform the tree search on
     * @param depth The depth to search to
     * @param alpha The alpha value
     * @param beta  The beta value
     * @param turn  Turn indicator
     * @return The value of the board
     */
    private double alphaBeta(Board board, int depth, double alpha, double beta, int turn) {
        if (depth == 0) {
            return BoardUtils.heuristic(board);
        }
        double value, bestValue;
        int slide1, slide2, push;
        if (turn == 0) {
            value = Double.MIN_VALUE;
            bestValue = Double.MIN_VALUE;
            for (long move : GameUtils.getMoves(board, turn)) {
                slide1 = (int) (move / 10000000);
                slide2 = (int) ((move % 10000000) / 1000);
                push = (int) (move % 1000);

                // System.out.println("calling move " + slide1 + ", " + slide2 + ", " + push
                // + " decoded from " + move);
                board.move(slide1, slide2, push);
                value = Math.max(value, alphaBeta(board, depth - 1, alpha, beta, 1 - turn));
                board.restoreBoard();

                if (value > bestValue) {
                    bestValue = value;
                    bestMove[0] = slide1;
                    bestMove[1] = slide2;
                    bestMove[2] = push;
                }

                alpha = Math.max(alpha, value);
                if (alpha >= beta)
                    break;
            }
            return value;
        } else {
            value = Double.MAX_VALUE;
            bestValue = Double.MAX_VALUE;
            for (long move : GameUtils.getMoves(board, turn)) {
                slide1 = (int) (move / 10000000);
                slide2 = (int) ((move % 10000000) / 1000);
                push = (int) (move % 1000);

                board.move(slide1, slide2, push);
                value = Math.min(value, alphaBeta(board, depth - 1, alpha, beta, 1 - turn));
                board.restoreBoard();

                if (value < bestValue) {
                    bestValue = value;
                    bestMove[0] = slide1;
                    bestMove[1] = slide2;
                    bestMove[2] = push;
                }

                beta = Math.min(beta, value);
                if (beta <= alpha)
                    break;
            }
            return value;
        }
    }
}
