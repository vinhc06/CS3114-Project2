/**
 * PR QuadTree container.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
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


    public void dump() {
        System.out.println("QuadTree dump:");
        int nodeCount = root.dump(0, 0, WORLD_SIZE, 0);
        System.out.println(nodeCount + " quadtree nodes printed");
    }
}
