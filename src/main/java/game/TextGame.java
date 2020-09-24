package main.java.game;

import java.util.HashSet;
import java.util.Scanner;

import main.java.agents.Agent;
import main.java.agents.alphaBeta.AlphaBetaAgent;
import main.java.agents.random.RandomAgent;
import main.java.board.Board;

public class TextGame {
    private Scanner scan;
    private Board board;
    private int turn;
    private Agent p1, p2;

    public TextGame() {
        scan = new Scanner(System.in);
        board = new Board();
        turn = 0;
        // setup players
        choosePlayer(0);
        choosePlayer(1);
        // we're skipping the setup phase of the game for now
        // setup();
        skipSetup();
        gameLoop();
    }

    /**
     * Prompt user to pick the player
     * 
     * @param turn Turn indicator
     */
    public void choosePlayer(int turn) {
        System.out.print("Choose Player " + (turn + 1) + " (human|random|alpha): ");
        String player = scan.nextLine();
        switch (player.trim().toLowerCase()) {
            case "random":
                setAgent(turn, new RandomAgent());
                break;
            case "alpha":
                setAgent(turn, new AlphaBetaAgent());
                break;
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
        int loser;
        while (true) {
            board.show();

            loser = makeMove(turn);
            if (loser != -1) {
                board.show();
                System.out.println("Player " + (1 - loser + 1) + " wins!");
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
     * @return The player who just lost (0|1) or -1 if no loser
     */
    public int makeMove(int turn) {
        if (turn == 0) {
            if (p1 == null)
                return userMove(turn);
            return agentMove(p1, turn);
        } else {
            if (p2 == null)
                return userMove(turn);
            return agentMove(p2, turn);
        }
    }

    /**
     * Get, decode, and perform move from Agent
     * 
     * @param agent The agent to make the move
     * @param turn  Turn indicator
     * @return The player who just lost (0|1) or -1 if no loser
     */
    public int agentMove(Agent agent, int turn) {
        // get the move from the agent
        int[] move = agent.getMove(board, turn);

        // decode and perform two sliding actions
        int startPos, endPos;
        for (int i = 0; i < 2; i++) {
            // can skip sliding action
            if (move[i] == 0)
                continue;
            startPos = move[i] / 100;
            endPos = move[i] % 100;
            System.out.println("Player " + (turn + 1) + ": " + posToLabel(startPos) + " => "
                    + posToLabel(endPos));
            board.slide(startPos / 10, startPos % 10, endPos / 10, endPos % 10);
        }
        // decode and perform push action
        startPos = move[2] / 10;
        char dir = GameUtils.dirIntToChar(move[2] % 10);
        System.out.println("Player " + (turn + 1) + ": " + posToLabel(startPos) + " going " + dir);
        return board.push(startPos / 10, startPos % 10, dir);
    }

    /**
     * Prompt user for input to make their turn
     * 
     * @param turn Turn indicator
     * @return The player who just lost (0|1) or -1 if no loser
     */
    public int userMove(int turn) {
        int i;
        // perform two sliding actions
        for (i = 0; i < 2; i++) {
            slideAction(turn, i + 1);
        }
        // perform one pushing action
        return pushAction(turn);
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
        HashSet<Integer> dests = GameUtils.findSlideDests(board, startPos / 10, startPos % 10);

        // get sliding action destination
        int endPos = -1;
        System.out.println("Player " + (turn + 1) + ", choose destination");
        while (endPos == -1 || !dests.contains(endPos)) {
            endPos = getInput();

            // allow for an empty action
            if (startPos == endPos) {
                board.show();
                return;
            }
        }

        board.slide(startPos / 10, startPos % 10, endPos / 10, endPos % 10);
        board.show();
    }

    /**
     * Get input and validate a push action
     * 
     * @param turn Turn indicator
     * @return owner of piece that was pushed off, or -1 if none
     */
    public int pushAction(int turn) {
        int pos = -1;
        int row = -1, col = -1;
        char dir = ' ';
        while (true) {
            // prompt for piece to push
            System.out.println("Player " + (turn + 1) + ", choose a piece to push (required)");
            while (pos == -1)
                pos = getInput(turn);
            row = pos / 10;
            col = pos % 10;
            // prompt for push direction
            System.out.println("Player " + (turn + 1) + ", choose pushing direction (r|l|u|d)");
            dir = getDir();

            // validate push, and loop if invalid
            if (!GameUtils.isValidPush(board, row, col, dir)) {
                board.show();
                pos = -1;
                continue;
            }

            // perform push
            return board.push(row, col, dir);
        }
    }

    /**
     * Prompt user for a location on the board
     * 
     * @return Board position encoded as row * 10 + col
     */
    public int getInput() {
        String line;
        int row = -1, col = -1;
        while (!board.isValid(row, col)) {
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
        return row * 10 + col;

    }

    /**
     * Prompt user for a location that they own
     * 
     * @param turn Turn indicator
     * @return Board position encoded as row * 10 + col
     */
    public int getInput(int turn) {
        String line;
        int row = -1, col = -1;
        while (!board.isValid(row, col) || !board.owns(row, col, turn)) {
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
        return row * 10 + col;
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
     * Return label representation of board position
     * 
     * @param pos Board position to label in form row * 10 + col
     * @return The board label (e.g. a3) corresponding to the position
     */
    public String posToLabel(int pos) {
        return (char) ('a' + (pos / 10)) + "" + (pos % 10 + 1);
    }

    /**
     * For convenience, skip setup step and use a default setup
     * 
     * @return true if setup was skipped, else false
     */
    public boolean skipSetup() {
        // System.out.println("Use default setup? (y|n)");
        // String line = scan.nextLine();
        // if (line.equalsIgnoreCase("y")) {

        // set player 1's pieces
        board.setPiece(0, 3, 2);
        board.setPiece(1, 3, 1);
        board.setPiece(2, 3, 1);
        board.setPiece(3, 3, 2);
        board.setPiece(2, 2, 2);
        // set player 2's pieces
        board.setPiece(0, 4, 4);
        board.setPiece(1, 4, 3);
        board.setPiece(2, 4, 3);
        board.setPiece(3, 4, 4);
        board.setPiece(1, 5, 4);
        return true;

        // }
        // return false;
    }

    /**
     * Prompt user to layout their pieces
     */
    public void setup() {
        if (skipSetup())
            return;
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
                board.setPiece(pos / 10, pos % 10, idx + 1);
                board.show();
            }
        }
    }
}
