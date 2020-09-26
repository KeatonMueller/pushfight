package main.java;

import main.java.game.GUIGame;
import main.java.game.TextGame;

public class PushFight {
    public static void main(String[] args) {
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
