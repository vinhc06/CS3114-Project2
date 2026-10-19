import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PR QuadTree container class for spatial queries.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-18
 */
public class QuadTree {
    private QuadNode root;
    private static final int WORLD_SIZE = 1024;

    public QuadTree() {
        root = EmptyNode.getInstance();
    }


    public void insert(Point p) {
        root = root.insert(p, 0, 0, WORLD_SIZE);
    }


    public void remove(Point p) {
        root = root.remove(p, 0, 0, WORLD_SIZE);
    }


    public void regionsearch(int rx, int ry, int rw, int rh) {
        System.out.println("Points intersecting region (" + rx + ", " + ry
            + ", " + rw + ", " + rh + "):");
        int visited = root.regionsearch(rx, ry, rw, rh, 0, 0, WORLD_SIZE);
        System.out.println(visited + " quadtree nodes visited");
    }


    public void duplicates() {
        System.out.println("Duplicate points:");
        List<Point> allPoints = new ArrayList<>();
        root.collectPoints(allPoints);

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Point p : allPoints) {
            String coord = "(" + p.getX() + ", " + p.getY() + ")";
            counts.put(coord, counts.getOrDefault(coord, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println(entry.getKey());
            }
        }
    }


    public void dump() {
        System.out.println("QuadTree dump:");
        int nodeCount = root.dump(0, 0, WORLD_SIZE, 0);
        System.out.println(nodeCount + " quadtree nodes printed");
    }
}
