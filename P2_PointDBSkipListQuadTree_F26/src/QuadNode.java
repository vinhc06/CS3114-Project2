
import java.util.List;

/**
 * Base interface for all QuadTree nodes.
 * Defines the operations shared by empty,
 * leaf, and internal nodes.
 *
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public interface QuadNode {

    /**
     * Inserts a point into the quadrant.
     *
     * @param p
     *            point to insert
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return updated root of this subtree
     */
    QuadNode insert(Point p, int x, int y, int size);


    /**
     * Removes a point from the quadrant.
     *
     * @param p
     *            point to remove
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return updated root of this subtree
     */
    QuadNode remove(Point p, int x, int y, int size);


    /**
     * Searches for points inside a rectangular region.
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
     * @return number of QuadTree nodes visited
     */
    int regionsearch(int rx, int ry, int rw, int rh,
        int x, int y, int size);


    /**
     * Collects all points stored in this subtree.
     *
     * @param allPoints
     *            list receiving the points
     */
    void collectPoints(List<Point> allPoints);


    /**
     * Prints this node and all its descendants.
     *
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation depth
     * @return total number of nodes printed
     */
    int dump(int x, int y, int size, int level);
}
