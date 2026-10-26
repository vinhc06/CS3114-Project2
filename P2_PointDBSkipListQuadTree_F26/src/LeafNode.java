
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a leaf node in the PR QuadTree.
 * Stores up to three points, or more if all points
 * have identical coordinates.
 *
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class LeafNode implements QuadNode {

    private List<Point> points;

    /**
     * Creates an empty leaf node.
     */
    public LeafNode() {
        points = new ArrayList<>();
    }


    /**
     * Returns the points stored in this leaf.
     *
     * @return list of points
     */
    public List<Point> getPoints() {
        return points;
    }


    /**
     * Inserts a point into the leaf.
     * Splits when more than three points exist
     * at different coordinates.
     *
     * @param p
     *            point to insert
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return updated QuadTree node
     */
    @Override
    public QuadNode insert(Point p, int x, int y, int size) {
        points.add(p);

        if (points.size() > 3 && hasDistinctCoordinates() && size > 1) {

            InternalNode internal = new InternalNode();

            for (Point pt : points) {
                internal.insert(pt, x, y, size);
            }

            return internal;
        }

        return this;
    }


    /**
     * Checks whether the stored points occupy
     * different coordinates.
     *
     * @return true if coordinates differ
     */
    private boolean hasDistinctCoordinates() {
        int firstX = points.get(0).getX();
        int firstY = points.get(0).getY();

        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getX() != firstX || points.get(i)
                .getY() != firstY) {

                return true;
            }
        }

        return false;
    }


    /**
     * Removes a single matching point from the leaf.
     *
     * @param p
     *            point to remove
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return updated node or EmptyNode
     */
    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        Iterator<Point> it = points.iterator();
        while (it.hasNext()) {
            Point pt = it.next();
            // Check identity or matching coordinates
            boolean match = (pt == p) || (pt.getX() == p.getX() && pt
                .getY() == p.getY() && (p.getName().isEmpty() || pt.getName()
                    .equals(p.getName())));
            if (match) {
                it.remove();
                break;
            }
        }

        if (points.isEmpty()) {
            return EmptyNode.getInstance();
        }
        return this;
    }


    /**
     * Searches for points inside the requested rectangle.
     *
     * @param rx
     *            rectangle x coordinate
     * @param ry
     *            rectangle y coordinate
     * @param rw
     *            rectangle width
     * @param rh
     *            rectangle height
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return number of nodes visited
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
        long right = (long)rx + rw;
        long bottom = (long)ry + rh;
        for (Point pt : points) {
            int px = pt.getX();
            int py = pt.getY();
            // Changed < right and < bottom to <= right and <= bottom
            if (px >= rx && px <= right && py >= ry && py <= bottom) {
                System.out.println("Point found: " + pt);
            }
        }
        return 1;
    }


    /**
     * Adds all stored points to the given list.
     *
     * @param allPoints
     *            list receiving the points
     */
    @Override
    public void collectPoints(List<Point> allPoints) {
        allPoints.addAll(points);
    }


    /**
     * Prints this leaf and its stored points.
     *
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation depth
     * @return number of nodes printed
     */
    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println("Node at " + x + ", " + y + ", " + size + ":");

        for (Point pt : points) {
            for (int i = 0; i < level; i++) {
                System.out.print("  ");
            }

            System.out.println(pt);
        }

        return 1;
    }
}
