package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class represents a listener that is triggered when the selected object changes.
 * Currently, this event can only be triggered by the tree view,
 * but it could be triggered by other components in the future
 * (e.g. a mouse click in the canvas to select an object in the 3D scene).
 */
public class SelectionChangeListener implements TreeSelectionListener {
    /**
     * The controller of the application.
     */
    private final Controller controller;

    /**
     * Creates a new SelectionChangeListener.
     * @param controller_ : The controller of the application.
     */
    public SelectionChangeListener(Controller controller_)
    {
        this.controller = controller_;
    }

    /**
     * Changes the selected object and the factory to create the components of the selection panel.
     * @param e the event that characterizes the change.
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode selectedNode;
        try
        {
            selectedNode = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
        }
        catch (NullPointerException exception)
        {
            selectedNode = (DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent();
        }

        if(selectedNode == null || selectedNode.getUserObject() == null)
        {
            return;
        }

        if(selectedNode.getUserObject() instanceof Selectable selectable)
        {
            this.changeSelectedObject(selectable);
        }
    }

    /**
     * Changes the selected object and the factory to create the components of the selection panel.
     * @param selectable_ : The object that is selected.
     */
    private void changeSelectedObject(Selectable selectable_)
    {
        this.controller.changeSelectedObject(selectable_);
    }
}
