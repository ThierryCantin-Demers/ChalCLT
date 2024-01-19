package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.gui.events.Observer;
import ca.ulaval.glo2004.gui.selection.ISelectionPanelComponentsFactory;
import ca.ulaval.glo2004.gui.selection.NoneSelectionPanelComponentsFactory;
import ca.ulaval.glo2004.gui.selection.SelectionPanelComponents;
import ca.ulaval.glo2004.gui.selection.SelectionType;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.UUID;

/**
 * The panel that displays the selected object's information.
 */
public class SelectionPanel extends JPanel implements Observer {
    /**
     * The sub-panel that contains the selected object's information depending on the type of selected object.
     */
    private SelectionPanelComponents components;

    /**
     * The controller of the application.
     */
    private final Controller controller;

    /**
     * Creates a new SelectionPanel.
     * @param controller_ : The controller of the application.
     */
    public SelectionPanel(Controller controller_)
    {
        super();
        this.controller = controller_;
        this.setPreferredSize(new Dimension(350, 800));
        this.setMinimumSize(new Dimension(350, 0));
        init();
    }

    /**
     * Initializes the components and subscribes to the event that notifies when the selected object changes.
     */
    public void init()
    {
        ISelectionPanelComponentsFactory componentsFactory = new NoneSelectionPanelComponentsFactory();
        this.components = componentsFactory.createComponents(new Selectable(UUID.randomUUID(), "None", SelectionType.NONE), this.controller);

        this.add(components);

        this.controller.subscribe(EventType.SELECTED_ITEM_CHANGED, this, this::selectionChanged);
        this.controller.subscribe(EventType.NEW_PROJECT, this, this::projectReset);
    }

    /**
     * Changes the components to display the selected object's information.
     */
    public void selectionChanged()
    {
        Optional<Selectable> selectedObjectOpt = this.controller.getSelectedObject();
        Selectable selectedObject = selectedObjectOpt.orElse(new Selectable(UUID.randomUUID(), "None", SelectionType.NONE));

        ISelectionPanelComponentsFactory componentsFactory = this.controller.getSelectionPanelComponentsFactory();

        this.components.unsubscribe(this.controller);

        this.remove(this.components);
        this.components = componentsFactory.createComponents(selectedObject, this.controller);
        this.add(this.components);

        this.updateUI();
    }

    /**
     * Resets the components when a new project a created
     */
    private void projectReset()
    {
        this.components.unsubscribe(controller);
        this.remove(this.components);

        ISelectionPanelComponentsFactory componentsFactory = new NoneSelectionPanelComponentsFactory();
        this.components = componentsFactory.createComponents(new Selectable(UUID.randomUUID(), "None", SelectionType.NONE), this.controller);

        this.add(components);

        this.updateUI();
    }
}
