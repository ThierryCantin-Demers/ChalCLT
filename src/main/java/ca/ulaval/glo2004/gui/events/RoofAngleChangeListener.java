package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class RoofAngleChangeListener implements ActionListener, FocusListener {

    private final Controller controller;

    private final JTextField angleField;


    public RoofAngleChangeListener(Controller controller_, JTextField angleField) {
        this.controller = controller_;
        this.angleField = angleField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        changeRoofAngle();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeRoofAngle();
    }

    private void changeRoofAngle() {
        try {
            float temp = Float.parseFloat(angleField.getText());
            this.controller.changeRoofAngle(temp);
        } catch (NumberFormatException ignored) {
            angleField.setText(String.valueOf(this.controller.getRoofAngle()));
        }
    }
}
