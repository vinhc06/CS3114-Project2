/**
 * Flyweight singleton for empty nodes in PR QuadTree.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
 */
public class EmptyNode implements QuadNode {
    private static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode() {
    }


    public static EmptyNode getInstance() {
        return INSTANCE;
    }


    @Override
    public QuadNode insert(Point p, int x, int y, int size) {
        LeafNode leaf = new LeafNode();
        return leaf.insert(p, x, y, size);
    }


    @Override
    public QuadNode remove(Point p, int x, int y, int size) {
        return this;
    }


    @Override
    public int dump(int x, int y, int size, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println("Node at " + x + ", " + y + ", " + size + ": Empty");
        return 1;
    }
}
