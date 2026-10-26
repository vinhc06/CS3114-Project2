import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        assertTrue(systemOut().getHistory().contains(
            "Usage: PointsProject <command-file>"));

        systemOut().clearHistory();
        PointsProject.main(new String[] {});
        assertTrue(systemOut().getHistory().contains(
            "Usage: PointsProject <command-file>"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "a", "b" });
        assertTrue(systemOut().getHistory().contains(
            "Usage: PointsProject <command-file>"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "nonexistent_file_xyz.txt" });
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file as |nonexistent_file_xyz.txt|"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "src" });
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file as |src|"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        String out1 = systemOut().getHistory();
        assertTrue(out1.contains("Point rejected: (p_p, -1, -20)"));
        assertTrue(out1.contains("SkipList dump:"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "SyntaxTest2.txt" });
        String out2 = systemOut().getHistory();
        assertTrue(out2.contains("Point inserted: (p_p, 1, 20)"));
        assertTrue(out2.contains("13 quadtree nodes printed"));
    }


    /**
     * Tests CommandProcessor valid and invalid branches with strict output
     * assertions.
     */
    public void testCommandProcessorBranches() {
        CommandProcessor cp = new CommandProcessor();

        // 1. Null and empty lines produce NO output
        systemOut().clearHistory();
        cp.processor(null);
        cp.processor("");
        cp.processor("   ");
        assertEquals("", systemOut().getHistory());

        // 2. Wrong argument counts produce NO output (and NOT Unrecognized)
        cp.processor("insert p 1");
        cp.processor("insert p 1 2 3");
        cp.processor("remove");
        cp.processor("remove a b c");
        cp.processor("regionsearch 0 0 10");
        cp.processor("regionsearch 0 0 10 10 10");
        cp.processor("duplicates extra");
        cp.processor("search");
        cp.processor("search a b");
        cp.processor("dump extra");
        assertEquals("", systemOut().getHistory());

        // 3. Unrecognized command
        cp.processor("unknownCommand");
        assertTrue(systemOut().getHistory().contains("Unrecognized command."));

        // 4. Valid commands through CommandProcessor
        systemOut().clearHistory();
        cp.processor("  insert   p1   10   20  ");
        assertTrue(systemOut().getHistory().contains(
            "Point inserted: (p1, 10, 20)"));

        systemOut().clearHistory();
        cp.processor("insert p2 10 20");
        cp.processor("duplicates");
        assertTrue(systemOut().getHistory().contains("(10, 20)"));

        systemOut().clearHistory();
        cp.processor("search p1");
        assertTrue(systemOut().getHistory().contains("Found (p1, 10, 20)"));

        systemOut().clearHistory();
        cp.processor("regionsearch 0 0 50 50");
        assertTrue(systemOut().getHistory().contains(
            "Point found: (p1, 10, 20)"));

        systemOut().clearHistory();
        cp.processor("dump");
        assertTrue(systemOut().getHistory().contains("SkipList size is: 2"));

        systemOut().clearHistory();
        cp.processor("remove p1");
        assertTrue(systemOut().getHistory().contains(
            "Point removed: (p1, 10, 20)"));

        systemOut().clearHistory();
        cp.processor("remove 10 20");
        assertTrue(systemOut().getHistory().contains(
            "Point removed: (p2, 10, 20)"));

        // 5. NumberFormatExceptions
        systemOut().clearHistory();
        cp.processor("insert p bad 20");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));

        systemOut().clearHistory();
        cp.processor("remove 10 bad");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));

        systemOut().clearHistory();
        cp.processor("regionsearch 0 0 bad 10");
        assertTrue(systemOut().getHistory().contains(
            "Invalid command arguments."));
    }


    /**
     * Tests Database validations, boundary conditions, and operations.
     */
    public void testDatabaseBranches() {
        Database db = new Database();

        // Boundary insertions: 0 and 1023 valid, -1 and 1024 invalid
        systemOut().clearHistory();
        db.insert("min", 0, 0);
        assertTrue(systemOut().getHistory().contains(
            "Point inserted: (min, 0, 0)"));

        systemOut().clearHistory();
        db.insert("max", 1023, 1023);
        assertTrue(systemOut().getHistory().contains(
            "Point inserted: (max, 1023, 1023)"));

        systemOut().clearHistory();
        db.insert("badX", -1, 0);
        assertTrue(systemOut().getHistory().contains(
            "Point rejected: (badX, -1, 0)"));

        systemOut().clearHistory();
        db.insert("badY", 0, 1024);
        assertTrue(systemOut().getHistory().contains(
            "Point rejected: (badY, 0, 1024)"));

        // Search found and miss
        systemOut().clearHistory();
        db.search("min");
        assertTrue(systemOut().getHistory().contains("Found (min, 0, 0)"));

        systemOut().clearHistory();
        db.search("missing");
        assertTrue(systemOut().getHistory().contains(
            "Point not found: missing"));

        // Remove by name: miss and hit
        systemOut().clearHistory();
        db.remove("missing");
        assertTrue(systemOut().getHistory().contains(
            "Point not removed: missing"));

        systemOut().clearHistory();
        db.remove("min");
        assertTrue(systemOut().getHistory().contains(
            "Point removed: (min, 0, 0)"));
        db.search("min");
        assertTrue(systemOut().getHistory().contains("Point not found: min"));

        // Remove by coords: rejected, not found, and hit
        systemOut().clearHistory();
        db.remove(-1, 50);
        assertTrue(systemOut().getHistory().contains(
            "Point rejected: (-1, 50)"));

        systemOut().clearHistory();
        db.remove(50, 1024);
        assertTrue(systemOut().getHistory().contains(
            "Point rejected: (50, 1024)"));

        systemOut().clearHistory();
        db.remove(50, 50);
        assertTrue(systemOut().getHistory().contains(
            "Point not found: (50, 50)"));

        systemOut().clearHistory();
        db.remove(1023, 1023);
        assertTrue(systemOut().getHistory().contains(
            "Point removed: (max, 1023, 1023)"));

        // Regionsearch rejections: w <= 0, h <= 0, vs w = 1, h = 1
        systemOut().clearHistory();
        db.regionsearch(0, 0, 0, 10);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, 0, 10)"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, -1, 10);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, -1, 10)"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, 10, 0);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, 10, 0)"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, 10, -1);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, 10, -1)"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, 1, 1);
        assertTrue(systemOut().getHistory().contains(
            "Points intersecting region (0, 0, 1, 1):"));
    }


    /**
     * Tests Point and KVPair methods, exact math in hashCode, and bounds.
     */
    public void testPointAndKVPairBranches() {
        Point p = new Point("a", 10, 20);
        assertEquals("a", p.getName());
        assertEquals(10, p.getX());
        assertEquals(20, p.getY());
        assertEquals("(a, 10, 20)", p.toString());

        // Exact hashCode formula check kills 31 * x + y arithmetic mutants
        assertEquals(31 * Integer.hashCode(10) + Integer.hashCode(20), p
            .hashCode());

        // Equals permutations
        assertTrue(p.equals(p));
        assertTrue(p.equals(new Point("diffName", 10, 20)));
        assertFalse(p.equals(null));
        assertFalse(p.equals("Not a Point"));
        assertFalse(p.equals(new Point("a", 99, 20)));
        assertFalse(p.equals(new Point("a", 10, 99)));

        // Exact boundary checks on isInvalid: 0 and 1023 vs -1 and 1024
        assertFalse(new Point("p", 0, 0).isInvalid());
        assertFalse(new Point("p", 0, 1023).isInvalid());
        assertFalse(new Point("p", 1023, 0).isInvalid());
        assertFalse(new Point("p", 1023, 1023).isInvalid());
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
        assertEquals(0, pair1.compareTo(new KVPair<>("k1", p)));
        assertTrue(pair1.compareTo(pair2) < 0);
        assertTrue(pair2.compareTo(pair1) > 0);
        assertTrue(pair1.compareTo("k2") < 0);
        assertTrue(pair2.compareTo("k1") > 0);
    }


    /**
     * Tests SkipList multi-level traversal, search, remove, and removeByValue.
     */
    public void testSkipListBranches() {
        SkipList<String, Point> sl = new SkipList<>();
        assertEquals(0, sl.size());

        // Insert multiple items with varying heights to exercise multi-level
        // forward pointers in search, insert, remove, and removeByValue
        TestableRandom.setNextBooleans(false); // level 1
        sl.insert(new KVPair<>("d", new Point("d", 4, 4)));

        TestableRandom.setNextBooleans(true, true, false); // level 3
        sl.insert(new KVPair<>("b", new Point("b", 2, 2)));

        TestableRandom.setNextBooleans(true, false); // level 2
        sl.insert(new KVPair<>("f", new Point("f", 6, 6)));

        TestableRandom.setNextBooleans(false); // level 1
        sl.insert(new KVPair<>("a", new Point("a", 1, 1)));

        TestableRandom.setNextBooleans(true, false); // level 2
        sl.insert(new KVPair<>("c", new Point("c", 3, 3)));

        TestableRandom.setNextBooleans(true, true, false); // level 3
        sl.insert(new KVPair<>("e", new Point("e", 5, 5)));

        TestableRandom.setNextBooleans(false); // level 1 (duplicate key)
        sl.insert(new KVPair<>("c", new Point("c", 30, 30)));

        assertEquals(7, sl.size());

        // AdjustHead early return when newLevel <= head.level
        sl.adjustHead(2);
        sl.adjustHead(3);

        // Multi-level searches: beginning, middle, end, duplicate, and misses
        assertEquals(1, sl.search("a").size());
        assertEquals(1, sl.search("b").size());
        assertEquals(2, sl.search("c").size());
        assertEquals(1, sl.search("d").size());
        assertEquals(1, sl.search("e").size());
        assertEquals(1, sl.search("f").size());
        assertEquals(0, sl.search("0").size()); // before head
        assertEquals(0, sl.search("cmiss").size()); // middle miss
        assertEquals(0, sl.search("z").size()); // after tail

        // Dump verification with nodes present
        systemOut().clearHistory();
        sl.dump();
        String dumpOut = systemOut().getHistory();
        assertTrue(dumpOut.contains("Node has depth 3, Value (null)"));
        assertTrue(dumpOut.contains("Node has depth 3, Value (b, 2, 2)"));
        assertTrue(dumpOut.contains("SkipList size is: 7"));

        // Iterator verification
        Iterator<KVPair<String, Point>> it = sl.iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next().key());
        assertEquals("b", it.next().key());
        assertEquals("c", it.next().key());
        assertEquals("c", it.next().key());
        assertEquals("d", it.next().key());
        assertEquals("e", it.next().key());
        assertEquals("f", it.next().key());
        assertFalse(it.hasNext());
        try {
            it.next();
            fail("Expected NoSuchElementException");
        }
        catch (NoSuchElementException e) {
            assertNotNull(e);
        }

        // Remove misses (before first, middle, after last)
        assertNull(sl.remove("0"));
        assertNull(sl.remove("cmiss"));
        assertNull(sl.remove("z"));
        assertEquals(7, sl.size());

        // RemoveByValue misses and multi-level hits
        assertNull(sl.removeByValue(new Point("missing", 99, 99)));
        // Remove high-level node 'e' (level 3) by value
        KVPair<String, Point> remE = sl.removeByValue(new Point("e", 5, 5));
        assertNotNull(remE);
        assertEquals("e", remE.key());
        assertEquals(6, sl.size());
        assertEquals(0, sl.search("e").size());
        // Verify forward pointers after 'e' still reach 'f'
        assertEquals(1, sl.search("f").size());

        // Remove high-level node 'b' (level 3) by key
        KVPair<String, Point> remB = sl.remove("b");
        assertNotNull(remB);
        assertEquals("b", remB.key());
        assertEquals(5, sl.size());
        assertEquals(0, sl.search("b").size());
        assertEquals(2, sl.search("c").size());

        // Remove remaining nodes
        assertNotNull(sl.remove("a"));
        assertNotNull(sl.remove("c"));
        assertNotNull(sl.remove("c"));
        assertNotNull(sl.removeByValue(new Point("d", 4, 4)));
        assertNotNull(sl.remove("f"));
        assertEquals(0, sl.size());
    }


    /**
     * Tests LeafNode split conditions, size=1 guard, regionsearch boundaries,
     * and removals.
     */
    public void testLeafNodeBranches() {
        // 1. Test size == 1 guard (4 distinct points at size 1 must NOT split)
        LeafNode tinyLeaf = new LeafNode();
        tinyLeaf.insert(new Point("p1", 0, 0), 0, 0, 1);
        tinyLeaf.insert(new Point("p2", 0, 1), 0, 0, 1);
        tinyLeaf.insert(new Point("p3", 1, 0), 0, 0, 1);
        QuadNode tinyRes = tinyLeaf.insert(new Point("p4", 1, 1), 0, 0, 1);
        assertSame(tinyLeaf, tinyRes);
        assertEquals(4, tinyLeaf.getPoints().size());

        // 2. Test hasDistinctCoordinates when only X differs vs only Y differs
        LeafNode diffYLeaf = new LeafNode();
        diffYLeaf.insert(new Point("a", 10, 10), 0, 0, 1024);
        diffYLeaf.insert(new Point("b", 10, 10), 0, 0, 1024);
        diffYLeaf.insert(new Point("c", 10, 10), 0, 0, 1024);
        // Same X (10), different Y (20) -> must split!
        QuadNode splitY = diffYLeaf.insert(new Point("d", 10, 20), 0, 0, 1024);
        assertTrue(splitY instanceof InternalNode);

        LeafNode diffXLeaf = new LeafNode();
        diffXLeaf.insert(new Point("a", 10, 10), 0, 0, 1024);
        diffXLeaf.insert(new Point("b", 10, 10), 0, 0, 1024);
        diffXLeaf.insert(new Point("c", 10, 10), 0, 0, 1024);
        // Different X (20), same Y (10) -> must split!
        QuadNode splitX = diffXLeaf.insert(new Point("d", 20, 10), 0, 0, 1024);
        assertTrue(splitX instanceof InternalNode);

        // 3. Test exact regionsearch rectangle boundaries in LeafNode
        // Region: (100, 200, 50, 60) -> X in [100, 150], Y in [200, 260]
        LeafNode searchLeaf = new LeafNode();
        searchLeaf.insert(new Point("inMin", 100, 200), 0, 0, 1024);
        searchLeaf.insert(new Point("inMax", 150, 260), 0, 0, 1024);
        searchLeaf.insert(new Point("outLeft", 99, 220), 0, 0, 1024);
        assertEquals(3, searchLeaf.getPoints().size());

        systemOut().clearHistory();
        assertEquals(1, searchLeaf.regionsearch(100, 200, 50, 60, 0, 0, 1024));
        String sOut = systemOut().getHistory();
        assertTrue(sOut.contains("(inMin, 100, 200)"));
        assertTrue(sOut.contains("(inMax, 150, 260)"));
        assertFalse(sOut.contains("outLeft"));

        LeafNode searchLeaf2 = new LeafNode();
        searchLeaf2.insert(new Point("outRight", 151, 220), 0, 0, 1024);
        searchLeaf2.insert(new Point("outTop", 120, 199), 0, 0, 1024);
        searchLeaf2.insert(new Point("outBot", 120, 261), 0, 0, 1024);
        systemOut().clearHistory();
        assertEquals(1, searchLeaf2.regionsearch(100, 200, 50, 60, 0, 0, 1024));
        assertEquals("", systemOut().getHistory());

        // 4. Test dump with indentation level 2 (verifies exact spaces)
        systemOut().clearHistory();
        assertEquals(1, searchLeaf.dump(128, 256, 64, 2));
        String dOut = systemOut().getHistory();
        assertTrue(dOut.contains("    Node at 128, 256, 64:\n    (inMin"));
    }


    /**
     * Tests InternalNode child InternalNode merge guard directly.
     */
    public void testInternalNodeChildGuard() {
        InternalNode parent = new InternalNode();
        Point p1 = new Point("p1", 10, 10);
        parent.insert(p1, 0, 0, 1024);

        // Force NW to become an InternalNode by inserting 4 distinct points
        // into NW
        parent.insert(new Point("a1", 10, 10), 0, 0, 1024);
        parent.insert(new Point("a2", 20, 20), 0, 0, 1024);
        parent.insert(new Point("a3", 30, 30), 0, 0, 1024);
        parent.insert(new Point("a4", 40, 40), 0, 0, 1024);

        // NW is now an InternalNode. Removing p1 should NOT merge parent
        // because NW is Internal.
        QuadNode result = parent.remove(p1, 0, 0, 1024);
        assertTrue(result instanceof InternalNode);
    }


    /**
     * Tests EmptyNode and QuadTree directly.
     */
    public void testEmptyNodeAndQuadTree() {
        EmptyNode empty = EmptyNode.getInstance();
        assertSame(empty, empty.remove(new Point("a", 0, 0), 0, 0, 1024));
        assertEquals(1, empty.regionsearch(0, 0, 10, 10, 0, 0, 1024));
        List<Point> list = new ArrayList<>();
        empty.collectPoints(list);
        assertEquals(0, list.size());

        systemOut().clearHistory();
        assertEquals(1, empty.dump(64, 128, 32, 2));
        assertTrue(systemOut().getHistory().contains(
            "    Node at 64, 128, 32: Empty"));

        QuadTree qt = new QuadTree();
        Point q1 = new Point("q1", 25, 25);
        Point q2 = new Point("q2", 25, 25);
        Point q3 = new Point("q3", 700, 700);
        qt.insert(q1);
        qt.insert(q2);
        qt.insert(q3);

        systemOut().clearHistory();
        qt.duplicates();
        String dupOut = systemOut().getHistory();
        assertTrue(dupOut.contains("Duplicate points:"));
        assertTrue(dupOut.contains("(25, 25)"));
        assertFalse(dupOut.contains("(700, 700)"));

        systemOut().clearHistory();
        qt.regionsearch(0, 0, 50, 50);
        String regOut = systemOut().getHistory();
        assertTrue(regOut.contains(
            "Points intersecting region (0, 0, 50, 50):"));
        assertTrue(regOut.contains("Point found: (q1, 25, 25)"));
        assertTrue(regOut.contains("1 quadtree nodes visited"));

        qt.remove(q1);
        qt.remove(q2);
        qt.remove(q3);
        systemOut().clearHistory();
        qt.dump();
        assertTrue(systemOut().getHistory().contains(
            "Node at 0, 0, 1024: Empty"));
    }
}
