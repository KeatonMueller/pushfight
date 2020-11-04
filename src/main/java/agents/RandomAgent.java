package main.java.agents;

import java.util.List;
import java.util.Set;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.util.GameUtils;
import main.java.util.SuccessorUtils;

public class RandomAgent extends Agent {
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
     * Perform a random move on the given board without instantiating a RandomAgent instance. Also
     * eliminates the need to generate every possible move, since it returns as soon as it generates
     * a single valid move.
     * 
     * @param board Board to perform random move on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     */
    public static void randomMove(Bitboard board, int turn, Random rand) {
        Bitboard initState = board.getState();
        int i;
        while (true) {
            for (i = 0; i < GameUtils.NUM_SLIDES; i++) {
                randomSlide(board, turn, rand);
            }

            if (!randomPush(board, turn, rand)) {
                board.restoreState(initState);
                continue;
            }
            return;
        }
    }

    /**
     * Perform a random slide on the given board
     * 
     * @param board Board to perform random slide on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     */
    public static void randomSlide(Bitboard board, int turn, Random rand) {
        List<Integer> actions = SuccessorUtils.getSlideActions(board, turn);
        int choice = rand.nextInt((actions.size() / 2) + 1); // plus one for skipped slide
        // skip slide
        if (choice * 2 >= actions.size())
            return;
        board.slide(actions.get(choice * 2), actions.get((choice * 2) + 1));
    }

    /**
     * Perform a random push on the given board
     * 
     * @param board Board to perform random push on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     * @return true if push was performed, else false
     */
    public static boolean randomPush(Bitboard board, int turn, Random rand) {
        List<Integer> actions = SuccessorUtils.getPushActions(board, turn);

        if (actions.size() == 0)
            return false;

        int choice = rand.nextInt(actions.size() / 2);
        board.push(actions.get(choice), (char) (int) actions.get(choice + 1));
        return true;
    }

    @Override
    public String toString() {
        return "Random Agent";
    }
}
