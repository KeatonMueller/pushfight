package main.java.util;

public class NumberUtils {
    /**
     * Round the given number to the given number of decimal places
     * 
     * @param number    Number to be rounded
     * @param precision Number of decimal places to round to
     * @return Number rounded to given precision
     */
    public static double round(double number, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(number * factor) / factor;
    }
}
