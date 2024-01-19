package ca.ulaval.glo2004.util.math;

/**
 * Class that represents a pair of values
 * @param <K> type of the first value
 * @param <V> type of the second value
 */
public class Pair<K, V> {
    /**
     * The first value of the pair.
     */
    private K key;

    /**
     * The second value of the pair.
     */
    private V value;

    /**
     * Constructor of the pair
     * @param key_ : The first value.
     * @param value_ : The second value.
     */
    public Pair(K key_, V value_)
    {
        this.key = key_;
        this.value = value_;
    }

    /**
     * Getter for the first value of the pair.
     * @return The first value of the pair.
     */
    public K getKey() {
        return key;
    }

    /**
     * Setter for the first value of the pair.
     * @param key The new first value of the pair.
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Getter for the second value of the pair.
     * @return The second value of the pair.
     */
    public V getValue() {
        return value;
    }

    /**
     * Setter for the second value of the pair.
     * @param value The new second value of the pair.
     */
    public void setValue(V value) {
        this.value = value;
    }
}
