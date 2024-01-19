package ca.ulaval.glo2004.util.math;

import java.io.Serializable;

/**
 * Represents a 2D point.
 *
 * @param <T> The type of the coordinates.
 */
public class Point2D<T> implements Serializable {
    /**
     * The x coordinate of the point.
     */
    private T x;

    /**
     * The y coordinate of the point.
     */
    private T y;

    /**
     * Creates a new Point2D object.
     *
     * @param x_ : The x coordinate of the point.
     * @param y_ : The y coordinate of the point.
     */
    public Point2D(T x_, T y_) {
        this.x = x_;
        this.y = y_;
    }

    /**
     * Gets the x coordinate of the point.
     *
     * @return The x coordinate of the point.
     */
    public T x() {
        return x;
    }

    /**
     * Sets the x coordinate of the point.
     *
     * @param x : The x coordinate of the point.
     */
    public void setX(T x) {
        this.x = x;
    }

    /**
     * Gets the y coordinate of the point.
     *
     * @return The y coordinate of the point.
     */
    public T y() {
        return y;
    }

    /**
     * Sets the y coordinate of the point.
     *
     * @param y : The y coordinate of the point.
     */
    public void setY(T y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point2D<?> other)) {
            return false;
        }
        return this.x.equals(other.x) && this.y.equals(other.y);
    }
}
