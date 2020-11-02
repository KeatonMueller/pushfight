package main.java.game;

import java.util.Scanner;

import main.java.agents.Agent;
import main.java.agents.AlphaBetaAgent;
import main.java.agents.MonteCarloAgent;
import main.java.agents.RandomAgent;
// import main.java.agents.oep.OEPAgent;
import main.java.board.Bitboard;
import main.java.util.BitboardUtils;
import main.java.util.GameUtils;

public class TextGame {
    private Scanner scan;
    private Bitboard board;
    private int turn;
    private Agent p1, p2;

    public TextGame() {
        scan = new Scanner(System.in);
        board = new Bitboard();
        turn = 0;

        // setup players
        choosePlayer(0);
        choosePlayer(1);
        // we're skipping the setup phase of the game for now
        // setup();
        BitboardUtils.skipSetup(board);
        gameLoop();
    }

    /**
     * Prompt user to pick the player
     * 
     * @param turn Turn indicator
     */
    public void choosePlayer(int turn) {
        System.out.print("Choose Player " + (turn + 1) + " (human|random|alpha|mcts): ");
        String player = scan.nextLine();
        switch (player.trim().toLowerCase()) {
            case "random":
                setAgent(turn, new RandomAgent());
                break;
            case "alpha":
                setAgent(turn, new AlphaBetaAgent());
                break;
            case "mcts":
                setAgent(turn, new MonteCarloAgent());
                break;
            // case "oep":
            // setAgent(turn, new OEPAgent());
            // break;
            case "human":
            default:
                setAgent(turn, null);
                break;
        }
    }

    /**
     * Set the appropriate agent to the given Agent object
     * 
     * @param turn  Turn indicator
     * @param agent Agent to be set
     */
    public void setAgent(int turn, Agent agent) {
        if (turn == 0)
            p1 = agent;
        else
            p2 = agent;
    }

    /**
     * Run the game loop, prompting for input and performing updates
     */
    public void gameLoop() {
        int winner;
        while (true) {
            board.show();

            makeMove(turn);
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                board.show();
                System.out.println("Player " + (winner + 1) + " wins!");
                scan.close();
                return;
            }

            // change turns
            turn = 1 - turn;
        }
    }

    /**
     * Have the player whose turn it is make their move
     * 
     * @param turn Turn indicator
     */
    public void makeMove(int turn) {
        if (turn == 0) {
            if (p1 == null)
                userMove(turn);
            else
                p1.agentMove(board, turn);
        } else {
            if (p2 == null)
                userMove(turn);
            else
                p2.agentMove(board, turn);
        }
    }

    /**
     * Prompt user for input to make their turn
     * 
     * @param turn Turn indicator
     */
    public void userMove(int turn) {
        int i;
        // perform sliding actions
        for (i = 0; i < GameUtils.NUM_SLIDES; i++) {
            slideAction(turn, i + 1);
        }
        // perform one pushing action
        pushAction(turn);
    }

    /**
     * Get input and validate a sliding action
     * 
     * @param turn Turn indicator
     * @param num  Which sliding action (1|2) is occuring
     */
    public void slideAction(int turn, int num) {
        // get sliding action start
        System.out.println(
                "Player " + (turn + 1) + ", choose piece to slide (optional " + (num) + "/2)");
        int startPos = getInput(turn);

        // allow sliding action to be skipped
        if (startPos == -1) {
            board.show();
            return;
        }

        // compute valid destinations given this starting position
        int dests = BitboardUtils.findSlideDests(board, startPos);
        // get sliding action destination
        int endPos = -1;
        System.out.println("Player " + (turn + 1) + ", choose destination");
        while (endPos == -1 || (dests & endPos) == 0) {
            endPos = getInput();

            // allow for an empty action
            if (startPos == endPos) {
                board.show();
                return;
            }
        }
        board.slide(startPos, endPos);
        board.show();
    }

    /**
     * Get input and validate a push action
     * 
     * @param turn Turn indicator
     */
    public void pushAction(int turn) {
        int pos = -1;
        char dir = ' ';
        while (true) {
            // prompt for piece to push
            System.out.println("Player " + (turn + 1) + ", choose a piece to push (required)");
            while (pos == -1)
                pos = getInput(turn);
            // prompt for push direction
            System.out.println("Player " + (turn + 1) + ", choose pushing direction (r|l|u|d)");
            dir = getDir();

            // validate push, and loop if invalid
            if (!BitboardUtils.isValidPush(board, pos, dir)) {
                board.show();
                pos = -1;
                continue;
            }

            // perform push
            board.push(pos, dir);
            return;
        }
    }

    /**
     * Prompt user for a location on the board
     * 
     * @return Board position encoded as a bit mask
     */
    public int getInput() {
        String line;
        int row = 0, col = 0;
        while (!board.isValid((1 << (row * 8 + col)))) {
            line = scan.nextLine();
            if (line.equals("")) {
                return -1;
            }
            try {
                row = line.charAt(0) - 'a';
                col = Integer.parseInt(line.substring(1)) - 1;
            } catch (Exception e) {
                row = 0;
                col = 0;
            }
        }
        return (1 << (row * 8 + col));

    }

    /**
     * Prompt user for a location that they own
     * 
     * @param turn Turn indicator
     * @return Board position encoded as a bit mask
     */
    public int getInput(int turn) {
        String line;
        int row = -1, col = -1;
        while (!board.isValid((1 << (row * 8 + col)))
                || !board.owns((1 << (row * 8 + col)), turn)) {
            line = scan.nextLine();
            if (line.equals("")) {
                return -1;
            }
            try {
                row = line.charAt(0) - 'a';
                col = Integer.parseInt(line.substring(1)) - 1;
            } catch (Exception e) {
                row = -1;
                col = -1;
            }
        }
        return (1 << (row * 8 + col));
    }

    /**
     * Prompt user for a push direction
     * 
     * @return The push direction (r|l|u|d)
     */
    public char getDir() {
        String line;
        char dir = ' ';
        while (!(dir == 'r' || dir == 'l' || dir == 'u' || dir == 'd')) {
            line = scan.nextLine();
            if (line.length() > 0) {
                dir = line.charAt(0);
            }
        }
        return dir;
    }

    /**
     * Prompt user to layout their pieces
     */
    public void setup() {
        String piece;
        int i, idx, num;
        int pos = -1;
        int[] iter = new int[] {1, 1, 2, 2};
        for (idx = 0; idx < 4; idx++) {
            num = idx % 2 == 0 ? 2 : 3;
            piece = idx % 2 == 0 ? "circle" : "square";
            for (i = 0; i < num; i++) {
                System.out.println("Player " + iter[idx] + ", place " + piece + " pieces (" + i
                        + "/" + num + " placed)");
                while (pos == -1)
                    pos = getInput();
                board.setPiece(pos, idx + 1);
                board.show();
            }
        }
    }
}
