package main.java;

import java.util.Scanner;
import main.java.board.heuristic.HeuristicUtils;
import main.java.game.GUIGame;
import main.java.game.TextGame;
import main.java.genetics.Coevolution;
import main.java.genetics.Evolution;

public class PushFight {
        public static void main(String[] args) {
                if (args.length == 0) {
                        System.out.println("Please specify either 'text' or 'gui'");
                        return;
                }
                Scanner scan = new Scanner(System.in);
                int time, size, type;
                switch (args[0]) {
                        case "text":
                                new TextGame();
                                break;
                        case "gui":
                                new GUIGame();
                                break;
                        case "evolve":
                                System.out.print("Time limit (seconds): ");
                                time = Integer.parseInt(scan.nextLine());
                                System.out.print("Population size: ");
                                size = Integer.parseInt(scan.nextLine());
                                new Evolution(time, size, HeuristicUtils.numComponents);
                                break;
                        case "coevolve":
                                System.out.print("Time limit (seconds): ");
                                time = Integer.parseInt(scan.nextLine());
                                System.out.print("Population size: ");
                                size = Integer.parseInt(scan.nextLine());
                                System.out.print("Type of coevolution (1|2): ");
                                type = Integer.parseInt(scan.nextLine());
                                new Coevolution(time, size, type);
                                break;
                }
                scan.close();
        }
}
