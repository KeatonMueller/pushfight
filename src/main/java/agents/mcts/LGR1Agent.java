package main.java.agents.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.agents.RandomAgent;
import main.java.board.Bitboard;
import main.java.board.Move;
import main.java.util.BitboardUtils;

/**
 * Agent using Monte-Carlo Tree Search along with the Last Good Reply enhancement to the default
 * policy.
 */
public class LGR1Agent extends VanillaMCTSAgent {
    /**
     * List of each player's best replies. replies.get(0) is a map from [p2's move] to [p1's best
     * reply]. replies.get(1) is a map from [p1's move] to [p2's best reply]
     */
    private List<Map<Move, Move>> replies;

    /**
     * Initialize Monte-Carlo Tree Search agent using Last Good Reply with given iteration limit
     * 
     * @param iterations Max number of iterations allowed per move
     */
    public LGR1Agent(long iterations) {
        super(iterations);
        replies = new ArrayList<>();
        replies.add(new HashMap<>());
        replies.add(new HashMap<>());
    }

    /**
     * Initialize Monte-Carlo Tree Search agent using Last Good Reply
     */
    public LGR1Agent() {
        super();
        replies = new ArrayList<>();
        replies.add(new HashMap<>());
        replies.add(new HashMap<>());
    }

    @Override
    protected double playout(Node node) {
        List<List<Move>> moves = new ArrayList<>();
        moves.add(new ArrayList<>());
        moves.add(new ArrayList<>());

        boardToNum.clear();
        Bitboard board = new Bitboard(node.state.board);
        int winner, count;
        List<Move> path = new ArrayList<>();
        Move lastMove = null;
        while (true) {
            winner = BitboardUtils.checkWinner(board);
            if (winner != -1) {
                processPath(path, winner);
                if (winner == 0)
                    return 1;
                return -1;
            }

            // add tie logic in rare case of long loop
            count = boardToNum.getOrDefault(board, 0);
            boardToNum.put(board, count + 1);
            if (count >= 5) {
                return 0;
            }

            if (lastMove == null || !replies.get(turn).containsKey(lastMove)) {
                // if there's no recorded last reply, do a random move
                lastMove = RandomAgent.getRandomMove(board, turn, rand);
            } else {
                // get the recorded last reply
                lastMove = replies.get(turn).get(lastMove);
                // attempt to perform it
                if (!lastMove.attempt(board, turn)) {
                    // do a random move if the last good reply isn't valid for current board state
                    lastMove = RandomAgent.getRandomMove(board, turn, rand);
                }
            }
            path.add(lastMove);
            turn = 1 - turn;
        }
    }

    private void processPath(List<Move> path, int winner) {
        if (path.size() == 0)
            return;

        // flip turn indicator so it matches up with the last element in the path
        turn = 1 - turn;

        // this should only happen if someone made a suicidal move
        if (winner != turn) {
            turn = 1 - turn;
            path.remove(path.size() - 1);
        }

        Move move, reply;
        for (int i = path.size() - 1; i > 0; i -= 2) {
            move = path.get(i - 1);
            reply = path.get(i);
            replies.get(winner).put(move, reply);
        }
    }

    @Override
    public String toString() {
        return "Last Good Reply MCTS Agent";
    }
}
