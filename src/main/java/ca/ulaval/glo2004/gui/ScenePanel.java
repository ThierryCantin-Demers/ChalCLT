package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.gui.events.*;
import ca.ulaval.glo2004.gui.rendering.RenderingCanvas;
import ca.ulaval.glo2004.util.math.Imperial;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class ScenePanel extends JPanel implements Observer {
    private JPanel toolBars;
    private JLabel showAllPanelsLabel;
    private JCheckBox showAllPanelsButton;
    private JLabel zoomLabel;
    private JButton zoomButton;
    private JButton dezoomButton;
    private JLabel gridLabel;
    private JCheckBox gridToggleButton;
    private JLabel gridDistanceLabel;
    private JTextField gridDistanceField;

    private JLabel viewsLabel;
    private JComboBox<Orientation> viewsCombo;

    private RenderingCanvas canvas;

    private Controller controller;


    public ScenePanel(Controller controller)
    {
        super();
        this.controller = controller;
        init();
    }

    private void init()
    {
        this.setLayout(new BorderLayout());
        createToolBar();
        createCanvas();
//        this.setPreferredSize(new Dimension(300, 800));
        this.setMinimumSize(new Dimension(350, 0));

        this.controller.subscribe(EventType.NEW_PROJECT, this, this::reset);
    }

    private void createToolBar()
    {
        if (this.toolBars != null && Arrays.stream(this.getComponents()).anyMatch(c -> c == this.toolBars))
        {
            this.remove(this.toolBars);
        }

        JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);

        // Zoom buttons
        zoomLabel = new JLabel("Zoom: ");
        toolBar1.add(zoomLabel);

        dezoomButton = new JButton("-");
        dezoomButton.addActionListener(new ZoomOutListener(controller));
        toolBar1.add(dezoomButton);

        zoomButton = new JButton("+");
        zoomButton.addActionListener(new ZoomInListener(controller));
        toolBar1.add(zoomButton);

        // Grid components
        gridLabel = new JLabel("Show grid: ");
        toolBar1.add(gridLabel);

        gridToggleButton = new JCheckBox();
        gridToggleButton.setSelected(this.controller.getGridToggle());
        gridToggleButton.addActionListener(new GridToggleListener(controller));
        toolBar1.add(gridToggleButton);

        gridDistanceLabel = new JLabel("Grid distance: ");
        toolBar1.add(gridDistanceLabel);

        gridDistanceField = new JTextField(this.controller.getGridDistance().toString());
        GridDistanceChangeListener gridDistanceListener = new GridDistanceChangeListener(this.controller, gridDistanceField);
        gridDistanceField.addActionListener(gridDistanceListener);
        gridDistanceField.addFocusListener(gridDistanceListener);
        this.controller.subscribe(EventType.GRID_DISTANCE_CHANGED,this, ()->this.gridDistanceField.setText(this.controller.getGridDistance().toString()));
        toolBar1.add(gridDistanceField);


        // Quick view selection
        viewsLabel = new JLabel("Quick view: ");
        toolBar1.add(viewsLabel);

        viewsCombo = new JComboBox<>();
        viewsCombo.addItem(Orientation.FRONT);
        viewsCombo.addItem(Orientation.LEFT);
        viewsCombo.addItem(Orientation.BACK);
        viewsCombo.addItem(Orientation.RIGHT);
        viewsCombo.addItem(Orientation.TOP);
        viewsCombo.setSelectedItem(controller.getOrientation());

        viewsCombo.addActionListener(new ViewChangeListener(controller));

        controller.subscribe(EventType.VIEW_CHANGED, this, ()-> viewsCombo.setSelectedItem(controller.getOrientation()));

        toolBar1.add(viewsCombo);


        JToolBar toolBar2 = new JToolBar();
        toolBar2.setFloatable(false);

        // Show all panels
        showAllPanelsLabel = new JLabel("Show all panels: ");
        toolBar2.add(showAllPanelsLabel);

        showAllPanelsButton = new JCheckBox();
        showAllPanelsButton.setSelected(this.controller.isShowAllPanels());
        showAllPanelsButton.addActionListener(new ShowAllPanelsListener(controller));
        toolBar2.add(showAllPanelsButton);


        // Thickness
        JLabel thicknessLabel = new JLabel("Panels Thickness: ");

        JTextField thicknessField = new JTextField(this.controller.getWallThickness().toString());
        thicknessField.setToolTipText("Thickness");

        WallThicknessChangeListener thicknessListener = new WallThicknessChangeListener(this.controller, thicknessField);
        thicknessField.addActionListener(thicknessListener);
        thicknessField.addFocusListener(thicknessListener);

        this.controller.subscribe(EventType.WALL_THICKNESS_CHANGED, this,
                ()-> thicknessField.setText(this.controller.getWallThickness().toString()));

        ComponentBoxGroupBuilder thicknessBuilder = new ComponentBoxGroupBuilder();
        JPanel thicknessPanel = thicknessBuilder.
                addGroup().addComponent(thicknessLabel).addComponent(thicknessField).generate();

        toolBar2.add(thicknessPanel);

        // Imprecision
        JLabel imprecisionLabel = new JLabel("Imprecision: ");

        JTextField imprecisionField = new JTextField(this.controller.getImprecision().toString());
        imprecisionField.setToolTipText("Imprecision");

        ImprecisionChangeListener imprecisionListener = new ImprecisionChangeListener(this.controller, imprecisionField);
        imprecisionField.addActionListener(imprecisionListener);
        imprecisionField.addFocusListener(imprecisionListener);

        this.controller.subscribe(EventType.IMPRECISION_CHANGED, this, ()->{
            imprecisionField.setText(this.controller.getImprecision().toString());
        });

        ComponentBoxGroupBuilder imprecisionBuilder = new ComponentBoxGroupBuilder();
        JPanel imprecisionPanel = imprecisionBuilder.
                addGroup().addComponent(imprecisionLabel).addComponent(imprecisionField).generate();

        toolBar2.add(imprecisionPanel);

        // Min distance between accessories
        JLabel minDistanceLabel = new JLabel("Min distance: ");

        JTextField minDistanceField = new JTextField(this.controller.getMinDistance().toString());
        minDistanceField.setToolTipText("Minimum distance between accessories");

        MinDistanceChangeListener minDistanceListener = new MinDistanceChangeListener(this.controller, minDistanceField);
        minDistanceField.addActionListener(minDistanceListener);
        minDistanceField.addFocusListener(minDistanceListener);

        this.controller.subscribe(EventType.MIN_DISTANCE_CHANGED, this, ()->{
            minDistanceField.setText(this.controller.getMinDistance().toString());
        });

        ComponentBoxGroupBuilder minDistanceBuilder = new ComponentBoxGroupBuilder();
        JPanel minDistancePanel = minDistanceBuilder.
                addGroup().addComponent(minDistanceLabel).addComponent(minDistanceField).generate();

        toolBar2.add(minDistancePanel);

        ComponentBoxGroupBuilder toolBarsBuilder = new ComponentBoxGroupBuilder(ComponentBoxGroupBuilder.BoxLayoutType.Y_AXIS);
        this.toolBars = toolBarsBuilder.addGroup(ComponentBoxGroupBuilder.BoxLayoutType.Y_AXIS).
                        addComponent(toolBar2).addComponent(toolBar1).generate();

        this.add(this.toolBars, BorderLayout.NORTH);
    }

    private void createCanvas()
    {
        canvas = new RenderingCanvas(controller);
        canvas.setPreferredSize(new Dimension(300, 400));
        this.add(canvas);
    }

    public void startRenderingLoop()
    {
        if (canvas != null)
        {
            canvas.start();
        }
    }

    public void stopRenderingLoop()
    {
        if (canvas != null)
        {
            canvas.stop();
        }
    }

    private void reset()
    {
        this.controller.unsubscribe(this);

        createToolBar();

        this.controller.subscribe(EventType.NEW_PROJECT, this, this::reset);
    }
}
