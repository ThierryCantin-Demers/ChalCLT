package ca.ulaval.glo2004.util.math;

import ca.ulaval.glo2004.util.parsing.parser.*;
import ca.ulaval.glo2004.util.parsing.tokenizer.Token;
import ca.ulaval.glo2004.util.parsing.tokenizer.Tokenizer;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo2004.util.math.MathUtils;

/**
 * Class that represents an imperial value.
 * Feet, inches and fraction of an inch.
 * Does not support negative values.
 */
public class Imperial implements Cloneable, Serializable {
    /**
     * The maximum resolution of the fraction of an inch.
     */
    public static final int MAX_RESOLUTION = 128;

    /**
     * The number of feet of the imperial value.
     */
    private int feet;

    /**
     * The number of inches of the imperial value. (12 inches = 1 foot)
     */
    private int inches;

    /**
     * The numerator of the fraction of an inch.
     */
    private int numerator;

    /**
     * The denominator of the fraction of an inch.
     */
    private int denominator;

    private double realValue = -1.0d;

    /**
     * Default constructor.
     */
    public Imperial() {
        this.feet = 0;
        this.inches = 0;
        this.numerator = 0;
        this.denominator = 1;
    }

    /**
     * Constructor with feet and inches.
     *
     * @param feet_   : The number of feet.
     * @param inches_ : The number of inches.
     */
    public Imperial(int feet_, int inches_) {
        if (feet_ < 0 || inches_ < 0) {
            throw new IllegalArgumentException("feet_ and inches_ cannot be negative");
        }

        this.feet = feet_;
        this.inches = inches_;
        this.numerator = 0;
        this.denominator = 1;

        updateInchesToFeet();
    }

    /**
     * Constructor with feet, inches and fraction of an inch.
     *
     * @param feet_        : The number of feet.
     * @param inches_      : The number of inches.
     * @param numerator_   : The numerator of the fraction of an inch.
     * @param denominator_ : The denominator of the fraction of an inch.
     */
    public Imperial(int feet_, int inches_, int numerator_, int denominator_) {
        if (feet_ < 0 || inches_ < 0 || numerator_ < 0 || denominator_ < 0) {
            throw new IllegalArgumentException("feet_, inches_, numerator_ and denominator_ cannot be negative");
        } else if (denominator_ == 0) {
            throw new IllegalArgumentException("denominator_ cannot be 0");
        }

        this.feet = feet_;
        this.inches = inches_;
        this.numerator = numerator_;
        this.denominator = denominator_;

        updateInchesToFeet();
        updateFraction();
    }

    /**
     * Converts inches to feet.
     * Inches should be between 0 and 11 after the function.
     */
    private void updateInchesToFeet() {
        this.feet += this.inches / 12;
        this.inches %= 12;
    }

    /**
     * Updates the fraction of an inch to take MAX_RESOLUTION into account and reduces the fraction.
     */
    private void updateFraction() {
        if (this.numerator > 0) {
            reduceFraction();

            // Convert numerator to inches if possible
            while (this.numerator >= this.denominator) {
                this.numerator -= this.denominator;
                ++this.inches;
            }
            this.updateInchesToFeet();

            if (this.denominator > MAX_RESOLUTION) {
                this.numerator *= MAX_RESOLUTION;
                this.numerator /= this.denominator;
                this.denominator = MAX_RESOLUTION;
            }
        } else {
            this.denominator = 1;
        }
    }

    /**
     * Reduces the fraction.
     */
    private void reduceFraction() {
        int gcd = MathUtils.gcd(this.numerator, this.denominator);

        this.numerator /= gcd;
        this.denominator /= gcd;
    }

    /**
     * Returns the total number of inches (feet * 12 + inches).
     *
     * @return The total number of inches.
     */
    public int getTotalInches() {
        return this.feet * 12 + this.inches;
    }

    public void add(Imperial other) {
        //TODO: Find a better way to do operators to not copy code multiple times
        int thisTotalNumerator = this.getTotalInches() * this.denominator + this.numerator;
        int otherTotalNumerator = other.getTotalInches() * other.denominator + other.numerator;

        int totalNumerator = thisTotalNumerator * other.denominator + otherTotalNumerator * this.denominator;
        int totalDenominator = this.denominator * other.denominator;

        int totalInches = totalNumerator / totalDenominator;

        int totalNumeratorRemainder = totalNumerator % totalDenominator;

        this.feet = 0;
        this.inches = totalInches;
        this.updateInchesToFeet();

        this.numerator = totalNumeratorRemainder;
        this.denominator = totalDenominator;
        this.updateFraction();
    }

    /**
     * Adds an imperial value to the current one and returns the result.
     * Does not modify the current imperial value.
     *
     * @param other : The imperial value to add.
     * @return The result of the addition.
     */
    public Imperial plus(Imperial other) {
        int thisTotalNumerator = this.getTotalInches() * this.denominator + this.numerator;
        int otherTotalNumerator = other.getTotalInches() * other.denominator + other.numerator;

        int totalNumerator = thisTotalNumerator * other.denominator + otherTotalNumerator * this.denominator;
        int totalDenominator = this.denominator * other.denominator;

        int totalInches = totalNumerator / totalDenominator;

        int totalNumeratorRemainder = totalNumerator % totalDenominator;

        Imperial newVal = new Imperial();

        newVal.feet = 0;
        newVal.inches = totalInches;
        newVal.updateInchesToFeet();

        newVal.numerator = totalNumeratorRemainder;
        newVal.denominator = totalDenominator;
        newVal.updateFraction();

        return newVal;
    }

    /**
     * Subtracts an imperial value to the current one.
     * Modifies the current imperial value.
     *
     * @param other : The imperial value to subtract.
     */
    public void subtract(Imperial other) {
        if (this.lessThan(other)) {
            throw new IllegalArgumentException("The current imperial value is less than the other one and imperial values cannot be negative.");
        }

        int thisTotalNumerator = this.getTotalInches() * this.denominator + this.numerator;
        int otherTotalNumerator = other.getTotalInches() * other.denominator + other.numerator;

        int totalNumerator = thisTotalNumerator * other.denominator - otherTotalNumerator * this.denominator;
        int totalDenominator = this.denominator * other.denominator;

        int totalInches = totalNumerator / totalDenominator;

        int totalNumeratorRemainder = totalNumerator % totalDenominator;

        this.feet = 0;
        this.inches = totalInches;
        this.updateInchesToFeet();

        this.numerator = totalNumeratorRemainder;
        this.denominator = totalDenominator;
        this.updateFraction();
    }

    /**
     * Subtracts an imperial value to the current one and returns the result.
     * Does not modify the current imperial value.
     *
     * @param other : The imperial value to subtract.
     * @return The result of the subtraction.
     */
    public Imperial minus(Imperial other) {
        if (this.lessThan(other)) {
            throw new IllegalArgumentException("The current imperial value is less than the other one and imperial values cannot be negative.");
        }

        int thisTotalNumerator = this.getTotalInches() * this.denominator + this.numerator;
        int otherTotalNumerator = other.getTotalInches() * other.denominator + other.numerator;

        int totalNumerator = thisTotalNumerator * other.denominator - otherTotalNumerator * this.denominator;
        int totalDenominator = this.denominator * other.denominator;

        int totalInches = totalNumerator / totalDenominator;

        int totalNumeratorRemainder = totalNumerator % totalDenominator;

        Imperial newVal = new Imperial();

        newVal.feet = 0;
        newVal.inches = totalInches;
        newVal.updateInchesToFeet();

        newVal.numerator = totalNumeratorRemainder;
        newVal.denominator = totalDenominator;
        newVal.updateFraction();

        return newVal;
    }

    /**
     * Divides the Imperial value by a certain factor.
     * Does not modify the current Imperial value.
     *
     * @param factor : The divisor.
     * @return The result of the division.
     */
    public Imperial divide(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Cannot divide by a negative value or 0.");
        }

        int thisTotalNumerator = this.getTotalInches() * this.denominator + this.numerator;
        int newDenominator = this.denominator * factor;

        int totalInches = thisTotalNumerator / newDenominator;
        int totalNumeratorRemainder = thisTotalNumerator % newDenominator;

        Imperial newVal = new Imperial();

        newVal.feet = 0;
        newVal.inches = totalInches;
        newVal.updateInchesToFeet();

        newVal.numerator = totalNumeratorRemainder;
        newVal.denominator = newDenominator;
        newVal.updateFraction();

        return newVal;
    }

    /**
     * Multiplies the Imperial value by a certain factor.
     * Does not modify the current Imperial value.
     *
     * @param factor : The multiplier.
     * @return The result of the multiplication.
     */
    public Imperial multiply(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Cannot multiply by a negative value.");
        }

        int totalNumerator = (this.getTotalInches() * this.denominator + this.numerator) * factor;
        int totalDenominator = this.denominator;

        int totalInches = totalNumerator / totalDenominator;
        int totalNumeratorRemainder = totalNumerator % totalDenominator;

        Imperial newVal = new Imperial();

        newVal.feet = 0;
        newVal.inches = totalInches;
        newVal.updateInchesToFeet();

        newVal.numerator = totalNumeratorRemainder;
        newVal.denominator = totalDenominator;
        newVal.updateFraction();

        return newVal;
    }

    /**
     * Determines if two imperial values are equal.
     *
     * @param other : The imperial value to compare to.
     * @return true if the imperial values are equal, false otherwise.
     */
    public boolean equals(Imperial other) {
        return this.feet == other.feet && this.inches == other.inches && this.numerator == other.numerator && this.denominator == other.denominator;
    }

    /**
     * Determines if the current imperial value is less than the other one.
     *
     * @param other : The imperial value to compare to.
     * @return true if the current imperial value is less than the other one, false otherwise.
     */
    public boolean lessThan(Imperial other) {
        return this.getRawInchValue() < other.getRawInchValue();
    }

    /**
     * Determines if the current imperial value is less than or equal to the other one.
     *
     * @param other : The imperial value to compare to.
     * @return true if the current imperial value is less than or equal to the other one, false otherwise.
     */
    public boolean lessThanOrEqual(Imperial other) {
        return this.equals(other) || this.lessThan(other);
    }

    /**
     * Determines if the current imperial value is greater than the other one.
     *
     * @param other : The imperial value to compare to.
     * @return true if the current imperial value is greater than the other one, false otherwise.
     */
    public boolean greaterThan(Imperial other) {
        return this.getRawInchValue() > other.getRawInchValue();
    }

    /**
     * Determines if the current imperial value is greater than or equal to the other one.
     *
     * @param other : The imperial value to compare to.
     * @return true if the current imperial value is greater than or equal to the other one, false otherwise.
     */
    public boolean greaterThanOrEqual(Imperial other) {
        return this.equals(other) || this.greaterThan(other);
    }

    /**
     * Returns the raw value of the imperial value in inches represented by a double.
     *
     * @return The raw value of the imperial value in inches.
     */
    public double getRawInchValue() {
        return this.feet * 12 + this.inches + (double) this.numerator / this.denominator;
    }

    /**
     * Returns the raw value of the imperial value in inches represented by a float.
     *
     * @return The raw value of the imperial value in inches.
     */
    public float getRawInchValueFloat() {
        return this.feet * 12 + this.inches + (float) this.numerator / this.denominator;
    }

    /**
     * Returns an Imperial value from a raw float value
     *
     * @param rawValue
     * @return The Imperial value
     */
    public static Imperial fromFloat(float rawValue) {
        int totalInches = (int) rawValue;
        float remainingFraction = rawValue - totalInches;

        int feet = totalInches / 12;
        int inches = totalInches % 12;

        Pair<Integer,Integer> closestFraction = MathUtils.findClosestFraction(remainingFraction, MAX_RESOLUTION);


        return new Imperial(feet, inches, closestFraction.getKey(), closestFraction.getValue());
    }


    /**
     * Returns an Imperial value from a raw double value
     *
     * @param rawValue
     * @return The Imperial value
     */
    public static Imperial fromDouble(double rawValue) {
        int totalInches = (int) rawValue;
        double remainingFraction = rawValue - (double) totalInches;

        int feet = totalInches / 12;
        int inches = totalInches % 12;

        Pair<Integer,Integer> closestFraction = MathUtils.findClosestFraction(remainingFraction, MAX_RESOLUTION);

        return new Imperial(feet, inches, closestFraction.getKey(), closestFraction.getValue());
    }

    /**
     * Snaps an Imperial value to the nearest multiple of a certain Imperial value
     *
     * @param multiple
     * @return A rounded Imperial to the nearest multiple
     */
    public Imperial roundToNearest(Imperial multiple) {
        double rawValue = this.getRawInchValue();

        double multipleRawValue = multiple.getRawInchValue();

        double roundedValue = Math.round(rawValue / multipleRawValue) * multipleRawValue;

        return Imperial.fromDouble(roundedValue);
    }

    /**
     * Returns the number of feet of the imperial value.
     *
     * @return The number of feet of the imperial value.
     */
    public int getFeet() {
        return feet;
    }

    /**
     * Returns the number of inches of the imperial value.
     *
     * @return The number of inches of the imperial value.
     */
    public int getInches() {
        return inches;
    }

    /**
     * Returns the numerator of the fraction of an inch.
     *
     * @return The numerator of the fraction of an inch.
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * Returns the total value of the numerator to represent
     * the total value of the imperial value as a fraction of an inch.
     *
     * @return The total numerator.
     */
    public int getTotalNumerator() {
        return this.getTotalInches() * this.denominator + this.numerator;
    }

    /**
     * Returns the denominator of the fraction of an inch.
     *
     * @return The denominator of the fraction of an inch.
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Returns the string representation of the imperial value.
     * TODO: Does not take into account fractional part.
     *
     * @return The string representation of the imperial value.
     */
    @Override
    public String toString() {
        return feet + "' " + inches + "\"" + " " + this.numerator + "/" + this.denominator;
    }

    @Override
    public Object clone() {
        Imperial copy;

        try {
            copy = (Imperial) super.clone();
        } catch (CloneNotSupportedException e) {
            copy = new Imperial();
        }

        return copy;
    }

    /**
     * Creates a new Imperial value from a string.
     *
     * @param imperialString : the String to convert to an Imperial value
     * @return the corresponding Imperial value if the conversion worked, empty optional otherwise
     */
    public static Optional<Imperial> fromString(String imperialString) {
        int feet = 0;
        int inches = 0;
        int numerator = 0;
        int denominator = 1;

        try {
            Tokenizer tokenizer = new Tokenizer(imperialString);
            List<Token> tokens = tokenizer.tokenize();

            Parser parser = new Parser(tokens);
            List<Node> nodes = parser.parse();

            for (Node node : nodes) {
                if (node instanceof NodeFeet nodeFeet) {
                    feet = nodeFeet.getFeet();
                } else if (node instanceof NodeInches nodeInches) {
                    inches = nodeInches.getInches();
                } else if (node instanceof NodeFraction nodeFraction) {
                    numerator = nodeFraction.getNumerator();
                    denominator = nodeFraction.getDenominator();
                }
            }
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        return Optional.of(new Imperial(feet, inches, numerator, denominator));
    }

    public double getRealRawInchValueDouble() {
        if (realValue == -1.0d) {
            return this.getRawInchValue();
        }
        return realValue;
    }

    public void setRealValue(double realValue) {
        this.realValue = realValue;
    }
}
