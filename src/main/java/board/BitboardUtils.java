package main.java.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.game.GameUtils;

/**
 * Utility functions to facilitate play using bitboards
 */
public class BitboardUtils {
    private static Map<Integer, Set<Integer>> posToAdjCCID = new HashMap<>();
    private static Map<Integer, Integer> ccIDToOwner = new HashMap<>();
    private static Map<Integer, Integer> ownerToCCs = new HashMap<>();
    private static Map<Integer, Integer> ccIDToCC = new HashMap<>();

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
     * Get all sliding actions for a given board state for a given player
     * 
     * @param board Board to analyze
     * @param turn  Turn indicator
     * @return List<Integer> of sliding moves where the source and destination are set to 1
     */
    public static List<Integer> getSlideActions(Bitboard board, int turn) {
        posToAdjCCID.clear();
        ccIDToOwner.clear();
        ownerToCCs.clear();
        ccIDToCC.clear();
        int toCheck = (BitMasks.valid & (~board.getPieces()));
        int check;
        int cc, ccId = 0;
        while (toCheck != 0) {
            check = toCheck & ~(toCheck - 1);
            cc = SearchUtils.checkSpaces(board, check, ccId, posToAdjCCID, ccIDToOwner, ownerToCCs);
            toCheck ^= cc;
            ccIDToCC.put(ccId, cc);
            ccId++;
        }
        List<Integer> slides = new ArrayList<>();
        slides.add(0);
        int pieces = board.getPieces(turn);
        int pieceMask, dests, destMask;
        while (pieces != 0) {
            pieceMask = pieces & ~(pieces - 1);
            pieces ^= pieceMask;
            if (posToAdjCCID.containsKey(pieceMask)) {
                for (int id : posToAdjCCID.get(pieceMask)) {
                    dests = ccIDToCC.get(id);
                    while (dests != 0) {
                        destMask = dests & ~(dests - 1);
                        dests ^= destMask;
                        slides.add(pieceMask | destMask);
                    }
                }
            }
        }

        return slides;
    }

    /**
     * Get all the push actions for a given board state for a given player
     * 
     * @param board Board to analyze
     * @param turn  Turn indicator
     * @return List<Integer> of push actions encoded in a bit mask
     */
    public static List<Integer> getPushActions(Bitboard board, int turn) {
        List<Integer> pushes = new ArrayList<>();
        int pieceMask, dir;
        int piecesMask = board.getSquares(turn);
        while (piecesMask != 0) {
            pieceMask = piecesMask & ~(piecesMask - 1);
            piecesMask ^= pieceMask;
            for (dir = 0; dir < 4; dir++) {
                if (isValidPush(board, pieceMask, GameUtils.dirIntToChar[dir]))
                    pushes.add(pieceMask | BitMasks.dirMasks[dir]);
            }
        }
        return pushes;
    }

    /**
     * Find all possible next states from a given board position for a given player
     * 
     * @param board Board to analyze
     * @param turn  Turn indicator
     * @return List<int[]> of bitboards corresponding to possible next states
     */
    public static Set<Bitboard> getNextStates(Bitboard board, int turn) {
        Set<Bitboard> states = new HashSet<>();

        int pos1, pos2, dirMask;
        int startMask1 = 0, endMask1 = 0, startMask2 = 0, endMask2 = 0;
        Bitboard prePush;
        // for all possible first sliding actions
        for (int slide1 : getSlideActions(board, turn)) {
            // perform slide if not skipped
            if (slide1 != 0) {
                pos1 = slide1 & ~(slide1 - 1);
                pos2 = slide1 ^ pos1;
                if (board.owns(pos1, turn)) {
                    startMask1 = pos1;
                    endMask1 = pos2;
                } else if (board.owns(pos2, turn)) {
                    startMask1 = pos2;
                    endMask1 = pos1;
                } else {
                    System.out.println("error in first slide");
                    continue;
                }
                board.slide(startMask1, endMask1);
            }
            // for all possible second sliding actions
            for (int slide2 : getSlideActions(board, turn)) {
                // perform slide if not skipped
                if (slide2 != 0) {
                    pos1 = slide2 & ~(slide2 - 1);
                    pos2 = slide2 ^ pos1;
                    if (board.owns(pos1, turn)) {
                        startMask2 = pos1;
                        endMask2 = pos2;
                    } else if (board.owns(pos2, turn)) {
                        startMask2 = pos2;
                        endMask2 = pos1;
                    } else {
                        System.out.println("error in second slide");
                        continue;
                    }
                    board.slide(startMask2, endMask2);
                }

                // save board state pre-push
                prePush = board.getState();
                // perform all valid pushes
                for (int push : getPushActions(board, turn)) {
                    if (!board.isValid(push & ~(push - 1))) {
                        dirMask = push & ~(push - 1);
                        pos1 = push ^ dirMask;
                    } else {
                        pos1 = push & ~(push - 1);
                        dirMask = push ^ pos1;
                    }
                    board.push(pos1, BitMasks.dirMaskToChar.get(dirMask));

                    // only keep this state if it's not suicidal
                    if (checkWinner(board) != 1 - turn)
                        states.add(board.getState());

                    // reset board to pre-push state
                    board.restoreState(prePush);
                }
                // undo second slide
                if (slide2 != 0)
                    board.slide(endMask2, startMask2);
            }
            // undo first slide
            if (slide1 != 0)
                board.slide(endMask1, startMask1);
        }
        return states;
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
}
