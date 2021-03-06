package main.java.agents.alphaBeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import main.java.agents.Agent;
import main.java.agents.AgentInterface;
import main.java.board.Bitboard;
import main.java.board.Heuristic;
import main.java.util.BitboardUtils;
import main.java.util.SuccessorUtils;

/**
 * Agent using a modified Minimax algorithm with Alpha Beta Pruning. This method introduces a
 * pruning threshold that prunes less aggressively at lower levels in hopes of getting better
 * approximations of the minimax value. The final choice is a stochastic one based off of the values
 * of the root's children.
 */
public class StochasticABAgent extends Agent implements AgentInterface {
    /**
     * Private class to store return value of alpha beta function
     */
    private class AlphaReturn {
        public double value;
        public Bitboard state;

        public AlphaReturn(double value, Bitboard state) {
            this.value = value;
            this.state = state;
        }
    }

    /**
     * Comparator to sort AlphaReturn objects based on value in ascending order
     */
    public static Comparator<AlphaReturn> compareAsc = new Comparator<AlphaReturn>() {
        @Override
        public int compare(AlphaReturn a1, AlphaReturn a2) {
            if (a1.value < a2.value)
                return -1;
            else if (a1.value == a2.value)
                return 0;
            else
                return 1;
        }
    };

    /**
     * Comparator to sort AlphaReturn objects based on value in descending order
     */
    public static Comparator<AlphaReturn> compareDesc = new Comparator<AlphaReturn>() {
        @Override
        public int compare(AlphaReturn a1, AlphaReturn a2) {
            if (a1.value < a2.value)
                return 1;
            else if (a1.value == a2.value)
                return 0;
            else
                return -1;
        }
    };

    private Heuristic h; // heuristic used for board evaluation
    private int DEPTH = 2; // depth to perform minimax search to
    private double PT = 10; // pruning threshold
    private Random rand = new Random(); // Random object for stochasticity
    private List<AlphaReturn> options = new ArrayList<>(); // list of possible moves

    /**
     * Initialize Alpha Beta Agent with default heuristic
     */
    public StochasticABAgent() {
        h = new Heuristic();
    }

    public Bitboard getNextState(Bitboard board) {
        int turn = board.getTurn();
        options.clear();
        alphaBeta(board, DEPTH, -Double.MAX_VALUE, Double.MAX_VALUE, turn);
        if (options.size() == 0) {
            System.out.println("Error, no options");
            board.show();
            System.exit(1);
        }
        // int numOptions = options.size();

        // Set<Bitboard> next = BitboardUtils.getNextStates(board, turn);

        // for (AlphaReturn opt : options) {
        // if (!next.contains(opt.state)) {
        // System.out.println("this shouldn't be possible");
        // opt.state.repr();
        // opt.state.show();
        // System.exit(1);
        // }
        // System.out.println(opt.value);
        // }

        // sort so most desirable are at earliest indices
        if (turn == 0) {
            options.sort(compareDesc);
        } else {
            options.sort(compareAsc);
        }

        // System.out.println("Top three choices:");
        // for (int i = 2; i >= 0; i--) {
        // System.out.println((i + 1) + ": " + options.get(i).value);
        // options.get(i).state.repr();
        // options.get(i).state.show();
        // }

        // randomly pick one of the top 5 choices
        // 50% to pick 1st, 20% for 2nd, 15% for 3rd, 10% for 4th, 5% for 5th
        int choice;
        double r = rand.nextDouble();
        if (r < .5)
            choice = 0;
        else if (r < 0.7)
            choice = 1;
        else if (r < .85)
            choice = 2;
        else if (r < .95)
            choice = 3;
        else
            choice = 4;

        // ensure you're picking something that exists
        choice = Math.min(choice, options.size() - 1);

        return options.get(choice).state;


        // rank selection code, maybe worth trying
        // int totalRank = (numOptions * (numOptions + 1)) / 2;
        // int choice = rand.nextInt(totalRank);

        // int cum = 0;
        // int idx = 0;
        // while (cum <= choice) {
        // idx += 1;
        // cum += idx;
        // }

        // options.get(numOptions - 1).state.show();
        // return options.get(idx - 1).state;
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
        if (depth == 0 || BitboardUtils.checkWinner(board) != -1) {
            return new AlphaReturn(h.heuristic(board), board);
        }

        AlphaReturn best = new AlphaReturn(0, null);
        double candidateValue;

        if (turn == 0) {
            best.value = -Double.MAX_VALUE;
            for (Bitboard child : SuccessorUtils.getNextStates(board)) {
                candidateValue = alphaBeta(child, depth - 1, alpha, beta, 1 - turn).value;

                if (candidateValue > best.value) {
                    best.value = candidateValue;
                    best.state = child;
                }

                alpha = Math.max(alpha, best.value);
                if (depth < DEPTH) {
                    // if not at root, prune using threshold
                    if (alpha - PT >= beta)
                        break;
                } else {
                    // if at root, store all possible options
                    options.add(new AlphaReturn(candidateValue, child));
                }
            }
            return best;
        } else {
            best.value = Double.MAX_VALUE;
            for (Bitboard child : SuccessorUtils.getNextStates(board)) {
                candidateValue = alphaBeta(child, depth - 1, alpha, beta, 1 - turn).value;

                if (candidateValue < best.value) {
                    best.value = candidateValue;
                    best.state = child;
                }

                beta = Math.min(beta, best.value);
                if (depth < DEPTH) {
                    // if not at root, prune using threshold
                    if (beta + PT <= alpha)
                        break;
                } else {
                    // if at root, store all possible options
                    options.add(new AlphaReturn(candidateValue, child));
                }
            }
            return best;
        }
    }

    @Override
    public String toString() {
        return "Stochastic Alpha Beta Agent";
    }
}
