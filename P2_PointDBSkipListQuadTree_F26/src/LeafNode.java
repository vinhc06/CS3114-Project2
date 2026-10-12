import java.util.ArrayList;
import java.util.List;

/**
 * Leaf node storing a list of points in PR QuadTree.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
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
            if (points.get(i).getX() != firstX || points.get(i)
                .getY() != firstY) {
                return true;
            }
        }
        return false;
    }


    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        points.remove(p);
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
        System.out.print("Node at " + x + ", " + y + ", " + size + ":");
        for (Point pt : points) {
            System.out.print(" " + pt.toString());
        }
        System.out.println();
        return 1;
    }
}
