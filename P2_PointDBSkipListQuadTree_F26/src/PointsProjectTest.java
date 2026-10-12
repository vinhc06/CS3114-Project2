
import student.TestCase;

/**
 * Tests for Project 2.
 *
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-11
 */
public class PointsProjectTest extends TestCase {

    public void testMain() throws Exception {
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        assertTrue(systemOut().getHistory().contains(
            "QuadTree dump:"));
    }


    public void testInsert() {
        Database db = new Database();

        db.insert("apple", 100, 200);

        assertTrue(systemOut().getHistory().contains(
            "Point inserted: (apple, 100, 200)"));
    }


    public void testInvalidInsert() {
        Database db = new Database();

        db.insert("bad", -1, 100);
        db.insert("outside", 1024, 200);

        String output = systemOut().getHistory();

        assertTrue(output.contains("Point rejected:"));
        assertFalse(output.contains("Point inserted: (bad"));
        assertFalse(output.contains("Point inserted: (outside"));
    }


    public void testSearch() {
        Database db = new Database();

        db.insert("cat", 50, 60);
        db.search("cat");

        assertTrue(systemOut().getHistory().contains(
            "Found (cat, 50, 60)"));
    }


    public void testSearchNotFound() {
        Database db = new Database();

        db.search("missing");

        assertTrue(systemOut().getHistory().contains(
            "Point not found: missing"));
    }


    public void testDuplicateNames() {
        Database db = new Database();

        db.insert("dog", 10, 20);
        db.insert("dog", 30, 40);
        db.search("dog");

        String output = systemOut().getHistory();

        assertTrue(output.contains("Found (dog, 10, 20)"));
        assertTrue(output.contains("Found (dog, 30, 40)"));
    }


    public void testQuadTreeSplit() {
        Database db = new Database();

        db.insert("a", 100, 100);
        db.insert("b", 700, 100);
        db.insert("c", 100, 700);
        db.insert("d", 700, 700);

        db.dump();

        String output = systemOut().getHistory();

        assertTrue(output.contains("QuadTree dump:"));
        assertTrue(output.contains("Node at"));
    }


    public void testDuplicateCoordinates() {
        Database db = new Database();

        db.insert("a", 100, 100);
        db.insert("b", 100, 100);
        db.insert("c", 100, 100);
        db.insert("d", 100, 100);

        db.search("d");

        assertTrue(systemOut().getHistory().contains(
            "Found (d, 100, 100)"));
    }
}
