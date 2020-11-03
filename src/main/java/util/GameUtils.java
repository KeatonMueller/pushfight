package main.java.util;

public class GameUtils {
    public static final int LENGTH = 8;
    public static final int HEIGHT = 4;
    public static final int NUM_SLIDES = 2;
    public static final char[] DIRECTIONS = {'r', 'l', 'u', 'd'};

    /**
     * Converts a change in position into a direction of that change. The two given positions must
     * be orthogonally connected
     * 
     * @param oldRow Initial row
     * @param oldCol Initial column
     * @param newRow Final row
     * @param newCol Final column
     * @return A char (r|l|u|d) corresponding to the change in direction
     */
    public static char posChangeToDir(int oldRow, int oldCol, int newRow, int newCol) {
        if (oldRow - newRow == 1)
            return 'u';
        else if (oldRow - newRow == -1)
            return 'd';
        else if (oldCol - newCol == 1)
            return 'l';
        else if (oldCol - newCol == -1)
            return 'r';
        return ' ';
    }
}
