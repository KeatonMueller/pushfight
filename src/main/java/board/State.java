package main.java.board;

/**
 * A State class is simply a grouping of a Bitboard and a Move. The Move should signify it was the
 * move taken to reach the associated Bitboard.
 */
public class State {
    public Bitboard board;
    public Move move;

    public State(Bitboard board, Move move) {
        this.board = board;
        this.move = new Move(move);
    }

    public State(Bitboard board) {
        this.board = board;
        this.move = null;
    }

    @Override
    public int hashCode() {
        return board.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        State other = (State) obj;
        return this.board.equals(other.board);
    }
}
