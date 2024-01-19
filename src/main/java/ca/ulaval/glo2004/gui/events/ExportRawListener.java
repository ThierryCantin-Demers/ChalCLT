package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExportRawListener implements ActionListener {
    /**
     * The application controller
     */
    Controller controller;

    /**
     * constructor
     * @param controller_ : The application controller
     */
    public ExportRawListener(Controller controller_) {
        this.controller = controller_;
    }

    /**
     * Undo the last action on event
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        exportRaw();
    }

    /**
     * Undo the last action.
     */
    private void exportRaw() {
        this.controller.exportBrut();
    }
}
