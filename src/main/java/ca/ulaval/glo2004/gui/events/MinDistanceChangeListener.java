package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.util.math.Imperial;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;

public class MinDistanceChangeListener implements ActionListener, FocusListener {

    private final Controller controller;
    private final JTextField minDistanceField;

    public MinDistanceChangeListener(Controller controller_, JTextField minDistanceField_) {
        this.controller = controller_;
        this.minDistanceField = minDistanceField_;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        changeMinDistance();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeMinDistance();
    }

    private void changeMinDistance() {
        Optional<Imperial> minDistance = Imperial.fromString(minDistanceField.getText());

        minDistance.ifPresentOrElse(controller::changeMinDistance,
                () -> minDistanceField.setText(controller.getMinDistance().toString()));
    }
}
