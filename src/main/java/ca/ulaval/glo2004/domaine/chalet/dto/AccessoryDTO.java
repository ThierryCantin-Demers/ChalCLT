package ca.ulaval.glo2004.domaine.chalet.dto;

import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.util.math.Imperial;

import java.util.UUID;

/**
 * Class that represents a DTO of an accessory in the chalet.
 */
public class AccessoryDTO {
    /**
     * The x coordinate of the accessory.
     */
    public Imperial x;

    /**
     * The y coordinate of the accessory.
     */
    public Imperial y;

    /**
     * The width of the accessory.
     */
    public Imperial width;

    /**
     * The height of the accessory.
     */
    public Imperial height;

    /**
     * The UUID of the accessory.
     */
    public UUID uuid;

    /**
     * The validity of the accessory.
     */
    public boolean isValid;

    public String name;
    public boolean shouldAlignWithFloor;

    /**
     * Creates a new accessory DTO from an accessory.
     * @param accessory_ : The accessory to create the DTO from.
     */
    public AccessoryDTO(IAccessory accessory_)
    {
        this.uuid = accessory_.getUUID();
        this.x = (Imperial) accessory_.getX().clone();
        this.y = (Imperial) accessory_.getY().clone();
        this.width = (Imperial) accessory_.getWidth().clone();
        this.height = (Imperial) accessory_.getHeight().clone();
        this.isValid = accessory_.isValid();
        this.name = accessory_.getName();
        this.shouldAlignWithFloor = accessory_.shouldAlignWithFloor();
    }
}
