package main.java.agents.mcts;

/**
 * Agent using Monte-Carlo Tree Search with the First Play Urgency enhancement.
 */
public class FPUAgent extends VanillaMCTSAgent {
    private double FPU_CONSTANT = 0.8; // FPU constant

    /**
     * Initialize Monte-Carlo Tree Search agent with given iteration limit
     * 
     * @param iterations Max number of iterations allowed per move
     */
    public FPUAgent(long iterations) {
        super(iterations);
    }

    /**
     * Initialize Monte-Carlo Tree Search agent with given iteration limit and FPU constant
     * 
     * @param iterations   Max number of iterations allowed per move
     * @param FPU_CONSTANT FPU constant
     */
    public FPUAgent(long iterations, double FPU_CONSTANT) {
        super(iterations);
        this.FPU_CONSTANT = FPU_CONSTANT;
    }

    /**
     * Initialize Monte-Carlo Tree Search agent
     */
    public FPUAgent() {
        super();
    }

    @Override
    protected Node traverse(Tree tree) {
        Node node = tree.root;
        Node nextNode;

        // follow UCT until you find a non-fully-expanded node
        while (node.isFullyExpanded && !node.isTerminal) {
            nextNode = bestUCT(node, tree);
            nextNode.chosenParent = node;
            node = nextNode;
            turn = 1 - turn;
        }
        if (node.isTerminal) {
            return node;
        }

        // run UCT again (will pick unexplored node if FPU_CONSTANT is high enough)
        nextNode = bestUCT(node, tree);
        nextNode.chosenParent = node;
        return nextNode;
    }

    /**
     * Get the next child from the given node based on UCT
     * 
     * @param node Node to perform UCT on
     * @param tree Tree to get Nodes from
     * @return Next node to explore
     */
    private Node bestUCT(Node node, Tree tree) {
        Node bestNode = null;
        double bestUCB, ucb, avgReward;
        int totalPlays;
        double totalReward;
        Stats stats;
        boolean newBest = true;
        if (turn == 0) {
            bestUCB = -Double.MAX_VALUE;
            if (node.unexplored.size() > 0) {
                bestUCB = FPU_CONSTANT + Math.pow(2 * Math.log(node.totalVisits), 0.5);
                bestNode = tree.getNode(node.unexplored.get(0));
            }
            for (Node child : node.children) {
                totalReward = 0;
                totalPlays = 0;
                for (Node parent : child.parents) {
                    stats = parent.childToStats.get(child);
                    totalReward += stats.totalReward;
                    totalPlays += stats.numPlays;
                }
                avgReward = totalReward / totalPlays;
                stats = node.childToStats.get(child);
                ucb = avgReward + Math.pow(2 * Math.log(node.totalVisits) / stats.numPlays, 0.5);

                if (ucb > bestUCB) {
                    newBest = false;
                    bestUCB = ucb;
                    bestNode = child;
                }
            }
        } else {
            bestUCB = Double.MAX_VALUE;
            if (node.unexplored.size() > 0) {
                bestUCB = -FPU_CONSTANT - Math.pow(2 * Math.log(node.totalVisits), 0.5);
                bestNode = tree.getNode(node.unexplored.get(0));
            }
            for (Node child : node.children) {
                totalReward = 0;
                totalPlays = 0;
                for (Node parent : child.parents) {
                    stats = parent.childToStats.get(child);
                    totalReward += stats.totalReward;
                    totalPlays += stats.numPlays;
                }
                avgReward = totalReward / totalPlays;
                stats = node.childToStats.get(child);
                ucb = avgReward - Math.pow(2 * Math.log(node.totalVisits) / stats.numPlays, 0.5);

                if (ucb < bestUCB) {
                    newBest = false;
                    bestUCB = ucb;
                    bestNode = child;
                }
            }
        }
        // if UCT chose an unexplored node
        if (newBest) {
            node.unexplored.remove(0);
            node.isFullyExpanded = node.unexplored.size() == 0;
            node.children.add(bestNode);
            node.childToStats.put(bestNode, new Stats());
            bestNode.parents.add(node);
        }
        return bestNode;
    }

    @Override
    public String toString() {
        return FPU_CONSTANT + " First Play Urgency MCTS Agent";
    }
}
