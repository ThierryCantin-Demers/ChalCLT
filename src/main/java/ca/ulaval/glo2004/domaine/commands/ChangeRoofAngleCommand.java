package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.gui.events.EventType;

public class ChangeRoofAngleCommand implements ICommand{

    private Controller controller;
    private float oldAngle;
    private float newAngle;

    public ChangeRoofAngleCommand(Controller controller_, float angle){
        this.controller = controller_;
        this.newAngle = angle;
        this.oldAngle = this.controller.getRoofAngle();
    }

    @Override
    public void execute() {
        this.controller.updateRoofAngle(this.newAngle);

        this.controller.notify(EventType.ROOF_ANGLE_CHANGED);
    }

    @Override
    public void undo() {
        this.controller.updateRoofAngle(this.oldAngle);

        this.controller.notify(EventType.ROOF_ANGLE_CHANGED);
    }
}
