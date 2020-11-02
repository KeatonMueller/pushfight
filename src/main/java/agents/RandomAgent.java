package main.java.agents;

import java.util.List;
import java.util.Set;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.util.BitboardUtils;
import main.java.util.GameUtils;

public class RandomAgent extends Agent {
    Random rand;

    public RandomAgent() {
        rand = new Random();
    }

    public Bitboard getNextState(Bitboard board, int turn) {
        Set<Bitboard> nextStates = BitboardUtils.getNextStates(board, turn);
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
        int i, push;
        while (true) {
            for (i = 0; i < GameUtils.NUM_SLIDES; i++) {
                BitboardUtils.decodeSlide(board, randomSlide(board, turn, rand), turn);
            }

            push = randomPush(board, turn, rand);
            if (push == -1) {
                board.restoreState(initState);
                continue;
            }
            BitboardUtils.decodePush(board, push, turn);
            return;
        }
    }

    /**
     * Perform a random slide on the given board
     * 
     * @param board Board to perform random slide on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     * @return A random bit mask corresponding to a valid sliding action
     */
    public static int randomSlide(Bitboard board, int turn, Random rand) {
        List<Integer> actions = BitboardUtils.getSlideActions(board, turn);
        return actions.get(rand.nextInt(actions.size()));
    }

    /**
     * Perform a random push on the given board
     * 
     * @param board Board to perform random push on
     * @param turn  Turn indicator
     * @param rand  Instanc eof the Random class
     * @return A random bit mask corresponding to a valid push action, or -1 if no pushes possible
     *         from the given position
     */
    public static int randomPush(Bitboard board, int turn, Random rand) {
        List<Integer> actions = BitboardUtils.getPushActions(board, turn);

        if (actions.size() == 0)
            return -1;

        return actions.get(rand.nextInt(actions.size()));
    }
}
