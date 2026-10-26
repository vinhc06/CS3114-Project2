
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PR QuadTree container class for spatial queries.
 *
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-25
 */
public class QuadTree {
    private QuadNode root;
    private static final int WORLD_SIZE = 1024;

    /**
     * Creates an empty QuadTree.
     */
    public QuadTree() {
        root = EmptyNode.getInstance();
    }


    /**
     * Inserts a point into the QuadTree.
     *
     * @param p
     *            point to insert
     */
    public void insert(Point p) {
        root = root.insert(p, 0, 0, WORLD_SIZE);
    }


    /**
     * Removes a point from the QuadTree.
     *
     * @param p
     *            point to remove
     */
    public void remove(Point p) {
        root = root.remove(p, 0, 0, WORLD_SIZE);
    }


    /**
     * Searches for points within the given rectangular region.
     *
     * @param rx
     *            rectangle x coordinate
     * @param ry
     *            rectangle y coordinate
     * @param rw
     *            rectangle width
     * @param rh
     *            rectangle height
     */
    public void regionsearch(int rx, int ry, int rw, int rh) {
        System.out.println("Points intersecting region (" + rx + ", "
            + ry + ", " + rw + ", " + rh + "):");

        int visited = root.regionsearch(rx, ry, rw, rh, 0, 0,
            WORLD_SIZE);

        System.out.println(visited + " quadtree nodes visited");
    }


    /**
     * Finds and prints coordinates shared by multiple points.
     */
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


    /**
     * Prints the entire QuadTree and the total node count.
     */
    public void dump() {
        System.out.println("QuadTree dump:");

        int nodeCount = root.dump(0, 0, WORLD_SIZE, 0);

        System.out.println(nodeCount + " quadtree nodes printed");
    }
}
