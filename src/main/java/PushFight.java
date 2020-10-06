package main.java;

import main.java.board.Bitboard;
import main.java.board.BitboardUtils;
import main.java.game.GUIGame;
import main.java.game.TextGame;

public class PushFight {
    public static void main(String[] args) {
        Bitboard board = new Bitboard();
        BitboardUtils.skipSetup(board);
        System.out.println(BitboardUtils.getNextStates(board, 0).size());
        if (args.length == 0) {
            System.out.println("Please specify either 'text' or 'gui'");
            return;
        }
        switch (args[0]) {
            case "text":
                new TextGame();
                break;
            case "gui":
                new GUIGame();
                break;
        }
    }
}
