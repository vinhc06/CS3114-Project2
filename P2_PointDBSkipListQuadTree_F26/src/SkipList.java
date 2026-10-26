
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import student.TestableRandom;

/**
 * Implements a SkipList that stores key-value pairs in sorted
 * order by key.
 *
 * @author CS Staff, Vinh Chau, Mahro Kim
 * @version 2026-10-25
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class SkipList<K extends Comparable<K>, V>
    implements Iterable<KVPair<K, V>> {

    private SkipNode head;
    private int size;
    private Random rng;

    /**
     * Initializes an empty SkipList.
     */
    public SkipList() {
        head = new SkipNode(null, 1);
        size = 0;
        rng = new TestableRandom();
    }


    /**
     * Generates a random level for a new node.
     *
     * @return the generated level
     */
    public int randomLevel() {
        int level = 1;

        while (rng.nextBoolean()) {
            level++;
        }

        return level;
    }


    /**
     * Searches for all pairs matching the given key.
     *
     * @param key
     *            key to search for
     * @return matching key-value pairs
     */
    public ArrayList<KVPair<K, V>> search(K key) {
        ArrayList<KVPair<K, V>> results = new ArrayList<>();

        SkipNode current = head;

        for (int i = head.level - 1; i >= 0; i--) {
            while (current.forward[i] != null
                && current.forward[i].element().key()
                    .compareTo(key) < 0) {

                current = current.forward[i];
            }
        }

        current = current.forward[0];

        while (current != null
            && current.element().key().compareTo(key) == 0) {

            results.add(current.element());
            current = current.forward[0];
        }

        return results;
    }


    /**
     * Returns the number of entries.
     *
     * @return number of entries
     */
    public int size() {
        return size;
    }


    /**
     * Inserts a key-value pair into the SkipList.
     *
     * @param it
     *            pair to insert
     */
    @SuppressWarnings("unchecked")
    public void insert(KVPair<K, V> it) {
        int newLevel = randomLevel();

        if (newLevel > head.level) {
            adjustHead(newLevel);
        }

        SkipNode[] update = (SkipNode[])Array.newInstance(
            SkipNode.class, head.level);

        SkipNode current = head;

        for (int i = head.level - 1; i >= 0; i--) {
            while (current.forward[i] != null
                && current.forward[i].element().key()
                    .compareTo(it.key()) < 0) {

                current = current.forward[i];
            }

            update[i] = current;
        }

        SkipNode newNode = new SkipNode(it, newLevel);

        for (int i = 0; i < newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }

        size++;
    }


    /**
     * Increases the level of the head node.
     *
     * @param newLevel
     *            new head level
     */
    public void adjustHead(int newLevel) {
        if (newLevel <= head.level) {
            return;
        }

        SkipNode newHead = new SkipNode(null, newLevel);

        for (int i = 0; i < head.level; i++) {
            newHead.forward[i] = head.forward[i];
        }

        head = newHead;
    }


    /**
     * Removes the first pair matching the key.
     *
     * @param key
     *            key to remove
     * @return removed pair, or null if not found
     */
    @SuppressWarnings("unchecked")
    public KVPair<K, V> remove(K key) {
        SkipNode[] update = (SkipNode[])Array.newInstance(
            SkipNode.class, head.level);

        SkipNode current = head;

        for (int i = head.level - 1; i >= 0; i--) {
            while (current.forward[i] != null
                && current.forward[i].element().key()
                    .compareTo(key) < 0) {

                current = current.forward[i];
            }

            update[i] = current;
        }

        current = current.forward[0];

        if (current == null
            || current.element().key().compareTo(key) != 0) {

            return null;
        }

        for (int i = 0; i < current.level; i++) {
            if (update[i].forward[i] == current) {
                update[i].forward[i] = current.forward[i];
            }
        }

        size--;
        return current.element();
    }


    /**
     * Removes the first pair matching the value.
     *
     * @param val
     *            value to remove
     * @return removed pair, or null if not found
     */
    public KVPair<K, V> removeByValue(V val) {
        SkipNode current = head.forward[0];

        while (current != null) {
            if (current.element().value().equals(val)) {
                KVPair<K, V> removed = current.element();

                for (int i = 0; i < current.level; i++) {
                    SkipNode temp = head;

                    while (temp.forward[i] != null
                        && temp.forward[i] != current) {

                        temp = temp.forward[i];
                    }

                    if (temp.forward[i] == current) {
                        temp.forward[i] = current.forward[i];
                    }
                }

                size--;
                return removed;
            }

            current = current.forward[0];
        }

        return null;
    }


    /**
     * Prints every node in the SkipList.
     */
    public void dump() {
        System.out.println("SkipList dump:");

        SkipNode current = head;

        while (current != null) {
            if (current == head) {
                System.out.println("Node has depth "
                    + current.level + ", Value (null)");
            }
            else {
                System.out.println("Node has depth "
                    + current.level + ", Value ("
                    + current.element() + ")");
            }

            current = current.forward[0];
        }

        System.out.println("SkipList size is: " + size);
    }


    /**
     * Represents an individual SkipList node.
     */
    private class SkipNode {

        private KVPair<K, V> pair;
        private SkipNode[] forward;
        private int level;

        /**
         * Creates a node.
         *
         * @param tempPair
         *            stored key-value pair
         * @param level
         *            number of levels
         */
        @SuppressWarnings("unchecked")
        public SkipNode(KVPair<K, V> tempPair, int level) {
            pair = tempPair;
            forward = (SkipNode[])Array.newInstance(
                SkipNode.class, level);
            this.level = level;
        }


        /**
         * Returns the stored pair.
         *
         * @return key-value pair
         */
        public KVPair<K, V> element() {
            return pair;
        }
    }


    /**
     * Iterates through the SkipList in key order.
     */
    private class SkipListIterator
        implements Iterator<KVPair<K, V>> {

        private SkipNode current;

        /**
         * Creates an iterator.
         */
        public SkipListIterator() {
            current = head;
        }


        /**
         * Checks whether another element exists.
         *
         * @return true if another element exists
         */
        @Override
        public boolean hasNext() {
            return current.forward[0] != null;
        }


        /**
         * Returns the next key-value pair.
         *
         * @return next pair
         */
        @Override
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            current = current.forward[0];
            return current.element();
        }
    }


    /**
     * Creates an iterator for the SkipList.
     *
     * @return iterator
     */
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new SkipListIterator();
    }
}
