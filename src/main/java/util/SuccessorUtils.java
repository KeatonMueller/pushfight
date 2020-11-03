package main.java.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.board.Bitboard;
import main.java.board.BitMasks;
import main.java.board.Move;
import main.java.board.State;

/**
 * Utility functions to determine the next states or available actions for a board state
 */
public class SuccessorUtils {
    private static Map<Integer, Set<Integer>> posToAdjCCID = new HashMap<>();
    private static Map<Integer, Integer> ccIDToOwner = new HashMap<>();
    private static Map<Integer, Integer> ownerToCCs = new HashMap<>();
    private static Map<Integer, Integer> ccIDToCC = new HashMap<>();

    /**
     * Find all possible next states from a given board position for a given player
     * 
     * @param board Board to analyze
     * @param turn  Turn indicator
     * @return Set<Bitboard> of bitboards corresponding to possible next states
     */
    public static Set<Bitboard> getNextStates(Bitboard board, int turn) {
        // computed list of next states
        Set<Bitboard> states = new HashSet<>();
        // record set of board states seen at each level to avoid recomputation
        List<Set<Bitboard>> seen = new ArrayList<>();
        for (int i = 0; i < GameUtils.NUM_SLIDES + 1; i++) {
            seen.add(new HashSet<>());
        }
        getNextStatesHelper(board, turn, GameUtils.NUM_SLIDES, states, seen);
        return states;
    }


    /**
     * Helper function for finding next states with a variable number of sliding actions
     * 
     * @param board     Board to find moves for
     * @param turn      Turn indicator
     * @param numSlides Number of slides remaining in turn
     * @param states    Set<Bitboard> of computed next states
     * @param seen      List<Set<Bitboard>> List of seen board states at different levels
     */
    public static void getNextStatesHelper(Bitboard board, int turn, int numSlides,
            Set<Bitboard> states, List<Set<Bitboard>> seen) {
        // skip if been here before
        if (seen.get(numSlides).contains(board))
            return;
        // remember state
        seen.get(numSlides).add(board);
        // save board state
        Bitboard preState = board.getState();

        if (numSlides == 0) {
            // if zero slides remaining, check all push actions
            List<Integer> pushes = getPushActions(board, turn);
            for (int i = 0; i < pushes.size() - 1; i += 2) {
                // perform push
                board.push(pushes.get(i), (char) (int) pushes.get(i + 1));

                // only keep this state if it's not suicidal
                if (BitboardUtils.checkWinner(board) != 1 - turn)
                    states.add(board.getState());

                // undo push
                board.restoreState(preState);
            }
        } else {
            // otherwise check all slide actions
            List<Integer> slides = getSlideActions(board, turn);
            // recurse on skipped slide action
            getNextStatesHelper(board, turn, numSlides - 1, states, seen);
            // check all slides
            for (int i = 0; i < slides.size() - 1; i += 2) {
                // perform slide
                board.slide(slides.get(i), slides.get(i + 1));
                // recurse
                getNextStatesHelper(board, turn, numSlides - 1, states, seen);
                // undo slide
                board.slide(slides.get(i + 1), slides.get(i));
            }
        }
    }

    /**
     * Find all possible next states from a given board position for a given player, and also record
     * the move taken to reach that state
     * 
     * @param board Board to analyze
     * @param turn  Turn indicator
     * @return Set<Successor> of Successors corresponding to possible next states and the moves
     *         taken to reach them
     */
    public static Set<State> getSuccessors(Bitboard board, int turn) {
        // computed list of next states
        Set<State> successors = new HashSet<>();
        // record set of board states seen at each level to avoid recomputation
        List<Set<Bitboard>> seen = new ArrayList<>();
        for (int i = 0; i < GameUtils.NUM_SLIDES + 1; i++) {
            seen.add(new HashSet<>());
        }
        getSuccessorsHelper(board, turn, GameUtils.NUM_SLIDES, new Move(), successors, seen);
        return successors;
    }


    /**
     * Helper function for finding next successors with a variable number of sliding actions
     * 
     * @param board      Board to find moves for
     * @param turn       Turn indicator
     * @param numSlides  Number of slides remaining in turn
     * @param move       Generated Move so far
     * @param successors Set<Successor> of computed next successors
     * @param seen       List<Set<Bitboard>> List of seen board states at different levels
     */
    public static void getSuccessorsHelper(Bitboard board, int turn, int numSlides, Move move,
            Set<State> states, List<Set<Bitboard>> seen) {
        // skip if been here before
        if (seen.get(numSlides).contains(board))
            return;
        // remember state
        seen.get(numSlides).add(board);
        // save board state
        Bitboard preState = board.getState();

        if (numSlides == 0) {
            // if zero slides remaining, check all push actions
            List<Integer> pushes = getPushActions(board, turn);
            for (int i = 0; i < pushes.size() - 1; i += 2) {
                // perform push
                board.push(pushes.get(i), (char) (int) pushes.get(i + 1));
                move.add(pushes.get(i));
                move.add(pushes.get(i + 1));

                // only keep this state if it's not suicidal
                if (BitboardUtils.checkWinner(board) != 1 - turn)
                    states.add(new State(board.getState(), move));

                // undo push
                board.restoreState(preState);
                move.pop();
                move.pop();
            }
        } else {
            // otherwise check all slide actions
            List<Integer> slides = getSlideActions(board, turn);
            // recurse on skipped slide action
            getSuccessorsHelper(board, turn, numSlides - 1, move, states, seen);
            // check all slides
            for (int i = 0; i < slides.size() - 1; i += 2) {
                // perform slide
                board.slide(slides.get(i), slides.get(i + 1));
                move.add(slides.get(i));
                move.add(slides.get(i + 1));
                // recurse
                getSuccessorsHelper(board, turn, numSlides - 1, move, states, seen);
                // undo slide
                board.slide(slides.get(i + 1), slides.get(i));
                move.pop();
                move.pop();
            }
        }
    }

    /**
     * Get all sliding actions for a given board state for a given player
     * 
     * @param board Board to analyze
     * @param turn  Turn indicator
     * @return List of sliding actions, alternating source and destination bit masks
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
                        slides.add(pieceMask);
                        slides.add(destMask);
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
     * @return List of push actions, alternating location and direction indicators
     */
    public static List<Integer> getPushActions(Bitboard board, int turn) {
        List<Integer> pushes = new ArrayList<>();
        int pieceMask;
        int piecesMask = board.getSquares(turn);
        while (piecesMask != 0) {
            pieceMask = piecesMask & ~(piecesMask - 1);
            piecesMask ^= pieceMask;
            for (char dir : GameUtils.DIRECTIONS) {
                if (BitboardUtils.isValidPush(board, pieceMask, dir)) {
                    pushes.add(pieceMask);
                    pushes.add((int) dir);
                }
            }
        }
        return pushes;
    }
}
