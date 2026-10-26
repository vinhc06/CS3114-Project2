
import java.util.List;

/**
 * Represents an empty node in the PR QuadTree.
 * Uses the Flyweight design pattern so all empty
 * quadrants share one instance.
 *
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class EmptyNode implements QuadNode {

    private static final EmptyNode INSTANCE = new EmptyNode();

    /**
     * Private constructor to prevent other instances
     * from being created.
     */
    private EmptyNode() {
        // Singleton instance
    }


    /**
     * Returns the shared EmptyNode instance.
     *
     * @return the singleton EmptyNode
     */
    public static EmptyNode getInstance() {
        return INSTANCE;
    }


    /**
     * Creates a leaf node containing the inserted point.
     *
     * @param p
     *            point to insert
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return new LeafNode containing the point
     */
    @Override
    public QuadNode insert(Point p, int x, int y, int size) {
        LeafNode leaf = new LeafNode();
        return leaf.insert(p, x, y, size);
    }


    /**
     * Does nothing when removing from an empty node.
     *
     * @param p
     *            point to remove
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return this empty node
     */
    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        return this;
    }


    /**
     * Counts this empty node during a region search.
     *
     * @param rx
     *            region x coordinate
     * @param ry
     *            region y coordinate
     * @param rw
     *            region width
     * @param rh
     *            region height
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return one visited node
     */
    @Override
    public int regionsearch(
        int rx,
        int ry,
        int rw,
        int rh,
        int x,
        int y,
        int size) {

        return 1;
    }


    /**
     * Collects points from this node.
     * Empty nodes contain no points.
     *
     * @param allPoints
     *            list receiving points
     */
    @Override
    public void collectPoints(List<Point> allPoints) {
        // No points to collect
    }


    /**
     * Prints the empty node and its quadrant location.
     *
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation depth
     * @return one printed node
     */
    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println("Node at " + x + ", " + y
            + ", " + size + ": Empty");

        return 1;
    }
}
