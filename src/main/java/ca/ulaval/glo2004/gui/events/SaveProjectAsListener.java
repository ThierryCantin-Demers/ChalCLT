package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveProjectAsListener implements ActionListener {
    private final Controller controller;

    public SaveProjectAsListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.saveProjectAs();
    }

    private void saveProjectAs() {
        this.controller.saveProjectAs();
    }
}
