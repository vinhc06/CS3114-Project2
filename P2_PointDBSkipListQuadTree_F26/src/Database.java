import java.util.ArrayList;

/**
 * Coordinates SkipList and PR QuadTree operations.
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
 */
public class Database {
    private SkipList<String, Point> list;
    private QuadTree quadTree;

    /**
     * Creates and initializes the database containers.
     */
    public Database() {
        list = new SkipList<>();
        quadTree = new QuadTree();
    }


    /**
     * Inserts a point into SkipList and QuadTree.
     * 
     * @param name
     *            point name
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     */
    public void insert(String name, int x, int y) {
        Point pt = new Point(name, x, y);
        if (pt.isInvalid()) {
            System.out.println("Point rejected: (" + name + ", " + x + ", " + y
                + ")");
            return;
        }
        list.insert(new KVPair<>(name, pt));
        quadTree.insert(pt);
        System.out.println("Point inserted: (" + name + ", " + x + ", " + y
            + ")");
    }


    /**
     * Removes a point by name from both structures.
     * 
     * @param name
     *            point name to remove
     */
    public void remove(String name) {
        KVPair<String, Point> removed = list.remove(name);
        if (removed == null) {
            System.out.println("Point not removed: " + name);
        }
        else {
            quadTree.remove(removed.value());
            Point p = removed.value();
            System.out.println("Point removed: (" + removed.key() + ", " + p
                .getX() + ", " + p.getY() + ")");
        }
    }


    /**
     * Removes a point by coordinates from both structures.
     * 
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     */
    public void remove(int x, int y) {
        Point dummy = new Point(x, y);
        if (dummy.isInvalid()) {
            System.out.println("Point rejected: (" + x + ", " + y + ")");
            return;
        }
        KVPair<String, Point> removed = list.removeByValue(dummy);
        if (removed == null) {
            System.out.println("Point not found: (" + x + ", " + y + ")");
            return;
        }
        Point p = removed.value();
        quadTree.remove(p);

        System.out.println("Point removed: (" + removed.key() + ", " + p.getX()
            + ", " + p.getY() + ")");
    }


    /**
     * Searches for points with matching name in the SkipList.
     * 
     * @param name
     *            point name
     */
    public void search(String name) {
        ArrayList<KVPair<String, Point>> results = list.search(name);
        if (results.isEmpty()) {
            System.out.println("Point not found: " + name);
            return;
        }
        for (KVPair<String, Point> pair : results) {
            Point p = pair.value();
            System.out.println("Found (" + pair.key() + ", " + p.getX() + ", "
                + p.getY() + ")");
        }
    }


    /**
     * Queries points intersecting region box.
     * 
     * @param x
     *            region x
     * @param y
     *            region y
     * @param w
     *            region width
     * @param h
     *            region height
     */
    public void regionsearch(int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            System.out.println("Rectangle rejected: (" + x + ", " + y + ", " + w
                + ", " + h + ")");
            return;
        }
        quadTree.regionsearch(x, y, w, h);
    }


    /**
     * Finds and prints points with duplicate coordinates.
     */
    public void duplicates() {
        quadTree.duplicates();
    }


    /**
     * Prints state dump of SkipList and QuadTree.
     */
    public void dump() {
        list.dump();
        quadTree.dump();
    }
}
