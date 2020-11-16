package main.java.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.java.agents.Agent;
import main.java.agents.alphaBeta.AlphaBetaAgent;
import main.java.board.Bitboard;
import main.java.util.BitboardUtils;
import main.java.util.NumberUtils;
import main.java.util.SuccessorUtils;

/**
 * Run several games to analyze average branching factor
 */
public class BranchingAnalysis {
    private Bitboard board = new Bitboard();
    private int turn;
    private Agent a1 = new AlphaBetaAgent(AlphaBetaAgent.Type.STOCHASTIC);
    private Agent a2 = new AlphaBetaAgent(AlphaBetaAgent.Type.STOCHASTIC);
    private Map<Bitboard, Integer> stateToNum = new HashMap<>();
    private Map<Integer, List<Integer>> playerToBranches = new HashMap<>();

    /**
     * Analyze average branching factor for Push Fight games. Uses stochastic alpha beta agents to
     * introduce variation between games.
     */
    public BranchingAnalysis() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter number of games: ");
        int numGames = Integer.parseInt(scan.nextLine().trim());

        playerToBranches.put(0, new ArrayList<>());
        playerToBranches.put(1, new ArrayList<>());

        for (int i = 0; i < numGames; i++) {
            System.out.print("Running game " + i + "\r");
            simulate();
        }
        System.out.println();

        // perform analysis on recorded branching factors
        double p1Avg, p2Avg, totalAvg;
        int p1Sum = 0, p2Sum = 0, totalSum;
        for (int bf : playerToBranches.get(0)) {
            p1Sum += bf;
        }
        for (int bf : playerToBranches.get(1)) {
            p2Sum += bf;
        }
        totalSum = p1Sum + p2Sum;

        p1Avg = (double) p1Sum / playerToBranches.get(0).size();
        p2Avg = (double) p2Sum / playerToBranches.get(1).size();
        totalAvg = (double) totalSum
                / (playerToBranches.get(0).size() + playerToBranches.get(1).size());

        System.out.println("Average branching factor over " + numGames + " games");
        System.out.println("\tP1 avg: " + NumberUtils.round(p1Avg, 2));
        System.out.println("\tP2 avg: " + NumberUtils.round(p2Avg, 2));
        System.out.println("\tOverall avg: " + NumberUtils.round(totalAvg, 2));

        scan.close();
    }

    /**
     * Simulate one game between the two stochastic alpha beta agents, recording the branching
     * factor at each turn
     */
    public void simulate() {
        int count;
        turn = 0;
        a1.newGame(0);
        a2.newGame(1);
        board.reset();
        BitboardUtils.skipSetup(board);
        stateToNum.clear();

        while (true) {
            // record branching factor for this player
            playerToBranches.get(turn).add(SuccessorUtils.getNextStates(board, turn).size());
            // make move
            makeMove(turn);
            count = stateToNum.getOrDefault(board, 0);
            stateToNum.put(board, count + 1);

            if (BitboardUtils.checkWinner(board) != -1) {
                return;
            }
            // detect fifth time repeating a board state, call it a tie
            if (count >= 5) {
                return;
            }
            // change turns
            turn = 1 - turn;
        }
    }

    /**
     * Have the agent whose turn it is make their move
     * 
     * @param turn Turn indicator
     */
    private void makeMove(int turn) {
        if (turn == 0) {
            a1.agentMove(board, turn);
        } else {
            a2.agentMove(board, turn);
        }
    }
}
