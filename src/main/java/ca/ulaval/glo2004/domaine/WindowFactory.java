package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.domaine.chalet.Window;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Point2D;

/**
 * Factory for creating windows.
 */
public class WindowFactory implements IAccessoryFactory {
    /**
     * Creates a window.
     * @param position_ : The position of the window.
     * @param dimensions_ : The dimensions of the window.
     * @return The created window.
     */
    @Override
    public IAccessory createAccessory(Point2D<Imperial> position_, Dimensions<Imperial> dimensions_) {
        return new Window(position_.x(), position_.y(), dimensions_.getWidth(), dimensions_.getHeight());
    }

    /**
     * Creates a window with default dimensions.
     * @param position_ : The position of the window.
     * @return The created window.
     */
    @Override
    public IAccessory createAccessory(Point2D<Imperial> position_) {
        return new Window(position_.x(), position_.y());
    }
}
