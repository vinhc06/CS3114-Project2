/**
 * Represents a point in 2D space.
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
 */
public class Point {
    private String name;
    private int x;
    private int y;

    /**
     * Size of the world bounding box.
     */
    public static final int WORLD_SIZE = 1024;

    /**
     * Minimum valid coordinate value.
     */
    public static final int MIN_COORD = 0;

    /**
     * Creates a named point.
     * 
     * @param name
     *            point identifier
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     */
    public Point(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }


    /**
     * Creates an unnamed point for coordinate searches.
     * 
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     */
    public Point(int x, int y) {
        this("", x, y);
    }


    /**
     * Returns the name of the point.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the x coordinate.
     * 
     * @return x coordinate
     */
    public int getX() {
        return x;
    }


    /**
     * Returns the y coordinate.
     * 
     * @return y coordinate
     */
    public int getY() {
        return y;
    }


    /**
     * Validates whether coordinates fall inside the world box.
     * 
     * @return true if outside [0, 1024)
     */
    public boolean isInvalid() {
        return x < MIN_COORD || y < MIN_COORD || x >= WORLD_SIZE
            || y >= WORLD_SIZE;
    }


    /**
     * Compares points by coordinates.
     * 
     * @param obj
     *            object to compare
     * @return true if x and y match
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point)obj;
        return this.x == other.x && this.y == other.y;
    }


    /**
     * Formats point as (name, x, y).
     * 
     * @return formatted point string
     */
    @Override
    public String toString() {
        return "(" + name + ", " + x + ", " + y + ")";
    }
}
