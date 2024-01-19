package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Point2D;

/**
 * Interface for accessory factories.
 */
public interface IAccessoryFactory {
    IAccessory createAccessory(Point2D<Imperial> position_, Dimensions<Imperial> dimensions_);
    IAccessory createAccessory(Point2D<Imperial> position_);
}
