package ca.ulaval.glo2004.util.parsing.parser;

/**
 * Node for fractions.
 */
public class NodeFraction implements Node {
    /**
     * The numerator of the fraction.
     */
    int numerator;

    /**
     * The denominator of the fraction.
     */
    int denominator;

    /**
     * constructor
     * @param num_ : the numerator
     * @param denom_ : the denominator
     */
    public NodeFraction(int num_, int denom_) {
        this.numerator = num_;
        this.denominator = denom_;
    }

    /**
     * numerator getter
     * @return the numerator
     */
    public int getNumerator()
    {
        return this.numerator;
    }

    /**
     * denominator getter
     * @return the denominator
     */
    public int getDenominator()
    {
        return this.denominator;
    }
}
