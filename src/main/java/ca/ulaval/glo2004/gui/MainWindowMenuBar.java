package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.gui.events.*;

import javax.swing.*;


public class MainWindowMenuBar extends JMenuBar {
    Controller controller;
    
    private JButton undoButton;
    private JButton redoButton;
    private JButton newProjectButton;
    private JButton loadProjectButton;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton exportRawButton;
    private JButton exportRemovedButton;
    private JButton exportFinishedButton;



    public MainWindowMenuBar(Controller controller_)
    {
        super();
        this.controller = controller_;
        init();
    }

    public void init()
    {
        this.add(Box.createHorizontalGlue());
        undoButton = new JButton("Undo");
        undoButton.addActionListener(new UndoListener(controller));
        this.add(undoButton);

        redoButton = new JButton("Redo");
        redoButton.addActionListener(new RedoListener(controller));
        this.add(redoButton);

        saveButton = new JButton("Save project");
        saveButton.addActionListener(new SaveProjectListener(controller));
        this.add(saveButton);

        saveAsButton = new JButton("Save project as...");
        saveAsButton.addActionListener(new SaveProjectAsListener(controller));
        this.add(saveAsButton);

        newProjectButton = new JButton("New project");
        newProjectButton.addActionListener(new NewProjectListener(controller));
        this.add(newProjectButton);

        loadProjectButton = new JButton("Load project");
        loadProjectButton.addActionListener(new LoadProjectListener(controller));
        this.add(loadProjectButton);

        exportRawButton = new JButton("Export raw");
        exportRawButton.addActionListener(new ExportRawListener(controller));
        this.add(exportRawButton);

        exportRemovedButton = new JButton("Export removed");
        exportRemovedButton.addActionListener(new ExportRemovedListener(controller));
        this.add(exportRemovedButton);

        exportFinishedButton = new JButton("Export finished");
        exportFinishedButton.addActionListener(new ExportFinishedListener(controller));
        this.add(exportFinishedButton);

        this.add(Box.createHorizontalGlue());
    }
}
