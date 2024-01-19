package ca.ulaval.glo2004.gui.selection;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.domaine.chalet.dto.AccessoryDTO;
import ca.ulaval.glo2004.gui.ComponentBoxGroupBuilder;
import ca.ulaval.glo2004.gui.events.AccessoryDimensionsChangeListener;
import ca.ulaval.glo2004.gui.events.AccessoryPositionChangeListener;
import ca.ulaval.glo2004.gui.events.EventType;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * This class represents a factory that can create the components of a selection panel for when an accessory is selected.
 */
public class AccessorySelectionPanelComponentsFactory implements ISelectionPanelComponentsFactory {
    /**
     * Creates the components of a selection panel for an accessory.
     *
     * @param selectable_ : The object that is selected.
     * @param controller_ : The controller of the application.
     * @return The panel containing the components of a selection panel for an accessory.
     */
    @Override
    public SelectionPanelComponents createComponents(Selectable selectable_, Controller controller_) {
        Optional<AccessoryDTO> accessoryOpt = controller_.getAccessoryDTOFromUUID(selectable_.getUUID());

        if (accessoryOpt.isEmpty()) {
            return new NoneSelectionPanelComponentsFactory().createComponents(selectable_, controller_);
        }

        AccessoryDTO accessory = accessoryOpt.get();

        SelectionPanelComponents components = new SelectionPanelComponents();
        components.setLayout(new BoxLayout(components, BoxLayout.PAGE_AXIS));

        // Type
        components.add(new JLabel(selectable_.getName()));

        // Position
        JTextField xPosition = new JTextField(accessory.x.toString());
        JTextField yPosition = new JTextField(accessory.y.toString());
        xPosition.setEditable(true);
        yPosition.setEditable(true);
        xPosition.setToolTipText("x");
        yPosition.setToolTipText("y");
        xPosition.setPreferredSize(new Dimension(100, 20));
        yPosition.setPreferredSize(new Dimension(100, 20));
        yPosition.setEditable(!accessory.shouldAlignWithFloor);

        AccessoryPositionChangeListener positionListener = new AccessoryPositionChangeListener(controller_, xPosition, yPosition);
        xPosition.addActionListener(positionListener);
        yPosition.addActionListener(positionListener);
        xPosition.addFocusListener(positionListener);
        yPosition.addFocusListener(positionListener);

        controller_.subscribe(EventType.ACCESSORY_MODIFIED, components, () ->
                controller_.getSelectedAccessoryDTO().ifPresentOrElse(
                        accessoryDTO -> {
                            xPosition.setText(accessoryDTO.x.toString());
                            yPosition.setText(accessoryDTO.y.toString());
                        },
                        () -> {
                            xPosition.setText("No accessory is currently selected.");
                            yPosition.setText("No accessory is currently selected.");
                        }));

        ComponentBoxGroupBuilder positionBuilder = new ComponentBoxGroupBuilder();
        JPanel positionPanel = positionBuilder.addGroup("Position:").
                addGroup().addTextFieldLabelPair(xPosition).
                addGroup().addTextFieldLabelPair(yPosition).generate();

        components.add(positionPanel);

        // Dimensions
        JTextField widthField = new JTextField(accessory.width.toString());
        widthField.setToolTipText("Width");

        JTextField heightField = new JTextField(accessory.height.toString());
        heightField.setToolTipText("Height");

        AccessoryDimensionsChangeListener dimensionsListener = new AccessoryDimensionsChangeListener(controller_, widthField, heightField);
        widthField.addActionListener(dimensionsListener);
        heightField.addActionListener(dimensionsListener);
        widthField.addFocusListener(dimensionsListener);
        heightField.addFocusListener(dimensionsListener);

        controller_.subscribe(EventType.ACCESSORY_MODIFIED, components, () -> controller_.getSelectedAccessoryDTO().ifPresentOrElse(
                accessoryDTO -> {
                    widthField.setText(accessoryDTO.width.toString());
                    heightField.setText(accessoryDTO.height.toString());
                },
                () -> {
                    widthField.setText("No accessory is currently selected.");
                    heightField.setText("No accessory is currently selected.");
                })
        );

        ComponentBoxGroupBuilder dimensionsBuilder = new ComponentBoxGroupBuilder();
        JPanel dimensionsPanel = dimensionsBuilder.addGroup("Dimensions:").
                addGroup().addTextFieldLabelPair(widthField).setGroupPreferredSize(100, 20).
                addGroup().addTextFieldLabelPair(heightField).setGroupPreferredSize(100, 20).generate();

        components.add(dimensionsPanel);

        // Remove accessory
        JButton removeAccessoryButton = new JButton("Remove accessory");
        removeAccessoryButton.addActionListener((e) ->
        {
            controller_.changeSelectedObject(new Selectable(null, "None", SelectionType.NONE));
            controller_.removeAccessoryWithUUID(selectable_.getUUID());
        });

        components.add(removeAccessoryButton);

        return components;
    }
}
