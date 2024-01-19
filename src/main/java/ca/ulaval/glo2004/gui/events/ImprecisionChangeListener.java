package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.util.math.Imperial;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;

public class ImprecisionChangeListener implements ActionListener, FocusListener {

    private final Controller controller;
    private final JTextField imprecisionField;

    public ImprecisionChangeListener(Controller controller_, JTextField imprecisionField_) {
        this.controller = controller_;
        this.imprecisionField = imprecisionField_;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        changeImprecision();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeImprecision();
    }

    private void changeImprecision() {
        Optional<Imperial> imprecision = Imperial.fromString(imprecisionField.getText());

        imprecision.ifPresentOrElse(controller::changeImprecision,
                () -> imprecisionField.setText(controller.getImprecision().toString()));
    }
}
