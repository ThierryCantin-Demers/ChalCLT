package ca.ulaval.glo2004.gui.selection;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;

import javax.swing.*;

/**
 * This class represents a factory that can create the components of a selection panel for when nothing is selected.
 */
public class NoneSelectionPanelComponentsFactory implements ISelectionPanelComponentsFactory {
    /**
     * Creates the components of a selection panel when nothing is selected.
     * @param selectable_ : The object that is selected.
     * @param controller_ : The controller of the application.
     * @return The panel containing the components of a selection panel for when nothing is selected.
     */
    @Override
    public SelectionPanelComponents createComponents(Selectable selectable_, Controller controller_) {
        SelectionPanelComponents components = new SelectionPanelComponents();
        components.add(new JLabel("Nothing is selected right now"));

        return components;
    }
}
