package main.java.gui;

import java.awt.geom.Path2D;

public class RoundedCorner {
    /**
     * Get a square with a single rounded corner
     * 
     * @param length Side length of square
     * @param radius Radius of curved corner
     * @param corner Corner indicator. Starting from top-left and going clockwise, it's 0|1|2|3
     */
    public static Path2D.Float getRoundedCorner(float length, float radius, int corner) {
        switch (corner) {
            case 0:
                return new RoundedCornerTopLeft(length, radius);
            case 1:
                return new RoundedCornerTopRight(length, radius);
            case 2:
                return new RoundedCornerBottomRight(length, radius);
            case 3:
                return new RoundedCornerBottomLeft(length, radius);
            default:
                return null;
        }
    }
}


final class RoundedCornerTopLeft extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a square with a rounded top left corner
     * 
     * @param length Side length of square
     * @param radius Radius of curved corner
     */
    public RoundedCornerTopLeft(float length, float radius) {
        moveTo(length, 0);
        lineTo(radius, 0);
        curveTo(0, 0, 0, 0, 0, radius);
        lineTo(0, length);
        lineTo(length, length);
        closePath();
    }
}


final class RoundedCornerTopRight extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a square with a rounded top right corner
     * 
     * @param length Side length of square
     * @param radius Radius of curved corner
     */
    public RoundedCornerTopRight(float length, float radius) {
        moveTo(0, 0);
        lineTo(length - radius, 0);
        curveTo(length, 0, length, 0, length, radius);
        lineTo(length, length);
        lineTo(0, length);
        closePath();
    }
}


final class RoundedCornerBottomLeft extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a square with a rounded bottom left corner
     * 
     * @param length Side length of square
     * @param radius Radius of curved corner
     */
    public RoundedCornerBottomLeft(float length, float radius) {
        moveTo(0, 0);
        lineTo(0, length - radius);
        curveTo(0, length, 0, length, radius, length);
        lineTo(length, length);
        lineTo(length, 0);
        closePath();
    }
}


final class RoundedCornerBottomRight extends Path2D.Float {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a square with a rounded bottom right corner
     * 
     * @param length Side length of square
     * @param radius Radius of curved corner
     */
    public RoundedCornerBottomRight(float length, float radius) {
        moveTo(length, 0);
        lineTo(length, length - radius);
        curveTo(length, length, length, length, length - radius, length);
        lineTo(0, length);
        lineTo(0, 0);
        closePath();
    }
}
