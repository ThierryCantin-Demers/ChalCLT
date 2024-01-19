package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Orientation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoofOrientationChangeListener implements ActionListener {
    private final Controller controller;

    public RoofOrientationChangeListener(Controller controller_) {
        this.controller = controller_;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<?> cb = (JComboBox<?>) e.getSource();
        Object selectedItem = cb.getSelectedItem();
        if (selectedItem instanceof Orientation orientation) {
            this.controller.changeRoofOrientation(orientation);
        } else {
            System.err.println("Selected item is not an Orientation");
        }
    }
}
