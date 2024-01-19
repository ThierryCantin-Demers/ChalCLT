package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.gui.events.EventType;

public class ChangeRoofOrientationCommand implements ICommand{
    private Controller controller;
    private Orientation newOrientation;
    private Orientation oldOrientation;

    public ChangeRoofOrientationCommand(Controller controller_, Orientation orientation_){
        this.controller = controller_;
        this.newOrientation = orientation_;
        this.oldOrientation = this.controller.getRoofOrientation();
    }

    @Override
    public void execute() {
        this.controller.updateRoofOrientation(this.newOrientation);

        this.controller.notify(EventType.ROOF_ORIENTATION_CHANGED);
    }

    @Override
    public void undo() {
        this.controller.updateRoofOrientation(this.oldOrientation);

        this.controller.notify(EventType.ROOF_ORIENTATION_CHANGED);
    }
}
