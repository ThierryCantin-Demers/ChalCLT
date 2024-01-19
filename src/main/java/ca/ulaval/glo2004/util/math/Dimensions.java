package ca.ulaval.glo2004.util.math;

import java.io.Serializable;

/**
 * Represents the dimensions of an object.
 * @param <T> The type of the dimensions.
 */
public class Dimensions<T> implements Serializable {
    /**
     * The width of the object.
     */
    private T width;

    /**
     * The height of the object.
     */
    private T height;

    /**
     * Creates a new Dimensions object.
     * @param width_ : The width of the object.
     * @param height_ : The height of the object.
     */
    public Dimensions(T width_, T height_)
    {
        this.width = width_;
        this.height = height_;
    }

    /**
     * Gets the width of the object.
     * @return The width of the object.
     */
    public T getWidth() {
        return width;
    }

    /**
     * Sets the width of the object.
     * @param width : The width of the object.
     */
    public void setWidth(T width) {
        this.width = width;
    }

    /**
     * Gets the height of the object.
     * @return The height of the object.
     */
    public T getHeight() {
        return height;
    }

    /**
     * Sets the height of the object.
     * @param height : The height of the object.
     */
    public void setHeight(T height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dimensions<?> other))
            return false;
        return this.width.equals(other.width) && this.height.equals(other.height);
    }
}
