import student.TestCase;

/**
 * Complete test suite for Project 2 meeting coverage requirements.
 * 
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-18
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests driver execution with both syntax test inputs.
     * 
     * @throws Exception
     *             on I/O error
     */
    public void testSyntaxFiles() throws Exception {
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        String out1 = systemOut().getHistory();
        assertTrue(out1.contains("1 quadtree nodes printed"));

        PointsProject.main(new String[] { "SyntaxTest2.txt" });
        String out2 = systemOut().getHistory();
        assertTrue(out2.contains("Points intersecting region (0, 0, 25, 25):"));
        assertTrue(out2.contains("Point found: (p_p, 1, 20)"));
    }


    /**
     * Tests bad command-line arguments.
     * 
     * @throws Exception
     *             on I/O error
     */
    public void testMainBadArgs() throws Exception {
        PointsProject.main(new String[] {});
        assertTrue(systemOut().getHistory().contains("Usage: PointsProject"));

        PointsProject.main(new String[] { "nonexistent_file.txt" });
        assertTrue(systemOut().getHistory().contains(
            "There is no such input file"));
    }


    /**
     * Tests basic insertions and boundary rejections.
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
     * Tests search and duplicate names.
     */
    public void testSearchOperations() {
        Database db = new Database();
        db.search("none");
        assertTrue(systemOut().getHistory().contains("Point not found: none"));

        db.insert("pt", 10, 20);
        db.insert("pt", 30, 40);
        db.search("pt");
        String out = systemOut().getHistory();
        assertTrue(out.contains("Found (pt, 10, 20)"));
        assertTrue(out.contains("Found (pt, 30, 40)"));
    }


    /**
     * Tests QuadTree splitting and node dumping.
     */
    public void testSplitsAndDumps() {
        Database db = new Database();
        db.insert("p1", 100, 100); // NW
        db.insert("p2", 700, 100); // NE
        db.insert("p3", 100, 700); // SW
        db.insert("p4", 700, 700); // SE -> causes split to InternalNode
        db.dump();

        String out = systemOut().getHistory();
        assertTrue(out.contains("Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("Node at 0, 0, 512:"));
        assertTrue(out.contains("(p1, 100, 100)"));
    }


    /**
     * Tests that duplicate coordinates do not trigger node splits.
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
     * Tests removal operations and automatic node merging.
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

        // Remove by name
        db.remove("a");
        assertTrue(systemOut().getHistory().contains(
            "Point removed: (a, 100, 100)"));

        // Remaining 3 points merge back into a single LeafNode
        db.dump();
        String out = systemOut().getHistory();
        assertTrue(out.contains("Node at 0, 0, 1024:"));
        assertFalse(out.contains("Internal"));

        // Remove remaining points by coordinate
        db.remove(700, 100);
        db.remove(100, 700);
        db.remove(700, 700);

        // Entire tree collapses to EmptyNode
        db.dump();
        assertTrue(systemOut().getHistory().contains(
            "Node at 0, 0, 1024: Empty"));
    }


    /**
     * Tests region search and rejection.
     */
    public void testRegionSearch() {
        Database db = new Database();
        db.regionsearch(0, 0, -5, 10);
        assertTrue(systemOut().getHistory().contains(
            "Rectangle rejected: (0, 0, -5, 10)"));

        db.insert("in", 50, 50);
        db.insert("out", 500, 500);
        db.regionsearch(0, 0, 100, 100);

        String out = systemOut().getHistory();
        assertTrue(out.contains("Point found: (in, 50, 50)"));
        assertFalse(out.contains("Point found: (out, 500, 500)"));
        assertTrue(out.contains("quadtree nodes visited"));
    }
}
