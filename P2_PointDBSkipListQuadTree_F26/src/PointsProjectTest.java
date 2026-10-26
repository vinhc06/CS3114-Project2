
import student.TestCase;

/**
 * Tests the PointDB project, including SkipList, QuadTree,
 * command parsing, and boundary behavior.
 *
 * @author Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests processing commands from a real temporary input file.
     *
     * @throws Exception
     *             if the file cannot be written or read
     */
    public void testSyntaxFiles() throws Exception {
        java.io.File file = java.io.File.createTempFile(
            "pointdb_commands_", ".txt");
        file.deleteOnExit();
        try (java.io.PrintWriter writer =
            new java.io.PrintWriter(file)) {
            writer.println("insert alpha 100 100");
            writer.println("insert beta 700 700");
            writer.println("search alpha");
            writer.println("regionsearch 0 0 200 200");
            writer.println("dump");
            writer.println("remove alpha");
            writer.println("search alpha");
        }

        systemOut().clearHistory();
        PointsProject.main(new String[] { file.getAbsolutePath() });
        String out = systemOut().getHistory();
        assertTrue(out.contains("Point inserted: (alpha, 100, 100)"));
        assertTrue(out.contains("Point inserted: (beta, 700, 700)"));
        assertTrue(out.contains("Found (alpha, 100, 100)"));
        assertTrue(out.contains("Point found: (alpha, 100, 100)"));
        assertTrue(out.contains("QuadTree dump:"));
        assertTrue(out.contains("SkipList dump:"));
        assertTrue(out.contains("Point removed: (alpha, 100, 100)"));
        assertTrue(out.contains("Point not found: alpha"));
    }


    /**
     * Tests missing arguments and invalid files.
     *
     * @throws Exception
     *             if an input error occurs
     */
    public void testMainBadArgs() throws Exception {
        PointsProject.main(new String[] {});
        assertTrue(systemOut().getHistory().contains(
                "Usage: PointsProject"));

        PointsProject.main(new String[] { "nonexistent_file.txt" });
        assertTrue(systemOut().getHistory().contains(
                "There is no such input file"));
    }

    /**
     * Tests inserting valid and invalid points.
     */
    public void testInsertAndBounds() {
        Database db = new Database();

        db.insert("origin", 0, 0);
        db.insert("edge", 1023, 1023);
        db.insert("negX", -1, 50);
        db.insert("negY", 50, -1);
        db.insert("overX", 1024, 50);
        db.insert("overY", 50, 1024);

        String out = systemOut().getHistory();

        assertTrue(out.contains("Point inserted: (origin, 0, 0)"));
        assertTrue(out.contains("Point inserted: (edge, 1023, 1023)"));
        assertTrue(out.contains("Point rejected: (negX, -1, 50)"));
        assertTrue(out.contains("Point rejected: (negY, 50, -1)"));
        assertTrue(out.contains("Point rejected: (overX, 1024, 50)"));
        assertTrue(out.contains("Point rejected: (overY, 50, 1024)"));
    }

    /**
     * Tests searching the SkipList.
     */
    public void testSearchOperations() {
        Database db = new Database();

        db.search("missing");
        assertTrue(systemOut().getHistory().contains(
                "Point not found: missing"));

        db.insert("point", 10, 20);
        db.insert("point", 30, 40);

        db.search("point");

        String out = systemOut().getHistory();

        assertTrue(out.contains("Found (point, 10, 20)"));
        assertTrue(out.contains("Found (point, 30, 40)"));
    }

    /**
     * Tests the QuadTree splitting into four quadrants.
     */
    public void testSplitsAndDumps() {
        Database db = new Database();

        db.insert("nw", 100, 100);
        db.insert("ne", 700, 100);
        db.insert("sw", 100, 700);
        db.insert("se", 700, 700);

        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("Node at 0, 0, 512:"));
        assertTrue(out.contains("(nw, 100, 100)"));
        assertTrue(out.contains("(ne, 700, 100)"));
        assertTrue(out.contains("(sw, 100, 700)"));
        assertTrue(out.contains("(se, 700, 700)"));
    }

    /**
     * Tests that the QuadTree can split more than once.
     */
    public void testDeepSplitting() {
        Database db = new Database();

        db.insert("a", 10, 10);
        db.insert("b", 20, 20);
        db.insert("c", 30, 30);
        db.insert("d", 40, 40);
        db.insert("e", 50, 50);

        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("Node at 0, 0, 512: Internal"));
        assertTrue(out.contains("(a, 10, 10)"));
        assertTrue(out.contains("(e, 50, 50)"));
    }

    /**
     * Tests duplicate coordinates without splitting.
     */
    public void testDuplicateCoordsNoSplit() {
        Database db = new Database();

        db.insert("d1", 50, 50);
        db.insert("d2", 50, 50);
        db.insert("d3", 50, 50);
        db.insert("d4", 50, 50);
        db.insert("d5", 50, 50);

        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("Node at 0, 0, 1024:"));
        assertFalse(out.contains("Node at 0, 0, 1024: Internal"));

        db.duplicates();

        assertTrue(systemOut().getHistory().contains("(50, 50)"));
    }

    /**
     * Tests duplicate names with different coordinates.
     */
    public void testDuplicateNames() {
        Database db = new Database();

        db.insert("same", 100, 100);
        db.insert("same", 200, 200);
        db.insert("same", 300, 300);

        db.search("same");

        String out = systemOut().getHistory();

        assertTrue(out.contains("Found (same, 100, 100)"));
        assertTrue(out.contains("Found (same, 200, 200)"));
        assertTrue(out.contains("Found (same, 300, 300)"));
    }

    /**
     * Tests removing points and merging nodes.
     */
    public void testRemovalAndMerge() {
        Database db = new Database();

        db.remove("missing");
        assertTrue(systemOut().getHistory().contains(
                "Point not removed: missing"));

        db.remove(-1, -1);
        assertTrue(systemOut().getHistory().contains(
                "Point rejected: (-1, -1)"));

        db.remove(500, 500);
        assertTrue(systemOut().getHistory().contains(
                "Point not found: (500, 500)"));

        db.insert("a", 100, 100);
        db.insert("b", 700, 100);
        db.insert("c", 100, 700);
        db.insert("d", 700, 700);

        db.remove("a");

        assertTrue(systemOut().getHistory().contains(
                "Point removed: (a, 100, 100)"));

        systemOut().clearHistory();
        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("Node at 0, 0, 1024:"));
        assertFalse(out.contains("Internal"));

        db.remove(700, 100);
        db.remove(100, 700);
        db.remove(700, 700);

        systemOut().clearHistory();
        db.dump();

        assertTrue(systemOut().getHistory().contains(
                "Node at 0, 0, 1024: Empty"));
    }

    /**
     * Tests removing one point when multiple points
     * share the same coordinates.
     */
    public void testRemoveDuplicateCoordinates() {
        Database db = new Database();

        db.insert("a", 50, 50);
        db.insert("b", 50, 50);
        db.insert("c", 50, 50);

        db.remove(50, 50);

        systemOut().clearHistory();
        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("Node at 0, 0, 1024:"));
        assertTrue(out.contains("SkipList size is: 2"));

        int remaining = 0;

        if (out.contains("(a, 50, 50)")) {
            remaining++;
        }
        if (out.contains("(b, 50, 50)")) {
            remaining++;
        }
        if (out.contains("(c, 50, 50)")) {
            remaining++;
        }

        assertEquals(2, remaining);
    }

    /**
     * Tests removing points from an empty database.
     */
    public void testRemoveFromEmpty() {
        Database db = new Database();

        db.remove("missing");
        db.remove(100, 100);

        String out = systemOut().getHistory();

        assertTrue(out.contains("Point not removed: missing"));
        assertTrue(out.contains("Point not found: (100, 100)"));
    }

    /**
     * Tests a region search.
     */
    public void testRegionSearch() {
        Database db = new Database();

        db.regionsearch(0, 0, -5, 10);

        assertTrue(systemOut().getHistory().contains(
                "Rectangle rejected: (0, 0, -5, 10)"));

        db.insert("inside", 50, 50);
        db.insert("outside", 500, 500);

        systemOut().clearHistory();
        db.regionsearch(0, 0, 100, 100);

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (inside, 50, 50)"));
        assertFalse(out.contains(
                "Point found: (outside, 500, 500)"));
        assertTrue(out.contains("quadtree nodes visited"));
    }

    /**
     * Tests searching a region crossing quadrants.
     */
    public void testRegionAcrossQuadrants() {
        Database db = new Database();

        db.insert("nw", 500, 500);
        db.insert("ne", 520, 500);
        db.insert("sw", 500, 520);
        db.insert("se", 520, 520);

        systemOut().clearHistory();
        db.regionsearch(490, 490, 50, 50);

        String out = systemOut().getHistory();

        assertTrue(out.contains("Point found: (nw, 500, 500)"));
        assertTrue(out.contains("Point found: (ne, 520, 500)"));
        assertTrue(out.contains("Point found: (sw, 500, 520)"));
        assertTrue(out.contains("Point found: (se, 520, 520)"));
    }

    /**
     * Tests searching a region without matching points.
     */
    public void testEmptyRegionSearch() {
        Database db = new Database();

        db.insert("point", 900, 900);

        systemOut().clearHistory();
        db.regionsearch(0, 0, 100, 100);

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Points intersecting region (0, 0, 100, 100):"));
        assertFalse(out.contains("Point found:"));
        assertTrue(out.contains("quadtree nodes visited"));
    }

    /**
     * Tests duplicate-coordinate reporting.
     */
    public void testDuplicates() {
        Database db = new Database();

        db.insert("a", 100, 100);
        db.insert("b", 100, 100);
        db.insert("c", 200, 200);
        db.insert("d", 200, 200);
        db.insert("e", 300, 300);

        systemOut().clearHistory();
        db.duplicates();

        String out = systemOut().getHistory();

        assertTrue(out.contains("(100, 100)"));
        assertTrue(out.contains("(200, 200)"));
        assertFalse(out.contains("(300, 300)"));
    }

    /**
     * Tests dumping an empty database.
     */
    public void testEmptyDump() {
        Database db = new Database();

        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("Node at 0, 0, 1024: Empty"));
        assertTrue(out.contains("1 quadtree nodes printed"));
    }

    /**
     * Tests an internal node's four child directions and merge.
     */
    public void testInternalNodeDirectly() {
        InternalNode node = new InternalNode();
        Point nw = new Point("nw", 10, 10);
        Point ne = new Point("ne", 900, 10);
        Point sw = new Point("sw", 10, 900);
        Point se = new Point("se", 900, 900);

        node.insert(nw, 0, 0, 1024);
        node.insert(ne, 0, 0, 1024);
        node.insert(sw, 0, 0, 1024);
        node.insert(se, 0, 0, 1024);

        systemOut().clearHistory();
        assertEquals(5, node.dump(0, 0, 1024, 0));
        String out = systemOut().getHistory();
        assertTrue(out.contains("(nw, 10, 10)"));
        assertTrue(out.contains("(ne, 900, 10)"));
        assertTrue(out.contains("(sw, 10, 900)"));
        assertTrue(out.contains("(se, 900, 900)"));

        assertEquals(2, node.regionsearch(
            0, 0, 512, 512, 0, 0, 1024));
        assertEquals(2, node.regionsearch(
            512, 0, 512, 512, 0, 0, 1024));
        assertEquals(2, node.regionsearch(
            0, 512, 512, 512, 0, 0, 1024));
        assertEquals(2, node.regionsearch(
            512, 512, 512, 512, 0, 0, 1024));

        QuadNode merged = node.remove(nw, 0, 0, 1024);
        assertTrue(merged instanceof LeafNode);
        systemOut().clearHistory();
        assertEquals(1, merged.dump(0, 0, 1024, 0));
        out = systemOut().getHistory();
        assertFalse(out.contains("(nw, 10, 10)"));
        assertTrue(out.contains("(ne, 900, 10)"));
        assertTrue(out.contains("(sw, 10, 900)"));
        assertTrue(out.contains("(se, 900, 900)"));
    }


    /**
     * Tests leaf split threshold and identical coordinates.
     */
    public void testLeafNodeDirectly() {
        QuadNode node = new LeafNode();
        node = node.insert(new Point("a", 10, 10), 0, 0, 1024);
        node = node.insert(new Point("b", 10, 10), 0, 0, 1024);
        node = node.insert(new Point("c", 10, 10), 0, 0, 1024);
        node = node.insert(new Point("d", 10, 10), 0, 0, 1024);
        assertTrue(node instanceof LeafNode);
        assertEquals(1, node.dump(0, 0, 1024, 0));

        node = node.insert(new Point("e", 900, 900), 0, 0, 1024);
        assertTrue(node instanceof InternalNode);
        assertEquals(5, node.dump(0, 0, 1024, 0));
    }


    /**
     * Tests the point equality contract and formatting.
     */
    public void testPointEqualityAndHash() {
        Point a = new Point("a", 100, 200);
        Point b = new Point("b", 100, 200);
        Point c = new Point("c", 101, 200);
        Point d = new Point("d", 100, 201);

        assertTrue(a.equals(a));
        assertTrue(a.equals(b));
        assertFalse(a.equals(c));
        assertFalse(a.equals(d));
        assertFalse(a.equals(null));
        assertFalse(a.equals("point"));
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(31 * Integer.hashCode(100)
            + Integer.hashCode(200), a.hashCode());
        assertEquals("(a, 100, 200)", a.toString());
        assertEquals(100, a.getX());
        assertEquals(200, a.getY());
        assertEquals("a", a.getName());
    }


    /**
     * Tests zero and negative rectangle dimensions.
     */
    public void testInvalidRectangleDimensions() {
        Database db = new Database();
        db.regionsearch(0, 0, 0, 10);
        db.regionsearch(0, 0, 10, 0);
        db.regionsearch(0, 0, -1, 10);
        db.regionsearch(0, 0, 10, -1);

        String out = systemOut().getHistory();
        assertTrue(out.contains("Rectangle rejected: (0, 0, 0, 10)"));
        assertTrue(out.contains("Rectangle rejected: (0, 0, 10, 0)"));
        assertTrue(out.contains("Rectangle rejected: (0, 0, -1, 10)"));
        assertTrue(out.contains("Rectangle rejected: (0, 0, 10, -1)"));
    }


    /**
     * Tests the empty-node flyweight and nested dump indentation.
     */
    public void testEmptyNodeDirectly() {
        EmptyNode empty = EmptyNode.getInstance();
        assertTrue(empty == EmptyNode.getInstance());
        assertTrue(empty.remove(new Point("x", 10, 10),
            0, 0, 1024) == empty);
        assertEquals(1, empty.regionsearch(
            0, 0, 100, 100, 0, 0, 1024));

        systemOut().clearHistory();
        assertEquals(1, empty.dump(0, 0, 256, 2));
        assertTrue(systemOut().getHistory().contains(
            "    Node at 0, 0, 256: Empty"));

        QuadNode result = empty.insert(
            new Point("a", 100, 100), 0, 0, 1024);
        assertTrue(result instanceof LeafNode);
    }


    /**
     * Tests SkipList iterator order, size, search, and value removal.
     */
    public void testSkipListDirectly() {
        SkipList<String, Point> list = new SkipList<>();
        assertEquals(0, list.size());
        assertFalse(list.iterator().hasNext());
        assertTrue(list.remove("missing") == null);
        assertTrue(list.removeByValue(new Point(1, 1)) == null);

        list.insert(new KVPair<>("charlie", new Point("charlie", 3, 3)));
        list.insert(new KVPair<>("alpha", new Point("alpha", 1, 1)));
        list.insert(new KVPair<>("bravo", new Point("bravo", 2, 2)));
        assertEquals(3, list.size());
        assertEquals(1, list.search("bravo").size());
        assertEquals(0, list.search("missing").size());

        java.util.Iterator<KVPair<String, Point>> it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("alpha", it.next().key());
        assertEquals("bravo", it.next().key());
        assertEquals("charlie", it.next().key());
        assertFalse(it.hasNext());

        assertEquals("bravo", list.remove("bravo").key());
        assertEquals(2, list.size());
        assertEquals("alpha", list.removeByValue(
            new Point(1, 1)).key());
        assertEquals(1, list.size());
        assertTrue(list.remove("bravo") == null);
    }


    /**
     * Checks removing exact and equivalent points from a leaf.
     */
    public void testLeafRemovalPaths() {
        LeafNode leaf = new LeafNode();
        Point first = new Point("a", 10, 10);
        Point second = new Point("b", 10, 10);
        leaf.insert(first, 0, 0, 1024);
        leaf.insert(second, 0, 0, 1024);

        assertTrue(leaf.remove(first, 0, 0, 1024) == leaf);
        assertEquals(1, leaf.getPoints().size());
        assertTrue(leaf.getPoints().get(0) == second);

        // Equal names and coordinates can remove a different instance.
        assertTrue(leaf.remove(new Point("b", 10, 10),
            0, 0, 1024) instanceof EmptyNode);
    }


    /**
     * Checks the leaf boundary inclusion rules and returned visit count.
     */
    public void testLeafRegionBoundaries() {
        LeafNode leaf = new LeafNode();
        leaf.insert(new Point("first", 10, 10), 0, 0, 1024);
        leaf.insert(new Point("right", 20, 10), 0, 0, 1024);
        leaf.insert(new Point("bottom", 10, 20), 0, 0, 1024);

        systemOut().clearHistory();
        assertEquals(1, leaf.regionsearch(
            10, 10, 10, 10, 0, 0, 1024));
        String out = systemOut().getHistory();
        assertTrue(out.contains("Point found: (first, 10, 10)"));
        assertFalse(out.contains("Point found: (right, 20, 10)"));
        assertFalse(out.contains("Point found: (bottom, 10, 20)"));
    }


    /**
     * Checks that deeper internal nodes are retained until they can merge.
     */
    public void testDeepInternalMerging() {
        QuadTree tree = new QuadTree();
        Point a = new Point("a", 10, 10);
        Point b = new Point("b", 11, 11);
        Point c = new Point("c", 12, 12);
        Point d = new Point("d", 13, 13);
        Point e = new Point("e", 900, 900);
        tree.insert(a);
        tree.insert(b);
        tree.insert(c);
        tree.insert(d);
        tree.insert(e);
        systemOut().clearHistory();
        tree.dump();
        assertTrue(systemOut().getHistory().contains("Internal"));

        tree.remove(e);
        tree.remove(d);
        systemOut().clearHistory();
        tree.dump();
        String out = systemOut().getHistory();
        assertTrue(out.contains("(a, 10, 10)"));
        assertTrue(out.contains("(b, 11, 11)"));
        assertTrue(out.contains("(c, 12, 12)"));
        assertTrue(out.contains("1 quadtree nodes printed"));
    }


    /**
     * Checks nonmatching name removal and size after duplicate-key inserts.
     */
    public void testSkipListDuplicateKeysAndIterator() {
        SkipList<String, Point> list = new SkipList<>();
        list.insert(new KVPair<>("a", new Point("a", 1, 1)));
        list.insert(new KVPair<>("a", new Point("a", 2, 2)));
        list.insert(new KVPair<>("b", new Point("b", 3, 3)));
        assertEquals(3, list.size());
        assertEquals(2, list.search("a").size());
        assertEquals("a", list.remove("a").key());
        assertEquals(1, list.search("a").size());
        assertEquals(2, list.size());
        assertTrue(list.removeByValue(new Point(99, 99)) == null);
        assertEquals(2, list.size());
    }


    /**
     * Exercises command parsing with whitespace and invalid numbers.
     */
    public void testCommandProcessorBranches() {
        CommandProcessor cp = new CommandProcessor();
        cp.processor("   ");
        cp.processor("  insert    abc   10   20  ");
        systemOut().clearHistory();
        cp.processor("search abc");
        assertTrue(systemOut().getHistory().contains(
            "Found (abc, 10, 20)"));

        systemOut().clearHistory();
        cp.processor("regionsearch 0 0 0 20");
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, 0, 20)"));

        systemOut().clearHistory();
        cp.processor("remove 10 20");
        assertTrue(systemOut().getHistory().contains(
            "Point removed: (abc, 10, 20)"));

        systemOut().clearHistory();
        cp.processor("insert abc notANumber 20");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));
    }

}
