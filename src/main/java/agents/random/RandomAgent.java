package main.java.agents.random;

import java.util.List;
import java.util.Random;

import main.java.agents.Agent;
import main.java.board.Board;
import main.java.game.GameUtils;

public class RandomAgent implements Agent {
    Random rand;

    public RandomAgent() {
        rand = new Random();
    }

    public int[] getMove(Board board, int turn) {
        int[] move = new int[] {0, 0, 0};

        int[] slide;
        int i;
        List<Integer> actions;

        // loop until valid move sequence is selected
        while (true) {
            // randomly choose two slide actions
            for (i = 0; i < 2; i++) {
                actions = GameUtils.getSlideActions(board, turn);

                // select random slide action
                move[i] = actions.get(rand.nextInt(actions.size()));
                // perform slide if it's not a skipped action
                if (move[i] != 0) {
                    slide = GameUtils.decodeSlideAction(move[i]);
                    board.slide(slide[0], slide[1], slide[2], slide[3]);
                }
            }

            // generate all possible push actions
            actions = GameUtils.getPushActions(board, turn);

            // undo the slide actions
            for (i = 1; i >= 0; i--) {
                if (move[i] != 0) {
                    slide = GameUtils.decodeSlideAction(move[i]);
                    board.slide(slide[2], slide[3], slide[0], slide[1]);
                }
            }

            // if there aren't any valid pushes, re-try
            if (actions.size() == 0) {
                continue;
            }
            // otherwise pick a random one and return
            move[2] = actions.get(rand.nextInt(actions.size()));
            return move;
        }
    }
}
