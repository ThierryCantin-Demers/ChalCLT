package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domaine.Controller;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame{
    private JPanel root;

    private ExplorerPanel explorerPanel;
    private SelectionPanel selectionPanel;
    private ScenePanel scenePanel;
    private MainWindowMenuBar mainWindowMenuBar;

    private JSplitPane leftSplit;
    private JSplitPane rightSplit;

    private Controller controller;

    public MainWindow()
    {
        super();
        System.out.println("Building main window");
        root = new JPanel();
        root.setLayout(new GridLayout());

        rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftSplit.setRightComponent(rightSplit);
        rightSplit.setResizeWeight(1);
        leftSplit.setResizeWeight(0);

        root.add(leftSplit);

        this.setContentPane(root);
    }

    public void setController(Controller controller_)
    {
        this.controller = controller_;
    }

    public void setExplorerPanel(ExplorerPanel panel)
    {
        explorerPanel = panel;
        leftSplit.setLeftComponent(explorerPanel);
    }

    public void setSelectionPanel(SelectionPanel panel)
    {
        selectionPanel = panel;
        rightSplit.setRightComponent(selectionPanel);
    }

    public void setScenePanel(ScenePanel panel)
    {
        scenePanel = panel;
        rightSplit.setLeftComponent(scenePanel);
    }

    public void setMainWindowMenuBar(MainWindowMenuBar menuBar)
    {
        mainWindowMenuBar = menuBar;
        this.setJMenuBar(mainWindowMenuBar);
    }
}
