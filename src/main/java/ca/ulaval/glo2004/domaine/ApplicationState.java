package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.util.math.Imperial;

import java.util.Optional;

public class ApplicationState {
    private static final Imperial DEFAULT_GRID_DISTANCE = new Imperial(1,0);
    private static final boolean DEFAULT_GRID_TOGGLE = false;
    private static final boolean DEFAULT_SHOW_ALL_PANELS = true;
    private static final Orientation DEFAULT_ORIENTATION = Orientation.TOP;
    private Selectable selectedObject;

    private Orientation orientation;

    private Imperial gridDistance;

    private boolean gridToggle;

    private Optional<String> projectPath;

    private boolean showAllPanels;

    public ApplicationState(){
        //default orientation
        this.orientation = ApplicationState.DEFAULT_ORIENTATION;
        this.gridDistance = ApplicationState.DEFAULT_GRID_DISTANCE;
        this.gridToggle = ApplicationState.DEFAULT_GRID_TOGGLE;

        this.projectPath = Optional.empty();
        this.showAllPanels = ApplicationState.DEFAULT_SHOW_ALL_PANELS;
    }

    public Selectable getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Selectable selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setOrientation(Orientation orientation){
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public void setGridDistance(Imperial gridDistance) {
        this.gridDistance = gridDistance;
    }

    public Imperial getGridDistance() {
        return gridDistance;
    }

    public boolean getGridToggle() {
        return gridToggle;
    }

    public void setGridToggle(boolean gridToggle) {
        this.gridToggle = gridToggle;
    }

    public Optional<String> getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = Optional.of(projectPath);
    }

    public boolean isShowAllPanels() {
        return showAllPanels;
    }

    public void setShowAllPanels(boolean showAllPanels) {
        this.showAllPanels = showAllPanels;
    }

}
