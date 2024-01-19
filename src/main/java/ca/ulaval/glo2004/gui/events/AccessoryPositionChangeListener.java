package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Point2D;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;


/**
 * This class represents a listener that listens to changes in the accessory position.
 */
public class AccessoryPositionChangeListener implements ActionListener, FocusListener {
    /**
     * The controller of the application.
     */
    private final Controller controller;

    /**
     * The text field that contains the x position of the accessory.
     */
    private final JTextField xField;

    /**
     * The text field that contains the y position of the accessory.
     */
    private final JTextField yField;

    /**
     * Creates a new AccessoryPositionChangeListener.
     * Needs the text field to be able to get the new position.
     * @param controller_ : The controller of the application.
     * @param yField_ : The text field that contains the new x position of the accessory.
     * @param xField_ : The text field that contains the new y position of the accessory.
     */
    public AccessoryPositionChangeListener(Controller controller_, JTextField xField_, JTextField yField_)
    {
        this.controller = controller_;
        this.xField = xField_;
        this.yField = yField_;
    }

    /**
     * Changes the position of the selected accessory.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        changeAccessoryPosition();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeAccessoryPosition();
    }

    private void changeAccessoryPosition()
    {
        Optional<Imperial> x = Imperial.fromString(xField.getText());
        Optional<Imperial> y = Imperial.fromString(yField.getText());

        if(x.isPresent() && y.isPresent()) {
            this.controller.changeSelectedAccessoryPosition(new Point2D<>(x.get(), y.get()));
        }
        else {
            this.controller.getSelectedAccessoryDTO().ifPresent(accessoryDTO -> {
                this.xField.setText(accessoryDTO.x.toString());
                this.yField.setText(accessoryDTO.y.toString());
            });
        }
    }
}
