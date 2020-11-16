package main.java.agents.mcts;

import main.java.board.Bitboard;
import main.java.board.State;
import main.java.board.StateSet;
import main.java.util.BitboardUtils;
import main.java.util.SetUtils;
import main.java.util.SuccessorUtils;

public class BiasedMCTSAgent extends VanillaMCTSAgent {
    /**
     * Initialize Biased Monte-Carlo Tree Search agent with given iteration limit
     * 
     * @param iterations Max number of iterations allowed per move
     */
    public BiasedMCTSAgent(long iterations) {
        super(iterations);
    }

    /**
     * Initialize Biased Monte-Carlo Tree Search agent
     */
    public BiasedMCTSAgent() {
        super();
    }

    @Override
    protected double playout(Node node) {
        boardToNum.clear();
        Bitboard board = new Bitboard(node.state.board);
        StateSet stateSet;
        int winner, count;
        while (true) {
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                if (winner == 0)
                    return 1;
                return -1;
            }

            // add tie logic in rare case of long loop
            count = boardToNum.getOrDefault(board, 0);
            boardToNum.put(board, count + 1);
            if (count >= 5) {
                return 0;
            }

            stateSet = SuccessorUtils.getStateSet(board, turn);
            if (stateSet.winningStates.size() > 0) {
                board.restoreState(stateSet.iterator().next().board);
            } else if (stateSet.checkmateStates.size() > 0) {
                board.restoreState(stateSet.iterator().next().board);
            } else if (stateSet.noBorderStates.size() > 0) {
                // choose a no-border state 70% of the time
                if (rand.nextDouble() < 0.7) {
                    board.restoreState(
                            ((State) SetUtils.randomChoice(stateSet.noBorderStates)).board);
                } else {
                    board.restoreState(((State) SetUtils.randomChoice(stateSet.otherStates)).board);
                }
            } else {
                board.restoreState(((State) SetUtils.randomChoice(stateSet.otherStates)).board);
            }

            turn = 1 - turn;
        }
    }

    @Override
    public String toString() {
        return "Biased MCTS Agent";
    }
}
