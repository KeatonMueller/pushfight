package main.java.board.heuristic;

import java.util.Map;

public class HeuristicUtils {

    /**
     * Default values used to weight heuristic
     */
    public static double[] defaultValues = {1.0, 2.0, 1.0, 1.0, 200.0, 1000000.0, 100.0, -10.0, 3.0,
            -10.0, -10.0, 1.0, 10.0, -10.0, 0.0, 7.0, 10.0, -10.0, 3.0, 5.0};

    /**
     * Number of weights needed by heuristic
     */
    public static int numValues = defaultValues.length;

    /**
     * Number of components in the heuristic
     */
    public static int numComponents = 7;

    /**
     * Initialize mapping from board position to values with given values
     * 
     * @param boardValues Map from board position to position value
     * @param values      Array of board values
     */
    public static void initBoardValues(Map<Integer, Double> boardValues, double[] values) {
        boardValues.put((1 << 2), values[0]); // a3
        boardValues.put((1 << 29), values[0]); // d6

        boardValues.put((1 << 3), values[1]); // a4
        boardValues.put((1 << 28), values[1]); // d5

        boardValues.put((1 << 8), values[2]); // b1
        boardValues.put((1 << 23), values[2]); // c8

        boardValues.put((1 << 9), values[3]); // b2
        boardValues.put((1 << 22), values[3]); // c7

        boardValues.put((1 << 10), values[4]); // b3
        boardValues.put((1 << 21), values[4]); // c6

        boardValues.put((1 << 11), values[5]); // b4
        boardValues.put((1 << 20), values[5]); // c5

        boardValues.put((1 << 16), values[6]); // c1
        boardValues.put((1 << 15), values[6]); // b8

        boardValues.put((1 << 17), values[7]); // c2
        boardValues.put((1 << 14), values[7]); // b7

        boardValues.put((1 << 18), values[8]); // c3
        boardValues.put((1 << 13), values[8]); // b6

        boardValues.put((1 << 19), values[9]); // c4
        boardValues.put((1 << 12), values[9]); // b5

        boardValues.put((1 << 25), values[10]); // d2
        boardValues.put((1 << 6), values[10]); // a7

        boardValues.put((1 << 26), values[11]); // d3
        boardValues.put((1 << 5), values[11]); // a6

        boardValues.put((1 << 27), values[12]); // d4
        boardValues.put((1 << 4), values[12]); // a5
    }
}
