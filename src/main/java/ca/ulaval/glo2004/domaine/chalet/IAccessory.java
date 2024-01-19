package ca.ulaval.glo2004.domaine.chalet;

import ca.ulaval.glo2004.util.math.Imperial;

import java.io.Serializable;
import java.util.UUID;

/**
 * Interface that represents an accessory on a wall.
 */
public interface IAccessory extends Serializable {
    Imperial getX();
    void setX(Imperial x_);
    Imperial getY();
    void setY(Imperial y_);
    Imperial getWidth();
    void setWidth(Imperial width_);
    Imperial getHeight();
    void setHeight(Imperial height_);
    UUID getUUID();
    boolean isValid();
    void setIsValid(boolean valid_);
    boolean intersectsWith(IAccessory accessory_, Imperial minDistance);
    String getName();
    boolean shouldAlignWithFloor();
}
