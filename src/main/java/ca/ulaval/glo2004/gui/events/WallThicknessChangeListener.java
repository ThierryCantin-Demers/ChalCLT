package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.util.math.Imperial;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;

/**
 * This class represents a listener that listens to changes in the wall thickness.
 */
public class WallThicknessChangeListener implements ActionListener, FocusListener {
    /**
     * The controller of the application.
     */
    private final Controller controller;

    /**
     * The text field that contains the thickness of the walls.
     */
    private final JTextField thicknessField;

    /**
     * Creates a new WallThicknessChangeListener.
     * @param controller_ : The controller of the application.
     * @param thicknessField_ : The text field that contains the new thickness of the walls.
     */
    public WallThicknessChangeListener(Controller controller_, JTextField thicknessField_) {
        this.controller = controller_;
        this.thicknessField = thicknessField_;
    }

    /**
     * Changes the thickness of the walls.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        changeWallThickness();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeWallThickness();
    }

    private void changeWallThickness() {
        Optional<Imperial> thickness = Imperial.fromString(this.thicknessField.getText());

        thickness.ifPresentOrElse(this.controller::changeWallThickness,
                () -> this.thicknessField.setText(this.controller.getWallThickness().toString()));
    }
}
