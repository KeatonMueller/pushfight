package main.java.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.agents.Agent;
import main.java.agents.AgentUtils;
import main.java.board.Board;
import main.java.board.BoardUtils;
import main.java.game.GameUtils;

class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int PADDING = 64;
    private static final int DEFAULT_DIVIDER_LENGTH = 5;

    private static Color boardColor = new Color(245, 184, 0);
    private static Color dividerColor = new Color(184, 138, 0);
    private static Color wallColor = new Color(102, 51, 0);

    private Set<Integer> slideDests;
    private Set<Integer> pushable;
    private Path2D.Float topLeft, topRight, botRight, botLeft;
    private Path2D.Float arrowRight, arrowLeft, arrowUp, arrowDown;
    private int sideLength, dividerLength, drawLength, wallLength, pieceSize;
    private int topXPadding, topYPadding, botXPadding, botYPadding;
    private int midXPadding, secondYPadding, thirdYPadding;
    private int arc;

    private Board board;
    private Agent p1, p2;
    private int frameWidth, frameHeight;
    private int turn;
    private int selected, slidesRemaining;
    private int winner;

    public Canvas(int fw, int fh, Board b, Agent a1, Agent a2) {
        super();
        frameWidth = fw;
        frameHeight = fh;
        board = b;
        p1 = a1;
        p2 = a2;
        winner = -1;
        turn = 0;
        selected = -1;
        slidesRemaining = 2;
        slideDests = new HashSet<>();
        pushable = new HashSet<>();

        calculateLengths();
        // listen for clicks now that board is loaded
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (winner != -1)
                    return;

                int x = e.getX();
                int y = e.getY();

                if (y - topYPadding < 0 || x - midXPadding < 0) {
                    quitClick();
                    return;
                }

                int row = (y - topYPadding) / drawLength;
                int col = (x - midXPadding) / drawLength;

                if (!board.isValid(row, col)) {
                    quitClick();
                    return;
                }

                if (pushable.contains(row * 10 + col)) {
                    int initRow = selected / 10;
                    int initCol = selected % 10;
                    char dir = GameUtils.posChangeToDir(initRow, initCol, row, col);
                    int[] pushResult = board.push(selected / 10, selected % 10, dir);
                    slidesRemaining = 2;
                    quitClick();
                    if (pushResult[5] != -1) {
                        winner = 1 - pushResult[5];
                        winner();
                    }
                    turn = 1 - turn;
                    awaitNextMove();
                } else if (board.owns(row, col, turn)) {
                    slideDests.clear();
                    if (slidesRemaining > 0)
                        slideDests.addAll(GameUtils.findSlideDests(board, row, col));
                    pushable.clear();
                    pushable.addAll(GameUtils.findPushablePieces(board, row, col));
                    selected = row * 10 + col;
                } else if (slideDests.contains(row * 10 + col)) {
                    board.slide(selected / 10, selected % 10, row, col);
                    slidesRemaining--;
                    quitClick();
                } else {
                    quitClick();
                    return;
                }
                repaint();
            }
        });
    }

    /**
     * Clean up various data when exiting mouseListener
     */
    private void quitClick() {
        slideDests.clear();
        pushable.clear();
        selected = -1;
        repaint();
    }

    /**
     * Display winning message. In the future, implement play again functionality
     */
    private void winner() {
        if (winner != -1) {

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "Player " + (winner + 1) + " won!", null,
                            JOptionPane.PLAIN_MESSAGE);
                    int again = JOptionPane.showConfirmDialog(null, "Play Again?");
                    if (again == JOptionPane.YES_OPTION) {
                        winner = -1;
                        board.reset();
                        BoardUtils.skipSetup(board);
                        turn = 0;
                        repaint();
                        awaitNextMove();
                    }
                }
            });
        }
    }

    /**
     * Wait for drawing to finish, and then call for next move
     */
    public void awaitNextMove() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                nextMove();
            }
        });
    }

    /**
     * Get next move from agent, or do nothing if human to move
     */
    private void nextMove() {
        if (winner != -1)
            return;
        // if next player is a human, do nothing
        if ((turn == 0 && p1 == null) || (turn == 1 && p2 == null))
            return;
        // otherwise get the move from the appropriate agent
        int loser;
        if (turn == 0) {
            loser = AgentUtils.agentMove(p1, board, turn);
        } else {
            loser = AgentUtils.agentMove(p2, board, turn);
        }
        // change turn, repaint, and get next turn
        turn = 1 - turn;
        if (loser != -1) {
            winner = 1 - loser;
            winner();
        }
        repaint();
        awaitNextMove();
    }

    /**
     * Handle window resize
     *
     * @param width  New window width
     * @param height New window height
     */
    public void newSize(int width, int height) {
        frameWidth = width;
        frameHeight = height;
        calculateLengths();
    }

    /**
     * Calculate pixel lengths based on window size
     */
    private void calculateLengths() {
        sideLength = (int) (Math.min(
                (frameWidth - (2 * PADDING) - (7 * DEFAULT_DIVIDER_LENGTH)) / GameUtils.LENGTH,
                (frameHeight - (2 * PADDING) - (3 * DEFAULT_DIVIDER_LENGTH))
                        / (GameUtils.HEIGHT + (2.0 / 3.0))));

        dividerLength = (int) Math.min(5, sideLength * .1);
        drawLength = sideLength + dividerLength;
        wallLength = (int) sideLength / 3;

        topXPadding = PADDING + 2 * drawLength;
        topYPadding = PADDING + wallLength;
        botXPadding = PADDING + drawLength;
        botYPadding = PADDING + wallLength + 3 * drawLength;

        midXPadding = PADDING;
        secondYPadding = PADDING + wallLength + drawLength;
        thirdYPadding = PADDING + wallLength + 2 * drawLength;

        arc = (int) (sideLength * .4);

        topLeft = RoundedCorner.getRoundedCorner(sideLength, arc, 0);
        topRight = RoundedCorner.getRoundedCorner(sideLength, arc, 1);
        botRight = RoundedCorner.getRoundedCorner(sideLength, arc, 2);
        botLeft = RoundedCorner.getRoundedCorner(sideLength, arc, 3);

        pieceSize = sideLength - 2 * dividerLength;

        arrowRight = Arrow.getArrow(pieceSize * 0.5f, dividerLength, 'r');
        arrowLeft = Arrow.getArrow(pieceSize * 0.5f, dividerLength, 'l');
        arrowUp = Arrow.getArrow(pieceSize * 0.5f, dividerLength, 'u');
        arrowDown = Arrow.getArrow(pieceSize * 0.5f, dividerLength, 'd');

        repaint();
    }

    /**
     * Draw the board with no pieces or highlighting
     *
     * @param g Graphics object
     */
    private void drawBoard(Graphics g) {
        if (board == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int tile;

        // draw the top and bottom rows of squares
        g2d.setColor(boardColor);
        for (tile = 0; tile < 5; tile++) {
            g.fillRect((tile * drawLength) + topXPadding, topYPadding, sideLength, sideLength);
            g.fillRect((tile * drawLength) + botXPadding, botYPadding, sideLength, sideLength);
        }
        // draw middle six squares of the two middle rows
        for (tile = 1; tile < 7; tile++) {
            g.fillRect((tile * drawLength) + midXPadding, secondYPadding, sideLength, sideLength);
            g.fillRect((tile * drawLength) + midXPadding, thirdYPadding, sideLength, sideLength);
        }

        // draw the ends of the two middle rows with rounded corners
        AffineTransform origin = g2d.getTransform();
        g2d.translate(midXPadding, secondYPadding);
        g2d.fill(topLeft);
        g2d.translate(0, drawLength);
        g2d.fill(botLeft);
        g2d.translate(drawLength * 7, 0);
        g2d.fill(botRight);
        g2d.translate(0, -drawLength);
        g2d.fill(topRight);
        g2d.setTransform(origin);

        // draw the dividers
        g2d.setColor(dividerColor);
        // horizontal dividers
        g2d.fillRect(topXPadding, secondYPadding - dividerLength, drawLength * 5 - dividerLength,
                dividerLength);
        g2d.fillRect(midXPadding, thirdYPadding - dividerLength, drawLength * 8 - dividerLength,
                dividerLength);
        g2d.fillRect(botXPadding, botYPadding - dividerLength, drawLength * 5 - dividerLength,
                dividerLength);
        for (tile = 0; tile < 2; tile++) {
            // outer two vertical dividers
            g2d.fillRect(midXPadding + sideLength + (drawLength * 6 * tile), secondYPadding,
                    dividerLength, drawLength * 2 - dividerLength);
            // second from outer two vertical dividers
            g2d.fillRect(topXPadding - dividerLength + (drawLength * 4 * tile),
                    secondYPadding - (drawLength * tile), dividerLength,
                    drawLength * 3 - dividerLength);
        }
        // middle three vertical dividers
        for (tile = 0; tile < 3; tile++) {
            g2d.fillRect(topXPadding + sideLength + (drawLength * tile), topYPadding, dividerLength,
                    drawLength * 4 - dividerLength);
        }

        // draw the walls
        g2d.setColor(wallColor);
        g2d.fillRect(topXPadding, topYPadding - wallLength, drawLength * 5 - dividerLength,
                wallLength);
        g2d.fillRect(botXPadding, botYPadding + sideLength, drawLength * 5 - dividerLength,
                wallLength);

    }

    /**
     * Draw the pieces on the board
     *
     * @param g Graphics object
     */
    private void drawPieces(Graphics g) {
        if (board == null)
            return;

        Graphics2D g2d = (Graphics2D) g;

        // draw each player's pieces
        int row, col;
        for (int turn = 0; turn < 2; turn++) {
            for (int pos : board.getPieceLocs(turn)) {
                row = pos / 10;
                col = pos % 10;
                drawPiece(g2d, row, col, turn);
            }
        }

        // draw the anchor
        int anchorPos = board.getAnchorPos();
        if (anchorPos != 0) {
            g2d.setColor(Color.RED);
            drawExtra(g2d, anchorPos);
        }

        // highlight movable squares
        g2d.setColor(new Color(0, 0, 0, 75));
        for (int pos : slideDests) {
            drawExtra(g2d, pos);
        }

        // highlight pushable squares
        g2d.setColor(new Color(255, 0, 0, 125));
        for (int pos : pushable) {
            drawArrow(g2d, pos);
        }
    }

    /**
     * Draw an individual piece on the board
     * 
     * @param g2d  Graphics2D object
     * @param row  Row position of the piece
     * @param col  Column position of the piece
     * @param turn Turn indicator
     */
    private void drawPiece(Graphics2D g2d, int row, int col, int turn) {
        if (turn == 0)
            g2d.setColor(Color.WHITE);
        else
            g2d.setColor(Color.BLACK);

        int x = midXPadding + drawLength * col + dividerLength;
        int y = topYPadding + drawLength * row + dividerLength;

        if (board.isSquare(row, col)) {
            g2d.fillRoundRect(x, y, pieceSize, pieceSize, arc, arc);
        } else {
            g2d.fillOval(x, y, pieceSize, pieceSize);
        }
    }

    /**
     * Draw an "extra" shape (e.g. anchor or highlighted square) on the board. Assume the proper
     * color has already been set
     * 
     * @param g2d Graphics2D object
     * @param pos Position at which to draw
     */
    private void drawExtra(Graphics2D g2d, int pos) {
        int row = pos / 10;
        int col = pos % 10;
        int x = (int) (midXPadding + drawLength * col + dividerLength + (pieceSize * .25));
        int y = (int) (topYPadding + drawLength * row + dividerLength + (pieceSize * .25));
        int extraSize = (int) (pieceSize * 0.5);
        g2d.fillOval(x, y, extraSize, extraSize);
    }

    /**
     * Draw an arrow indicating a potential push action. Assume the proper color has already been
     * set
     * 
     * @param g2d Graphics2D object
     * @param pos Position at which to draw
     */
    private void drawArrow(Graphics2D g2d, int pos) {
        int row = pos / 10;
        int col = pos % 10;
        int x = (int) (midXPadding + drawLength * col + dividerLength + (pieceSize * .25));
        int y = (int) (topYPadding + drawLength * row + dividerLength + (pieceSize * .25));

        AffineTransform origin = g2d.getTransform();
        g2d.translate(x, y);
        char dir = GameUtils.posChangeToDir(selected / 10, selected % 10, pos / 10, pos % 10);
        switch (dir) {
            case 'r':
                g2d.fill(arrowRight);
                break;
            case 'l':
                g2d.fill(arrowLeft);
                break;
            case 'u':
                g2d.fill(arrowUp);
                break;
            case 'd':
                g2d.fill(arrowDown);
                break;
        }
        g2d.setTransform(origin);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }
}
