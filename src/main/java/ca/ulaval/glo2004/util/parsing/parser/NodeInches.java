package ca.ulaval.glo2004.util.parsing.parser;

/**
 * Node for inches.
 */
public class NodeInches implements Node {
    /**
     * The inches value.
     */
    int inches;

    /**
     * constructor
     *
     * @param inches_ : the inches value
     */
    public NodeInches(int inches_) {
        this.inches = inches_;
    }

    /**
     * inches getter
     *
     * @return the inches value
     */
    public int getInches()
    {
        return this.inches;
    }
}
