
import student.TestCase;

/**
 * Tests the PointDB project, including the SkipList
 * and QuadTree operations.
 *
 * @author Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests running the program with input files.
     *
     * @throws Exception if an input error occurs
     */
    public void testSyntaxFiles() throws Exception {
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        String out = systemOut().getHistory();
        assertNotNull(out);

        PointsProject.main(new String[] { "SyntaxTest2.txt" });
        assertNotNull(systemOut().getHistory());
    }


    /**
     * Tests missing arguments and invalid files.
     *
     * @throws Exception if an input error occurs
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
     * Tests removing points with duplicate coordinates.
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
        assertTrue(out.contains("(50, 50)"));
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
}
