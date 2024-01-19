//package ca.ulaval.glo2004.gui.events;
//
//import ca.ulaval.glo2004.domaine.Controller;
//
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//
//import ca.ulaval.glo2004.domaine.Orientation;
//
//public class ViewChangeListener implements ItemListener {
//   private Controller controller;
//    public ViewChangeListener(Controller _controller){this.controller = _controller;}
//
//    @Override
//    public void itemStateChanged(ItemEvent e) {
////        controller.Orient
//        if(e.getStateChange() == ItemEvent.SELECTED)
//        {
//            controller.changeOrientation((Orientation)e.getItem());
//        }
//    }
//}

package ca.ulaval.glo2004.gui.events;

        import ca.ulaval.glo2004.domaine.Controller;

        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;

        import ca.ulaval.glo2004.domaine.Orientation;

        import javax.swing.*;

public class ViewChangeListener implements ActionListener {
    private final Controller controller;

    public ViewChangeListener(Controller _controller) {
        this.controller = _controller;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<?> cb = (JComboBox<?>) e.getSource();
        Object selectedItem = cb.getSelectedItem();
        if (selectedItem instanceof Orientation ori) {
            controller.changeOrientation(ori);
        } else {
            System.err.println("Selected item is not an Orientation");
        }
    }
}