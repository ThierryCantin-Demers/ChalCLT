package ca.ulaval.glo2004.domaine.chalet.dto;

import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.domaine.chalet.Wall;
import ca.ulaval.glo2004.util.math.Imperial;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class that represents a DTO of a wall in the chalet.
 */
public class WallDTO {
    /**
     * The UUID of the wall.
     */
    public UUID uuid;

    /**
     * The x coordinate of the point located at the bottom left of the wall.
     */
    public Imperial x;

    /**
     * The y position of the point located at the bottom left of the wall.
     */
    public Imperial y;

    /**
     * The z position of the point located at the bottom left of the wall.
     */
    public Imperial z;

    /**
     * The width of the wall.
     */
    public Imperial width;

    /**
     * The height of the wall.
     */
    public Imperial height;

    /**
     * The list of accessories that are on the wall.
     */
    public ArrayList<AccessoryDTO> accessories;

    /**
     * The minimum distance between accessories and with the borders of the wall.
     */
    public Imperial minDistance;

    /**
     * The orientation of the wall.
     */
    public Orientation wallOrientation;

    public boolean isOvertaking;
    public Imperial thickness;
    public Imperial imprecision;

    /**
     * Creates a new wall DTO from a wall.
     * @param wall_ : The wall to create the DTO from.
     */
    public WallDTO(Wall wall_) {
        this.uuid = wall_.getUUID();
        this.x = (Imperial) wall_.getX().clone();
        this.y = (Imperial) wall_.getY().clone();
        this.z = (Imperial) wall_.getZ().clone();
        this.width = (Imperial) wall_.getWidth().clone();
        this.height = (Imperial) wall_.getHeight().clone();
        this.accessories = new ArrayList<>();
        for (IAccessory accessory: wall_.getAccessories()) {
            this.accessories.add(new AccessoryDTO(accessory));
        }
        this.minDistance = (Imperial) wall_.getMinDistance().clone();
        this.wallOrientation = wall_.getWallOrientation();
        this.isOvertaking = wall_.isOvertaking();
        this.thickness = (Imperial) wall_.getThickness().clone();
        this.imprecision = (Imperial) wall_.getImprecision().clone();
    }
    public Imperial realWidth()
    {
        if(isOvertaking)
        {
            return width;
        }
        else {
            return width.minus(thickness).minus(imprecision);
        }
    }

    public Imperial realInteriorWidth()
    {
        return realWidth().minus(thickness).minus(imprecision);
    }

}
