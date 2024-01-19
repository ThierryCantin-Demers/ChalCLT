package ca.ulaval.glo2004.gui.selection;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.gui.events.Observer;

import javax.swing.*;

public class SelectionPanelComponents extends JPanel implements Observer {
    public void unsubscribe(Controller controller_) {
        controller_.unsubscribe(this);
    }
}
