package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.chalet.Door;
import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Point2D;

/**
 * Factory for creating doors.
 */
public class DoorFactory implements IAccessoryFactory{
    /**
     * Creates a door.
     * @param position_ : The position of the door.
     * @param dimensions_ : The dimensions of the door.
     * @return The created door.
     */
    @Override
    public IAccessory createAccessory(Point2D<Imperial> position_, Dimensions<Imperial> dimensions_) {
        return new Door(position_.x(), dimensions_.getWidth(), dimensions_.getHeight());
    }

    /**
     * Creates a door with default dimensions.
     * @param position_ : The position of the door.
     * @return The created door.
     */
    @Override
    public IAccessory createAccessory(Point2D<Imperial> position_) {
        return new Door(position_.x());
    }
}
