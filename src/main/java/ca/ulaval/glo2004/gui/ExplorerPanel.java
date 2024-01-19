package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Selectable;
import ca.ulaval.glo2004.domaine.chalet.dto.*;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.gui.events.Observer;
import ca.ulaval.glo2004.gui.events.SelectionChangeListener;
import ca.ulaval.glo2004.gui.selection.SelectionType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;
import java.util.UUID;

public class ExplorerPanel extends JPanel implements Observer {
    private final Controller controller;

    private JTree componentExplorer;
    private JScrollPane treeScrollPane;
    private DefaultMutableTreeNode treeRoot;
    private DefaultMutableTreeNode leftWallNode;
    private DefaultMutableTreeNode frontWallNode;
    private DefaultMutableTreeNode backWallNode;
    private DefaultMutableTreeNode rightWallNode;

    private DefaultMutableTreeNode extensionNode;
    private DefaultMutableTreeNode leftGableNode;
    private DefaultMutableTreeNode rightGableNode;
    private DefaultMutableTreeNode slopeNode;

    public ExplorerPanel(Controller controller_) {
        super();
        this.controller = controller_;
        setupTree();
        setPreferredSize(new Dimension(300, 800));
        setMinimumSize(new Dimension(200, 0));
    }

    private void setupTree() {
        // Tree
        this.treeRoot = new DefaultMutableTreeNode("Root");

//        toitNode.add(new DefaultMutableTreeNode("Rallonge verticale"));
//        toitNode.add(new DefaultMutableTreeNode("Pignon gauche"));
//        toitNode.add(new DefaultMutableTreeNode("Pignon droit"));
//        toitNode.add(new DefaultMutableTreeNode("Pente"));

        DefaultMutableTreeNode murNode = getWallMutableTreeNode();
        DefaultMutableTreeNode toitNode = getRoofMutableTreeNode();
        this.controller.subscribe(EventType.NEW_PROJECT, this, this::updateWallNodes);

        this.treeRoot.add(toitNode);
        this.treeRoot.add(murNode);

        this.controller.subscribe(EventType.ACCESSORY_ADDED_OR_REMOVED, this, this::updateAccessories);
        this.controller.subscribe(EventType.SELECTED_ITEM_CHANGED, this, this::expandSelectedUUIDNode);

        this.componentExplorer = new JTree(this.treeRoot);
        this.componentExplorer.setRootVisible(false);
        this.componentExplorer.addTreeSelectionListener(new SelectionChangeListener(this.controller));

        this.treeScrollPane = new JScrollPane(this.componentExplorer);

        setLayout(new BorderLayout());
        add(this.treeScrollPane, BorderLayout.CENTER);

        //making menu open by default
        for(int i = 0 ; i < this.componentExplorer.getRowCount(); i++)
        {
            this.componentExplorer.expandRow(i);
        }
    }
    private DefaultMutableTreeNode getRoofMutableTreeNode() {
        DefaultMutableTreeNode toitNode = new DefaultMutableTreeNode("Roof");

        ExtensionDTO extension = this.controller.getExtensionDTO();
        GableDTO leftGable = this.controller.getLeftGableDTO();
        GableDTO rightGable = this.controller.getRightGableDTO();
        SlopeDTO slope = this.controller.getSlopeDTO();

        this.extensionNode = new DefaultMutableTreeNode(new Selectable(extension.getUuid(),"Extension",SelectionType.EXTENSION));
        this.leftGableNode = new DefaultMutableTreeNode(new Selectable(leftGable.getUuid(), "Left Gable", SelectionType.GABLE));
        this.rightGableNode = new DefaultMutableTreeNode(new Selectable(rightGable.getUuid(),"Right Gable", SelectionType.GABLE));
        this.slopeNode = new DefaultMutableTreeNode(new Selectable(slope.getUuid(),"Slope", SelectionType.SLOPE));

        toitNode.add(this.extensionNode);
        toitNode.add(this.leftGableNode);
        toitNode.add(this.rightGableNode);
        toitNode.add(this.slopeNode);

        return toitNode;
    }

    /**
     * Generates the tree node for the walls.
     * @return The tree node for the walls.
     */
    private DefaultMutableTreeNode getWallMutableTreeNode() {
        DefaultMutableTreeNode murNode = new DefaultMutableTreeNode("Walls");
        
        WallDTO[] wallDTOs = this.controller.getWallDTOs();
        this.frontWallNode = new DefaultMutableTreeNode(new Selectable(wallDTOs[0].uuid, wallDTOs[0].wallOrientation.toString(), SelectionType.WALL));
        this.leftWallNode = new DefaultMutableTreeNode(new Selectable(wallDTOs[1].uuid, wallDTOs[1].wallOrientation.toString(), SelectionType.WALL));
        this.backWallNode = new DefaultMutableTreeNode(new Selectable(wallDTOs[2].uuid, wallDTOs[2].wallOrientation.toString(), SelectionType.WALL));
        this.rightWallNode = new DefaultMutableTreeNode(new Selectable(wallDTOs[3].uuid, wallDTOs[3].wallOrientation.toString(), SelectionType.WALL));

        murNode.add(this.frontWallNode);
        murNode.add(this.leftWallNode);
        murNode.add(this.backWallNode);
        murNode.add(this.rightWallNode);
        
        return murNode;
    }

    private void updateWallNodes()
    {
        WallDTO[] wallDTOs = this.controller.getWallDTOs();
        this.frontWallNode.setUserObject(new Selectable(wallDTOs[0].uuid, wallDTOs[0].wallOrientation.toString(), SelectionType.WALL));
        this.leftWallNode.setUserObject(new Selectable(wallDTOs[1].uuid, wallDTOs[1].wallOrientation.toString(), SelectionType.WALL));
        this.backWallNode.setUserObject(new Selectable(wallDTOs[2].uuid, wallDTOs[2].wallOrientation.toString(), SelectionType.WALL));
        this.rightWallNode.setUserObject(new Selectable(wallDTOs[3].uuid, wallDTOs[3].wallOrientation.toString(), SelectionType.WALL));

        this.updateAccessories();

        //making menu open by default
        for(int i = 0 ; i < this.componentExplorer.getRowCount(); i++)
        {
            this.componentExplorer.expandRow(i);
        }
    }

    /**
     * Updates the accessory nodes in the tree.
     */
    private void updateAccessories()
    {
        WallDTO[] walls = this.controller.getWallDTOs();
        
        this.frontWallNode.removeAllChildren();
        this.leftWallNode.removeAllChildren();
        this.backWallNode.removeAllChildren();
        this.rightWallNode.removeAllChildren();

        this.addAllAccessoriesToWallNode(this.frontWallNode, walls[0]);
        this.addAllAccessoriesToWallNode(this.leftWallNode, walls[1]);
        this.addAllAccessoriesToWallNode(this.backWallNode, walls[2]);
        this.addAllAccessoriesToWallNode(this.rightWallNode, walls[3]);

        this.componentExplorer.updateUI();
    }

    /**
     * Adds nodes for all accessories of a wall to the wall node.
     * @param wallNode : The wall node to add the accessories to.
     * @param wall_ : The wall to get the accessories from.
     */
    private void addAllAccessoriesToWallNode(DefaultMutableTreeNode wallNode, WallDTO wall_)
    {
        int accessoryCount = 0;
        for(AccessoryDTO accessoryDTO: wall_.accessories)
        {
            wallNode.add(new DefaultMutableTreeNode(new Selectable(accessoryDTO.uuid, accessoryDTO.name + " " + accessoryCount++, SelectionType.ACCESSORY)));
        }
    }

    /**
     * Expands the node of the selected object if there is one.
     * inspired by <a href="https://stackoverflow.com/a/8210759">...</a>
     */
    private void expandSelectedUUIDNode() {
        this.controller.getSelectedObject().ifPresent(
                selectable -> {
                    TreePath path = findNodePathFromUUID(this.treeRoot, selectable.getUUID());
                    if (path != null) {
                        this.componentExplorer.setSelectionPath(path);
                        this.componentExplorer.scrollPathToVisible(path);
                    }
                });
    }

    /**
     * Finds the node in the tree with the given UUID.
     * inspired by <a href="https://stackoverflow.com/a/8210759">...</a>
     *
     * @param root : The root node to start the search from.
     * @param uuid_ : The UUID to search for.
     * @return The path to the node with the given UUID.
     */
    private TreePath findNodePathFromUUID(DefaultMutableTreeNode root, UUID uuid_) {
        Enumeration<TreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() != null && node.getUserObject() instanceof Selectable selectable) {
                if (selectable.getUUID().equals(uuid_)) {
                    return new TreePath(node.getPath());
                }
            }
        }

        return null;
    }
}