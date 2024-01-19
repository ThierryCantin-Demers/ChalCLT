package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.util.math.Dimensions;
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
public class AccessoryDimensionsChangeListener implements ActionListener, FocusListener {
    /**
     * The controller of the application.
     */
    private final Controller controller;

    /**
     * The text field that contains the width of the accessory.
     */
    private final JTextField widthField;

    /**
     * The text field that contains the height of the accessory.
     */
    private final JTextField heightField;

    /**
     * Creates a new AccessoryDimensionsChangeListener.
     * Needs the text field to be able to get the new dimensions.
     * @param controller_ : The controller of the application.
     * @param widthField_ : The text field that contains the new width of the accessory.
     * @param heightField_ : The text field that contains the new height of the accessory.
     */
    public AccessoryDimensionsChangeListener(Controller controller_, JTextField widthField_, JTextField heightField_)
    {
        this.controller = controller_;
        this.widthField = widthField_;
        this.heightField = heightField_;
    }

    /**
     * Changes the dimensions of the selected accessory.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        changeAccessoryDimensions();
    }

    @Override
    public void focusGained(FocusEvent e) {
        //do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        changeAccessoryDimensions();
    }

    private void changeAccessoryDimensions() {
        Optional<Imperial> width = Imperial.fromString(widthField.getText());
        Optional<Imperial> height = Imperial.fromString(heightField.getText());

        if (width.isPresent() && height.isPresent()) {
            this.controller.changeSelectedAccessoryDimensions(new Dimensions<>(width.get(), height.get()));
        } else {
            this.controller.getSelectedAccessoryDTO().ifPresent(accessoryDTO -> {
                this.widthField.setText(accessoryDTO.width.toString());
                this.heightField.setText(accessoryDTO.height.toString());
            });
        }
    }
}
