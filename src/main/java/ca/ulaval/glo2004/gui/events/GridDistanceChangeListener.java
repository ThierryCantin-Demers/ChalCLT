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
 * This class represents a listener that listens to changes in the accessory dimensions.
 */
public class GridDistanceChangeListener implements ActionListener, FocusListener {
    /**
     * The controller of the application.
     */
    private final Controller controller;

    /**
     * The text field that contains the distance of the grid.
     */
    private final JTextField distanceField;

    /**
     * Creates a new GridDistanceChangeListener.
     * Needs the text field to be able to get the new distance.
     * @param controller_ : The controller of the application.
     * @param distanceField_ : The text field that contains the distance of the grid.
     */
    public GridDistanceChangeListener(Controller controller_, JTextField distanceField_)
    {
        this.controller = controller_;
        this.distanceField = distanceField_;
    }

    /**
     * Changes the grid distance.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        changeGridDistance();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeGridDistance();
    }

    private void changeGridDistance() {
        Optional<Imperial> distance = Imperial.fromString(this.distanceField.getText());

        distance.ifPresentOrElse(this.controller::changeGridDistance,
                () -> this.distanceField.setText(this.controller.getGridDistance().toString()));
    }
}
