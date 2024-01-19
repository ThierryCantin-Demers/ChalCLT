package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.util.math.Imperial;

/**
 * This class represents the command to change the minimum distance between accessories.
 */
public class ChangeMinDistanceCommand implements ICommand{
    /**
     * The application controller.
     */
    private Controller controller;

    /**
     * The new minimum distance between accessories.
     */
    private Imperial newMinDistance;

    /**
     * The old minimum distance between accessories.
     */
    private Imperial oldMinDistance;

    /**
     * Constructor
     * @param controller_ : The application controller.
     * @param newMinDistance_ : The new minimum distance between accessories.
     */
    public ChangeMinDistanceCommand(Controller controller_, Imperial newMinDistance_){
        this.controller = controller_;
        this.newMinDistance = newMinDistance_;
        this.oldMinDistance = controller.getMinDistance();
    }

    /**
     * Update the minimum distance between accessories to the new value.
     * Notify the observers that the minimum distance has changed.
     */
    @Override
    public void execute() {
        this.controller.updateMinDistance(this.newMinDistance);
        this.controller.updateDoorPositions();
        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.MIN_DISTANCE_CHANGED);
    }

    /**
     * Update the minimum distance between accessories to the old value.
     * Notify the observers that the minimum distance has changed.
     */
    @Override
    public void undo() {
        this.controller.updateMinDistance(this.oldMinDistance);
        this.controller.updateDoorPositions();
        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.MIN_DISTANCE_CHANGED);
    }
}
