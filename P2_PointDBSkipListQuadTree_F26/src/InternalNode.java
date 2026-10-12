/**
 * Internal node managing 4 quadrants in PR QuadTree.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
 */
public class InternalNode implements QuadNode {
    private QuadNode nw;
    private QuadNode ne;
    private QuadNode sw;
    private QuadNode se;

    public InternalNode() {
        nw = EmptyNode.getInstance();
        ne = EmptyNode.getInstance();
        sw = EmptyNode.getInstance();
        se = EmptyNode.getInstance();
    }


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
        return this;
    }


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
