
import java.util.ArrayList;
import java.util.List;

/**
 * Leaf node for the quadtree.
 */
public class LeafNode implements QuadNode {
    private List<Point> points;

    public LeafNode() {
        points = new ArrayList<>();
    }

    public List<Point> getPoints() {
        return points;
    }

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

    private boolean hasDistinctCoordinates() {
        int firstX = points.get(0).getX();
        int firstY = points.get(0).getY();

        for (int i = 1; i < points.size(); i++) {
            Point p = points.get(i);

            if (p.getX() != firstX || p.getY() != firstY) {
                return true;
            }
        }

        return false;
    }

    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i) == p) {
                points.remove(i);
                break;
            }
        }

        if (points.isEmpty()) {
            return EmptyNode.getInstance();
        }

        return this;
    }

    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println("Node at " + x + ", " + y
            + ", " + size + ":");

        for (Point p : points) {
            System.out.println(p);
        }

        return 1;
    }
}
