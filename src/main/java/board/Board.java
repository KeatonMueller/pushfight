package main.java.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import main.java.game.GameUtils;

public class Board {
    private final int LENGTH = GameUtils.LENGTH;
    private final int HEIGHT = GameUtils.HEIGHT;
    private int[][] board;
    private int anchorRow, anchorCol;
    private List<HashSet<Integer>> locations;

    // Board values:
    // -1 : invalid
    // 0 : empty
    // 1 : p1 circle
    // 2 : p1 square
    // 3 : p2 circle
    // 4 : p2 square

    public Board() {
        // initialize board
        board = new int[HEIGHT][LENGTH];
        // configure irregular board shape by setting invalid spots
        board[0][0] = -1;
        board[0][1] = -1;
        board[0][LENGTH - 1] = -1;
        board[HEIGHT - 1][0] = -1;
        board[HEIGHT - 1][LENGTH - 1] = -1;
        board[HEIGHT - 1][LENGTH - 2] = -1;
        // anchor is initially not present
        anchorRow = -1;
        anchorCol = -1;
        // initialize the sets of piece locations for each player
        locations = new ArrayList<>();
        locations.add(new HashSet<>());
        locations.add(new HashSet<>());
    }

    /**
     * Sets the board at the given position with the given value
     * 
     * @param row Row to set
     * @param col Column to set
     * @param val Value to be placed at the position
     */
    public void setPiece(int row, int col, int val) {
        if (!isValid(row, col) || board[row][col] != 0)
            return;
        board[row][col] = val;
        // add this position to this player's list of piece locations
        locations.get(getOwner(val)).add(row * 10 + col);
    }

    /**
     * Check if a given player owns the piece at a board location
     * 
     * @param row  Row to check
     * @param col  Column to check
     * @param turn Turn indicator
     * @return true if the player owns the piece, else false
     */
    public boolean owns(int row, int col, int turn) {
        return getOwner(board[row][col]) == turn;
    }

    /**
     * Get the owner of the given piece value
     * 
     * @param value The board value to find the owner of
     * @return 0 if owned by p1, 1 if owned by p2, -1 if no owner
     */
    private int getOwner(int value) {
        if (value == 1 || value == 2)
            return 0;
        if (value == 3 || value == 4)
            return 1;
        return -1;
    }

    /**
     * Return the locations of all the pieces for the given player
     * 
     * @param turn Turn indicator
     * @return HashSet of locations of the pieces
     */
    public HashSet<Integer> getPieceLocs(int turn) {
        if (turn == 0 || turn == 1)
            return locations.get(turn);
        return null;
    }

    /**
     * Check if given board position contains a square
     * 
     * @param row The row to check
     * @param col The column to check
     * @return true if the position has a square, else false
     */
    public boolean isSquare(int row, int col) {
        return board[row][col] == 2 || board[row][col] == 4;
    }

    /**
     * Check if a row and column is on the board
     * 
     * @param row The row to check
     * @param col The column to check
     * @return true if position specifies a location on the board, else false
     */
    public boolean isValid(int row, int col) {
        // check for out of bounds
        if (row < 0 || row >= HEIGHT || col < 0 || col >= LENGTH) {
            return false;
        }
        // check for the 6 irregularities in the board
        if ((row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == LENGTH - 1)
                || (row == HEIGHT - 1 && col == 0) || (row == HEIGHT - 1 && col == LENGTH - 1)
                || (row == HEIGHT - 1 && col == LENGTH - 2)) {
            return false;
        }
        return true;
    }

    /**
     * Check if the given position is empty. Validation of position must be done by caller
     * 
     * @param row Row to check
     * @param col Column to check
     * @return true if board position is empty, else false
     */
    public boolean isEmpty(int row, int col) {
        return board[row][col] == 0;
    }

    /**
     * Check if given position is anchored
     * 
     * @param row Row to check
     * @param col Col to check
     * @return true if position is anchored, else false
     */
    public boolean isAnchored(int row, int col) {
        return row == anchorRow && col == anchorCol;
    }

    /**
     * Perform a sliding move from old position to new position. Slide must be validated by caller
     * 
     * @param oldRow Old row of piece
     * @param oldCol Old column of piece
     * @param newRow New row of piece
     * @param newCol New column of piece
     */
    public void slide(int oldRow, int oldCol, int newRow, int newCol) {
        board[newRow][newCol] = board[oldRow][oldCol];
        board[oldRow][oldCol] = 0;
        // update sets of piece locations
        int owner = getOwner(board[newRow][newCol]);
        if (owner == -1) {
            System.out.println(
                    "ERROR! Asked to move empty/invalid position " + oldRow + ", " + oldCol);
            return;
        }
        locations.get(owner).remove(oldRow * 10 + oldCol);
        locations.get(owner).add(newRow * 10 + newCol);
    }

    /**
     * Perform a push and update the board state. Push must be validated by caller
     * 
     * @param row Row of piece to push from
     * @param col Column of piece to push from
     * @param dir Direction to push (r|l|u|d)
     * @return A push result of form [oldAnchorRow, oldAnchorCol, finalRow, finalCol, lastPiece,
     *         lastPieceOwner]
     */
    public int[] push(int row, int col, char dir) {
        int[] pushResult = new int[6];
        // save anchor position
        pushResult[0] = anchorRow;
        pushResult[1] = anchorCol;
        // get deltas based on pushing direction
        int[] delta = GameUtils.getDeltas(dir);
        // update anchor position
        anchorRow = row + delta[0];
        anchorCol = col + delta[1];
        // push the pieces
        int temp, prevPiece = 0;
        int oldOwner, newOwner;
        while (isValid(row, col)) {
            // swap pieces
            temp = board[row][col];
            board[row][col] = prevPiece;
            prevPiece = temp;

            // update ownership lists
            oldOwner = getOwner(prevPiece);
            newOwner = getOwner(board[row][col]);
            if (oldOwner != -1) {
                locations.get(oldOwner).remove(row * 10 + col);
            }
            if (newOwner != -1) {
                locations.get(newOwner).add(row * 10 + col);
            }

            // update row and col
            row += delta[0];
            col += delta[1];
            if (prevPiece == 0) {
                break;
            }
        }
        // store location of the end of the push and last piece
        pushResult[2] = row - delta[0];
        pushResult[3] = col - delta[1];
        pushResult[4] = prevPiece;
        pushResult[5] = getOwner(prevPiece);
        return pushResult;
    }

    /**
     * Undo the given push action
     * 
     * @param pushResult A push result of form [oldAnchorRow, oldAnchorCol, finalRow, finalCol,
     *                   lastPiece, lastPieceOwner]
     * @param dir        The direction the original push was made in
     */
    public void undoPush(int[] pushResult, char dir) {
        // get deltas based on pushing direction
        int[] delta = GameUtils.getDeltas(dir);
        // reverse deltas
        delta[0] *= -1;
        delta[1] *= -1;

        // restore anchor position
        anchorRow = pushResult[0];
        anchorCol = pushResult[1];

        // push the pieces
        int temp, prevPiece = pushResult[4];
        int oldOwner, newOwner;
        int row = pushResult[2];
        int col = pushResult[3];
        while (isValid(row, col)) {
            // swap pieces
            temp = board[row][col];
            board[row][col] = prevPiece;
            prevPiece = temp;

            // update ownership lists
            oldOwner = getOwner(prevPiece);
            newOwner = getOwner(board[row][col]);
            if (oldOwner != -1) {
                locations.get(oldOwner).remove(row * 10 + col);
            }
            if (newOwner != -1) {
                locations.get(newOwner).add(row * 10 + col);
            }

            // update row and col
            row += delta[0];
            col += delta[1];
            if (prevPiece == 0) {
                return;
            }
        }
    }

    /**
     * Perform a full, 3-action move sequence
     * 
     * @param slide1 The first slide action, encoded as oldPos * 100 + newPos
     * @param slide2 The second slide action, encoded as oldPos * 100 + newPos
     * @param push   The push action, encoded as pos * 10 + dir
     * @return The push result of form [oldAnchorRow, oldAnchorCol, finalRow, finalCol, lastPiece,
     *         lastPieceOwner]
     */
    public int[] move(int slide1, int slide2, int push) {
        // saveBoard();
        int oldPos, newPos;
        if (slide1 != 0) {
            oldPos = slide1 / 100;
            newPos = slide1 % 100;
            slide(oldPos / 10, oldPos % 10, newPos / 10, newPos % 10);
        }
        if (slide2 != 0) {
            oldPos = slide2 / 100;
            newPos = slide2 % 100;
            slide(oldPos / 10, oldPos % 10, newPos / 10, newPos % 10);
        }
        oldPos = push / 10;
        return push(oldPos / 10, oldPos % 10, GameUtils.dirIntToChar(push % 10));
    }

    /**
     * Undo a full, 3-action move sequence
     * 
     * @param slide1     The first slide action, encoded as oldPos * 100 + newPos
     * @param slide2     The second slide action, encoded as oldPos * 100 + newPos
     * @param pushResult The push result of form [oldAnchorRow, oldAnchorCol, finalRow, finalCol,
     *                   lastPiece, lastPieceOwner]
     * @param dir        The direction of the original push
     */
    public void undoMove(int slide1, int slide2, int[] pushResult, char dir) {
        int oldPos, newPos;
        undoPush(pushResult, dir);
        if (slide2 != 0) {
            oldPos = slide2 / 100;
            newPos = slide2 % 100;
            slide(newPos / 10, newPos % 10, oldPos / 10, oldPos % 10);
        }
        if (slide1 != 0) {
            oldPos = slide1 / 100;
            newPos = slide1 % 100;
            slide(newPos / 10, newPos % 10, oldPos / 10, oldPos % 10);
        }
    }

    /**
     * Generate a BoardState object given current board position
     * 
     * @return The BoardState corresponding to current state
     */
    public BoardState generateState() {
        return new BoardState(board, anchorRow, anchorCol);
    }

    /**
     * Get character representation of board piece
     * 
     * @param row Row of piece
     * @param col Column of piece
     * @return Character representing the piece at the given row and column
     */
    public String getChar(int row, int col) {
        // anchored piece denoted by an x (p1) or X (p2)
        if (isAnchored(row, col)) {
            if (board[row][col] == 2)
                return "x ";
            else if (board[row][col] == 4)
                return "X ";
        }
        switch (board[row][col]) {
            case -1:
                return "  ";
            case 0:
                return ". ";
            case 1:
                return "c ";
            case 2:
                return "s ";
            case 3:
                return "C ";
            case 4:
                return "S ";
            default:
                return "? ";
        }
    }

    /**
     * Print column labels, shifted by 2 spaces
     */
    public void printColumnLabels() {
        System.out.print("  ");
        for (int col = 0; col < LENGTH; col++) {
            System.out.print(col + 1 + " ");
        }
        System.out.println();
    }

    /**
     * Print the board state
     */
    public void show() {
        printColumnLabels();
        System.out.println("      ---------");
        for (int row = 0; row < HEIGHT; row++) {
            System.out.print((char) ('a' + row) + " ");
            for (int col = 0; col < LENGTH; col++) {
                System.out.print(getChar(row, col));
            }
            System.out.println((char) ('a' + row));
        }
        System.out.println("    ---------");
        printColumnLabels();
    }
}
