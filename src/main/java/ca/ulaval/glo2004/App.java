package ca.ulaval.glo2004;


import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.gui.*;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import javax.swing.*;


public class App {
    //Exemple de creation d'une fenetre et d'un bouton avec swing. Lorsque vous allez creer votre propre GUI
    // Vous n'aurez pas besoin d'ecrire tout ce code, il sera genere automatiquement par intellij ou netbeans
    // Par contre vous aurez a creer les actions listener pour vos boutons et etc.
    public static void main(String[] args) {
        FlatOneDarkIJTheme.setup();

        MainWindow mainWindow = new MainWindow();
        Controller controller = new Controller();

        ExplorerPanel explorerPanel = new ExplorerPanel(controller);
        SelectionPanel selectionPanel = new SelectionPanel(controller);
        ScenePanel scenePanel = new ScenePanel(controller);
        MainWindowMenuBar mainWindowMenuBar = new MainWindowMenuBar(controller);

        mainWindow.setExplorerPanel(explorerPanel);
        mainWindow.setSelectionPanel(selectionPanel);
        mainWindow.setScenePanel(scenePanel);
        mainWindow.setMainWindowMenuBar(mainWindowMenuBar);

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.pack();
        mainWindow.setVisible(true);

        // start the scene rendering only once the window is visible
        scenePanel.startRenderingLoop();

    }
}

