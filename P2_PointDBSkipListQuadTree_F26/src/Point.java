/**
 * Represents a point in 2D space.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
 */
public class Point {
    private String name;
    private int x;
    private int y;

    public static final int WORLD_SIZE = 1024;
    public static final int MIN_COORD = 0;

    public Point(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }


    public Point(int x, int y) {
        this("", x, y);
    }


    public String getName() {
        return name;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public boolean isInvalid() {
        return x < MIN_COORD || y < MIN_COORD || x >= WORLD_SIZE
            || y >= WORLD_SIZE;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point)obj;
        return this.x == other.x && this.y == other.y;
    }


    @Override
    public String toString() {
        return "(" + name + ", " + x + ", " + y + ")";
    }
}
