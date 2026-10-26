import java.util.Iterator;
import java.util.NoSuchElementException;
import student.TestCase;
import student.TestableRandom;

/**
 * Complete test suite targeting all branch conditions and mutants.
 * 
 * @author Vinh Chau, Mahro Kim
 * @version 2026-10-26
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests PointsProject main method argument and file guards.
     * 
     * @throws Exception
     *             if an error occurs
     */
    public void testMainMethodGuards() throws Exception {
        PointsProject.main(null);
        PointsProject.main(new String[] {});
        PointsProject.main(new String[] { "a", "b" });
        assertTrue(systemOut().getHistory().contains(
            "Usage: PointsProject <command-file>"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "nonexistent_file_xyz.txt" });
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "src" });
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        assertTrue(systemOut().getHistory().contains("SkipList dump:"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "SyntaxTest2.txt" });
        assertTrue(systemOut().getHistory().contains("13 quadtree nodes"));
    }


    /**
     * Tests CommandProcessor guards and argument validations.
     */
    public void testCommandProcessorBranches() {
        CommandProcessor cp = new CommandProcessor();

        // Null and empty lines
        cp.processor(null);
        cp.processor("");
        cp.processor("   ");

        // Invalid argument counts for every command
        cp.processor("insert p 1");
        cp.processor("insert p 1 2 3");
        cp.processor("remove");
        cp.processor("remove a b c d");
        cp.processor("regionsearch 0 0 10");
        cp.processor("regionsearch 0 0 10 10 10");
        cp.processor("duplicates extra");
        cp.processor("search");
        cp.processor("search a b");
        cp.processor("dump extra");
        cp.processor("unknownCommand");

        // NumberFormatException
        systemOut().clearHistory();
        cp.processor("insert p bad 20");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));

        systemOut().clearHistory();
        cp.processor("remove bad 20");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));

        systemOut().clearHistory();
        cp.processor("regionsearch bad 0 10 10");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));
    }


    /**
     * Tests Database validations and search misses.
     */
    public void testDatabaseBranches() {
        Database db = new Database();

        // Search miss
        systemOut().clearHistory();
        db.search("missing");
        assertTrue(systemOut().getHistory().contains(
            "Point not found: missing"));

        // Remove by coords: rejected and not found
        systemOut().clearHistory();
        db.remove(-1, 50);
        assertTrue(systemOut().getHistory().contains(
            "Point rejected: (-1, 50)"));

        systemOut().clearHistory();
        db.remove(50, -1);
        assertTrue(systemOut().getHistory().contains(
            "Point rejected: (50, -1)"));

        systemOut().clearHistory();
        db.remove(50, 50);
        assertTrue(systemOut().getHistory().contains(
            "Point not found: (50, 50)"));

        // Regionsearch rejections (w <= 0 and h <= 0)
        systemOut().clearHistory();
        db.regionsearch(0, 0, 0, 10);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, 0, 10)"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, 10, 0);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, 10, 0)"));
    }


    /**
     * Tests Point and KVPair methods and equals branch permutations.
     */
    public void testPointAndKVPairBranches() {
        Point p = new Point("a", 10, 20);
        assertEquals("a", p.getName());
        assertEquals(10, p.getX());
        assertEquals(20, p.getY());
        assertEquals("(a, 10, 20)", p.toString());
        assertTrue(p.hashCode() != 0);

        // Equals permutations (short-circuiting branches)
        assertTrue(p.equals(p));
        assertTrue(p.equals(new Point("diffName", 10, 20)));
        assertFalse(p.equals(null));
        assertFalse(p.equals("Not a Point"));
        assertFalse(p.equals(new Point("a", 99, 20))); // x differs
        assertFalse(p.equals(new Point("a", 10, 99))); // y differs

        // Invalid coordinate bounds
        assertFalse(p.isInvalid());
        assertTrue(new Point("p", -1, 0).isInvalid());
        assertTrue(new Point("p", 0, -1).isInvalid());
        assertTrue(new Point("p", 1024, 0).isInvalid());
        assertTrue(new Point("p", 0, 1024).isInvalid());

        // KVPair
        KVPair<String, Point> pair1 = new KVPair<>("k1", p);
        KVPair<String, Point> pair2 = new KVPair<>("k2", p);
        assertEquals("k1", pair1.key());
        assertEquals(p, pair1.value());
        assertEquals("k1, (a, 10, 20)", pair1.toString());
        assertEquals(0, pair1.compareTo("k1"));
        assertTrue(pair1.compareTo(pair2) < 0);
        assertTrue(pair2.compareTo(pair1) > 0);
    }


    /**
     * Tests SkipList iterator, random level, and deletion branches.
     */
    public void testSkipListBranches() {
        SkipList<String, Point> sl = new SkipList<>();
        assertEquals(0, sl.size());

        // Adjust head level 1 -> 2
        TestableRandom.setNextBooleans(false);
        sl.insert(new KVPair<>("b", new Point("b", 2, 2)));
        TestableRandom.setNextBooleans(true, false);
        sl.insert(new KVPair<>("a", new Point("a", 1, 1)));
        assertEquals(2, sl.size());

        // AdjustHead early return (newLevel <= head.level)
        sl.adjustHead(1);

        // Search found and missing
        assertEquals(1, sl.search("a").size());
        assertEquals(0, sl.search("z").size());

        // Iterator
        Iterator<KVPair<String, Point>> it = sl.iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next().key());
        assertTrue(it.hasNext());
        assertEquals("b", it.next().key());
        assertFalse(it.hasNext());
        try {
            it.next();
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e) {
            assertNotNull(e);
        }

        // Removals (found and missing)
        assertNull(sl.remove("missing"));
        assertNull(sl.removeByValue(new Point("missing", 99, 99)));
        assertNotNull(sl.remove("a"));
        assertNotNull(sl.removeByValue(new Point("b", 2, 2)));
        assertEquals(0, sl.size());

        systemOut().clearHistory();
        sl.dump();
        assertTrue(systemOut().getHistory().contains("SkipList size is: 0"));
    }


    /**
     * Tests LeafNode split conditions, removals, and indentations.
     */
    public void testLeafNodeBranches() {
        LeafNode leaf = new LeafNode();
        Point p1 = new Point("p1", 10, 10);
        Point p2 = new Point("p2", 20, 20);
        Point p3 = new Point("p3", 30, 30);
        Point p4 = new Point("p4", 40, 40);

        leaf.insert(p1, 0, 0, 1024);
        leaf.insert(p2, 0, 0, 1024);
        leaf.insert(p3, 0, 0, 1024);

        // Dump with indentation (level > 0)
        systemOut().clearHistory();
        assertEquals(1, leaf.dump(0, 0, 1024, 2));
        assertTrue(systemOut().getHistory().contains("  Node at 0, 0, 1024:"));

        // LeafNode removal: not empty after removal vs empty after removal
        QuadNode res = leaf.remove(new Point("p1", 10, 10), 0, 0, 1024);
        assertTrue(res instanceof LeafNode);
        assertEquals(2, ((LeafNode)res).getPoints().size());

        leaf.remove(p2, 0, 0, 1024);
        QuadNode emptyRes = leaf.remove(p3, 0, 0, 1024);
        assertTrue(emptyRes instanceof EmptyNode);

        // Split with distinct coordinates
        LeafNode splittable = new LeafNode();
        splittable.insert(p1, 0, 0, 1024);
        splittable.insert(p2, 0, 0, 1024);
        splittable.insert(p3, 0, 0, 1024);
        QuadNode splitRes = splittable.insert(p4, 0, 0, 1024);
        assertTrue(splitRes instanceof InternalNode);

        // Size > 3 with identical coordinates (does not split)
        LeafNode dupLeaf = new LeafNode();
        dupLeaf.insert(new Point("d1", 5, 5), 0, 0, 1024);
        dupLeaf.insert(new Point("d2", 5, 5), 0, 0, 1024);
        dupLeaf.insert(new Point("d3", 5, 5), 0, 0, 1024);
        QuadNode noSplitRes = dupLeaf.insert(new Point("d4", 5, 5), 0, 0, 1024);
        assertTrue(noSplitRes instanceof LeafNode);
    }


    /**
     * Tests InternalNode insertion, removal, regionsearch, and merges.
     */
    public void testInternalNodeBranches() {
        InternalNode in = new InternalNode();
        Point nw = new Point("nw", 100, 100);
        Point ne = new Point("ne", 700, 100);
        Point sw = new Point("sw", 100, 700);
        Point se = new Point("se", 700, 700);

        // Insert into each quadrant
        in.insert(nw, 0, 0, 1024);
        in.insert(ne, 0, 0, 1024);
        in.insert(sw, 0, 0, 1024);
        in.insert(se, 0, 0, 1024);

        // Dump indentation level > 0
        systemOut().clearHistory();
        assertEquals(5, in.dump(0, 0, 1024, 1));
        assertTrue(systemOut().getHistory().contains(" Node at 0, 0, 1024:"));

        // Regionsearch visiting specific children
        assertEquals(2, in.regionsearch(0, 0, 200, 200, 0, 0, 1024));
        assertEquals(2, in.regionsearch(600, 0, 200, 200, 0, 0, 1024));
        assertEquals(2, in.regionsearch(0, 600, 200, 200, 0, 0, 1024));
        assertEquals(2, in.regionsearch(600, 600, 200, 200, 0, 0, 1024));

        // Removals across quadrants
        in.remove(nw, 0, 0, 1024); // 3 points remain -> merges to LeafNode
        in.remove(ne, 0, 0, 1024);
        in.remove(sw, 0, 0, 1024);
        QuadNode allRemoved = in.remove(se, 0, 0, 1024);
        assertTrue(allRemoved instanceof EmptyNode);

        // Merge when remaining points share identical coordinates (> 3 points)
        Database db = new Database();
        db.insert("c1", 50, 50);
        db.insert("c2", 50, 50);
        db.insert("c3", 50, 50);
        db.insert("c4", 50, 50);
        db.insert("diff", 800, 800); // Forces InternalNode
        db.remove("diff"); // Leaves 4 duplicate points; forces Leaf merge
        systemOut().clearHistory();
        db.dump();
        assertFalse(systemOut().getHistory().contains("Internal"));
    }


    /**
     * Tests EmptyNode methods directly.
     */
    public void testEmptyNodeDirect() {
        EmptyNode empty = EmptyNode.getInstance();
        assertTrue(empty.remove(new Point("a", 0, 0), 0, 0, 1024) == empty);
        assertEquals(1, empty.regionsearch(0, 0, 10, 10, 0, 0, 1024));
        systemOut().clearHistory();
        assertEquals(1, empty.dump(0, 0, 1024, 0));
        assertTrue(systemOut().getHistory().contains(
            "Node at 0, 0, 1024: Empty"));
    }


    /**
     * Precision assertions targeting surviving mutants in InternalNode,
     * LeafNode, and QuadTree by validating exact return types and sizes.
     */
    public void testSlayRemainingMutants() {
        // 1. Kill InternalNode quadrant-routing and regionsearch mutants
        InternalNode in = new InternalNode();
        Point pNW = new Point("nw", 100, 100);
        Point pNE = new Point("ne", 700, 100);
        Point pSW = new Point("sw", 100, 700);
        Point pSE = new Point("se", 700, 700);

        assertSame(in, in.insert(pNW, 0, 0, 1024));
        assertSame(in, in.insert(pNE, 0, 0, 1024));
        assertSame(in, in.insert(pSW, 0, 0, 1024));
        assertSame(in, in.insert(pSE, 0, 0, 1024));

        // Exact isolated single-quadrant visits: Internal (1) + Child (1) = 2
        assertEquals(2, in.regionsearch(50, 50, 100, 100, 0, 0, 1024));
        assertEquals(2, in.regionsearch(650, 50, 100, 100, 0, 0, 1024));
        assertEquals(2, in.regionsearch(50, 650, 100, 100, 0, 0, 1024));
        assertEquals(2, in.regionsearch(650, 650, 100, 100, 0, 0, 1024));

        // Two-quadrant spans: Internal (1) + 2 Children (2) = 3
        assertEquals(3, in.regionsearch(50, 50, 700, 100, 0, 0, 1024));
        assertEquals(3, in.regionsearch(50, 50, 100, 700, 0, 0, 1024));

        // 2. Kill InternalNode removal mutants (non-merging vs merging)
        InternalNode complex = new InternalNode();
        for (int i = 0; i < 4; i++) {
            complex.insert(new Point("nw" + i, 10 + i, 10 + i), 0, 0, 1024);
        }
        complex.insert(pNE, 0, 0, 1024);
        // Removing pNE leaves NW as an InternalNode, so it must NOT merge
        QuadNode noMerge = complex.remove(pNE, 0, 0, 1024);
        assertTrue(noMerge instanceof InternalNode);

        // 3. Kill LeafNode coordinate and name match mutants
        LeafNode leaf = new LeafNode();
        Point base = new Point("target", 40, 40);
        assertSame(leaf, leaf.insert(base, 0, 0, 1024));

        // Same coord, different name -> should NOT remove
        assertSame(leaf, leaf.remove(new Point("wrong", 40, 40), 0, 0, 1024));
        assertEquals(1, leaf.getPoints().size());

        // Same name, different X or Y -> should NOT remove
        assertSame(leaf, leaf.remove(new Point("target", 99, 40), 0, 0, 1024));
        assertSame(leaf, leaf.remove(new Point("target", 40, 99), 0, 0, 1024));
        assertEquals(1, leaf.getPoints().size());

        // Coordinate-only removal (empty name) -> matches and empties
        QuadNode drained = leaf.remove(new Point(40, 40), 0, 0, 1024);
        assertSame(EmptyNode.getInstance(), drained);

        // 4. Exercise QuadTree class directly
        QuadTree qt = new QuadTree();
        Point q1 = new Point("q1", 25, 25);
        Point q2 = new Point("q2", 25, 25);
        qt.insert(q1);
        qt.insert(q2);

        systemOut().clearHistory();
        qt.duplicates();
        assertTrue(systemOut().getHistory().contains("(25, 25)"));

        qt.remove(q1);
        qt.remove(q2);
        systemOut().clearHistory();
        qt.dump();
        assertTrue(systemOut().getHistory().contains(
            "Node at 0, 0, 1024: Empty"));
    }
}
