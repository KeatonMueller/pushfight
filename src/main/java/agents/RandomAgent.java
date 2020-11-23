package main.java.agents;

import java.util.List;
import java.util.Set;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.board.Move;
import main.java.util.BitboardUtils;
import main.java.util.GameUtils;
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
     * Generate a random Move from the given board without instantiating a RandomAgent instance.
     * Also eliminates the need to generate every possible move, since it returns as soon as it
     * generates a single valid move. The provided board is updated to have the move performed on
     * it.
     * 
     * @param board Board to find next State from
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     * @return Move object of the randomly chosen next move
     */
    public static Move getRandomMove(Bitboard board, int turn, Random rand) {
        Bitboard initState = board.getState();
        Move move = new Move();
        int i;
        while (true) {
            for (i = 0; i < GameUtils.NUM_SLIDES; i++) {
                randomSlide(board, turn, rand, move);
            }

            if (!randomPush(board, turn, rand, move)) {
                board.restoreState(initState);
                move.clear();
                System.out.println("didnt find a valid move sequence");
                continue;
            }
            return move;
        }
    }

    /**
     * Perform a random slide on the given board. Record it in the given Move object
     * 
     * @param board Board to perform random slide on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     * @param move  Move object to populate with chosen slide
     */
    public static void randomSlide(Bitboard board, int turn, Random rand, Move move) {
        List<Integer> actions = SuccessorUtils.getSlideActions(board, turn);
        int choice = rand.nextInt((actions.size() / 2) + 1); // plus one for skipped slide
        // skip slide
        if (choice * 2 >= actions.size())
            return;
        int src = actions.get(choice * 2);
        int dst = actions.get((choice * 2) + 1);
        board.slide(src, dst);
        move.add(src);
        move.add(dst);
    }

    /**
     * Perform a random push on the given board. Record it in the given Move object
     * 
     * @param board Board to perform random push on
     * @param turn  Turn indicator
     * @param rand  Instance of the Random class
     * @param move  Mov eobject to populate with chosen push
     * @return true if push was performed, else false
     */
    public static boolean randomPush(Bitboard board, int turn, Random rand, Move move) {
        List<Integer> actions = SuccessorUtils.getPushActions(board, turn);

        if (actions.size() == 0)
            return false;

        int choice = rand.nextInt(actions.size() / 2);
        int loc = actions.get(choice);
        int dir = actions.get(choice + 1);
        board.push(loc, (char) dir);

        // don't allow for suicidal moves
        if (BitboardUtils.checkWinner(board) == 1 - turn) {
            return false;
        }

        move.add(loc);
        move.add(dir);
        return true;
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

        // don't allow for suicidal moves
        if (BitboardUtils.checkWinner(board) == 1 - turn) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Random Agent";
    }
}
