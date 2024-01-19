package ca.ulaval.glo2004.util.parsing.parser;

/**
 * Node for feet.
 */
public class NodeFeet implements Node{
    /**
     * The feet value.
     */
    int feet;

    /**
     * constructor
     * @param feet_ : the feet value
     */
    public NodeFeet(int feet_) {
        this.feet = feet_;
    }

    /**
     * feet getter
     * @return the feet value
     */
    public int getFeet()
    {
        return this.feet;
    }
}

