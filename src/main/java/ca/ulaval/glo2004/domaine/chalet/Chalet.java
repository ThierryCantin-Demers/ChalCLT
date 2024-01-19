package ca.ulaval.glo2004.domaine.chalet;

import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.domaine.chalet.dto.ExtensionDTO;
import ca.ulaval.glo2004.domaine.chalet.dto.GableDTO;
import ca.ulaval.glo2004.domaine.chalet.dto.RoofDTO;
import ca.ulaval.glo2004.domaine.chalet.dto.SlopeDTO;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that represents a chalet.
 */
public class Chalet implements Serializable {
    /**
     * The default minimum distance between accessories.
     */
    private static final Imperial DEFAULT_MIN_DISTANCE_BETWEEN_ACCESSORIES = new Imperial(0,3);

    /**
     * The default panel thickness.
     */
    private static final Imperial DEFAULT_PANEL_THICKNESS = new Imperial(0, 6);

    /**
     * The default imprecision.
     */
    private static final Imperial DEFAULT_IMPRECISION = new Imperial(0,0,1,4);

    /**
     * The default roof orientation.
     */
    private static final Orientation DEFAULT_ROOF_ORIENTATION = Orientation.FRONT;

    private static final float DEFAULT_ROOF_ANGLE = 30.0f;

    /**
     * The UUID of the chalet.
     */
    private final UUID uuid;

    /**
     * The left wall of the chalet.
     */
    private Wall leftWall;

    /**
     * The rear wall of the chalet.
     */
    private Wall backWall;

    /**
     * The right wall of the chalet.
     */
    private Wall rightWall;

    /**
     * The front wall of the chalet.
     */
    private Wall frontWall;

    /**
     * The roof of the chalet.
     */
    private Roof roof;

    /**
     * The orientation of the roof. Determines which walls overtakes the others.
     */
    private Orientation roofOrientation;

    /**
     * Determines the thickness of all the panels.
     */
    private Imperial panelThickness;

    /**
     * The minimum distance between accessories.
     */
    private Imperial minDistanceBetweenAccessories;

    /**
     * The imprecision of the cuts between walls.
     */
    private Imperial imprecision;

    /**
     * Default constructor of the chalet.
     * The coordinates are based in the exterior bottom left corner of the left wall.
     * Which means that the position of the left wall is (0,0,0) and the position of
     * every other wall and part of the roof is relative to that.
     * <p>
     * Here is a small diagram that shows a quick view of the chalet from the top, where the walls are labeled with
     * their respective names, the exterior bottom left corner of the left wall is labeled with p and
     * the axis x and z are shown.
     * <p>
     *              back
     *            z+
     *          --------------->
     *          p------------
     *        |  |          |
     *  left  |  |          |  right
     *      x+|  |          |
     *        |  |          |
     *        v  ------------
     *              front
     */
    public Chalet()
    {
        this.uuid = UUID.randomUUID();

        // Section Walls
        this.leftWall = new Wall();
        this.leftWall.setWallOrientation(Orientation.LEFT);

        this.backWall = new Wall();
        this.backWall.setWallOrientation(Orientation.BACK);

        this.rightWall = new Wall();
        this.rightWall.setWallOrientation(Orientation.RIGHT);

        this.frontWall = new Wall();
        this.frontWall.setWallOrientation(Orientation.FRONT);

        //Section Roof
        this.roof = new Roof();
        this.roof.setWidth(Wall.DEFAULT_WIDTH);
        this.roof.setDepth(Wall.DEFAULT_WIDTH);
        this.roof.setX(new Imperial(0,0));
        this.roof.setZ(new Imperial(0,0));
        this.roof.setY(Wall.DEFAULT_HEIGHT);

        // Set default values
        this.updateImprecision(Chalet.DEFAULT_IMPRECISION);
        this.updateMinDistanceBetweenAccessories(Chalet.DEFAULT_MIN_DISTANCE_BETWEEN_ACCESSORIES);
        this.updateWallThickness(Chalet.DEFAULT_PANEL_THICKNESS);
        this.updateRoofOrientation(Chalet.DEFAULT_ROOF_ORIENTATION);
        this.updateRoofAngle(Chalet.DEFAULT_ROOF_ANGLE);

        this.updatePanelPositions();
    }

    /**
     * Updates the positions of every wall and every part of the roof.
     * The position of the left wall is never changed since it's always (0,0,0)
     * TODO: Does not take the roof into consideration yet.
     */
    private void updatePanelPositions()
    {
        this.backWall.setZ(this.backWall.getWidth());

        this.rightWall.setZ(this.backWall.getZ());
        this.rightWall.setX(this.rightWall.getWidth());

        this.frontWall.setX(this.leftWall.getWidth());

        switch(this.roofOrientation)
        {
            case FRONT:
                roof.getLeftGable().setX(leftWall.getX().plus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));
                roof.getLeftGable().setY(this.leftWall.getHeight());
                roof.getLeftGable().setZ(leftWall.getZ());

                roof.getRightGable().setX(roof.getLeftGable().getX());
                roof.getRightGable().setY(this.leftWall.getHeight());
                roof.getRightGable().setZ(rightWall.getZ());

                roof.getExtension().setX(backWall.getX());
                roof.getExtension().setY(this.leftWall.getHeight());
                roof.getExtension().setZ(backWall.getZ());

                roof.getSlope().setX(frontWall.getX());
                roof.getSlope().setY(frontWall.getHeight());
                roof.getSlope().setZ(frontWall.getZ());
                break;

            case LEFT:
                roof.getLeftGable().setX(backWall.getX());
                roof.getLeftGable().setY(this.backWall.getHeight());
                roof.getLeftGable().setZ(backWall.getZ().minus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));

                roof.getRightGable().setX(rightWall.getX().minus(panelThickness));
                roof.getRightGable().setY(this.rightWall.getHeight());
                roof.getRightGable().setZ(rightWall.getZ().plus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));

                roof.getExtension().setX(rightWall.getX());
                roof.getExtension().setY(this.rightWall.getHeight());
                roof.getExtension().setZ(rightWall.getZ());

                roof.getSlope().setX(leftWall.getX());
                roof.getSlope().setY(leftWall.getHeight());
                roof.getSlope().setZ(leftWall.getZ());
                break;

            case BACK:
                roof.getLeftGable().setX(rightWall.getX().minus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));
                roof.getLeftGable().setY(this.rightWall.getHeight());
                roof.getLeftGable().setZ(rightWall.getZ());

                roof.getRightGable().setX(frontWall.getX().minus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));
                roof.getRightGable().setY(this.leftWall.getHeight());
                roof.getRightGable().setZ(frontWall.getZ().plus(panelThickness).plus(panelThickness));

                roof.getExtension().setX(frontWall.getX());
                roof.getExtension().setY(this.leftWall.getHeight());
                roof.getExtension().setZ(frontWall.getZ());

                roof.getSlope().setX(backWall.getX());
                roof.getSlope().setY(backWall.getHeight());
                roof.getSlope().setZ(backWall.getZ());
                break;

            case RIGHT:
                roof.getLeftGable().setX(frontWall.getX());
                roof.getLeftGable().setY(this.backWall.getHeight());
                roof.getLeftGable().setZ(frontWall.getZ().plus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));

                roof.getRightGable().setX(leftWall.getX().plus(panelThickness));
                roof.getRightGable().setY(this.rightWall.getHeight());
                roof.getRightGable().setZ(leftWall.getZ().plus(panelThickness).plus((leftWall.getImprecision().plus(leftWall.getThickness())).divide(2)));

                roof.getExtension().setX(leftWall.getX());
                roof.getExtension().setY(this.leftWall.getHeight());
                roof.getExtension().setZ(leftWall.getZ());

                roof.getSlope().setX(rightWall.getX());
                roof.getSlope().setY(rightWall.getHeight());
                roof.getSlope().setZ(rightWall.getZ());
                break;

        }
    }

    /**
     * Updates the dimensions of every wall and every part of the roof.
     * TODO: Does not take the roof into consideration yet.
     */
    public void updatePanelDimensions(Orientation wallToUpdate_, Dimensions<Imperial> newDimensions_)
    {
        Wall[] orderedWalls = this.getWallCorrespondence(wallToUpdate_);

        // Set the dimensions of the requested wall and the one facing it to the new dimensions
        orderedWalls[0].setDimensions(newDimensions_);
        orderedWalls[2].setDimensions(newDimensions_);

        // Set the height of the walls on the left and right of the requested wall to the new height
        orderedWalls[1].setHeight(newDimensions_.getHeight());
        orderedWalls[3].setHeight(newDimensions_.getHeight());

        // Roof
        this.roof.getExtension().setWidth(orderedWalls[0].getWidth());
        this.roof.getSlope().setWidth(orderedWalls[0].getWidth());
        this.roof.getLeftGable().setWidth(orderedWalls[1].getWidth());
        this.roof.getRightGable().setWidth(orderedWalls[1].getWidth());

        this.updatePanelPositions();
    }

    /**
     * Gets the corresponding wall from an orientation.
     * @param wallOrientation_ : The requested orientation of the wall.
     * @return The wall corresponding to the orientation.
     * @throws IllegalArgumentException : If the requested orientation is not valid.
     */
    public Wall getWallFromOrientation(Orientation wallOrientation_)
    {
        return switch (wallOrientation_) {
            case FRONT -> frontWall;
            case LEFT -> leftWall;
            case BACK -> backWall;
            case RIGHT -> rightWall;
            default -> throw new IllegalArgumentException("The requested wall orientation is not valid.");
        };
    }

    /**
     * Gets the corresponding wall from a UUID.
     * @param uuid_ : The requested UUID of the wall.
     * @return The wall corresponding to the UUID.
     * @throws IllegalArgumentException : If there is no wall with the requested UUID.
     */
    public Optional<Wall> getWallFromUUID(UUID uuid_)
    {
        if (uuid_.equals(this.frontWall.getUUID())) {
            return Optional.of(this.frontWall);
        }
        else if (uuid_.equals(this.leftWall.getUUID())) {
            return Optional.of(this.leftWall);
        }
        else if (uuid_.equals(this.backWall.getUUID())) {
            return Optional.of(this.backWall);
        }
        else if (uuid_.equals(this.rightWall.getUUID())) {
            return Optional.of(this.rightWall);
        }

        return Optional.empty();
    }

    public Optional<Wall> getWallOfAccessoryUUID(UUID uuid_)
    {
        for(Wall wall : this.getWallCorrespondence(Orientation.FRONT))
        {
            for(IAccessory accessory : wall.getAccessories())
            {
                if(accessory.getUUID().equals(uuid_))
                {
                    return Optional.of(wall);
                }
            }
        }

        // The accessory was not found
        return Optional.empty();
    }


    public Optional<IAccessory> getAccessoryFromUUID(UUID uuid_)
    {
        for(Wall wall : this.getWallCorrespondence(Orientation.FRONT))
        {
            for(IAccessory accessory : wall.getAccessories())
            {
                if(accessory.getUUID().equals(uuid_))
                {
                    return Optional.of(accessory);
                }
            }
        }

        // The accessory was not found
        return Optional.empty();
    }

    public Optional<Gable> getGableFromUUID(UUID uuid_)
    {
        if(this.roof.getLeftGable().getUuid().equals(uuid_))
        {
            return Optional.of(this.roof.getLeftGable());
        }
        if(this.roof.getRightGable().getUuid().equals(uuid_))
        {
            return Optional.of(this.roof.getRightGable());
        }

        return Optional.empty();
    }

    public Optional<Slope> getSlopeFromUUID(UUID uuid_)
    {
        if(this.roof.getSlope().getUuid().equals(uuid_))
        {
            return Optional.of(this.roof.getSlope());
        }

        return Optional.empty();
    }

    public Optional<Extension> getExtensionFromUUID(UUID uuid_)
    {
        if(this.roof.getExtension().getUuid().equals(uuid_))
        {
            return Optional.of(this.roof.getExtension());
        }

        return Optional.empty();
    }

    public RoofDTO getRoofDTO()
    {
        return new RoofDTO(roof);
    }

    public GableDTO getLeftGableDTO()
    {
        return new GableDTO(this.roof.getLeftGable());
    }

    public GableDTO getRightGableDTO()
    {
        return new GableDTO(this.roof.getRightGable());
    }

    public SlopeDTO getSlopeDTO()
    {
        return new SlopeDTO(this.roof.getSlope());
    }

    public ExtensionDTO getExtensionDTO()
    {
        return new ExtensionDTO(this.roof.getExtension());
    }

    public float getRoofAngle()
    {
        return this.roof.getAngle();
    }

    /**
     * Returns the walls in an array where the first one is the requested wall and the others are in clockwise order.
     * That means that the second wall is on the left of the requested, the third in front of the requested and the
     * last (4th) is on the right of the requested.
     * @param requestedOrientation_ : determines which wall is the first in the array.
     * @return the array of walls in the order described in the description.
     */
    public Wall[] getWallCorrespondence(Orientation requestedOrientation_)
    {
        return switch (requestedOrientation_) {
            case FRONT -> new Wall[]{frontWall, leftWall, backWall, rightWall};
            case LEFT -> new Wall[]{leftWall, backWall, rightWall, frontWall};
            case BACK -> new Wall[]{backWall, rightWall, frontWall, leftWall};
            case RIGHT -> new Wall[]{rightWall, frontWall, leftWall, backWall};
            default -> throw new IllegalArgumentException("The requested orientation is not valid.");
        };
    }

    public void removeAccessoryWithUUID(UUID uuid_) {
        for(Wall wall: this.getWallCorrespondence(Orientation.FRONT))
        {
            for(IAccessory accessory: wall.getAccessories())
            {
                if(accessory.getUUID().equals(uuid_))
                {
                    wall.removeAccessory(accessory);
                    return;
                }
            }
        }
    }

    /**
     * Revalidates all the accessories in all the walls.
     */
    public void revalidateAllAccessories()
    {
        for (Wall wall : this.getWallCorrespondence(Orientation.FRONT))
        {
            wall.revalidateAllAccessories();
        }
    }

    /**
     * Updates the thickness of all the walls.
     * @param thickness_ : The new thickness of the walls.
     */
    public void updateWallThickness(Imperial thickness_)
    {
        this.panelThickness = thickness_;
        this.leftWall.setThickness(thickness_);
        this.backWall.setThickness(thickness_);
        this.rightWall.setThickness(thickness_);
        this.frontWall.setThickness(thickness_);
        this.roof.setThickness(thickness_);
    }

    /**
     * Gets the thickness of the walls.
     * @return The thickness of the walls.
     */
    public Imperial getWallThickness()
    {
        return this.panelThickness;
    }

    public void updateRoofAngle(float angle)
    {
        assert(angle >= 0.0f && angle < 90.f);
        this.roof.setAngle(angle);

        Wall[] walls = this.getWallCorrespondence(this.getRoofOrientation());
        float height = walls[3].getWidth().minus(walls[3].getImprecision().divide(2)).getRawInchValueFloat() * (float) Math.tan(Math.toRadians(angle));
        this.roof.getExtension().setHeight(Imperial.fromFloat(height));
        this.roof.getLeftGable().setHeight(Imperial.fromFloat(height));
        this.roof.getRightGable().setHeight(Imperial.fromFloat(height));
        this.roof.getSlope().setHeight(Imperial.fromFloat(height));
    }

    public void updateImprecision(Imperial imprecision_) {
        this.imprecision = imprecision_;
        this.leftWall.setImprecision(imprecision_);
        this.backWall.setImprecision(imprecision_);
        this.rightWall.setImprecision(imprecision_);
        this.frontWall.setImprecision(imprecision_);
    }

    public Imperial getImprecision() {
        return this.imprecision;
    }

    public void updateRoofOrientation(Orientation orientation_)
    {
        this.roofOrientation = orientation_;
        this.roof.setOrientation(orientation_);

        switch(orientation_)
        {
            case FRONT:
            case BACK:
                this.frontWall.setOvertaking(true);
                this.backWall.setOvertaking(true);
                this.leftWall.setOvertaking(false);
                this.rightWall.setOvertaking(false);
                break;

            case LEFT:
            case RIGHT:
                this.frontWall.setOvertaking(false);
                this.backWall.setOvertaking(false);
                this.leftWall.setOvertaking(true);
                this.rightWall.setOvertaking(true);
                break;

            default:
                System.err.println("The requested orientation is not valid.");

        }
        updatePanelPositions();
    }

    public Orientation getRoofOrientation()
    {
        return this.roofOrientation;
    }

    public void updateMinDistanceBetweenAccessories(Imperial minDistance_)
    {
        this.minDistanceBetweenAccessories = minDistance_;
        this.leftWall.setMinDistance(minDistance_);
        this.backWall.setMinDistance(minDistance_);
        this.rightWall.setMinDistance(minDistance_);
        this.frontWall.setMinDistance(minDistance_);
    }

    public Imperial getMinDistanceBetweenAccessories()
    {
        return this.minDistanceBetweenAccessories;
    }

    /**
     * Updates the y position of all the doors to be at the height of the minimum distance between accessories.
     * This is used to make the doors valid.
     */
    public void updateDoorPositions()
    {
        for(Wall wall : this.getWallCorrespondence(Orientation.FRONT))
        {
            for(IAccessory accessory : wall.getAccessories())
            {
                if(accessory.shouldAlignWithFloor())
                {
                    accessory.setY((Imperial) this.minDistanceBetweenAccessories.clone());
                }
            }
        }
    }

    /**
     * Returns the new relative position of an accessory after a change in the dimensions of the wall.
     *
     * @param oldPosition               The old relative position of the accessory.
     * @param oldCorrespondingDimension The old dimension of the wall in the same axis as the old position.
     * @param newCorrespondingDimension The new dimension of the wall in the same axis as the old position.
     * @return The new relative position of the accessory.
     */
    public static Imperial getNewRelativePosition(Imperial oldPosition, Imperial oldCorrespondingDimension, Imperial newCorrespondingDimension) {
        double realValue = oldPosition.getRealRawInchValueDouble() * newCorrespondingDimension.getRawInchValue() / oldCorrespondingDimension.getRawInchValue();
        Imperial newPosition = Imperial.fromDouble(realValue);
        newPosition.setRealValue(realValue);
        return newPosition;
    }
}
