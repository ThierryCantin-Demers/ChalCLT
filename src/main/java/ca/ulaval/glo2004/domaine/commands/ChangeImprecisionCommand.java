package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.util.math.Imperial;

public class ChangeImprecisionCommand implements ICommand{
    private final Controller controller;
    private final Imperial newImprecision;
    private final Imperial oldImprecision;

    public ChangeImprecisionCommand(Controller controller_, Imperial newImprecision_){
        this.controller = controller_;
        this.newImprecision = newImprecision_;
        this.oldImprecision = this.controller.getImprecision();
    }

    @Override
    public void execute() {
        this.controller.updateImprecision(this.newImprecision);

        this.controller.notify(EventType.IMPRECISION_CHANGED);
    }

    @Override
    public void undo() {
        this.controller.updateImprecision(this.oldImprecision);

        this.controller.notify(EventType.IMPRECISION_CHANGED);
    }
}
