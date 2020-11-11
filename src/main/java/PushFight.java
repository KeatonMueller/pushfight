package main.java;

import main.java.evaluation.ComparePrevious;
import main.java.evaluation.Evaluation;
import main.java.game.GUIGame;
import main.java.game.TextGame;
import main.java.genetics.Evolution;

public class PushFight {
        public static void main(String[] args) {
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
                        case "evolve":
                                new Evolution();
                                break;
                        case "evaluate":
                                new Evaluation();
                                break;
                        case "compare":
                                new ComparePrevious();
                                break;
                        case "debug":
                                new Debug();
                                break;
                }
        }
}
