/**
 * PR QuadTree node base interface.
 * 
 * @author Mahro Kim, Vinh Chau
 * @version 2026-10-04
 */
public interface QuadNode {
    QuadNode insert(Point p, int x, int y, int size);


    QuadNode remove(Point p, int x, int y, int size);


    int dump(int x, int y, int size, int level);
}
