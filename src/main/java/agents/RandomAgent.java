package main.java.agents;

import java.util.Set;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.board.Move;
import main.java.board.State;
import main.java.util.SuccessorUtils;

/**
 * Agent that performs random moves. Also provides static methods to allow making random moves
 * without instantiating an instance of this class.
 */
public class RandomAgent extends Agent implements AgentInterface {
    Random rand;

    public RandomAgent() {
        rand = new Random();
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        Set<Bitboard> nextStates = SuccessorUtils.getNextStates(board, turn);
        int chosenIdx = rand.nextInt(nextStates.size());
        int idx = 0;
        for (Bitboard state : nextStates) {
            if (idx == chosenIdx)
                return state;
            idx++;
        }
        return null;
    }

    /**
     * Generate a random Move from the given board without instantiating a RandomAgent instance. The
     * provided board is updated to have the move performed on it.
     * 
     * @param board Board to find next State from
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     * @return Move object of the randomly chosen next move
     */
    public static Move getRandomMove(Bitboard board, int turn, Random rand) {
        Set<State> nextStates = SuccessorUtils.getSuccessors(board, turn);
        if (nextStates.size() == 0) {
            System.err.println("No successors for board");
            board.show();
            System.exit(1);
        }
        int chosenIdx = rand.nextInt(nextStates.size());
        int idx = 0;
        for (State state : nextStates) {
            if (idx == chosenIdx) {
                board.restoreState(state.board);
                return state.move;
            }
            idx++;
        }
        return null;
    }

    /**
     * Perform a random move on the given board without instantiating a RandomAgent instance.
     * 
     * @param board Board to perform random move on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     */
    public static void randomMove(Bitboard board, int turn, Random rand) {
        Set<Bitboard> nextStates = SuccessorUtils.getNextStates(board, turn);
        if (nextStates.size() == 0) {
            System.err.println("No successors for board");
            board.show();
            System.exit(1);
        }
        int chosenIdx = rand.nextInt(nextStates.size());
        int idx = 0;
        for (Bitboard state : nextStates) {
            if (idx == chosenIdx) {
                board.restoreState(state);
                return;
            }
            idx++;
        }
    }

    @Override
    public String toString() {
        return "Random Agent";
    }
}
