package ca.ulaval.glo2004.domaine.chalet;

import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class that represents a wall in the chalet.
 */
public class Wall implements Serializable {
    /**
     * The default width of a wall.
     */
    static final Imperial DEFAULT_WIDTH = new Imperial(12, 0);

    /**
     * The default height of a wall.
     */
    static final Imperial DEFAULT_HEIGHT = new Imperial(8, 0);

    /**
     * The UUID of the wall.
     */
    private final UUID uuid;

    /**
     * The x coordinate of the point located at the bottom left of the wall.
     */
    private Imperial x;

    /**
     * The y position of the point located at the bottom left of the wall.
     */
    private Imperial y;

    /**
     * The z position of the point located at the bottom left of the wall.
     */
    private Imperial z;

    /**
     * The width of the wall.
     */
    private Imperial width;

    /**
     * The height of the wall.
     */
    private Imperial height;

    /**
     * The list of accessories that are on the wall.
     */
    private final List<IAccessory> accessories;

    /**
     * The minimum distance between accessories and with the borders of the wall.
     */
    private Imperial minDistance;

    private Orientation wallOrientation;

    private boolean isOvertaking;
    private Imperial thickness;
    private Imperial imprecision;

    /**
     * Default constructor of the wall.
     */
    public Wall() {
        this(new Imperial(), new Imperial(), new Imperial());
    }

    /**
     * Constructor of the wall.
     *
     * @param x_ : The x coordinate of the point located at the bottom left of the wall.
     * @param y_ : The y coordinate of the point located at the bottom left of the wall.
     * @param z_ : The z coordinate of the point located at the bottom left of the wall.
     */
    public Wall(Imperial x_, Imperial y_, Imperial z_) {
        this(x_, y_, z_, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Constructor of the wall.
     *
     * @param x_      : The x coordinate of the point located at the bottom left of the wall.
     * @param y_      : The y coordinate of the point located at the bottom left of the wall.
     * @param z_      : The z coordinate of the point located at the bottom left of the wall.
     * @param width_  : The width of the wall.
     * @param height_ : The height of the wall.
     */
    public Wall(Imperial x_, Imperial y_, Imperial z_, Imperial width_, Imperial height_) {
        this(x_, y_, z_, width_, height_, new Imperial());
    }

    /**
     * Constructor of the wall.
     *
     * @param x_           : The x coordinate of the point located at the bottom left of the wall.
     * @param y_           : The y coordinate of the point located at the bottom left of the wall.
     * @param z_           : The z coordinate of the point located at the bottom left of the wall.
     * @param width_       : The width of the wall.
     * @param height_      : The height of the wall.
     * @param minDistance_ : The minimum distance between accessories and with the borders of the wall.
     */
    public Wall(Imperial x_, Imperial y_, Imperial z_, Imperial width_, Imperial height_, Imperial minDistance_) {
        this.uuid = UUID.randomUUID();
        this.x = x_;
        this.y = y_;
        this.z = z_;
        this.width = width_;
        this.height = height_;
        this.minDistance = minDistance_;
        this.accessories = new ArrayList<>();
    }

    /**
     * Determines if an accessory is valid.
     * An accessory is valid if it is within the bounds of the wall and if it does not overlap with any other accessory,
     * considering the minimum distance.
     *
     * @param accessory_ : The accessory to validate.
     * @return true if the accessory is valid, false otherwise.
     */
    public boolean isAccessoryValid(IAccessory accessory_) {
        Imperial minDistance = this.minDistance;

        if (!isAccessoryWithinWallBounds(accessory_)) {
            return false;
        }

        for (IAccessory accessory : this.accessories) {
            if (!accessory.equals(accessory_) && accessory.intersectsWith(accessory_, minDistance)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if an accessory is within the bounds of the wall, considering the minimum distance.
     * Takes into consideration the roof orientation.
     *
     * @param accessory_ : The accessory to validate.
     * @return true if the accessory is within the bounds of the wall, false otherwise.
     */
    public boolean isAccessoryWithinWallBounds(IAccessory accessory_) {
        Imperial distFromWall = this.isOvertaking ? this.thickness.plus(this.minDistance) : this.thickness.plus(this.imprecision).plus(this.minDistance);
        return accessory_.getX().greaterThanOrEqual(distFromWall) &&
                accessory_.getX().plus(accessory_.getWidth()).lessThanOrEqual(this.width.minus(distFromWall)) &&
                accessory_.getY().greaterThanOrEqual(this.minDistance) &&
                accessory_.getY().plus(accessory_.getHeight()).lessThanOrEqual(this.height.minus(this.minDistance));
    }

    /**
     * Revalidates all the accessories on the wall.
     */
    public void revalidateAllAccessories() {
        for (IAccessory accessory : this.accessories) {
            accessory.setIsValid(this.isAccessoryWithinWallBounds(accessory));

            if (accessory.isValid()) {
                for (IAccessory otherAccessory : this.accessories) {
                    if (!otherAccessory.equals(accessory) && accessory.intersectsWith(otherAccessory, this.minDistance)) {
                        accessory.setIsValid(false);
                        break;
                    }
                }
            }
        }
    }


    /**
     * Adds an accessory to the wall.
     *
     * @param accessory_ : The accessory to add.
     */
    public void addAccessory(IAccessory accessory_) {
        this.accessories.add(accessory_);

        this.revalidateAllAccessories();
    }

    /**
     * Removes an accessory from the wall.
     *
     * @param accessory_ : The accessory to remove.
     */
    public void removeAccessory(IAccessory accessory_) {
        this.accessories.remove(accessory_);

        this.revalidateAllAccessories();
    }

    /**
     * Sets the minimum distance between accessories and with the borders of the wall.
     *
     * @param minDistance : the minimum distance.
     */
    public void setMinDistance(Imperial minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * Gets the minimum distance between accessories and with the borders of the wall.
     *
     * @return the minimum distance.
     */
    public Imperial getMinDistance() {
        return this.minDistance;
    }

    /**
     * Gets the width of the wall.
     *
     * @return the width.
     */
    public Imperial getWidth() {
        return this.width;
    }

    /**
     * Gets the height of the wall.
     *
     * @return the height.
     */
    public Imperial getHeight() {
        return this.height;
    }

    /**
     * Gets the UUID of the wall.
     *
     * @return the UUID.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Gets the list of accessories that are on the wall.
     *
     * @return the list of accessories.
     */
    public List<IAccessory> getAccessories() {
        return this.accessories;
    }

    /**
     * Gets the orientation of the wall.
     *
     * @return the orientation.
     */
    public Orientation getWallOrientation() {
        return wallOrientation;
    }

    /**
     * Sets the orientation of the wall.
     *
     * @param wallOrientation : the orientation.
     */
    public void setWallOrientation(Orientation wallOrientation) {
        this.wallOrientation = wallOrientation;
    }

    public void setDimensions(Dimensions<Imperial> newDimensions_) {
        this.width = newDimensions_.getWidth();
        this.height = newDimensions_.getHeight();
    }

    public void setWidth(Imperial width_) {
        this.width = width_;
    }

    public void setHeight(Imperial height_) {
        this.height = height_;
    }

    /**
     * Gets the x coordinate of the point located at the bottom left of the wall.
     *
     * @return the x coordinate of the point located at the bottom left of the wall.
     */
    public Imperial getX() {
        return x;
    }

    /**
     * Sets the x coordinate of the point located at the bottom left of the wall.
     *
     * @param x_ : the x coordinate of the point located at the bottom left of the wall.
     */
    public void setX(Imperial x_) {
        this.x = x_;
    }

    /**
     * Gets the y coordinate of the point located at the bottom left of the wall.
     *
     * @return the y coordinate of the point located at the bottom left of the wall.
     */
    public Imperial getY() {
        return y;
    }

    /**
     * Sets the y coordinate of the point located at the bottom left of the wall.
     *
     * @param y_ : the y coordinate of the point located at the bottom left of the wall.
     */
    public void setY(Imperial y_) {
        this.y = y_;
    }

    /**
     * Gets the z coordinate of the point located at the bottom left of the wall.
     *
     * @return the z coordinate of the point located at the bottom left of the wall.
     */
    public Imperial getZ() {
        return z;
    }

    /**
     * Sets the z coordinate of the point located at the bottom left of the wall.
     *
     * @param z_ : the z coordinate of the point located at the bottom left of the wall.
     */
    public void setZ(Imperial z_) {
        this.z = z_;
    }

    public boolean isOvertaking() {
        return isOvertaking;
    }

    public void setOvertaking(boolean overtaking) {
        isOvertaking = overtaking;
    }

    public Imperial getThickness() {
        return thickness;
    }

    public void setThickness(Imperial thickness) {
        this.thickness = thickness;
    }

    public Imperial getImprecision() {
        return imprecision;
    }

    public void setImprecision(Imperial imprecision) {
        this.imprecision = imprecision;
    }
}
