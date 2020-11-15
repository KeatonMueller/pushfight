package main.java.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class to group potential next states into different categories
 */
public class StateSet implements Iterable<State> {
    /**
     * States that contain winning moves
     */
    public Set<State> winningStates;
    /**
     * States that put your opponent into "checkmate"
     */
    public Set<State> checkmateStates;
    /**
     * States that leave none of your own pieces on the border
     */
    public Set<State> noBorderStates;
    /**
     * All other states
     */
    public Set<State> otherStates;

    public StateSet() {
        this.winningStates = new HashSet<>();
        this.checkmateStates = new HashSet<>();
        this.noBorderStates = new HashSet<>();
        this.otherStates = new HashSet<>();
    }

    @Override
    public Iterator<State> iterator() {
        return new StateSetIterator(this);
    }

    /**
     * Custom iterator class to easily iterate over a StateSet in order of best moves to worst,
     * according to the groups
     */
    private class StateSetIterator implements Iterator<State> {
        /**
         * Private list of iterators for each group
         */
        private List<Iterator<State>> its = new ArrayList<>();

        public StateSetIterator(StateSet stateSet) {
            // add each group's iterator to iterator list, in order of value
            its.add(winningStates.iterator());
            its.add(checkmateStates.iterator());
            its.add(noBorderStates.iterator());
            its.add(otherStates.iterator());
        }

        @Override
        public boolean hasNext() {
            // return true if any of the iterators' hasNext() is true
            while (its.size() > 0) {
                if (its.get(0).hasNext())
                    return true;
                // discard any empty iterators
                its.remove(0);
            }
            return false;
        }

        @Override
        public State next() {
            // return the next iterator value
            if (!hasNext())
                return null;
            return its.get(0).next();
        }
    }
}
