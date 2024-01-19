package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowAllPanelsListener implements ActionListener {
    private final Controller controller;

    public ShowAllPanelsListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JCheckBox toggleButton = (JCheckBox) e.getSource();

            this.controller.changeShowAllPanels(toggleButton.isSelected());
        }
        catch(ClassCastException ignored)
        {
        }
    }
}
