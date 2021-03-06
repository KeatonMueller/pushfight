package main.java.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.java.agents.Agent;
import main.java.board.Bitboard;
import main.java.util.BitboardUtils;

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int FRAME_X = 400, FRAME_Y = 100;
    private static final int FRAME_WIDTH = 1000, FRAME_HEIGHT = 700;
    private static final String FRAME_TITLE = "Push Fight";

    private Bitboard board;
    private Canvas canvas;
    private Agent p1, p2;

    public GUI() {
        super();
        board = new Bitboard();
        BitboardUtils.skipSetup(board);
        getPlayers();
        canvas = new Canvas(FRAME_WIDTH, FRAME_HEIGHT, board, p1, p2);
        add(canvas);
        setTitle(FRAME_TITLE);
        setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // listen for frame resizes
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                canvas.newSize(getWidth(), getHeight());
            }
        });
    }

    /**
     * Start the game loop
     */
    public void startGame() {
        canvas.awaitNextMove();
    }

    /**
     * Display dialog for selecting players
     */
    private void getPlayers() {
        PlayerSelect selector = new PlayerSelect();
        JOptionPane.showMessageDialog(null, selector, "Select Players", JOptionPane.PLAIN_MESSAGE);
        Agent[] players = selector.getPlayers();
        p1 = players[0];
        p2 = players[1];
    }
}
