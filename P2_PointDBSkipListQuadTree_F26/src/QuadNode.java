
@Override
public QuadNode remove(Point p, int x, int y, int size) {
    int half = size / 2;

    if (p.getX() < x + half) {
        if (p.getY() < y + half) {
            nw = nw.remove(p, x, y, half);
        }
        else {
            sw = sw.remove(p, x, y + half, half);
        }
    }
    else {
        if (p.getY() < y + half) {
            ne = ne.remove(p, x + half, y, half);
        }
        else {
            se = se.remove(p, x + half, y + half, half);
        }
    }

    // See if the children can be merged
    ArrayList<Point> points = new ArrayList<>();

    if (collectPoints(points)) {
        if (points.isEmpty()) {
            return EmptyNode.getInstance();
        }

        if (points.size() <= 3 || sameLocation(points)) {
            LeafNode leaf = new LeafNode();
            leaf.getPoints().addAll(points);
            return leaf;
        }
    }

    return this;
}


private boolean collectPoints(ArrayList<Point> points) {
    QuadNode[] children = { nw, ne, sw, se };

    for (QuadNode child : children) {
        if (child instanceof InternalNode) {
            return false;
        }

        if (child instanceof LeafNode) {
            LeafNode leaf = (LeafNode)child;
            points.addAll(leaf.getPoints());
        }
    }

    return true;
}


private boolean sameLocation(ArrayList<Point> points) {
    int x = points.get(0).getX();
    int y = points.get(0).getY();

    for (Point p : points) {
        if (p.getX() != x || p.getY() != y) {
            return false;
        }
    }

    return true;
}
