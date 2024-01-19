package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener that listens for a redo action.
 */
public class RedoListener implements ActionListener {
    /**
     * The application controller
     */
    Controller controller;

    /**
     * constructor
     * @param controller_ : The application controller
     */
    public RedoListener(Controller controller_)
    {
        this.controller = controller_;
    }

    /**
     * Redo the last action on event
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        redo();
    }

    /**
     * Redo the last action.
     */
    private void redo()
    {
        this.controller.redo();
    }
}
