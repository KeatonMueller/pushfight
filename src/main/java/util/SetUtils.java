package main.java.util;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class SetUtils {
    private static Random rand = new Random();

    /**
     * Return a random element from the given set
     * 
     * @param set Set to get a random element from
     * @return A random element of the set
     */
    public static Object randomChoice(Set<?> set) {
        int choice = rand.nextInt(set.size());
        int idx = 0;
        Iterator<?> it = set.iterator();
        while (choice != idx) {
            it.next();
            idx++;
        }
        return it.next();
    }
}
