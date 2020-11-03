package main.java.board;

import java.util.LinkedList;

/**
 * Class to uniquely represent a move in Push Fight to facilitate MAST
 */
public class Move {
    private LinkedList<Integer> actions;
    public double reward;

    public Move() {
        actions = new LinkedList<>();
    }

    public Move(Move m) {
        actions = new LinkedList<>(m.actions);
    }

    public void add(Integer action) {
        actions.push(action);
    }

    public void pop() {
        actions.pop();
    }

    @Override
    public int hashCode() {
        return actions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Move other = (Move) obj;
        return this.actions.equals(other.actions);
    }
}
