package ca.ulaval.glo2004.gui.selection;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;

/**
 * This interface represents a factory that can create the components of a selection panel.
 */
public interface ISelectionPanelComponentsFactory {
    SelectionPanelComponents createComponents(Selectable selectable_, Controller controller_);
}
