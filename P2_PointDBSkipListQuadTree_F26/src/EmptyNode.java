import java.util.List;

/**
 * Flyweight singleton for empty leaf nodes in the PR QuadTree.
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
 */
public class EmptyNode implements QuadNode {
    private static final EmptyNode INSTANCE = new EmptyNode();

    /**
     * Private constructor for flyweight singleton.
     */
    private EmptyNode() {
        // Singleton pattern
    }


    /**
     * Returns the singleton instance.
     * 
     * @return singleton EmptyNode
     */
    public static EmptyNode getInstance() {
        return INSTANCE;
    }


    /**
     * Transitions empty node to a leaf node holding p.
     * 
     * @param p
     *            point to insert
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return new leaf node
     */
    @Override
    public QuadNode insert(Point p, int x, int y, int size) {
        LeafNode leaf = new LeafNode();
        return leaf.insert(p, x, y, size);
    }


    /**
     * Removal on empty node is a no-op.
     * 
     * @param p
     *            point to remove
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return this
     */
    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        return this;
    }


    /**
     * Visits this empty node without output.
     * 
     * @param rx
     *            region x
     * @param ry
     *            region y
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
     * @return 1
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
     * No points to collect from an empty node.
     * 
     * @param allPoints
     *            accumulation list
     */
    @Override
    public void collectPoints(List<Point> allPoints) {
        // Intentionally empty
    }


    /**
     * Dumps the empty node representation.
     * 
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation level
     * @return 1
     */
    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println("Node at " + x + ", " + y + ", " + size + ": Empty");
        return 1;
    }
}
