package main.java.agents.alphaBeta;

import main.java.agents.Agent;
import main.java.board.Board;
import main.java.board.BoardUtils;
import main.java.game.GameUtils;

public class AlphaBetaAgent implements Agent {
    private int DEPTH = 2;
    private int explored;

    public int[] getMove(Board board, int turn) {
        System.out.print("Alpha Beta searching for a move for player " + (turn + 1) + "... ");
        explored = 0;
        double bestMove = alphaBeta(board, DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE, turn, -1)[1];
        System.out.println(explored + " nodes explored");
        int[] move = new int[3];
        move[0] = (int) (bestMove / 10000000);
        move[1] = (int) ((bestMove % 10000000) / 1000);
        move[2] = (int) (bestMove % 1000);
        return move;
    }

    /**
     * Perform the Minimax algorithm with Alpha-Beta pruning to find the next move
     * 
     * @param board The board to perform the tree search on
     * @param depth The depth to search to
     * @param alpha The alpha value
     * @param beta  The beta value
     * @param turn  Turn indicator
     * @param loser The losing player given the board state (0|1) or -1 if no loser
     * @return Length 2 array of doubles of the form [board value, best move]
     */
    private double[] alphaBeta(Board board, int depth, double alpha, double beta, int turn,
            int loser) {
        explored++;
        if (depth == 0 || loser != -1) {
            return new double[] {BoardUtils.heuristic(board), 0.0};
        }
        double[] bestValue = new double[2];
        double[] localBest;
        int slide1, slide2, push;
        int[] result;
        if (turn == 0) {
            bestValue[0] = -Double.MAX_VALUE;
            for (long move : GameUtils.getMoves(board, turn)) {
                slide1 = (int) (move / 10000000);
                slide2 = (int) ((move % 10000000) / 1000);
                push = (int) (move % 1000);

                result = board.move(slide1, slide2, push);

                localBest = alphaBeta(board, depth - 1, alpha, beta, 1 - turn, result[5]);

                board.undoMove(slide1, slide2, result, GameUtils.dirIntToChar(push % 10));

                if (localBest[0] > bestValue[0]) {
                    bestValue[0] = localBest[0];
                    bestValue[1] = move;
                }

                alpha = Math.max(alpha, bestValue[0]);
                if (alpha >= beta)
                    break;
            }
            return bestValue;
        } else {
            bestValue[0] = Double.MAX_VALUE;
            for (long move : GameUtils.getMoves(board, turn)) {
                slide1 = (int) (move / 10000000);
                slide2 = (int) ((move % 10000000) / 1000);
                push = (int) (move % 1000);

                result = board.move(slide1, slide2, push);

                localBest = alphaBeta(board, depth - 1, alpha, beta, 1 - turn, result[5]);

                board.undoMove(slide1, slide2, result, GameUtils.dirIntToChar(push % 10));

                if (localBest[0] < bestValue[0]) {
                    bestValue[0] = localBest[0];
                    bestValue[1] = move;
                }

                beta = Math.min(beta, bestValue[0]);
                if (beta <= alpha)
                    break;
            }
            return bestValue;
        }
    }
}
