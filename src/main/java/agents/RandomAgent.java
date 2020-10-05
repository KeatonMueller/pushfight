package main.java.agents;

import java.util.Set;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;

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
}
