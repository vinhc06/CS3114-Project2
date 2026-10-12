import student.TestCase;

/**
 * Initial test suite to trigger Web-CAT execution.
 * 
 * @author Vinh Chau
 * @version 2026-10-04
 */
public class PointsProjectTest extends TestCase {

    /**
     * Tests driver execution with syntax test.
     * 
     * @throws Exception
     *             if I/O error occurs
     */
    public void testMain() throws Exception {
        PointsProject.main(new String[] { "SyntaxTest1.txt" });
        assertTrue(systemOut().getHistory().contains("QuadTree dump:"));
    }
}
