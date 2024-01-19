package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.util.math.Imperial;

/**
 * Command that changes the thickness of the walls.
 */
public class ChangeWallThicknessCommand implements ICommand{
    /**
     * The controller of the application.
     */
    private Controller controller;

    /**
     * The new thickness of the walls.
     */
    private Imperial newThickness;

    /**
     * The old thickness of the walls.
     */
    private Imperial oldThickness;

    /**
     * constructor
     * @param controller_ : The controller of the application.
     * @param newThickness_ : The new thickness of the walls.
     */
    public ChangeWallThicknessCommand(Controller controller_, Imperial newThickness_) {
        this.controller = controller_;
        this.newThickness = newThickness_;
        this.oldThickness = this.controller.getWallThickness();
    }

    /**
     * Changes the thickness of the walls to the new thickness.
     * Notifies the controller that the thickness of the walls has been changed.
     */
    @Override
    public void execute() {
        this.controller.updateWallThickness(this.newThickness);
        this.controller.revalidateAllAccessories();
        this.controller.notify(EventType.WALL_THICKNESS_CHANGED);
    }

    /**
     * Reverts the thickness of the walls to the old thickness.
     * Notifies the controller that the thickness of the walls has been changed.
     */
    @Override
    public void undo() {
        this.controller.updateWallThickness(this.oldThickness);
        this.controller.revalidateAllAccessories();
        this.controller.notify(EventType.WALL_THICKNESS_CHANGED);
    }
}
