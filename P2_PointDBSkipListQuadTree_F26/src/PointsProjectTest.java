
import student.TestCase;

/**
 * Tests the PointDB project, including SkipList,
 * QuadTree, and command processing operations.
 *
 * @author Vinh Chau, Mahro Kim
 * @version 2026-10-25
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests running the provided syntax files.
     *
     * @throws Exception
     *                   if an input error occurs
     */

    /**
     * Tests reading and processing commands from a file.
     *
     * @throws Exception
     *                   if file operations fail
     */
    public void testSyntaxFiles() throws Exception {
        java.io.File file = java.io.File.createTempFile(
                "pointdb_test_", ".txt");

        file.deleteOnExit();

        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {

            writer.println("insert alpha 100 100");
            writer.println("insert beta 700 700");
            writer.println("search alpha");
            writer.println("regionsearch 0 0 200 200");
            writer.println("duplicates");
            writer.println("dump");
            writer.println("remove alpha");
            writer.println("search alpha");
        }

        PointsProject.main(new String[] {
                file.getAbsolutePath()
        });

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point inserted: (alpha, 100, 100)"));
        assertTrue(out.contains(
                "Point inserted: (beta, 700, 700)"));
        assertTrue(out.contains(
                "Found (alpha, 100, 100)"));
        assertTrue(out.contains(
                "Point found: (alpha, 100, 100)"));
        assertTrue(out.contains("Duplicate points:"));
        assertTrue(out.contains("SkipList dump:"));
        assertTrue(out.contains("QuadTree dump:"));
        assertTrue(out.contains(
                "Point removed: (alpha, 100, 100)"));
        assertTrue(out.contains(
                "Point not found: alpha"));
    }

    /**
     * Tests missing arguments and invalid files.
     *
     * @throws Exception
     *                   if an input error occurs
     */
    public void testMainBadArgs() throws Exception {
        PointsProject.main(new String[] {});

        assertTrue(systemOut().getHistory().contains(
                "Usage: PointsProject"));

        systemOut().clearHistory();

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

        systemOut().clearHistory();
        db.search("point");

        String out = systemOut().getHistory();

        assertTrue(out.contains("Found (point, 10, 20)"));
        assertTrue(out.contains("Found (point, 30, 40)"));
    }

    /**
     * Tests splitting into four quadrants.
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

        assertTrue(out.contains(
                "Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("Node at 0, 0, 512:"));
        assertTrue(out.contains("(nw, 100, 100)"));
        assertTrue(out.contains("(ne, 700, 100)"));
        assertTrue(out.contains("(sw, 100, 700)"));
        assertTrue(out.contains("(se, 700, 700)"));
        assertTrue(out.contains("5 quadtree nodes printed"));
    }

    /**
     * Tests splitting deeper than the root.
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

        assertTrue(out.contains(
                "Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains(
                "Node at 0, 0, 512: Internal"));
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
        assertFalse(out.contains(
                "Node at 0, 0, 1024: Internal"));
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
     * Tests removal and merging.
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
        assertTrue(out.contains("1 quadtree nodes printed"));

        db.remove(700, 100);
        db.remove(100, 700);
        db.remove(700, 700);

        systemOut().clearHistory();
        db.dump();

        assertTrue(systemOut().getHistory().contains(
                "Node at 0, 0, 1024: Empty"));
    }

    /**
     * Tests removing one of several duplicate coordinates.
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
     * Tests removal from an empty database.
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
     * Tests region searching and invalid dimensions.
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
     * Tests a region crossing quadrant boundaries.
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

        assertTrue(out.contains(
                "Point found: (nw, 500, 500)"));
        assertTrue(out.contains(
                "Point found: (ne, 520, 500)"));
        assertTrue(out.contains(
                "Point found: (sw, 500, 520)"));
        assertTrue(out.contains(
                "Point found: (se, 520, 520)"));
    }

    /**
     * Tests searching a region without points.
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
     * Tests duplicate coordinate reporting.
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

        assertTrue(out.contains(
                "Node at 0, 0, 1024: Empty"));
        assertTrue(out.contains("1 quadtree nodes printed"));
        assertTrue(out.contains("SkipList size is: 0"));
    }

    /**
     * Tests commands through the command processor.
     */
    public void testCommandProcessorOperations() {
        CommandProcessor cp = new CommandProcessor();

        cp.processor("insert alpha 100 100");

        assertTrue(systemOut().getHistory().contains(
                "Point inserted: (alpha, 100, 100)"));

        systemOut().clearHistory();
        cp.processor("search alpha");

        assertTrue(systemOut().getHistory().contains(
                "Found (alpha, 100, 100)"));

        systemOut().clearHistory();
        cp.processor("regionsearch 0 0 200 200");

        assertTrue(systemOut().getHistory().contains(
                "Point found: (alpha, 100, 100)"));

        systemOut().clearHistory();
        cp.processor("duplicates");

        assertTrue(systemOut().getHistory().contains(
                "Duplicate points:"));

        systemOut().clearHistory();
        cp.processor("dump");

        String out = systemOut().getHistory();

        assertTrue(out.contains("SkipList dump:"));
        assertTrue(out.contains("QuadTree dump:"));

        systemOut().clearHistory();
        cp.processor("remove alpha");

        assertTrue(systemOut().getHistory().contains(
                "Point removed: (alpha, 100, 100)"));

        cp.processor("insert beta 200 200");

        systemOut().clearHistory();
        cp.processor("remove 200 200");

        assertTrue(systemOut().getHistory().contains(
                "Point removed: (beta, 200, 200)"));

        systemOut().clearHistory();
        cp.processor("unknown");

        assertTrue(systemOut().getHistory().contains(
                "Unrecognized command."));
    }

    /**
     * Tests malformed commands and invalid arguments.
     */
    public void testInvalidCommands() {
        CommandProcessor cp = new CommandProcessor();

        cp.processor("");
        cp.processor("   ");
        cp.processor("insert");
        cp.processor("remove");
        cp.processor("regionsearch");
        cp.processor("search");
        cp.processor("duplicates extra");
        cp.processor("dump extra");

        systemOut().clearHistory();
        cp.processor("insert bad abc 20");

        assertTrue(systemOut().getHistory().contains(
                "Invalid command arguments."));

        systemOut().clearHistory();
        cp.processor("remove abc 20");

        assertTrue(systemOut().getHistory().contains(
                "Invalid command arguments."));

        systemOut().clearHistory();
        cp.processor("regionsearch 0 0 abc 10");

        assertTrue(systemOut().getHistory().contains(
                "Invalid command arguments."));
    }

    /**
     * Tests region searching in each quadrant.
     */
    public void testAllQuadrantSearches() {
        Database db = new Database();

        db.insert("nw", 100, 100);
        db.insert("ne", 900, 100);
        db.insert("sw", 100, 900);
        db.insert("se", 900, 900);

        systemOut().clearHistory();
        db.regionsearch(0, 0, 512, 512);

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (nw, 100, 100)"));
        assertFalse(out.contains(
                "Point found: (ne, 900, 100)"));
        assertFalse(out.contains(
                "Point found: (sw, 100, 900)"));
        assertFalse(out.contains(
                "Point found: (se, 900, 900)"));
        assertTrue(out.contains("2 quadtree nodes visited"));

        systemOut().clearHistory();
        db.regionsearch(512, 0, 512, 512);

        out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (ne, 900, 100)"));
        assertFalse(out.contains(
                "Point found: (nw, 100, 100)"));
        assertTrue(out.contains("2 quadtree nodes visited"));

        systemOut().clearHistory();
        db.regionsearch(0, 512, 512, 512);

        out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (sw, 100, 900)"));
        assertFalse(out.contains(
                "Point found: (se, 900, 900)"));
        assertTrue(out.contains("2 quadtree nodes visited"));

        systemOut().clearHistory();
        db.regionsearch(512, 512, 512, 512);

        out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (se, 900, 900)"));
        assertFalse(out.contains(
                "Point found: (sw, 100, 900)"));
        assertTrue(out.contains("2 quadtree nodes visited"));
    }

    /**
     * Tests points on quadrant boundaries.
     */
    public void testQuadrantBoundaries() {
        Database db = new Database();

        db.insert("a", 511, 511);
        db.insert("b", 512, 511);
        db.insert("c", 511, 512);
        db.insert("d", 512, 512);

        systemOut().clearHistory();
        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Node at 0, 0, 1024: Internal"));
        assertTrue(out.contains("Node at 0, 0, 512:"));
        assertTrue(out.contains("Node at 512, 0, 512:"));
        assertTrue(out.contains("Node at 0, 512, 512:"));
        assertTrue(out.contains("Node at 512, 512, 512:"));
        assertTrue(out.contains("5 quadtree nodes printed"));

        systemOut().clearHistory();
        db.regionsearch(512, 512, 1, 1);

        out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (d, 512, 512)"));
        assertFalse(out.contains(
                "Point found: (a, 511, 511)"));
        assertFalse(out.contains(
                "Point found: (b, 512, 511)"));
        assertFalse(out.contains(
                "Point found: (c, 511, 512)"));
        assertTrue(out.contains("2 quadtree nodes visited"));
    }

    /**
     * Tests removal and merging from different quadrants.
     */
    public void testQuadrantRemoval() {
        Database db = new Database();

        db.insert("nw", 100, 100);
        db.insert("ne", 900, 100);
        db.insert("sw", 100, 900);
        db.insert("se", 900, 900);

        db.remove("nw");
        db.remove("ne");

        systemOut().clearHistory();
        db.dump();

        String out = systemOut().getHistory();

        assertTrue(out.contains("(sw, 100, 900)"));
        assertTrue(out.contains("(se, 900, 900)"));
        assertFalse(out.contains("(nw, 100, 100)"));
        assertFalse(out.contains("(ne, 900, 100)"));
        assertTrue(out.contains("1 quadtree nodes printed"));

        db.remove("sw");
        db.remove("se");

        systemOut().clearHistory();
        db.dump();

        out = systemOut().getHistory();

        assertTrue(out.contains(
                "Node at 0, 0, 1024: Empty"));
        assertTrue(out.contains("1 quadtree nodes printed"));
    }

    /**
     * Tests an empty QuadTree search.
     */
    public void testEmptyTreeRegionSearch() {
        Database db = new Database();

        db.regionsearch(0, 0, 1024, 1024);

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Points intersecting region (0, 0, 1024, 1024):"));
        assertFalse(out.contains("Point found:"));
        assertTrue(out.contains("1 quadtree nodes visited"));
    }

    /**
     * Tests rectangle searches touching world boundaries.
     */
    public void testWorldBoundarySearch() {
        Database db = new Database();

        db.insert("first", 0, 0);
        db.insert("last", 1023, 1023);

        systemOut().clearHistory();
        db.regionsearch(0, 0, 1024, 1024);

        String out = systemOut().getHistory();

        assertTrue(out.contains(
                "Point found: (first, 0, 0)"));
        assertTrue(out.contains(
                "Point found: (last, 1023, 1023)"));

        systemOut().clearHistory();
        db.regionsearch(1024, 1024, 10, 10);

        out = systemOut().getHistory();

        assertFalse(out.contains("Point found:"));
    }
}
