package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Point2D;

/**
 * Command that changes the dimensions of an accessory.
 */
public class ChangeAccessoryPositionCommand implements ICommand {
    /**
     * The controller of the application.
     */
    Controller controller;

    /**
     * The accessory that will be modified.
     */
    IAccessory accessory;

    /**
     * The new position of the accessory.
     */
    Point2D<Imperial> newPosition;

    /**
     * The old position of the accessory.
     */
    Point2D<Imperial> oldPosition;

    /**
     * constructor
     *
     * @param controller_  : The controller of the application.
     * @param accessory_   : The accessory that will be modified.
     * @param newPosition_ : The new position of the accessory.
     */
    public ChangeAccessoryPositionCommand(Controller controller_, IAccessory accessory_, Point2D<Imperial> newPosition_) {
        this(controller_, accessory_, new Point2D<>(accessory_.getX(), accessory_.getY()), newPosition_);
    }

    /**
     * constructor
     *
     * @param controller_  : The controller of the application.
     * @param accessory_   : The accessory that will be modified.
     * @param newPosition_ : The new position of the accessory.
     */
    public ChangeAccessoryPositionCommand(Controller controller_, IAccessory accessory_, Point2D<Imperial> oldPosition_, Point2D<Imperial> newPosition_) {
        this.controller = controller_;
        this.accessory = accessory_;
        this.newPosition = newPosition_;
        this.oldPosition = oldPosition_;
    }


    /**
     * Changes the position of the accessory.
     * Rechecks the validity of all the accessories.
     * Notifies the controller that an accessory has been modified.
     */
    @Override
    public void execute() {
        this.accessory.setX(this.newPosition.x());
        this.accessory.setY(this.newPosition.y());
        this.controller.updateDoorPositions();
        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.ACCESSORY_MODIFIED);
    }

    /**
     * Reverts the position of the accessory to the original value.
     * Rechecks the validity of all the accessories.
     * Notifies the controller that an accessory has been modified.
     */
    @Override
    public void undo() {
        this.accessory.setX(this.oldPosition.x());
        this.accessory.setY(this.oldPosition.y());
        this.controller.updateDoorPositions();
        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.ACCESSORY_MODIFIED);
    }
}
