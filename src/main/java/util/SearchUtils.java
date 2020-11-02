package main.java.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.java.board.Bitboard;
import main.java.board.BitMasks;

public class SearchUtils {
    /**
     * Explore empty spaces in the map and determine "ownership"
     * 
     * @param board   Board to explore
     * @param posMask Position of initial empty space
     * @param ccId    ID of current connected component
     * @return Bit mask of the explored connected component
     */
    public static int checkSpaces(Bitboard board, int posMask, int ccId,
            Map<Integer, Set<Integer>> posToAdjCCID, Map<Integer, Integer> ccIDToOwner,
            Map<Integer, Integer> ownerToCCs) {
        // perform basic BFS to explore the connected component
        // (this isn't a real queue... but we're not doing shortest path so its fine)
        int queue = posMask;
        int p1Adj = 0, p2Adj = 0;
        int cc = 0;
        int myVisit = 0;
        Set<Integer> CCIDs;
        while (queue != 0) {
            posMask = queue & ~(queue - 1);
            queue ^= posMask;

            if ((myVisit & posMask) != 0)
                continue;

            myVisit |= posMask;

            if (!board.isValid(posMask))
                continue;

            if (!board.isEmpty(posMask)) {
                // update this piece's list of adjacent connected components
                CCIDs = posToAdjCCID.getOrDefault(posMask, new HashSet<Integer>());
                CCIDs.add(ccId);
                if (!posToAdjCCID.containsKey(posMask))
                    posToAdjCCID.put(posMask, CCIDs);

                if (board.owns(posMask, 0))
                    p1Adj++;
                else
                    p2Adj++;

                continue;
            }
            cc |= posMask;
            queue |= BitMasks.orthogonal.get(posMask);
        }

        // update maps
        if (p1Adj > p2Adj) {
            ownerToCCs.put(0, ownerToCCs.getOrDefault(0, 0) | cc);
            ccIDToOwner.put(ccId, 0);
        } else if (p2Adj > p1Adj) {
            ownerToCCs.put(1, ownerToCCs.getOrDefault(1, 0) | cc);
            ccIDToOwner.put(ccId, 1);
        } else {
            ccIDToOwner.put(ccId, -1);
        }

        return cc;
    }
}
