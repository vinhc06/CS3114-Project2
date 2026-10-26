
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an internal PR QuadTree node with four children.
 * The children are ordered NW, NE, SW, and SE.
 *
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class InternalNode implements QuadNode {
    private QuadNode nw;
    private QuadNode ne;
    private QuadNode sw;
    private QuadNode se;

    /**
     * Creates an internal node with four empty children.
     */
    public InternalNode() {
        nw = EmptyNode.getInstance();
        ne = EmptyNode.getInstance();
        sw = EmptyNode.getInstance();
        se = EmptyNode.getInstance();
    }


    /**
     * Inserts a point into the correct quadrant.
     *
     * @param p
     *            point to insert
     * @param x
     *            quadrant x coordinate
     * @param y
     *            quadrant y coordinate
     * @param size
     *            quadrant size
     * @return updated internal node
     */
    @Override
    public QuadNode insert(Point p, int x, int y, int size) {
        int half = size / 2;
        int px = p.getX();
        int py = p.getY();

        if (px < x + half) {
            if (py < y + half) {
                nw = nw.insert(p, x, y, half);
            }
            else {
                sw = sw.insert(p, x, y + half, half);
            }
        }
        else {
            if (py < y + half) {
                ne = ne.insert(p, x + half, y, half);
            }
            else {
                se = se.insert(p, x + half, y + half, half);
            }
        }

        return this;
    }


    /**
     * Removes a point from the correct quadrant and
     * merges children when possible.
     *
     * @param p
     *            point to remove
     * @param x
     *            quadrant x coordinate
     * @param y
     *            quadrant y coordinate
     * @param size
     *            quadrant size
     * @return updated node
     */
    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        int half = size / 2;
        int px = p.getX();
        int py = p.getY();

        if (px < x + half) {
            if (py < y + half) {
                nw = nw.remove(p, x, y, half);
            }
            else {
                sw = sw.remove(p, x, y + half, half);
            }
        }
        else {
            if (py < y + half) {
                ne = ne.remove(p, x + half, y, half);
            }
            else {
                se = se.remove(p, x + half, y + half, half);
            }
        }

        // Do not merge if a child is still an internal node.
        if (nw instanceof InternalNode || ne instanceof InternalNode
            || sw instanceof InternalNode || se instanceof InternalNode) {
            return this;
        }

        List<Point> remaining = new ArrayList<>();
        collectPoints(remaining);

        if (remaining.isEmpty()) {
            return EmptyNode.getInstance();
        }

        // Merge when at most three points remain, or all
        // remaining points share the same coordinates.
        if (remaining.size() <= 3
            || !hasDistinctCoordinates(remaining)) {

            LeafNode mergedLeaf = new LeafNode();

            for (Point pt : remaining) {
                mergedLeaf.getPoints().add(pt);
            }

            return mergedLeaf;
        }

        return this;
    }


    /**
     * Checks whether points occupy different coordinates.
     *
     * @param pts
     *            list of points
     * @return true if at least two coordinates differ
     */
    private boolean hasDistinctCoordinates(List<Point> pts) {
        int firstX = pts.get(0).getX();
        int firstY = pts.get(0).getY();

        for (int i = 1; i < pts.size(); i++) {
            if (pts.get(i).getX() != firstX
                || pts.get(i).getY() != firstY) {

                return true;
            }
        }

        return false;
    }


    /**
     * Searches quadrants that intersect the query rectangle.
     *
     * @param rx
     *            rectangle x
     * @param ry
     *            rectangle y
     * @param rw
     *            rectangle width
     * @param rh
     *            rectangle height
     * @param x
     *            quadrant x
     * @param y
     *            quadrant y
     * @param size
     *            quadrant size
     * @return number of visited nodes
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

        int visited = 1;
        int half = size / 2;

        if (intersects(rx, ry, rw, rh, x, y, half)) {
            visited += nw.regionsearch(rx, ry, rw, rh,
                x, y, half);
        }

        if (intersects(rx, ry, rw, rh, x + half, y, half)) {
            visited += ne.regionsearch(rx, ry, rw, rh,
                x + half, y, half);
        }

        if (intersects(rx, ry, rw, rh, x, y + half, half)) {
            visited += sw.regionsearch(rx, ry, rw, rh,
                x, y + half, half);
        }

        if (intersects(rx, ry, rw, rh,
            x + half, y + half, half)) {

            visited += se.regionsearch(rx, ry, rw, rh,
                x + half, y + half, half);
        }

        return visited;
    }


    /**
     * Checks whether a rectangle overlaps a quadrant.
     *
     * @param rx
     *            rectangle x
     * @param ry
     *            rectangle y
     * @param rw
     *            rectangle width
     * @param rh
     *            rectangle height
     * @param qx
     *            quadrant x
     * @param qy
     *            quadrant y
     * @param qsize
     *            quadrant size
     * @return true if the areas overlap
     */
    private boolean intersects(
        int rx,
        int ry,
        int rw,
        int rh,
        int qx,
        int qy,
        int qsize) {

        long right = (long)rx + rw;
        long bottom = (long)ry + rh;

        return rx <= qx + qsize
            && right >= qx
            && ry <= qy + qsize
            && bottom >= qy;
    }


    /**
     * Collects points from all four children.
     *
     * @param allPoints
     *            list receiving the points
     */
    @Override
    public void collectPoints(List<Point> allPoints) {
        nw.collectPoints(allPoints);
        ne.collectPoints(allPoints);
        sw.collectPoints(allPoints);
        se.collectPoints(allPoints);
    }


    /**
     * Prints this node and recursively prints its children.
     *
     * @param x
     *            quadrant x
     * @param y
     *            quadrant y
     * @param size
     *            quadrant size
     * @param level
     *            indentation level
     * @return total number of nodes printed
     */
    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println("Node at " + x + ", " + y
            + ", " + size + ": Internal");

        int half = size / 2;
        int count = 1;

        count += nw.dump(x, y, half, level + 1);
        count += ne.dump(x + half, y, half, level + 1);
        count += sw.dump(x, y + half, half, level + 1);
        count += se.dump(x + half, y + half, half, level + 1);

        return count;
    }
}
