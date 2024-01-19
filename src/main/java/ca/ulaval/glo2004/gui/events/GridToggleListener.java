package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridToggleListener implements ActionListener {

    private final Controller controller;

    public GridToggleListener(Controller controller_)
    {
        this.controller = controller_;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JCheckBox toggleButton = (JCheckBox) e.getSource();

            this.controller.changeGridToggle(toggleButton.isSelected());
        }
        catch(ClassCastException ignored)
        {
        }
    }
}
