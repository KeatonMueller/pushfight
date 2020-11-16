package main.java.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statically defined bit masks to facilitate play using bitboards
 */
public class BitMasks {
    // bits of an integer arranged as 4x8 grid
    // 00 01 02 03 04 05 06 07
    // 08 09 10 11 12 13 14 15
    // 16 17 18 19 20 21 22 23
    // 24 25 26 27 28 29 30 31

    /**
     * Valid squares on the board (everything except the holes)
     */
    public static int valid = ~(setBits(new int[] {0, 1, 7, 24, 30, 31}));

    /**
     * All valid positions on the right side of the board
     */
    public static int rightSide = setBits(new int[] {6, 15, 23, 29});
    /**
     * All valid positions on the left side of the board
     */
    public static int leftSide = setBits(new int[] {2, 8, 16, 25});
    /**
     * Just the very top row of the board
     */
    public static int topSide = setBits(new int[] {2, 3, 4, 5, 6});
    /**
     * Just the very bottom row of the board
     */
    public static int bottomSide = setBits(new int[] {25, 26, 27, 28, 29});
    /**
     * All edge positions that you can be pushed off from
     */
    public static int edges = setBits(new int[] {2, 6, 8, 9, 15, 16, 22, 23, 25, 29});

    /**
     * Orthogonally connected squares to the given bit mask. Ignores invalid positions
     */
    public static Map<Integer, Integer> orthogonal = new HashMap<>();
    static {
        orthogonal.put((1 << 2), setBits(new int[] {3, 10}));
        orthogonal.put((1 << 3), setBits(new int[] {2, 4, 11}));
        orthogonal.put((1 << 4), setBits(new int[] {3, 5, 12}));
        orthogonal.put((1 << 5), setBits(new int[] {4, 6, 13}));
        orthogonal.put((1 << 6), setBits(new int[] {5, 14}));
        orthogonal.put((1 << 8), setBits(new int[] {9, 16}));
        orthogonal.put((1 << 9), setBits(new int[] {8, 10, 17}));
        orthogonal.put((1 << 10), setBits(new int[] {2, 9, 11, 18}));
        orthogonal.put((1 << 11), setBits(new int[] {3, 10, 12, 19}));
        orthogonal.put((1 << 12), setBits(new int[] {4, 11, 13, 20}));
        orthogonal.put((1 << 13), setBits(new int[] {5, 12, 14, 21}));
        orthogonal.put((1 << 14), setBits(new int[] {6, 13, 15, 22}));
        orthogonal.put((1 << 15), setBits(new int[] {14, 23}));
        orthogonal.put((1 << 16), setBits(new int[] {8, 17}));
        orthogonal.put((1 << 17), setBits(new int[] {9, 16, 18, 25}));
        orthogonal.put((1 << 18), setBits(new int[] {10, 17, 19, 26}));
        orthogonal.put((1 << 19), setBits(new int[] {11, 18, 20, 27}));
        orthogonal.put((1 << 20), setBits(new int[] {12, 19, 21, 28}));
        orthogonal.put((1 << 21), setBits(new int[] {13, 20, 22, 29}));
        orthogonal.put((1 << 22), setBits(new int[] {14, 21, 23}));
        orthogonal.put((1 << 23), setBits(new int[] {15, 22}));
        orthogonal.put((1 << 25), setBits(new int[] {17, 26}));
        orthogonal.put((1 << 26), setBits(new int[] {18, 25, 27}));
        orthogonal.put((1 << 27), setBits(new int[] {19, 26, 28}));
        orthogonal.put((1 << 28), setBits(new int[] {20, 27, 29}));
        orthogonal.put((1 << 29), setBits(new int[] {21, 28}));
    }

    /**
     * List of checkmate patterns against circle pieces. Each element in the outer list is a single
     * checkmate pattern. Inner lists contain bitmasks in the following order: opponent's circle
     * piece location, current player's square piece location, current player's any-piece locations.
     */
    public static List<List<Integer>> circleCheckmatePatterns = new ArrayList<>();
    static {
        // capture circle in top left corner
        circleCheckmatePatterns.add(Arrays.asList((1 << 2), (1 << 3), (1 << 10)));
        // capture circle in bottom left corner
        circleCheckmatePatterns.add(Arrays.asList((1 << 25), (1 << 26), (1 << 17)));
        // capture circle in top right corner
        circleCheckmatePatterns.add(Arrays.asList((1 << 6), (1 << 5), (1 << 14)));
        // capture circle in bottom right corner
        circleCheckmatePatterns.add(Arrays.asList((1 << 29), (1 << 28), (1 << 21)));
    }

    /**
     * List of checkmate patterns against square pieces. Each element in the outer list is a single
     * checkmate pattern. Inner lists contain bitmasks in the following order: opponent's square
     * piece location, opponent's any-piece locations, current player's square piece location,
     * current player's any-piece locations.
     */
    public static List<List<Integer>> squareCheckmatePatterns = new ArrayList<>();
    static {
        // capture square in top left corner
        squareCheckmatePatterns
                .add(Arrays.asList((1 << 2), 0, (1 << 3), (1 << 10) | (1 << 18) | (1 << 26)));
        // alternate capture square in top left corner
        squareCheckmatePatterns.add(Arrays.asList((1 << 2), (1 << 10), (1 << 3),
                (1 << 9) | (1 << 11) | (1 << 18) | (1 << 26)));
        // capture square in bottom right corner
        squareCheckmatePatterns
                .add(Arrays.asList((1 << 29), 0, (1 << 28), (1 << 21) | (1 << 13) | (1 << 5)));
        // alternate capture square in bottom right corner
        squareCheckmatePatterns.add(Arrays.asList((1 << 29), (1 << 21), (1 << 28),
                (1 << 20) | (1 << 22) | (1 << 13) | (1 << 5)));
    }

    /**
     * Return an integer with the given bit positions set to 1
     * 
     * @param positions Array of bit indices. 0 <= positions[i] < 32
     * @return Integer with the proper bits set
     */
    private static int setBits(int[] positions) {
        int result = 0;
        for (int i = 0; i < positions.length; i++) {
            result |= (1 << positions[i]);
        }
        return result;
    }
}
