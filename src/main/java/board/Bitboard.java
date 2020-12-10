package main.java.board;

import java.util.Arrays;

import main.java.util.BitboardUtils;

public class Bitboard {
    // bitboards[0] - p1 circles
    // bitboards[1] - p1 squares
    // bitboards[2] - p2 circles
    // bitboards[3] - p2 squares
    // bitboards[4] - anchor
    private int[] bitboards;

    /**
     * Initialize empty bitboard
     */
    public Bitboard() {
        bitboards = new int[5];
    }

    /**
     * Create a bitboard that copies the given state
     * 
     * @param toCopy Bitboard to copy
     */
    public Bitboard(Bitboard toCopy) {
        bitboards = new int[5];
        for (int i = 0; i < 5; i++) {
            bitboards[i] = toCopy.bitboards[i];
        }
    }

    /**
     * For debugging purposes. Create a bitboard that copies the given bitboards
     * 
     * @param toCopy
     */
    public Bitboard(int[] toCopy) {
        bitboards = new int[5];
        for (int i = 0; i < 5; i++) {
            bitboards[i] = toCopy[i];
        }
    }

    /**
     * Reset the bitboard back to an empty state
     */
    public void reset() {
        bitboards = new int[5];
    }

    /**
     * Set the given piece at the given location
     * 
     * @param idx Position [0, 31] to place piece
     * @param val Value of piece
     */
    public void setPiece(int idx, int val) {
        if (!isValid((1 << idx)))
            return;
        bitboards[val] |= (1 << idx);
    }

    /**
     * Used for testing purposes only. Sets the position of the anchor
     * 
     * @param idx Position [0, 31] to set anchor
     */
    public void setAnchor(int idx) {
        bitboards[4] = (1 << idx);
    }

    /**
     * Return bitboard containing the given player's pieces
     * 
     * @param turn Turn indicator
     * @return Bitboard of the player's pieces
     */
    public int getPieces(int turn) {
        return bitboards[turn * 2] | bitboards[turn * 2 + 1];
    }

    /**
     * Return bitboard containing all pieces
     * 
     * @return Bitboard of all pieces
     */
    public int getPieces() {
        return bitboards[0] | bitboards[1] | bitboards[2] | bitboards[3];
    }

    /**
     * Return bitboard containing all squares for a given player
     * 
     * @param turn Turn indicator
     * @return Bitboard of the given player's squares
     */
    public int getSquares(int turn) {
        return bitboards[turn * 2 + 1];
    }

    /**
     * Return bitboard containing all cirlces for a given player
     * 
     * @param turn Turn indicator
     * @return Bitboard of the given player's cirlces
     */
    public int getCircles(int turn) {
        return bitboards[turn * 2];
    }

    /**
     * Return turn indicator of the player whose turn it is
     * 
     * @return Turn indicator of next player
     */
    public int getTurn() {
        // if the anchor is not on p1's pieces, it's p1's turn
        if ((bitboards[4] & getPieces(0)) == 0)
            return 0;
        // else it's p2's turn
        return 1;
    }

    /**
     * Check whether a player owns the piece at a given position
     * 
     * @param posMask Bit mask for position to check
     * @param turn    Turn indicator
     * @return true if player owns the piece at the given position, else false
     */
    public boolean owns(int posMask, int turn) {
        return (posMask & (getPieces(turn))) != 0;
    }

    /**
     * Check whether piece at given position is a square
     * 
     * @param posMask Bit mask for position to check
     * @return true if piece at given position is a square, else false
     */
    public boolean isSquare(int posMask) {
        return (posMask & bitboards[1]) != 0 || (posMask & bitboards[3]) != 0;
    }

    /**
     * Check whether given position is a valid position
     * 
     * @param posMask Bit mask for position to check
     * @return true if given position is valid, else false
     */
    public boolean isValid(int posMask) {
        return (BitMasks.valid & posMask) != 0;
    }

    /**
     * Check if given position is empty
     * 
     * @param posMask Bit mask for position to check
     * @return true if given position contains no pieces, else false
     */
    public boolean isEmpty(int posMask) {
        return (posMask & (getPieces())) == 0;
    }

    /**
     * Check if given position is anchored
     * 
     * @param posMask Bit mask for position to check
     * @return true if given position is anchored, else false
     */
    public boolean isAnchored(int posMask) {
        return posMask == bitboards[4];
    }

    /**
     * Return the position of the anchor
     * 
     * @return The anchor position [0, 31] or -1 if not anchored
     */
    public int getAnchorPos() {
        if (bitboards[4] == 0 || (bitboards[4] & BitMasks.valid) == 0)
            return -1;
        return (int) (Math.log(bitboards[4]) / Math.log(2));
    }

    /**
     * Get the index of the bitboard corresponding to the piece at the given position
     * 
     * @param posMask Bit mask for position to check
     * @return Index of bitboard containing the piece, or -1 if none exists
     */
    private int getBitBoardIdx(int posMask) {
        for (int i = 0; i < 4; i++) {
            if ((bitboards[i] & posMask) != 0)
                return i;
        }
        return -1;
    }

    /**
     * Perform sliding action from oldIdx to newIdx by updating bitboards
     * 
     * @param oldPosMask Starting position of slide
     * @param newPosMask Ending position of slide
     */
    public void slide(int oldPosMask, int newPosMask) {
        int bIdx = getBitBoardIdx(oldPosMask);
        if (bIdx == -1) {
            System.out.println("ERROR! Asked to move empty/invalid idx "
                    + (int) (Math.log(oldPosMask) / Math.log(2)));
            return;
        }
        bitboards[bIdx] ^= oldPosMask;
        bitboards[bIdx] |= newPosMask;
    }

    /**
     * Perform a pushing action by updating bitboards
     * 
     * @param posMask Bit mask for position to begin push from
     * @param dir     Direction of push
     */
    public void push(int posMask, char dir) {
        // update anchor position
        bitboards[4] = BitboardUtils.updateMask(posMask, dir);

        int bIdx;
        int prevBIdx = -1;
        while (isValid(posMask)) {
            bIdx = getBitBoardIdx(posMask);
            // add previous piece, if valid
            if (prevBIdx != -1)
                bitboards[prevBIdx] |= posMask;
            // check if we're done with the push
            if (bIdx == -1)
                break;
            // remove current piece if it doesn't match previous
            if (bIdx != prevBIdx)
                bitboards[bIdx] ^= posMask;
            // iterate
            posMask = BitboardUtils.updateMask(posMask, dir);
            prevBIdx = bIdx;
        }
        return;
    }

    /**
     * Restore state from another Bitboard object
     * 
     * @param state Bitboard to set current state from
     */
    public void restoreState(Bitboard other) {
        for (int i = 0; i < 5; i++) {
            bitboards[i] = other.bitboards[i];
        }
    }

    /**
     * Get a copy of the current board state
     * 
     * @return Copy of current object
     */
    public Bitboard getState() {
        return new Bitboard(this);
    }

    /**
     * For debugging purposes. Print unique representation of board state to the console
     */
    public void repr() {
        for (int i = 0; i < 4; i++) {
            System.out.print(bitboards[i] + ", ");
        }
        System.out.println(bitboards[4]);
    }

    /**
     * Get string representation of piece at given position
     * 
     * @param idx Position to check
     * @return String representation of piece
     */
    public String getString(int idx) {
        int mask = 1 << idx;
        if (isAnchored(mask)) {
            if (owns(mask, 0))
                return "x ";
            else if (owns(mask, 1))
                return "X ";
        }
        if (!isValid(mask))
            return "  ";
        if (isEmpty(mask))
            return ". ";
        if (owns(mask, 0)) {
            if (isSquare(mask))
                return "s ";
            return "c ";
        } else if (owns(mask, 1)) {
            if (isSquare(mask))
                return "S ";
            return "C ";
        }
        return "? ";
    }

    /**
     * Print the labels for each column
     */
    public void printColumnLabels() {
        System.out.print("  ");
        for (int col = 0; col < 8; col++) {
            System.out.print(col + 1 + " ");
        }
        System.out.println();
    }

    /**
     * Print board state to console
     */
    public void show() {
        printColumnLabels();
        System.out.println("      ---------");
        for (int row = 0; row < 4; row++) {
            System.out.print((char) ('a' + row) + " ");
            for (int col = 0; col < 8; col++) {
                System.out.print(getString(row * 8 + col));
            }
            System.out.println((char) ('a' + row));
        }
        System.out.println("    ---------");
        printColumnLabels();
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

        Bitboard other = (Bitboard) obj;
        for (int i = 0; i < this.bitboards.length; i++) {
            if (this.bitboards[i] != other.bitboards[i])
                return false;
        }

        return true;
    }
}
