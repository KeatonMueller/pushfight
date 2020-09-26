package main.java.gui;

import java.awt.geom.Path2D;

public class Arrow {
    /**
     * Get an arrow pointing in a certain direction
     * 
     * @param length Length of total drawing area
     * @param width  Thickness of arrow
     * @param dir    Direction of arrow (r|l|u|d)
     */
    public static Path2D.Float getArrow(float length, float width, char dir) {
        switch (dir) {
            case 'r':
                return new ArrowRight(length, width);
            case 'l':
                return new ArrowLeft(length, width);
            case 'u':
                return new ArrowUp(length, width);
            case 'd':
                return new ArrowDown(length, width);
            default:
                return null;
        }
    }
}


final class ArrowRight extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct an arrow pointing right
     * 
     * @param length Length of total drawing area
     * @param width  Thickness of arrow
     */
    public ArrowRight(float length, float width) {
        moveTo(0, 0);
        lineTo(width, 0);
        lineTo(length / 2, length / 2);
        lineTo(width, length);
        lineTo(0, length);
        lineTo(length / 2 - width, length / 2);
        closePath();
    }
}


final class ArrowLeft extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct an arrow pointing left
     * 
     * @param length Length of total drawing area
     * @param width  Thickness of arrow
     */
    public ArrowLeft(float length, float width) {
        moveTo(length, 0);
        lineTo(length - width, 0);
        lineTo(length / 2, length / 2);
        lineTo(length - width, length);
        lineTo(length, length);
        lineTo(length / 2 + width, length / 2);
        closePath();
    }
}


final class ArrowUp extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct an arrow pointing up
     * 
     * @param length Length of total drawing area
     * @param width  Thickness of arrow
     */
    public ArrowUp(float length, float width) {
        moveTo(0, length);
        lineTo(0, length - width);
        lineTo(length / 2, length / 2);
        lineTo(length, length - width);
        lineTo(length, length);
        lineTo(length / 2, length / 2 + width);
        closePath();
    }
}


final class ArrowDown extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct an arrow pointing down
     * 
     * @param length Length of total drawing area
     * @param width  Thickness of arrow
     */
    public ArrowDown(float length, float width) {
        moveTo(0, 0);
        lineTo(0, width);
        lineTo(length / 2, length / 2);
        lineTo(length, width);
        lineTo(length, 0);
        lineTo(length / 2, length / 2 - width);
        closePath();
    }
}
