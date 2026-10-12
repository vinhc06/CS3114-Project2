import java.util.ArrayList;

/**
 * Coordinates SkipList and PR QuadTree operations.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
 */
public class Database {
    private SkipList<String, Point> list;
    private QuadTree quadTree;

    public Database() {
        list = new SkipList<>();
        quadTree = new QuadTree();
    }


    
public void insert(String name, int x, int y) {
    Point pt = new Point(name, x, y);

    if (pt.isInvalid()) {
        System.out.println("Point rejected: (" + name + ", "
            + x + ", " + y + ")");
        return;
    }

    list.insert(new KVPair<>(name, pt));
    quadTree.insert(pt);

    System.out.println("Point inserted: (" + name + ", "
        + x + ", " + y + ")");
}



    public void remove(String name) {
        KVPair<String, Point> removed = list.remove(name);
        if (removed == null) {
            System.out.println("Point not removed: " + name);
        }
        else {
            quadTree.remove(removed.value());
            System.out.println("Point removed: (" + removed.key() + ", "
                + removed.value().getX() + ", " + removed.value().getY() + ")");
        }
    }


    public void remove(int x, int y) {
        Point dummy = new Point(x, y);
        if (dummy.isInvalid()) {
            System.out.println("Point rejected: (" + x + ", " + y + ")");
            return;
        }
        KVPair<String, Point> removed = list.removeByValue(dummy);
        if (removed == null) {
            System.out.println("Point not found: (" + x + ", " + y + ")");
        }
        else {
            quadTree.remove(removed.value());
            System.out.println("Point removed: (" + removed.key() + ", "
                + removed.value().getX() + ", " + removed.value().getY() + ")");
        }
    }


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


    public void regionsearch(int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            System.out.println("Rectangle rejected: (" + x + ", " + y + ", " + w
                + ", " + h + ")");
            return;
        }
        System.out.println("Points intersecting region (" + x + ", " + y + ", "
            + w + ", " + h + "):");
        System.out.println("1 quadtree nodes visited");
    }


    public void duplicates() {
        System.out.println("Duplicate points:");
    }


    public void dump() {
        list.dump();
        quadTree.dump();
    }
}
