package main.java.agents;

import java.util.List;
import java.util.Random;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;

public class RandomAgent extends Agent {
    Random rand;

    public RandomAgent() {
        rand = new Random();
    }

    public int[] getNextState(Bitboard board, int turn) {
        List<int[]> nextStates = BitboardUtils.getNextStates(board, turn);
        return nextStates.get(rand.nextInt(nextStates.size()));
    }
}
