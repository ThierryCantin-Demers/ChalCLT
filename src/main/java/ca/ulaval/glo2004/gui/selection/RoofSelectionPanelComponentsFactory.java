package ca.ulaval.glo2004.gui.selection;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.domaine.chalet.dto.RoofDTO;
import ca.ulaval.glo2004.gui.ComponentBoxGroupBuilder;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.gui.events.RoofAngleChangeListener;
import ca.ulaval.glo2004.gui.events.RoofOrientationChangeListener;

import javax.swing.*;

public class RoofSelectionPanelComponentsFactory implements ISelectionPanelComponentsFactory{
    @Override
    public SelectionPanelComponents createComponents(Selectable selectable_, Controller controller_) {
        RoofDTO roofDTO = controller_.getRoofDTO();


        SelectionPanelComponents components = new SelectionPanelComponents();
        components.setLayout(new BoxLayout(components, BoxLayout.PAGE_AXIS));

        // Type
        components.add(new JLabel("Roof"));



        // Angle
        JTextField angleField = new JTextField(String.valueOf(roofDTO.angle));
        angleField.setToolTipText("Angle");

        RoofAngleChangeListener roofAngleChangeListener = new RoofAngleChangeListener(controller_, angleField);
        angleField.addActionListener(roofAngleChangeListener);
        angleField.addFocusListener(roofAngleChangeListener);

        controller_.subscribe(EventType.ROOF_ANGLE_CHANGED, components,
                () -> angleField.setText(String.valueOf(controller_.getRoofAngle())));


        ComponentBoxGroupBuilder angleBuilder = new ComponentBoxGroupBuilder();
        JPanel anglePanel = angleBuilder.addGroup().
                addGroup().addTextFieldLabelPair(angleField).generate();

        components.add(anglePanel);

        // Roof orientation
        JLabel roofOrientationLabel = new JLabel("Roof orientation ");
        JComboBox<Orientation> roofOrientationCombo = new JComboBox<>();
        roofOrientationCombo.addItem(Orientation.FRONT);
        roofOrientationCombo.addItem(Orientation.LEFT);
        roofOrientationCombo.addItem(Orientation.BACK);
        roofOrientationCombo.addItem(Orientation.RIGHT);
        roofOrientationCombo.setSelectedItem(controller_.getRoofOrientation());
        roofOrientationCombo.setToolTipText("The direction/wall the roof is facing towards");

        roofOrientationCombo.addActionListener(new RoofOrientationChangeListener(controller_));

        controller_.subscribe(EventType.ROOF_ORIENTATION_CHANGED, components,
                () -> roofOrientationCombo.setSelectedItem(controller_.getRoofOrientation()));

        ComponentBoxGroupBuilder roofOrientationBuilder = new ComponentBoxGroupBuilder();
        JPanel roofOrientationPanel = roofOrientationBuilder.
                addGroup().addComponent(roofOrientationCombo).addComponent(roofOrientationLabel).generate();

        components.add(roofOrientationPanel);

        return components;
    }
}
