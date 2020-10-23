package main.java;

import java.util.Scanner;
import main.java.board.heuristic.HeuristicUtils;
import main.java.game.GUIGame;
import main.java.game.TextGame;
import main.java.genetics.Evolution;

public class PushFight {
        public static void main(String[] args) {
                if (args.length == 0) {
                        System.out.println("Please specify either 'text' or 'gui'");
                        return;
                }
                Scanner scan = new Scanner(System.in);
                switch (args[0]) {
                        case "text":
                                new TextGame();
                                break;
                        case "gui":
                                new GUIGame();
                                break;
                        case "evolve":
                                System.out.println("Choose evolution strategy:");
                                System.out.println("\t1. Coevolution");
                                System.out.println("\t2. Coevolution with Split Populations");
                                System.out.println(
                                                "\t3. Coevolution with Split and Reference Populations");
                                System.out.println("\t4. Cooperative Coevolution");
                                System.out.println(
                                                "\t5. Cooperative Coevolution with Split Populations");
                                int type = Integer.parseInt(scan.nextLine().trim());
                                int numWeights = 0;
                                if (type < 4) {
                                        System.out.println("Choose what to evolve:");
                                        System.out.println("\t1. Component weights");
                                        System.out.println("\t2. Component and position weights");
                                        numWeights = Integer.parseInt(scan.nextLine().trim());
                                }
                                System.out.print("Time limit (seconds): ");
                                int time = Integer.parseInt(scan.nextLine());
                                System.out.print("Population size: ");
                                int size = Integer.parseInt(scan.nextLine());
                                new Evolution(time, size,
                                                (numWeights == 1 ? HeuristicUtils.numComponents
                                                                : HeuristicUtils.numValues),
                                                type);
                                break;
                }
                scan.close();
        }
}
