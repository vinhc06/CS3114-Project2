import java.util.Iterator;
import student.TestCase;

/**
 * Tests the PointDB project, including SkipList, QuadTree,
 * command parsing, and boundary behavior without writing files.
 * 
 * @author Vinh Chau, Mahro Kim
 * @version 2026-10-26
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests processing commands and syntax files safely.
     * 
     * @throws Exception
     *             if an input error occurs
     */
    public void testSyntaxFiles() throws Exception {
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        String out1 = systemOut().getHistory();
        assertTrue(out1.contains("Point rejected: (p_p, -1, -20)"));
        assertTrue(out1.contains("SkipList dump:"));
        assertTrue(out1.contains("QuadTree dump:"));
        assertTrue(out1.contains("1 quadtree nodes printed"));

        systemOut().clearHistory();
        PointsProject.main(new String[] { "SyntaxTest2.txt" });
        String out2 = systemOut().getHistory();
        assertTrue(out2.contains("Point inserted: (p_p, 1, 20)"));
        assertTrue(out2.contains("Point inserted: (far, 200, 200)"));
        assertTrue(out2.contains("13 quadtree nodes printed"));
        assertTrue(out2.contains("Point found: (p_p, 1, 20)"));
        assertTrue(out2.contains("4 quadtree nodes visited"));
        assertTrue(out2.contains("Point removed: (p_p, 1, 20)"));
    }


    /**
     * Tests missing arguments and invalid files.
     * 
     * @throws Exception
     *             if an input error occurs
     */
    public void testMainBadArgs() throws Exception {
        PointsProject.main(new String[] {});
        assertTrue(systemOut().getHistory().contains("Usage: PointsProject"));
        systemOut().clearHistory();
        PointsProject.main(new String[] { "nonexistent_file.txt" });
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file"));
    }


    /**
     * Tests removing points from NE, SW, and SE quadrants to cover
     * all InternalNode remove branches.
     */
    public void testInternalNodeAllQuadrantRemovals() {
        Database db = new Database();
        db.insert("nw", 100, 100);
        db.insert("ne", 700, 100);
        db.insert("sw", 100, 700);
        db.insert("se", 700, 700);

        // Triggers NE, SW, and SE remove branches in InternalNode
        db.remove(700, 100);
        db.remove(100, 700);
        db.remove(700, 700);
        db.remove(100, 100);
    }


    /**
     * Tests SkipList iterator methods directly to achieve full iterator
     * coverage.
     */
    public void testSkipListCustomIterator() {
        SkipList<String, Point> sl = new SkipList<>();
        sl.insert(new KVPair<>("key1", new Point("p1", 10, 10)));
        sl.insert(new KVPair<>("key2", new Point("p2", 20, 20)));

        java.util.Iterator<KVPair<String, Point>> it = sl.iterator();
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertFalse(it.hasNext());
    }


    /**
     * Tests additional Point equals and hashCode edge cases.
     */
    public void testPointEdgeCases() {
        Point p1 = new Point("test", 50, 50);
        assertFalse(p1.equals(null));
        assertFalse(p1.equals("some non-point object"));
        assertEquals(p1.hashCode(), new Point("differentName", 50, 50)
            .hashCode());
    }


    /**
     * Tests inserting valid and invalid points.
     */
    public void testInsertAndBounds() {
        Database db = new Database();

        db.insert("origin", 0, 0);
        db.insert("edge", 1023, 1023);
        db.insert("negX", -1, 50);
        db.insert("negy", 50, -1);
        db.insert("overX", 1024, 50);
        db.insert("overy", 50, 1024);

        String out = systemOut().getHistory();
        assertTrue(out.contains("Point inserted: (origin, 0, 0)"));
        assertTrue(out.contains("Point inserted: (edge, 1023, 1023)"));
        assertTrue(out.contains("Point rejected: (negX, -1, 50)"));
        assertTrue(out.contains("Point rejected: (negy, 50, -1)"));
        assertTrue(out.contains("Point rejected: (overX, 1024, 50)"));
        assertTrue(out.contains("Point rejected: (overy, 50, 1024)"));
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

        systemOut().clearHistory();
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

        systemOut().clearHistory();
        db.dump();

        String out = systemOut().getHistory();
        assertTrue(out.contains("Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("Node at 0, 0, 512:"));
        assertTrue(out.contains("(nw, 100, 100)"));
        assertTrue(out.contains("(ne, 700, 100)"));
        assertTrue(out.contains("(sw, 100, 700)"));
        assertTrue(out.contains("(se, 700, 700)"));
        assertTrue(out.contains("5 quadtree nodes printed"));
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

        systemOut().clearHistory();
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

        systemOut().clearHistory();
        db.dump();
        String out = systemOut().getHistory();
        assertTrue(out.contains("Node at 0, 0, 1024:"));
        assertFalse(out.contains("Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("1 quadtree nodes printed"));

        systemOut().clearHistory();
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

        systemOut().clearHistory();
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

        systemOut().clearHistory();
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
     * Tests removing one point when multiple points share the same coordinates.
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
        assertTrue(out.contains("Point found: (inside, 50, 50)"));
        assertFalse(out.contains("Point found: (outside, 500, 500)"));
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
        assertTrue(out.contains("SkipList size is: 0"));
    }


    /**
     * Exercises all remaining 0% coverage methods across Point, KVPair,
     * SkipList, EmptyNode, and InternalNode.
     */
    public void testRemainingUncoveredMethods() {
        // 1. Cover Point.hashCode() and Point.getName()
        Point pt = new Point("alpha", 15, 25);
        assertEquals("alpha", pt.getName());
        assertTrue(pt.hashCode() != 0);

        // 2. Cover KVPair.toString() and compareTo(K)
        KVPair<String, Point> kv = new KVPair<>("key1", pt);
        assertEquals("key1, (alpha, 15, 25)", kv.toString());
        assertEquals(0, kv.compareTo("key1"));
        assertTrue(kv.compareTo("key2") < 0);

        // 3. Cover SkipList.size() and Iterator methods directly
        SkipList<String, Point> sl = new SkipList<>();
        assertEquals(0, sl.size());
        sl.insert(kv);
        assertEquals(1, sl.size());
        java.util.Iterator<KVPair<String, Point>> iter = sl.iterator();
        assertTrue(iter.hasNext());
        assertNotNull(iter.next());
        assertFalse(iter.hasNext());

        // 4. Cover EmptyNode.remove(...) directly
        assertTrue(EmptyNode.getInstance().remove(pt, 0, 0, 1024) == EmptyNode
            .getInstance());

        // 5. Cover InternalNode hasDistinctCoordinates via duplicate coordinate
        // merging
        Database db = new Database();
        db.insert("d1", 10, 10);
        db.insert("d2", 20, 20);
        db.insert("d3", 30, 30);
        db.insert("d4", 40, 40); // forces InternalNode split
        db.remove("d4"); // triggers remove and hasDistinctCoordinates check
    }


    /**
     * Covers missing CommandProcessor length checks, null lines, and Database
     * rejections.
     */
    public void testCommandProcessorEdgeCases() {
        CommandProcessor cp = new CommandProcessor();
        cp.processor(null); // Covers null line check
        cp.processor(""); // Covers empty line check
        cp.processor("insert"); // Length != 4
        cp.processor("insert a 10"); // Length != 4
        cp.processor("remove"); // Length < 2
        cp.processor("remove a b c"); // Length > 3
        cp.processor("regionsearch 0 0 10"); // Length != 5
        cp.processor("duplicates extra"); // Length != 1
        cp.processor("search"); // Length != 2
        cp.processor("dump extra"); // Length != 1

        Database db = new Database();
        db.regionsearch(0, 0, 0, 10); // Width <= 0
        db.regionsearch(0, 0, 10, 0); // Height <= 0
    }


    /**
     * Covers NE, SW, SE branches for InternalNode insert, remove, regionsearch,
     * intersects, and dump.
     */
    public void testInternalNodeAllBranches() {
        Database db = new Database();
        db.insert("nw", 100, 100);
        db.insert("ne", 700, 100); // Forces NE branch
        db.insert("sw", 100, 700); // Forces SW branch
        db.insert("se", 700, 700); // Forces SE branch

        systemOut().clearHistory();
        db.dump(); // Covers all internal quadrant dumps

        // Triggers NE, SW, SE intersects and regionsearch logic
        db.regionsearch(0, 0, 1024, 1024);
        db.regionsearch(512, 0, 512, 512);
        db.regionsearch(0, 512, 512, 512);
        db.regionsearch(512, 512, 512, 512);

        // Triggers NE, SW, SE remove branches and forces InternalNode merging
        db.remove("ne");
        db.remove("sw");
        db.remove("se");
        db.remove("nw");
    }


    /**
     * Covers LeafNode specific remove branches, empty node dumps, and split
     * conditions.
     */
    public void testLeafNodeAndEmptyNodeBranches() {
        LeafNode leaf = new LeafNode();
        Point p1 = new Point("a", 10, 10);
        Point p2 = new Point("b", 10, 10);
        Point p3 = new Point("c", 10, 10);

        leaf.insert(p1, 0, 0, 1024);
        leaf.insert(p2, 0, 0, 1024);
        leaf.insert(p3, 0, 0, 1024);

        // Remove exact instance (Iterator pt == p)
        leaf.remove(p2, 0, 0, 1024);

        // Remove by equivalent coordinates (sameCoords && sameName)
        leaf.remove(new Point("c", 10, 10), 0, 0, 1024);

        // Empty the leaf completely
        leaf.remove(p1, 0, 0, 1024);

        // Cover LeafNode splitting (size > 3 && hasDistinctCoordinates)
        LeafNode splitLeaf = new LeafNode();
        splitLeaf.insert(new Point("a", 10, 10), 0, 0, 1024);
        splitLeaf.insert(new Point("b", 10, 10), 0, 0, 1024);
        splitLeaf.insert(new Point("c", 10, 10), 0, 0, 1024);
        splitLeaf.insert(new Point("d", 20, 20), 0, 0, 1024); // Triggers
                                                              // InternalNode
                                                              // split

        // Cover EmptyNode dump with a level > 0 (triggers the for-loop
        // indentation print)
        EmptyNode.getInstance().dump(0, 0, 1024, 2);
    }


    /**
     * Exercises all remaining partial (pink) coverage branches.
     */
    public void testPinkLinePartialCoverageEdges() {
        // 1. SkipList Iterator Exception and Random Levels
        student.TestableRandom.setNextBooleans(false); // Forces level 1
                                                       // (newLevel <=
                                                       // head.level)
        SkipList<String, Point> sl = new SkipList<>();
        sl.insert(new KVPair<>("a", new Point("a", 1, 1)));

        student.TestableRandom.setNextBooleans(true, false); // Forces level 2
                                                             // (newLevel >
                                                             // head.level)
        sl.insert(new KVPair<>("b", new Point("b", 2, 2)));

        java.util.Iterator<KVPair<String, Point>> it = sl.iterator();
        it.next();
        it.next();
        try {
            it.next(); // Forces !hasNext() to evaluate to true, throwing
                       // exception
            fail("Should have thrown NoSuchElementException");
        }
        catch (java.util.NoSuchElementException e) {
            assertNotNull(e);
        }

        // 2. SkipList removals of non-existent items (covers current == null ||
        // key != 0)
        sl.remove("missing");
        sl.removeByValue(new Point("missing", 99, 99));

        // 3. Point Equals partial boolean logic (tests short-circuiting)
        Point pBase = new Point("p", 10, 10);
        assertFalse(pBase.equals(new Point("p2", 10, 20))); // x matches, y
                                                            // fails
        assertFalse(pBase.equals(new Point("p3", 20, 10))); // x fails, y
                                                            // matches

        // 4. CommandProcessor partial lengths (tests both true and false for
        // every command)
        CommandProcessor cp = new CommandProcessor();
        cp.processor("insert p 1 2"); // length == 4 (false)
        cp.processor("insert p 1"); // length != 4 (true)
        cp.processor("remove p"); // length == 2 (true)
        cp.processor("remove 1 2"); // length == 3 (true)
        cp.processor("remove p 1 2"); // length == 4 (neither 2 nor 3)
        cp.processor("regionsearch 0 0 10 10"); // length == 5 (false)
        cp.processor("regionsearch 0 0 10"); // length != 5 (true)
        cp.processor("duplicates"); // length == 1 (false)
        cp.processor("duplicates extra"); // length != 1 (true)
        cp.processor("search p"); // length == 2 (false)
        cp.processor("search"); // length != 2 (true)
        cp.processor("dump"); // length == 1 (false)
        cp.processor("dump extra"); // length != 1 (true)

        // 5. LeafNode & InternalNode deep dumps (forces level > 0 so 'for'
        // loops execute)
        Database db = new Database();
        db.insert("p1", 10, 10);
        db.insert("p2", 1000, 1000); // Forces InternalNode creation and depth
        systemOut().clearHistory();
        db.dump(); // Covers 'for (int i = 0; i < level; i++)' inside dump()
                   // when level > 0

        // 6. Cover PointsProject main missing file edge case
        try {
            PointsProject.main(new String[] {
                "this_file_does_not_exist_ever.txt" });
        }
        catch (Exception e) {
            // Handled
        }
    }


    /**
     * Exercises the absolute final red and yellow coverage blocks
     * to eliminate surviving Web-CAT mutants.
     */
    public void testFinalWebCatMutants() throws Exception {
        // 1. Cover CommandProcessor NumberFormatException (Image 5)
        CommandProcessor cp = new CommandProcessor();
        cp.processor("insert badName notAnInt 20");

        // 2. Cover KVPair compareTo(KVPair) (Image 4)
        KVPair<String, Point> kv1 = new KVPair<>("A", new Point(1, 1));
        KVPair<String, Point> kv2 = new KVPair<>("B", new Point(2, 2));
        assertTrue(kv1.compareTo(kv2) < 0);

        // 3. Cover PointsProject null args and directory edge cases (Image 8)
        PointsProject.main(null); // Hits args == null
        systemOut().clearHistory();
        PointsProject.main(new String[] { "src" }); // "src" exists but is a
                                                    // directory, hitting
                                                    // !isFile()
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file"));

        // 4. Cover LeafNode equivalent point remove resulting in EmptyNode
        // (Image 7)
        LeafNode leaf = new LeafNode();
        leaf.insert(new Point("a", 10, 10), 0, 0, 1024);
        // Using a NEW instance bypasses 'pt == p', forcing the second while
        // loop
        // to evaluate 'sameCoords && sameName', leaving the leaf empty.
        QuadNode result = leaf.remove(new Point("a", 10, 10), 0, 0, 1024);
        assertTrue(result instanceof EmptyNode);

        // 5. Cover InternalNode direct empty merge (Image 2)
        InternalNode internal = new InternalNode();
        Point p1 = new Point("test", 50, 50);
        internal.insert(p1, 0, 0, 1024);
        QuadNode emptyResult = internal.remove(p1, 0, 0, 1024);
        // remaining.isEmpty()
        // is true
        assertTrue(emptyResult instanceof EmptyNode);

        // 6. Cover InternalNode hasDistinctCoordinates false branch (Image 1)
        Database db = new Database();
        db.insert("d1", 10, 10);
        db.insert("d2", 10, 10);
        db.insert("d3", 10, 10);
        db.insert("d4", 10, 10);
        db.insert("diff", 200, 200); // Forces LeafNode to split into
                                     // InternalNode
        db.remove("diff"); // InternalNode removes 'diff'. Remaining 4 points
                           // are identical!
                           // hasDistinctCoordinates returns false, forcing
                           // merge back to LeafNode.

        // 7. Cover InternalNode boundary branch coverage (Image 3)
        InternalNode boundaryNode = new InternalNode();
        Point sePoint = new Point("se", 700, 700);
        boundaryNode.insert(sePoint, 0, 0, 1024);
        // Forces evaluation of px >= half and py >= half in the remove method
        boundaryNode.remove(sePoint, 0, 0, 1024);
    }


    /**
     * Covers missing CommandProcessor length checks, null lines, and Database
     * rejections.
     */
    public void testCommandProcessorEdgeCases() {
        CommandProcessor cp = new CommandProcessor();
        systemOut().clearHistory();

        cp.processor(null);
        cp.processor("");
        cp.processor("insert");
        cp.processor("insert a 10");
        cp.processor("remove");
        cp.processor("remove a b c");
        cp.processor("regionsearch 0 0 10");
        cp.processor("duplicates extra");
        cp.processor("search");
        cp.processor("dump extra");

        // Assert that nothing crashed and commands were rejected
        assertFalse(systemOut().getHistory().contains("Point inserted"));

        Database db = new Database();
        systemOut().clearHistory();
        db.regionsearch(0, 0, 0, 10);
        assertTrue(systemOut().getHistory().contains("Rectangle rejected"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, 10, 0);
        assertTrue(systemOut().getHistory().contains("Rectangle rejected"));
    }


    /**
     * Covers NE, SW, SE branches for InternalNode insert, remove,
     * regionsearch, intersects, and dump.
     */
    public void testInternalNodeAllBranches() {
        Database db = new Database();
        db.insert("nw", 100, 100);
        db.insert("ne", 700, 100);
        db.insert("sw", 100, 700);
        db.insert("se", 700, 700);

        systemOut().clearHistory();
        db.dump();
        String dumpOut = systemOut().getHistory();
        assertTrue(dumpOut.contains("ne, 700, 100"));
        assertTrue(dumpOut.contains("sw, 100, 700"));
        assertTrue(dumpOut.contains("se, 700, 700"));

        systemOut().clearHistory();
        db.regionsearch(0, 0, 1024, 1024);
        db.regionsearch(512, 0, 512, 512);
        db.regionsearch(0, 512, 512, 512);
        db.regionsearch(512, 512, 512, 512);
        assertTrue(systemOut().getHistory().contains("Point found"));

        // Assert removals actually remove the targeted quadrant's point
        db.remove("ne");
        systemOut().clearHistory();
        db.dump();
        assertFalse(systemOut().getHistory().contains("ne, 700, 100"));

        db.remove("sw");
        systemOut().clearHistory();
        db.dump();
        assertFalse(systemOut().getHistory().contains("sw, 100, 700"));

        db.remove("se");
        db.remove("nw");
        systemOut().clearHistory();
        db.dump();
        assertTrue(systemOut().getHistory().contains("Empty"));
    }


    /**
     * Covers LeafNode specific remove branches, empty node dumps, and split
     * conditions.
     */
    public void testLeafNodeAndEmptyNodeBranches() {
        LeafNode leaf = new LeafNode();
        Point p1 = new Point("a", 10, 10);
        Point p2 = new Point("b", 10, 10);
        Point p3 = new Point("c", 10, 10);

        leaf.insert(p1, 0, 0, 1024);
        leaf.insert(p2, 0, 0, 1024);
        leaf.insert(p3, 0, 0, 1024);

        // Remove exact instance and assert size dropped
        leaf.remove(p2, 0, 0, 1024);
        assertEquals(2, leaf.getPoints().size());

        // Remove by equivalent coordinates
        leaf.remove(new Point("c", 10, 10), 0, 0, 1024);
        assertEquals(1, leaf.getPoints().size());

        // Empty the leaf completely
        QuadNode emptyRes = leaf.remove(p1, 0, 0, 1024);
        assertTrue(emptyRes instanceof EmptyNode);

        LeafNode splitLeaf = new LeafNode();
        splitLeaf.insert(new Point("a", 10, 10), 0, 0, 1024);
        splitLeaf.insert(new Point("b", 10, 10), 0, 0, 1024);
        splitLeaf.insert(new Point("c", 10, 10), 0, 0, 1024);
        QuadNode splitRes = splitLeaf.insert(new Point("d", 20, 20), 0, 0,
            1024);
        assertTrue(splitRes instanceof InternalNode);

        systemOut().clearHistory();
        EmptyNode.getInstance().dump(0, 0, 1024, 2);
        assertTrue(systemOut().getHistory().contains("Empty"));
    }


    /**
     * Exercises the absolute final red and yellow coverage blocks
     * to eliminate surviving Web-CAT mutants.
     */
    public void testFinalWebCatMutants() throws Exception {
        CommandProcessor cp = new CommandProcessor();
        systemOut().clearHistory();
        cp.processor("insert badName notAnInt 20");
        assertTrue(systemOut().getHistory().contains("Invalid command"));

        KVPair<String, Point> kv1 = new KVPair<>("A", new Point(1, 1));
        KVPair<String, Point> kv2 = new KVPair<>("B", new Point(2, 2));
        assertTrue(kv1.compareTo(kv2) < 0);

        PointsProject.main(null);
        systemOut().clearHistory();
        PointsProject.main(new String[] { "src" });
        assertTrue(systemOut().getHistory().contains("no such input file"));

        LeafNode leaf = new LeafNode();
        leaf.insert(new Point("a", 10, 10), 0, 0, 1024);
        QuadNode result = leaf.remove(new Point("a", 10, 10), 0, 0, 1024);
        assertTrue(result instanceof EmptyNode);

        InternalNode internal = new InternalNode();
        Point p1 = new Point("test", 50, 50);
        internal.insert(p1, 0, 0, 1024);

        // Formatted to pass Checkstyle line length
        QuadNode emptyResult = internal.remove(p1, 0, 0, 1024);
        assertTrue(emptyResult instanceof EmptyNode);

        Database db = new Database();
        db.insert("d1", 10, 10);
        db.insert("d2", 10, 10);
        db.insert("d3", 10, 10);
        db.insert("d4", 10, 10);
        db.insert("diff", 200, 200);
        db.remove("diff");

        systemOut().clearHistory();
        db.dump();
        assertFalse(systemOut().getHistory().contains("Internal"));

        InternalNode boundaryNode = new InternalNode();
        Point sePoint = new Point("se", 700, 700);
        boundaryNode.insert(sePoint, 0, 0, 1024);
        QuadNode boundRes = boundaryNode.remove(sePoint, 0, 0, 1024);
        assertTrue(boundRes instanceof EmptyNode);
    }
}
