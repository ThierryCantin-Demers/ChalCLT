package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewProjectListener implements ActionListener {
    private final Controller controller;

    public NewProjectListener(Controller controller_)
    {
        this.controller = controller_;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.controller.generateNewProject();
    }
}
