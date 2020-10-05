package main.java.agents;

import java.util.Set;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;
import main.java.board.BitboardState;

public class RandomAgent extends Agent {
    Random rand;

    public RandomAgent() {
        rand = new Random();
    }

    public BitboardState getNextState(Bitboard board, int turn) {
        Set<BitboardState> nextStates = BitboardUtils.getNextStates(board, turn);
        int chosenIdx = rand.nextInt(nextStates.size());
        int idx = 0;
        for (BitboardState state : nextStates) {
            if (idx == chosenIdx)
                return state;
            idx++;
        }
        return null;
    }
}
