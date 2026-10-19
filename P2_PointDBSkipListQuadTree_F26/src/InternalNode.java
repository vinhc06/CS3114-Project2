import java.util.ArrayList;
import java.util.List;

/**
 * Internal node with four quadrants (NW, NE, SW, SE).
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
 */
public class InternalNode implements QuadNode {
    private QuadNode nw;
    private QuadNode ne;
    private QuadNode sw;
    private QuadNode se;

    /**
     * Constructs an InternalNode initializing children to EmptyNode.
     */
    public InternalNode() {
        nw = EmptyNode.getInstance();
        ne = EmptyNode.getInstance();
        sw = EmptyNode.getInstance();
        se = EmptyNode.getInstance();
    }


    /**
     * Inserts point into sub-quadrant.
     * 
     * @param p
     *            point to insert
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @return this
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
     * Removes point and merges children if decomposition criteria are met.
     * 
     * @param p
     *            point to remove
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
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

        if (nw instanceof InternalNode || ne instanceof InternalNode
            || sw instanceof InternalNode || se instanceof InternalNode) {
            return this;
        }

        List<Point> remaining = new ArrayList<>();
        collectPoints(remaining);

        if (remaining.isEmpty()) {
            return EmptyNode.getInstance();
        }

        if (remaining.size() <= 3 || !hasDistinctCoordinates(remaining)) {
            LeafNode mergedLeaf = new LeafNode();
            for (Point pt : remaining) {
                mergedLeaf.getPoints().add(pt);
            }
            return mergedLeaf;
        }

        return this;
    }


    /**
     * Checks if distinct coordinates exist in list.
     * 
     * @param pts
     *            list of points
     * @return true if different locations exist
     */
    private boolean hasDistinctCoordinates(List<Point> pts) {
        int firstX = pts.get(0).getX();
        int firstY = pts.get(0).getY();
        for (int i = 1; i < pts.size(); i++) {
            if (pts.get(i).getX() != firstX || pts.get(i).getY() != firstY) {
                return true;
            }
        }
        return false;
    }


    /**
     * Recursively traverses sub-quadrants intersecting the query region.
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
     * @return visited nodes count
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
            visited += nw.regionsearch(rx, ry, rw, rh, x, y, half);
        }
        if (intersects(rx, ry, rw, rh, x + half, y, half)) {
            visited += ne.regionsearch(rx, ry, rw, rh, x + half, y, half);
        }
        if (intersects(rx, ry, rw, rh, x, y + half, half)) {
            visited += sw.regionsearch(rx, ry, rw, rh, x, y + half, half);
        }
        if (intersects(rx, ry, rw, rh, x + half, y + half, half)) {
            visited += se.regionsearch(rx, ry, rw, rh, x + half, y + half,
                half);
        }

        return visited;
    }


    /**
     * Bounding box intersection check.
     * 
     * @param rx
     *            query box x
     * @param ry
     *            query box y
     * @param rw
     *            query box width
     * @param rh
     *            query box height
     * @param qx
     *            quadrant x
     * @param qy
     *            quadrant y
     * @param qsize
     *            quadrant dimension
     * @return true if boxes overlap
     */
    private boolean intersects(
        int rx,
        int ry,
        int rw,
        int rh,
        int qx,
        int qy,
        int qsize) {
        return rx < qx + qsize && rx + rw > qx && ry < qy + qsize && ry
            + rh > qy;
    }


    /**
     * Collects all points from the four quadrants.
     * 
     * @param allPoints
     *            accumulation list
     */
    @Override
    public void collectPoints(List<Point> allPoints) {
        nw.collectPoints(allPoints);
        ne.collectPoints(allPoints);
        sw.collectPoints(allPoints);
        se.collectPoints(allPoints);
    }


    /**
     * Preorder dump traversal.
     * 
     * @param x
     *            quadrant origin x
     * @param y
     *            quadrant origin y
     * @param size
     *            quadrant dimension
     * @param level
     *            indentation level
     * @return node count
     */
    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println("Node at " + x + ", " + y + ", " + size
            + ": Internal");
        int half = size / 2;
        int count = 1;
        count += nw.dump(x, y, half, level + 1);
        count += ne.dump(x + half, y, half, level + 1);
        count += sw.dump(x, y + half, half, level + 1);
        count += se.dump(x + half, y + half, half, level + 1);
        return count;
    }
}
