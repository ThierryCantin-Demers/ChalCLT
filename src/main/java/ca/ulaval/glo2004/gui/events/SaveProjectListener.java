package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveProjectListener implements ActionListener {
    private final Controller controller;

    public SaveProjectListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
     this.saveProject();
    }

    private void saveProject() {
        if(this.controller.getProjectPath().isPresent()) {
            this.controller.saveProject();
        } else {
            this.controller.saveProjectAs();
        }
    }
}
