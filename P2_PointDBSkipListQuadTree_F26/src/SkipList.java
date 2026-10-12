import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import student.TestableRandom;

/**
 * This class implements SkipList data structure and contains an inner SkipNode
 * class which the SkipList will make an array of to store data.
 * 
 * @author CS Staff
 * @version 2024-01-22
 * 
 * @param <K>
 *            Key
 * @param <V>
 *            Value
 */
public class SkipList<K extends Comparable<K>, V>
    implements Iterable<KVPair<K, V>> {

    private SkipNode head;
    private int size;
    private Random rng;

    /**
     * Initializes the fields.
     */
    public SkipList() {
        head = new SkipNode(null, 1);
        size = 0;
        rng = new TestableRandom();
    }


    /**
     * Returns a random level.
     *
     * @return random level
     */
    public int randomLevel() {
        int level = 1;

        while (rng.nextBoolean()) {
            level++;
        }

        return level;
    }


    /**
     * Searches for all KVPairs with the given key.
     *
     * @param key
     *            key to search for
     * @return list of matching pairs
     */
    public ArrayList<KVPair<K, V>> search(K key) {
        ArrayList<KVPair<K, V>> results = new ArrayList<KVPair<K, V>>();

        SkipNode current = head;

        // Start at the highest level and work down
        for (int i = head.level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].element()
                .key().compareTo(key) < 0) {

                current = current.forward[i];
            }
        }

        current = current.forward[0];

        // Get every pair with this key
        while (current != null && current.element().key().compareTo(key) == 0) {

            results.add(current.element());
            current = current.forward[0];
        }

        return results;
    }


    /**
     * Returns the number of entries in the SkipList.
     *
     * @return size
     */
    public int size() {
        return size;
    }


    /**
     * Inserts a KVPair into the SkipList.
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

        SkipNode[] update = (SkipNode[])Array.newInstance(SkipNode.class,
            head.level);

        SkipNode current = head;

        // Find where the new node belongs
        for (int i = head.level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].element()
                .key().compareTo(it.key()) < 0) {

                current = current.forward[i];
            }

            update[i] = current;
        }

        SkipNode newNode = new SkipNode(it, newLevel);

        // Put the new node into each of its levels
        for (int i = 0; i < newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }

        size++;
    }


    /**
     * Increases the number of levels in the head.
     *
     * @param newLevel
     *            new level of the head
     */
    @SuppressWarnings("unchecked")
    public void adjustHead(int newLevel) {
        SkipNode newHead = new SkipNode(null, newLevel);

        // Copy the old head pointers
        for (int i = 0; i < head.level; i++) {
            newHead.forward[i] = head.forward[i];
        }

        head = newHead;
    }


    /**
     * Removes a KVPair with the given key.
     *
     * @param key
     *            key to remove
     * @return removed pair or null
     */
    @SuppressWarnings("unchecked")
    public KVPair<K, V> remove(K key) {
        SkipNode[] update = (SkipNode[])Array.newInstance(SkipNode.class,
            head.level);

        SkipNode current = head;

        // Find the node before the key at each level
        for (int i = head.level - 1; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].element()
                .key().compareTo(key) < 0) {

                current = current.forward[i];
            }

            update[i] = current;
        }

        current = current.forward[0];

        // Key was not found
        if (current == null || current.element().key().compareTo(key) != 0) {

            return null;
        }

        // Remove the node from each level it appears in
        for (int i = 0; i < current.level; i++) {
            if (update[i].forward[i] == current) {
                update[i].forward[i] = current.forward[i];
            }
        }

        size--;
        return current.element();
    }


    /**
     * Removes the first KVPair with the specified value.
     *
     * @param val
     *            value to remove
     * @return removed pair or null
     */
    public KVPair<K, V> removeByValue(V val) {
        SkipNode current = head.forward[0];

        // Find the first matching value
        while (current != null) {
            if (current.element().value().equals(val)) {
                KVPair<K, V> removed = current.element();

                // Remove this exact node from all of its levels
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
     * Prints the SkipList.
     */
    public void dump() {
        System.out.println("SkipList dump:");

        SkipNode current = head;

        while (current != null) {
            if (current == head) {
                if (size == 0) {
                    System.out.println("Node has depth " + current.level
                        + ", Value null");
                }
                else {
                    System.out.println("Node with depth " + current.level
                        + ", Value null");
                }
            }
            else {
                System.out.println("Node with depth " + current.level
                    + ", Value (" + current.element() + ")");
            }
            current = current.forward[0];
        }

        System.out.println("SkipList size is: " + size);
    }

    /**
     * Node used by the SkipList.
     */
    private class SkipNode {

        private KVPair<K, V> pair;
        private SkipNode[] forward;
        private int level;

        /**
         * Creates a SkipNode.
         *
         * @param tempPair
         *            pair stored in node
         * @param level
         *            number of levels in node
         */
        @SuppressWarnings("unchecked")
        public SkipNode(KVPair<K, V> tempPair, int level) {
            pair = tempPair;

            forward = (SkipNode[])Array.newInstance(SkipNode.class, level);

            this.level = level;
        }


        /**
         * Returns the pair stored in the node.
         *
         * @return pair
         */
        public KVPair<K, V> element() {
            return pair;
        }
    }


    /**
     * Iterator for the SkipList.
     */
    private class SkipListIterator implements Iterator<KVPair<K, V>> {

        private SkipNode current;

        /**
         * Creates the iterator.
         */
        public SkipListIterator() {
            current = head;
        }


        @Override
        public boolean hasNext() {
            return current.forward[0] != null;
        }


        @Override
        public KVPair<K, V> next() {
            KVPair<K, V> elem = current.forward[0].element();
            current = current.forward[0];

            return elem;
        }
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new SkipListIterator();
    }
}
