package ca.ulaval.glo2004.gui.selection;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.DoorFactory;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.domaine.WindowFactory;
import ca.ulaval.glo2004.domaine.chalet.dto.WallDTO;
import ca.ulaval.glo2004.gui.ComponentBoxGroupBuilder;
import ca.ulaval.glo2004.gui.events.*;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Point2D;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * This class represents a factory that can create the components of a selection panel for a wall.
 */
public class WallSelectionPanelComponentsFactory implements ISelectionPanelComponentsFactory {
    /**
     * Creates the components of a selection panel for a wall.
     * @param selectable_ : The object that is selected.
     * @param controller_ : The controller of the application.
     * @return The panel containing the components of a selection panel for a wall.
     */
    @Override
    public SelectionPanelComponents createComponents(Selectable selectable_, Controller controller_) {
        Optional<WallDTO> wallOpt = controller_.getWallDTOFromUUID(selectable_.getUUID());

        if(wallOpt.isEmpty())
        {
            return new NoneSelectionPanelComponentsFactory().createComponents(selectable_, controller_);
        }

        WallDTO wall = wallOpt.get();

        SelectionPanelComponents components = new SelectionPanelComponents();
        components.setLayout(new BoxLayout(components, BoxLayout.PAGE_AXIS));

        // Type
        components.add(new JLabel(wall.wallOrientation.toString() + " wall"));

        // Position
        JTextField xPosition = new JTextField(wall.x.toString());
        JTextField yPosition = new JTextField(wall.y.toString());
        JTextField zPosition = new JTextField(wall.z.toString());
        xPosition.setEditable(false);
        yPosition.setEditable(false);
        zPosition.setEditable(false);
        xPosition.setToolTipText("x");
        yPosition.setToolTipText("y");
        zPosition.setToolTipText("z");
        xPosition.setPreferredSize(new Dimension(50,20));
        yPosition.setPreferredSize(new Dimension(50,20));
        zPosition.setPreferredSize(new Dimension(50,20));

        ComponentBoxGroupBuilder positionBuilder = new ComponentBoxGroupBuilder();
        JPanel positionPanel = positionBuilder.addGroup("Position:").
                                               addGroup().addTextFieldLabelPair(xPosition).
                                               addGroup().addTextFieldLabelPair(yPosition).
                                               addGroup().addTextFieldLabelPair(zPosition).generate();

        components.add(positionPanel);

        // Dimensions
        JTextField widthField = new JTextField(wall.width.toString());
        widthField.setToolTipText("Width");

        JTextField heightField = new JTextField(wall.height.toString());
        heightField.setToolTipText("Height");

        WallDimensionsChangeListener dimensionsListener = new WallDimensionsChangeListener(controller_, widthField, heightField);
        widthField.addActionListener(dimensionsListener);
        heightField.addActionListener(dimensionsListener);
        widthField.addFocusListener(dimensionsListener);
        heightField.addFocusListener(dimensionsListener);

        controller_.subscribe(EventType.WALL_DIMENSIONS_CHANGED, components, () -> controller_.getSelectedWallDTO().ifPresentOrElse(
                wallDTO -> {
                    widthField.setText(wallDTO.width.toString());
                    heightField.setText(wallDTO.height.toString());
                },
                () -> {
                    widthField.setText("No wall is currently selected.");
                    heightField.setText("No wall is currently selected.");
                }));

        ComponentBoxGroupBuilder dimensionsBuilder = new ComponentBoxGroupBuilder();
        JPanel dimensionsPanel = dimensionsBuilder.addGroup("Dimensions:").
                                                   addGroup().addTextFieldLabelPair(widthField).
                                                   addGroup().addTextFieldLabelPair(heightField).generate();

        components.add(dimensionsPanel);

        // Accessory
        JButton addDoorButton = new JButton("Add door to wall");
        addDoorButton.addActionListener((e)-> {
            controller_.addAccessoryToSelectedWall(new Point2D<>(new Imperial(), new Imperial()), new DoorFactory());
            controller_.updateDoorPositions();
        });

        JButton addWindowButton = new JButton("Add window to wall");
        addWindowButton.addActionListener((e)-> controller_.addAccessoryToSelectedWall(new Point2D<>(new Imperial(), new Imperial()), new WindowFactory()));


        ComponentBoxGroupBuilder accessoriesBuilder = new ComponentBoxGroupBuilder();
        JPanel accessoriesPanel = accessoriesBuilder.addGroup("Accessories:").
                                                     addGroup().addComponent(addDoorButton).addComponent(addWindowButton).generate();

        components.add(accessoriesPanel);

        return components;
    }
}
