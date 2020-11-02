package main.java.util;

import main.java.board.Bitboard;
import main.java.board.BitMasks;

/**
 * Utility functions to facilitate play using bitboards
 */
public class BitboardUtils {

    /**
     * Count the number of set bits in a bit mask
     * 
     * @param mask The mask to count the bits of
     * @return The number of set bits
     */
    private static int countSetBits(int mask) {
        int count = 0;
        while (mask != 0) {
            count++;
            mask &= (mask - 1);
        }
        return count;
    }

    /**
     * Check the board state to see if someone has won
     * 
     * @param board The board to check
     * @return Index of winner (0|1) or -1 if no winner
     */
    public static int checkWinner(Bitboard board) {
        if (countSetBits(board.getPieces(0)) != 5)
            return 1;
        if (countSetBits(board.getPieces(1)) != 5)
            return 0;
        return -1;
    }

    /**
     * For convenience, skip the setup phase of the game
     * 
     * @param board The board to perform the setup on
     */
    public static void skipSetup(Bitboard board) {
        // set player 1's pieces
        board.setPiece(3, 1);
        board.setPiece(11, 0);
        board.setPiece(19, 0);
        board.setPiece(27, 1);
        board.setPiece(18, 1);
        // set player 2's pieces
        board.setPiece(4, 3);
        board.setPiece(12, 2);
        board.setPiece(20, 2);
        board.setPiece(28, 3);
        board.setPiece(13, 3);
    }

    /**
     * Find the valid slide destinations for the given position
     * 
     * @param board   Board to analyze
     * @param posMask Position to begin slide from
     * @return Bit mask with valid destinations set to 1
     */
    public static int findSlideDests(Bitboard board, int posMask) {
        int dests = 0;
        int queue = 0;
        int visited = 0;

        queue |= BitMasks.orthogonal.get(posMask);
        int nextMask;
        while (queue != 0) {
            // get the next position off the queue
            nextMask = queue & ~(queue - 1);
            queue ^= nextMask;

            if ((nextMask & visited) != 0)
                continue;
            visited |= nextMask;

            if (!board.isValid(nextMask))
                continue;

            if (board.isEmpty(nextMask))
                dests |= nextMask;
            else
                continue;

            queue |= BitMasks.orthogonal.get(nextMask);
        }
        return dests;
    }

    /**
     * Find the orthogonally connected pieces which may be pushed by the piece at the given position
     * 
     * @param board   The board to analyze
     * @param posMask The position from which to push
     * @return Bit mask with valid pushable pieces set to 1
     */
    public static int findPushablePieces(Bitboard board, int posMask) {
        int pushable = 0;
        if (!board.isSquare(posMask))
            return pushable;
        for (int dir = 0; dir < 4; dir++) {
            if (isValidPush(board, posMask, GameUtils.dirIntToChar[dir])) {
                pushable |= updateMask(posMask, GameUtils.dirIntToChar[dir]);
            }
        }
        return pushable;
    }

    /**
     * Perform the slide encoded in the given int
     * 
     * @param board Bitboard to perform slide on
     * @param slide Encoded slide action, where source and destination bits are set
     * @param turn  Turn indicator
     */
    public static void decodeSlide(Bitboard board, int slide, int turn) {
        if (slide == 0)
            return;
        int pos1 = slide & ~(slide - 1);
        int pos2 = slide ^ pos1;
        if (board.owns(pos1, turn)) {
            board.slide(pos1, pos2);
        } else if (board.owns(pos2, turn)) {
            board.slide(pos2, pos1);
        } else {
            System.out.println("error in slide");
        }
    }

    /**
     * Perform the push encoded in the given int
     * 
     * @param board Bitboard to perform slide on
     * @param push  Encoded push action, where source bit and direction indicator are set
     * @param turn  Turn indicator
     */
    public static void decodePush(Bitboard board, int push, int turn) {
        int maskOne = push & ~(push - 1);
        int maskTwo = push ^ maskOne;
        if (board.isValid(maskOne)) {
            board.push(maskOne, BitMasks.dirMaskToChar.get(maskTwo));
        } else if (board.isValid(maskTwo)) {
            board.push(maskTwo, BitMasks.dirMaskToChar.get(maskOne));
        } else {
            System.out.println("error in push");
        }
    }

    public static void decodeActions(Bitboard board, int[] actions, int turn) {
        for (int i = 0; i < actions.length - 1; i++) {
            decodeSlide(board, actions[i], turn);
        }
        decodePush(board, actions[actions.length - 1], turn);
    }

    /**
     * Update the bit mask in the given direction
     * 
     * @param mask Bit mask to be updated
     * @param dir  Direction to move mask in
     * @return Updated bit mask, or 1 if pushed off a valid edge, or 0 if pushed off invalid edge
     */
    public static int updateMask(int mask, char dir) {
        switch (dir) {
            case 'r':
                if ((mask & BitMasks.rightSide) == 0)
                    return mask << 1;
                return 1;
            case 'l':
                if ((mask & BitMasks.leftSide) == 0)
                    return mask >> 1;
                return 1;
            case 'u':
                if ((mask & BitMasks.topSide) == 0)
                    return mask >> 8;
                return 0;
            case 'd':
                if ((mask & BitMasks.bottomSide) == 0)
                    return mask << 8;
                return 0;
            default:
                return 0;
        }
    }

    /**
     * Validate if a push in a given direction is valid
     * 
     * @param board   Board to analyze
     * @param posMask Location of pushing piece
     * @param dir     Direction of push
     * @return true if push is valid, else false
     */
    public static boolean isValidPush(Bitboard board, int posMask, char dir) {
        // pushing piece must be square
        if (!board.isSquare(posMask))
            return false;

        // next immediate piece must be valid and non-empty
        posMask = updateMask(posMask, dir);
        if (!board.isValid(posMask) || board.isEmpty(posMask))
            return false;

        // check the pieces in a line
        while (board.isValid(posMask)) {
            if (board.isEmpty(posMask))
                return true;
            if (board.isAnchored(posMask))
                return false;
            posMask = updateMask(posMask, dir);
        }
        // updateMask got pushed off an invalid edge, so push is invalid
        if (posMask == 0)
            return false;
        return true;
    }

    public static boolean isValidPush(Bitboard board, int push, int turn) {
        int maskOne = push & ~(push - 1);
        int maskTwo = push ^ maskOne;
        if (board.owns(maskOne, turn)) {
            return isValidPush(board, maskOne, BitMasks.dirMaskToChar.get(maskTwo));
        } else if (board.owns(maskTwo, turn)) {
            return isValidPush(board, maskTwo, BitMasks.dirMaskToChar.get(maskOne));
        } else {
            return false;
        }
    }

    public static boolean isValidSlide(Bitboard board, int slide, int turn) {
        if (slide == 0)
            return true;
        int pos1 = slide & ~(slide - 1);
        int pos2 = slide ^ pos1;
        if (board.owns(pos1, turn) && board.isEmpty(pos2)) {
            return true;
        } else if (board.owns(pos2, turn) && board.isEmpty(pos1)) {
            return true;
        } else {
            return false;
        }
    }
}
