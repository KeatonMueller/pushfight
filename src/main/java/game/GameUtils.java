package main.java.game;

public class GameUtils {
    public static final int LENGTH = 8;
    public static final int HEIGHT = 4;
    public static final int NUM_SLIDES = 2;

    /**
     * Convert direction int (0|1|2|3) to char (r|l|u|d)
     */
    public static char[] dirIntToChar = new char[] {'r', 'l', 'u', 'd'};

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
