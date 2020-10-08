package main.java.board.heuristic;

import java.util.Map;

public class HeuristicUtils {
    /**
     * Surface the total number of values needed for the heuristic
     */
    public static int numValues = 33;

    /**
     * Initialize mapping from board position to values with default values
     * 
     * @param boardValues Map from board position to position value
     */
    public static void initBoardValues(Map<Integer, Double> boardValues) {
        boardValues.put((1 << 2), -10.0); // a3
        boardValues.put((1 << 3), 3.0); // a4
        boardValues.put((1 << 4), 5.0); // a5
        boardValues.put((1 << 5), 3.0); // a6
        boardValues.put((1 << 6), -10.0); // a7
        boardValues.put((1 << 8), -10.0); // b1
        boardValues.put((1 << 9), -10.0); // b2
        boardValues.put((1 << 10), 1.0); // b3
        boardValues.put((1 << 11), 10.0); // b4
        boardValues.put((1 << 12), 10.0); // b5
        boardValues.put((1 << 13), 7.0); // b6
        boardValues.put((1 << 14), 0.0); // b7
        boardValues.put((1 << 15), -10.0); // b8
        boardValues.put((1 << 16), -10.0); // c1
        boardValues.put((1 << 17), 0.0); // c2
        boardValues.put((1 << 18), 7.0); // c3
        boardValues.put((1 << 19), 10.0); // c4
        boardValues.put((1 << 20), 10.0); // c5
        boardValues.put((1 << 21), 1.0); // c6
        boardValues.put((1 << 22), -10.0); // c7
        boardValues.put((1 << 23), -10.0); // c8
        boardValues.put((1 << 25), -10.0); // d2
        boardValues.put((1 << 26), 3.0); // d3
        boardValues.put((1 << 27), 5.0); // d4
        boardValues.put((1 << 28), 3.0); // d5
        boardValues.put((1 << 29), -10.0); // d6
    }

    /**
     * Initialize mapping from board position to values with given values
     * 
     * @param boardValues Map from board position to position value
     * @param values      Array of board values
     */
    public static void initBoardValues(Map<Integer, Double> boardValues, double[] values) {
        boardValues.put((1 << 2), values[0]); // a3
        boardValues.put((1 << 3), values[1]); // a4
        boardValues.put((1 << 4), values[2]); // a5
        boardValues.put((1 << 5), values[3]); // a6
        boardValues.put((1 << 6), values[4]); // a7
        boardValues.put((1 << 8), values[5]); // b1
        boardValues.put((1 << 9), values[6]); // b2
        boardValues.put((1 << 10), values[7]); // b3
        boardValues.put((1 << 11), values[8]); // b4
        boardValues.put((1 << 12), values[9]); // b5
        boardValues.put((1 << 13), values[10]); // b6
        boardValues.put((1 << 14), values[11]); // b7
        boardValues.put((1 << 15), values[12]); // b8
        boardValues.put((1 << 16), values[13]); // c1
        boardValues.put((1 << 17), values[14]); // c2
        boardValues.put((1 << 18), values[15]); // c3
        boardValues.put((1 << 19), values[16]); // c4
        boardValues.put((1 << 20), values[17]); // c5
        boardValues.put((1 << 21), values[18]); // c6
        boardValues.put((1 << 22), values[19]); // c7
        boardValues.put((1 << 23), values[20]); // c8
        boardValues.put((1 << 25), values[21]); // d2
        boardValues.put((1 << 26), values[22]); // d3
        boardValues.put((1 << 27), values[23]); // d4
        boardValues.put((1 << 28), values[24]); // d5
        boardValues.put((1 << 29), values[25]); // d6
    }
}
