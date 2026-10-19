import java.util.List;

/**
 * Base interface for QuadTree nodes.
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
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
     * Searches for points inside the given query region.
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
     * @return total nodes visited
     */
    int regionsearch(int rx, int ry, int rw, int rh, int x, int y, int size);


    /**
     * Collects all points contained in this subtree.
     * 
     * @param allPoints
     *            accumulation list
     */
    void collectPoints(List<Point> allPoints);


    /**
     * Traverses and prints this node and all descendants.
     * 
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation level
     * @return total nodes printed in this subtree
     */
    int dump(int x, int y, int size, int level);
}
