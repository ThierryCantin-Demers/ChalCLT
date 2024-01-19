package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.domaine.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZoomOutListener implements ActionListener {
    private final Controller controller;

    public ZoomOutListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.zoomOut();
    }

    public void zoomOut() {
        this.controller.zoomOut();
    }
}
