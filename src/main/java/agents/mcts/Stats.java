package main.java.agents.mcts;

/**
 * Stats object to track statistics for EDGES in the game tree
 */
public class Stats {
    protected int numPlays; // number of times this action/edge has been played
    protected int totalReward; // aggregate reward seen along edge

    /**
     * Initialize Stats object with zero values
     */
    public Stats() {
        this.numPlays = 0;
        this.totalReward = 0;
    }
}
