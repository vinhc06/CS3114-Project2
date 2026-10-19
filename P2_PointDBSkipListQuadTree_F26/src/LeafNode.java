import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Leaf node storing 1 to 3 points, or multiple points at the same location.
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
 */
public class LeafNode implements QuadNode {
    private List<Point> points;

    /**
     * Constructs a LeafNode with an empty point list.
     */
    public LeafNode() {
        points = new ArrayList<>();
    }


    /**
     * Accessor for stored points.
     * 
     * @return points list
     */
    public List<Point> getPoints() {
        return points;
    }


    /**
     * Inserts a point, splitting if > 3 points with distinct coordinates.
     * 
     * @param p
     *            point to insert
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return updated node
     */
    @Override
    public QuadNode insert(Point p, int x, int y, int size) {
        points.add(p);

        if (points.size() > 3 && hasDistinctCoordinates()) {
            InternalNode internal = new InternalNode();
            for (Point pt : points) {
                internal.insert(pt, x, y, size);
            }
            return internal;
        }
        return this;
    }


    /**
     * Checks if distinct coordinates exist in the leaf.
     * 
     * @return true if at least two points differ in location
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
     * Removes a single matching point by name and coordinates.
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
            if (pt.getX() == p.getX() && pt.getY() == p.getY() && (p.getName()
                .isEmpty() || pt.getName().equals(p.getName()))) {
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
     * Searches for points inside query rectangle.
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
        for (Point pt : points) {
            int px = pt.getX();
            int py = pt.getY();
            if (px >= rx && px < rx + rw && py >= ry && py < ry + rh) {
                System.out.println("Point found: " + pt.toString());
            }
        }
        return 1;
    }


    /**
     * Adds all points in this leaf to the accumulation list.
     * 
     * @param allPoints
     *            accumulation list
     */
    @Override
    public void collectPoints(List<Point> allPoints) {
        allPoints.addAll(points);
    }


    /**
     * Dumps the leaf node and its points with two-space indentation.
     * 
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation depth
     * @return 1
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
            System.out.println(pt.toString());
        }
        return 1;
    }
}
