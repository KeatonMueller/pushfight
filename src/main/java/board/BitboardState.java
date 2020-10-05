package main.java.board;

import java.util.Arrays;

/**
 * Object to store Bitboard state to allow for use in HashSets and HashMaps
 */
public class BitboardState {
    protected int[] bitboards;

    public BitboardState(int[] boards) {
        bitboards = new int[5];
        for (int i = 0; i < 5; i++) {
            bitboards[i] = boards[i];
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bitboards);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        BitboardState other = (BitboardState) obj;
        for (int i = 0; i < this.bitboards.length; i++) {
            if (this.bitboards[i] != other.bitboards[i])
                return false;
        }

        return true;
    }
}
