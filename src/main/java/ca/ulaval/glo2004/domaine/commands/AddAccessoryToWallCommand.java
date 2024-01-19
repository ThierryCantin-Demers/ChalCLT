package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.domaine.chalet.IAccessory;
import ca.ulaval.glo2004.domaine.chalet.Wall;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.gui.selection.SelectionType;

/**
 * Command that adds an accessory to a wall.
 */
public class AddAccessoryToWallCommand implements ICommand{
    /**
     * The controller of the application.
     */
    Controller controller;

    /**
     * The wall on which the accessory will be added.
     */
    Wall wall;

    /**
     * The accessory that will be added to the wall.
     */
    IAccessory accessory;

    /**
     * constructor
     * @param controller_ : The controller of the application.
     * @param wall_ : The wall on which the accessory will be added.
     * @param accessory_ : The accessory that will be added to the wall.
     */
    public AddAccessoryToWallCommand(Controller controller_, Wall wall_, IAccessory accessory_)
    {
        this.controller = controller_;
        this.wall = wall_;
        this.accessory = accessory_;
    }

    /**
     * Adds the accessory to the wall.
     */
    @Override
    public void execute() {
        this.wall.addAccessory(this.accessory);

        this.controller.notify(EventType.ACCESSORY_ADDED_OR_REMOVED);
    }

    /**
     * Removes the accessory from the wall.
     */
    @Override
    public void undo() {
        this.wall.removeAccessory(this.accessory);

        if (this.controller.getSelectedObject().isPresent() &&
                this.controller.getSelectedObject().get().getUUID() == this.accessory.getUUID()) {
            this.controller.changeSelectedObject(new Selectable(null, "Nothing is selected right now", SelectionType.NONE));
        }

        this.controller.notify(EventType.ACCESSORY_ADDED_OR_REMOVED);
    }
}
