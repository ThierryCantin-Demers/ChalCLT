package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;

/**
 * Command that changes the dimensions of an accessory.
 */
public class ChangeAccessoryDimensionsCommand implements ICommand{
    /**
     * The controller of the application.
     */
    Controller controller;

    /**
     * The accessory that will be modified.
     */
    IAccessory accessory;

    /**
     * The new dimensions of the accessory.
     */
    Dimensions<Imperial> newDimensions;

    /**
     * The old dimensions of the accessory.
     */
    Dimensions<Imperial> oldDimensions;

    /**
     * constructor
     * @param controller_ : The controller of the application.
     * @param accessory_ : The accessory that will be modified.
     * @param newDimensions_ : The new dimensions of the accessory.
     */
    public ChangeAccessoryDimensionsCommand(Controller controller_, IAccessory accessory_, Dimensions<Imperial> newDimensions_)
    {
        this.controller = controller_;
        this.accessory = accessory_;
        this.newDimensions = newDimensions_;
        this.oldDimensions = new Dimensions<>(accessory_.getWidth(), accessory_.getHeight());
    }

    public ChangeAccessoryDimensionsCommand(Controller controller_, IAccessory accessory_, Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_)
    {
        this.controller = controller_;
        this.accessory = accessory_;
        this.newDimensions = newDimensions_;
        this.oldDimensions = oldDimensions_;
    }


    /**
     * Changes the dimensions of the accessory.
     * Rechecks the validity of all the accessories.
     * Notifies the controller that an accessory has been modified.
     */
    @Override
    public void execute() {
        this.accessory.setWidth(this.newDimensions.getWidth());
        this.accessory.setHeight(this.newDimensions.getHeight());
        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.ACCESSORY_MODIFIED);
    }

    /**
     * Reverts the dimensions of the accessory to the original value.
     * Rechecks the validity of all the accessories.
     * Notifies the controller that an accessory has been modified.
     */
    @Override
    public void undo() {
        this.accessory.setWidth(this.oldDimensions.getWidth());
        this.accessory.setHeight(this.oldDimensions.getHeight());
        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.ACCESSORY_MODIFIED);
    }
}
