/**
 * KVPair class declaration
 *
 * @param <K>
 *            Key
 * @param <E>
 *            Element
 * @author CS Staff
 * @version September 2026
 */
public class KVPair<K extends Comparable<K>, E> 
    implements Comparable<KVPair<K, E>> {
    
    private K theKey; // The key
    private E theVal; // The value

    /**
     * Create a new KVPair object.
     *
     * @param key
     *            The key
     * @param value
     *            The value
     */
    KVPair(K key, E value) {
        theKey = key;
        theVal = value;
    }


    /**
     * Compare KVPairs
     *
     * @param it
     *            What to compare against
     * @return Standard values for compareTo
     */
    public int compareTo(KVPair<K, E> it) {
        return theKey.compareTo(it.key());
    }


    /**
     * Compare against a key.
     *
     * @param it
     *            The key to compare to
     * @return Standard values for compareTo
     */
    public int compareTo(K it) {
        return theKey.compareTo(it);
    }


    /**
     * Getter for key.
     *
     * @return The key
     */
    public K key() {
        return theKey;
    }


    /**
     * Getter for value.
     *
     * @return The value
     */
    public E value() {
        return theVal;
    }


    /**
     * toString override
     *
     * @return key, value as a string
     */

    public String toString() {
        return theKey.toString() + ", " + theVal.toString();
    }
}
