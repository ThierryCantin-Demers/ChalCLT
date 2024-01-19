package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.chalet.*;
import ca.ulaval.glo2004.domaine.chalet.STLChaletExporter.STLBuilder;
import ca.ulaval.glo2004.domaine.chalet.STLChaletExporter.STLBuilderASCII;
import ca.ulaval.glo2004.domaine.chalet.STLChaletExporter.STLTriangle;
import ca.ulaval.glo2004.domaine.chalet.dto.*;
import ca.ulaval.glo2004.domaine.commands.*;
import ca.ulaval.glo2004.gui.events.EventManager;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.gui.events.FilePathGetter;
import ca.ulaval.glo2004.gui.events.Observer;
import ca.ulaval.glo2004.gui.selection.*;
import ca.ulaval.glo2004.rendering.Mesh2;
import ca.ulaval.glo2004.rendering.SceneObject;
import ca.ulaval.glo2004.rendering.Space;
import ca.ulaval.glo2004.rendering.Transform;
import ca.ulaval.glo2004.util.math.*;

import java.io.*;
import java.util.*;

/**
 * Controller for the application.
 */
public class Controller {
    /**
     * The chalet.
     */
    private Chalet chalet;

    /**
     * The application state.
     */
    private ApplicationState applicationState;

    /**
     * The global command manager.
     */
    private CommandManager commandManager;

    /**
     * The global event manager.
     */
    private EventManager eventManager;

    private boolean isSTLBinary = true;

    /**
     * Creates a new controller.
     */
    public Controller() {
        initDefaultApp();
    }

    /**
     * Initializes the default state.
     */
    private void initDefaultApp() {
        this.chalet = new Chalet();
        this.applicationState = new ApplicationState();
        this.commandManager = new CommandManager();
        this.eventManager = new EventManager();
    }

    /**
     * Adds an accessory of with a certain position and dimensions to the selected wall.
     * The accessory is created with an accessory factory which is passed as parameter.
     *
     * @param position_         : The position of the accessory on the wall.
     * @param accessoryFactory_ : The accessory factory used to create the accessory.
     */
    public void addAccessoryToSelectedWall(Point2D<Imperial> position_, IAccessoryFactory accessoryFactory_) {
        Optional<Wall> selectedWallOpt = this.chalet.getWallFromUUID(this.applicationState.getSelectedObject().getUUID());

        selectedWallOpt.ifPresent(wall ->
        {
            IAccessory accessoryToAdd = accessoryFactory_.createAccessory(position_);
            this.commandManager.executeCommand(new AddAccessoryToWallCommand(this, wall, accessoryToAdd));
        });

    }

    public void changeSelectedAccessoryWall(UUID wallUUID) {
        Optional<Wall> wallOpt = this.chalet.getWallFromUUID(wallUUID);

        wallOpt.ifPresent(wall -> {
            Optional<Selectable> selectedObjectOpt = this.getSelectedObject();

            selectedObjectOpt.ifPresent(selectable -> {
                Optional<IAccessory> selectedAccOpt = this.chalet.getAccessoryFromUUID(selectable.getUUID());

                selectedAccOpt.ifPresent(acc -> {
                    this.chalet.removeAccessoryWithUUID(acc.getUUID());
                    wall.addAccessory(acc);
                    this.eventManager.notify(EventType.ACCESSORY_ADDED_OR_REMOVED);
                });
            });
        });
    }

    public Optional<WallDTO> getWallOfAccessoryUUID(UUID accessoryUUID) {
        Optional<Wall> wallOpt = this.chalet.getWallOfAccessoryUUID(accessoryUUID);

        if (wallOpt.isPresent())
        {
            return this.getWallDTOFromUUID(wallOpt.get().getUUID());
        }

        return Optional.empty();
    }

    public WallDTO[] getWallDTOCorrespondence(Orientation orientation)
    {
        WallDTO[] wallDTOs = new WallDTO[4];
        Wall[] wallCorrespondence = this.chalet.getWallCorrespondence(orientation);
        for (int i = 0; i < wallCorrespondence.length; i++) {
            Wall wall = wallCorrespondence[i];
            wallDTOs[i] =this.getWallDTOFromOrientation(wall.getWallOrientation());
        }
        return wallDTOs;
    }

    public Optional<Orientation> getSelectedAccessoryWallOrientation() {
        if (this.applicationState.getSelectedObject() != null)
        {
            Optional<Wall> wallOpt = this.chalet.getWallOfAccessoryUUID(this.applicationState.getSelectedObject().getUUID());
            if (wallOpt.isPresent())
                return Optional.ofNullable(wallOpt.get().getWallOrientation());
        }
        return Optional.empty();
    }

    public Optional<Orientation> getSelectedWallOrientation() {
        if (this.applicationState.getSelectedObject() != null)
        {
            Optional<Wall> selectedWallOpt = this.chalet.getWallFromUUID(this.applicationState.getSelectedObject().getUUID());
            if (selectedWallOpt.isPresent())
                return Optional.ofNullable(selectedWallOpt.get().getWallOrientation());
        }
        return Optional.empty();
    }

    /**
     * Adds an accessory of with a certain position and dimensions to the selected wall.
     * The accessory is created with an accessory factory which is passed as parameter.
     *
     * @param position_         : The position of the accessory on the wall.
     * @param dimensions_       : The dimensions of the accessory.
     * @param accessoryFactory_ : The accessory factory used to create the accessory.
     */
    public void addAccessoryToSelectedWall(Point2D<Imperial> position_, Dimensions<Imperial> dimensions_, IAccessoryFactory accessoryFactory_) {
        Optional<Wall> selectedWallOpt = this.chalet.getWallFromUUID(this.applicationState.getSelectedObject().getUUID());

        selectedWallOpt.ifPresent(wall ->
        {
            IAccessory accessoryToAdd = accessoryFactory_.createAccessory(position_, dimensions_);
            this.commandManager.executeCommand(new AddAccessoryToWallCommand(this, wall, accessoryToAdd));
        });
    }

    public void removeAccessoryWithUUID(UUID uuid_) {
        this.chalet.removeAccessoryWithUUID(uuid_);

        this.eventManager.notify(EventType.ACCESSORY_ADDED_OR_REMOVED);
        this.eventManager.notify(EventType.SELECTED_ITEM_CHANGED);
    }

    /**
     * Changes the selected object in the application state.
     * Notifies the event manager that the selected object has changed.
     *
     * @param selectable_ : The new selected object.
     */
    public void changeSelectedObject(Selectable selectable_) {
        this.applicationState.setSelectedObject(selectable_);
        this.eventManager.notify(EventType.SELECTED_ITEM_CHANGED);
    }

    /**
     * Changes the dimensions of the wall with the given orientation.
     * Notifies the event manager that the dimensions of a wall has changed.
     *
     * @param wallOrientation_ : The orientation of the wall.
     * @param dimensions_      : The new dimensions of the wall.
     */
    private void changeWallDimensions(Orientation wallOrientation_, Dimensions<Imperial> dimensions_) {
        if(!dimensions_.getWidth().equals(this.getWallDTOFromOrientation(wallOrientation_).width) ||
                !dimensions_.getHeight().equals(this.getWallDTOFromOrientation(wallOrientation_).height)){
            this.commandManager.executeCommand(new ChangeWallDimensionsCommand(this, wallOrientation_, dimensions_));
        }
    }

    private void changeWallDimensions(Orientation wallOrientation_, Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_) {
        if(!oldDimensions_.equals(newDimensions_)){
            this.commandManager.executeCommand(new ChangeWallDimensionsCommand(this, wallOrientation_, oldDimensions_, newDimensions_));
        }
    }

    private void changeWallDimensions(Orientation wallOrientation_, Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_, List<AccessoryDTO> oldCorrespondingAccessories_, List<AccessoryDTO> oldOtherAccessories_) {
        if(!oldDimensions_.equals(newDimensions_)){
            this.commandManager.executeCommand(new ChangeWallDimensionsCommand(this, wallOrientation_, oldDimensions_, newDimensions_, oldCorrespondingAccessories_, oldOtherAccessories_));
        }
    }

    private void changeWallDimensionsNoSave(Orientation wallOrientation_, Dimensions<Imperial> dimensions_) {
        if (!dimensions_.getWidth().equals(this.getWallDTOFromOrientation(wallOrientation_).width) ||
                !dimensions_.getHeight().equals(this.getWallDTOFromOrientation(wallOrientation_).height)) {
            new ChangeWallDimensionsCommand(this, wallOrientation_, dimensions_).execute();
        }
    }

    /**
     * Updates the dimensions of the wall with the given orientation.
     *
     * @param wallOrientation_ : The orientation of the wall.
     * @param dimensions_      : The new dimensions of the wall.
     */
    public void updatePanelDimensions(Orientation wallOrientation_, Dimensions<Imperial> dimensions_) {
        this.chalet.updatePanelDimensions(wallOrientation_, dimensions_);
    }

    /**
     * Changes the thickness of the walls.
     * Notifies the event manager that the thickness of the walls has changed.
     *
     * @param thickness_ : The new thickness of the walls.
     */
    public void changeWallThickness(Imperial thickness_) {
        if(!thickness_.equals(this.getWallThickness())) {
            this.commandManager.executeCommand(new ChangeWallThicknessCommand(this, thickness_));
        }
    }

    /**
     * Updates the thickness of the walls in the chalet.
     *
     * @param thickness_ : The new thickness of the walls.
     */
    public void updateWallThickness(Imperial thickness_) {
        this.chalet.updateWallThickness((Imperial) thickness_.clone());
    }

    /**
     * Gets the thickness of the walls.
     *
     * @return The thickness of the walls.
     */
    public Imperial getWallThickness() {
        return (Imperial) this.chalet.getWallThickness().clone();
    }

    public void changeImprecision(Imperial imprecision_) {
        if(!imprecision_.equals(this.getImprecision())) {
            this.commandManager.executeCommand(new ChangeImprecisionCommand(this, imprecision_));
        }
    }

    public void updateImprecision(Imperial imprecision_) {
        this.chalet.updateImprecision((Imperial) imprecision_.clone());
    }

    public Imperial getImprecision() {
        return (Imperial) this.chalet.getImprecision().clone();
    }


    /**
     * Changes the dimensions of the selected wall.
     *
     * @param dimensions_ : The new dimensions of the wall.
     */
    public void changeSelectedWallDimensions(Dimensions<Imperial> dimensions_) {
        this.getSelectedWallDTO().ifPresent(wallDTO -> {
            if(!dimensions_.getWidth().equals(wallDTO.width) || !dimensions_.getHeight().equals(wallDTO.height)){
                this.changeWallDimensions(wallDTO.wallOrientation, dimensions_);
            }
        });
    }

    public void changeSelectedWallDimensions(Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_) {
        this.getSelectedWallDTO().ifPresent(wallDTO -> {
            if(!oldDimensions_.equals(newDimensions_)){
                this.changeWallDimensions(wallDTO.wallOrientation, oldDimensions_, newDimensions_);
            }
        });
    }

    public void changeRoofAngle(float angle)
    {
        if(angle != this.getRoofAngle() && angle >= 0 && angle < 90)
        {
            this.commandManager.executeCommand(new ChangeRoofAngleCommand(this, angle));
        }
        else {
            this.eventManager.notify(EventType.ROOF_ANGLE_CHANGED);
        }
    }

    public void changeSelectedWallDimensions(Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_, List<AccessoryDTO> oldCorrespondingAccessories_, List<AccessoryDTO> oldOtherAccessories_) {
        this.getSelectedWallDTO().ifPresent(wallDTO -> {
            if(!oldDimensions_.equals(newDimensions_)){
                this.changeWallDimensions(wallDTO.wallOrientation, oldDimensions_, newDimensions_, oldCorrespondingAccessories_, oldOtherAccessories_);
            }
        });
    }

    /**
     * Changes the dimensions of the selected accessory.
     * Notifies the event manager that an accessory has been modified.
     *
     * @param dimensions_ : The new dimensions of the accessory.
     */
    public void changeSelectedAccessoryDimensions(Dimensions<Imperial> dimensions_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        accessoryOpt.ifPresent(accessory -> {
            if(!dimensions_.getWidth().equals(accessory.getWidth()) || !dimensions_.getHeight().equals(accessory.getHeight())){
                this.commandManager.executeCommand(new ChangeAccessoryDimensionsCommand(this, accessory, dimensions_));
            }
        });
    }

    public void changeSelectedAccessoryDimensions(Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        accessoryOpt.ifPresent(accessory -> {
            if (!oldDimensions_.equals(newDimensions_)) {
                this.commandManager.executeCommand(new ChangeAccessoryDimensionsCommand(this, accessory, oldDimensions_, newDimensions_));
            }
        });
    }

    /**
     * Changes the position of the selected accessory.
     * Notifies the event manager that an accessory has been modified.
     *
     * @param newPosition_ : The new position of the accessory.
     */
    public void changeSelectedAccessoryPosition(Point2D<Imperial> newPosition_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        accessoryOpt.ifPresent(accessory -> {
            if (!accessory.getX().equals(newPosition_.x()) || !accessory.getY().equals(newPosition_.y())) {
                this.commandManager.executeCommand(new ChangeAccessoryPositionCommand(this, accessory, newPosition_));
            }
        });
    }

    /**
     * Changes the position of the selected accessory.
     * Notifies the event manager that an accessory has been modified.
     *
     * @param oldPosition_: The old position of the accessory.
     * @param newPosition_ : The new position of the accessory.
     */
    public void changeSelectedAccessoryPosition(Point2D<Imperial> oldPosition_, Point2D<Imperial> newPosition_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        accessoryOpt.ifPresent(accessory -> {
            if(!oldPosition_.equals(newPosition_)){
                this.commandManager.executeCommand(new ChangeAccessoryPositionCommand(this, accessory, oldPosition_, newPosition_));
            }
        });
    }

    /**
     * Changes the position of the selected accessory.
     * Does not save the command in the command manager.
     * Notifies the event manager that an accessory has been modified.
     *
     * @param position_ : The new position of the accessory.
     */
    public void changeSelectedAccessoryPositionNoSave(Point2D<Imperial> position_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        accessoryOpt.ifPresent(accessory -> {
            if (!position_.x().equals(accessory.getX()) || !position_.y().equals(accessory.getY())) {
                new ChangeAccessoryPositionCommand(this, accessory, position_).execute();
            }
        });
    }

    /**
     * Changes the dimensions of the selected accessory.
     * Does not save the command in the command manager.
     * Notifies the event manager that an accessory has been modified.
     *
     * @param dimensions_ : The new dimensions of the accessory.
     */
    public void changeSelectedAccessoryDimensionsNoSave(Dimensions<Imperial> dimensions_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        accessoryOpt.ifPresent(accessory -> {
            if (!dimensions_.getWidth().equals(accessory.getWidth()) ||
                    !dimensions_.getHeight().equals(accessory.getHeight())) {
                new ChangeAccessoryDimensionsCommand(this, accessory, dimensions_).execute();
            }
        });
    }

    public void changeSelectedWallDimensionsNoSave(Dimensions<Imperial> dimensions_) {
        if(this.getSelectedWallOrientation().isPresent() &&
                !dimensions_.getWidth().equals(this.getWallDTOFromOrientation(this.getSelectedWallOrientation().get()).width) ||
                !dimensions_.getHeight().equals(this.getWallDTOFromOrientation(this.getSelectedWallOrientation().get()).height)) {
            this.getSelectedWallDTO().ifPresent(wallDTO -> this.changeWallDimensionsNoSave(wallDTO.wallOrientation, dimensions_));
        }
    }


    /**
     * Returns and array of all the wall DTOs.
     *
     * @return An array of wall DTOs.
     */
    public WallDTO[] getWallDTOs() {
        WallDTO[] wallDTOs = new WallDTO[4];

        wallDTOs[0] = new WallDTO(this.chalet.getWallFromOrientation(Orientation.FRONT));
        wallDTOs[1] = new WallDTO(this.chalet.getWallFromOrientation(Orientation.LEFT));
        wallDTOs[2] = new WallDTO(this.chalet.getWallFromOrientation(Orientation.BACK));
        wallDTOs[3] = new WallDTO(this.chalet.getWallFromOrientation(Orientation.RIGHT));

        return wallDTOs;
    }

    public WallDTO getWallDTOFromOrientation(Orientation orientation_) {
        return new WallDTO(this.chalet.getWallFromOrientation(orientation_));
    }

    /**
     * Gets a wall DTO from a UUID.
     *
     * @param uuid_ : The UUID of the wall.
     * @return The corresponding wall DTO.
     */
    public Optional<WallDTO> getWallDTOFromUUID(UUID uuid_) {
        if (this.chalet.getWallFromUUID(uuid_).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new WallDTO(this.chalet.getWallFromUUID(uuid_).get()));
    }

    public RoofDTO getRoofDTO()
    {
        return this.chalet.getRoofDTO();
    }

    public GableDTO getLeftGableDTO()
    {
        return this.chalet.getLeftGableDTO();
    }

    public GableDTO getRightGableDTO()
    {
        return this.chalet.getRightGableDTO();
    }

    public SlopeDTO getSlopeDTO()
    {
        return this.chalet.getSlopeDTO();
    }

    public ExtensionDTO getExtensionDTO()
    {
        return this.chalet.getExtensionDTO();
    }

    /**
     * Gets an accessory DTO from a UUID.
     *
     * @param uuid_ : The UUID of the accessory.
     * @return The corresponding accessory DTO.
     */
    public Optional<AccessoryDTO> getAccessoryDTOFromUUID(UUID uuid_) {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(uuid_);

        return accessoryOpt.map(AccessoryDTO::new);
    }

    /**
     * Gets the selected wall DTO.
     *
     * @return The selected wall DTO.
     */
    public Optional<WallDTO> getSelectedWallDTO() {
        Optional<Wall> selectedWallOpt = this.chalet.getWallFromUUID(this.applicationState.getSelectedObject().getUUID());

        return selectedWallOpt.map(WallDTO::new);
    }

    /**
     * Gets the selected accessory DTO.
     *
     * @return The selected accessory DTO.
     */
    public Optional<AccessoryDTO> getSelectedAccessoryDTO() {
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(this.applicationState.getSelectedObject().getUUID());

        return accessoryOpt.map(AccessoryDTO::new);
    }

    /**
     * Gets the factory that creates the components of the selection panel.
     * Creates the factory depending on the type of the selected object.
     */
    public ISelectionPanelComponentsFactory getSelectionPanelComponentsFactory() {

        Optional<Selectable> selectedObjectOpt = this.getSelectedObject();

        return selectedObjectOpt.map(selectable ->
                switch (selectable.getSelectionType()) {
                    case WALL -> new WallSelectionPanelComponentsFactory();
                    case ACCESSORY -> new AccessorySelectionPanelComponentsFactory();
                    case GABLE -> new RoofSelectionPanelComponentsFactory();
                    case SLOPE -> new RoofSelectionPanelComponentsFactory();
                    case EXTENSION -> new RoofSelectionPanelComponentsFactory();
                    default -> new NoneSelectionPanelComponentsFactory();
                }).orElseGet(NoneSelectionPanelComponentsFactory::new);
    }

    /**
     * Gets the selected object.
     *
     * @return The selected object.
     */
    public Optional<Selectable> getSelectedObject() {
        if (this.applicationState.getSelectedObject() == null) {
            return Optional.empty();
        }

        return Optional.of((Selectable) this.applicationState.getSelectedObject().clone());
    }

    /**
     * Subscribes an observer to an event in the event manager.
     *
     * @param eventType_ : The event type to subscribe to.
     * @param observer_  : The observer to subscribe.
     * @param event_     : The callback function to call when the event is triggered.
     */
    public void subscribe(EventType eventType_, Observer observer_, EventManager.Event event_) {
        this.eventManager.subscribe(eventType_, observer_, event_);
    }

    /**
     * Unsubscribes an observer from an event in the event manager.
     *
     * @param eventType_ : The event type to unsubscribe from.
     * @param observer_  : The observer to unsubscribe.
     */
    public void unsubscribe(EventType eventType_, Observer observer_) {
        this.eventManager.unsubscribe(eventType_, observer_);
    }

    /**
     * Unsubscribes an observer from all events in the event manager.
     *
     * @param observer_ : The observer to unsubscribe.
     */
    public void unsubscribe(Observer observer_) {
        this.eventManager.unsubscribe(observer_);
    }

    public void changeOrientation(Orientation orientation) {
        this.applicationState.setOrientation(orientation);

        this.eventManager.notify(EventType.VIEW_CHANGED);
    }

    public Orientation getOrientation() {
        return this.applicationState.getOrientation();
    }

    public void changeGridDistance(Imperial newDistance_) {
        this.applicationState.setGridDistance(newDistance_);

        this.eventManager.notify(EventType.GRID_DISTANCE_CHANGED);
    }

    public Imperial getGridDistance() {
        return (Imperial) this.applicationState.getGridDistance().clone();
    }

    public void changeGridToggle(boolean newToggle_) {
        this.applicationState.setGridToggle(newToggle_);

        this.eventManager.notify(EventType.GRID_TOGGLED);
    }

    public boolean getGridToggle() {
        return this.applicationState.getGridToggle();
    }

    /**
     * Notifies the event manager that an event has occurred.
     *
     * @param eventType_ : The type of the event that occurred.
     */
    public void notify(EventType eventType_) {
        this.eventManager.notify(eventType_);
    }

    /**
     * Revalidates all the accessories in the entire chalet.
     */
    public void revalidateAllAccessories() {
        this.chalet.revalidateAllAccessories();
    }

    /**
     * Undoes the last command executed by the command manager.
     */
    public void undo() {
        this.commandManager.undoCommand();
    }

    /**
     * Redoes the last command that has been undo-ed by the command manager.
     */
    public void redo() {
        this.commandManager.redoCommand();
    }

    public void changeRoofOrientation(Orientation orientation_) {
        if(!orientation_.equals(this.getRoofOrientation())){
            this.commandManager.executeCommand(new ChangeRoofOrientationCommand(this, orientation_));
        }
    }

    public void updateRoofOrientation(Orientation orientation_) {
        this.chalet.updateRoofOrientation(orientation_);
    }

    public void updateRoofAngle(float angle) {
        this.chalet.updateRoofAngle(angle);
    }

    public Orientation getRoofOrientation() {
        return this.chalet.getRoofOrientation();
    }

    public float getRoofAngle() {
        return this.chalet.getRoofAngle();
    }

    public void generateNewProject() {
        this.chalet = new Chalet();
        this.applicationState = new ApplicationState();

        this.commandManager.clearHistories();
        this.eventManager.notify(EventType.NEW_PROJECT);
    }

    public void exportBrut()
    {
        Optional<String> file = FilePathGetter.getSTLDirPath("Export Raw");

        if(file.isEmpty())
            return;

        for(WallDTO wall : getWallDTOs()) {
            String type = "";
            switch (wall.wallOrientation) {
                case FRONT:
                    type = "/Projet_Brut_F.stl";
                    break;
                case LEFT:
                    type = "/Projet_Brut_G.stl";
                    break;
                case BACK:
                    type = "/Projet_Brut_A.stl";
                    break;
                case RIGHT:
                    type = "/Projet_Brut_D.stl";
                    break;
            }
            if (isSTLBinary) {
                STLBuilder stlBuilder = new STLBuilder();
                List<STLTriangle> triangles = STLBuilder.generateMeshTriangles(MeshFactory.createPanelMesh(wall));
                Transform transform = new Transform();

                int[] normal = wall.wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                float x = wall.x.getRawInchValueFloat();
                float y = wall.y.getRawInchValueFloat();
                float z = wall.z.getRawInchValueFloat();

                transform.position.set(x, y, z);
                transform.rotation.set(Quaternion.fromDirection(normalVec));

                for (STLTriangle triangle :
                        STLBuilder.transformTriangles(triangles, transform)) {
                    stlBuilder.addTriangle(triangle);
                }

                stlBuilder.writeToFile(file.get() + type);
            } else if (!isSTLBinary) {
                STLBuilderASCII stlBuilder = new STLBuilderASCII();
                List<STLTriangle> triangles = STLBuilder.generateMeshTriangles(MeshFactory.createPanelMesh(wall));
                for (STLTriangle triangle : triangles) {
                    stlBuilder.addTriangle(triangle);
                }
                stlBuilder.writeToFile(file.get() + type);
            }
        }
    }

    private WallDTO getOrientedWallDTO(Orientation orientation)
    {
        for(WallDTO wall : getWallDTOs())
        {
            if(orientation.equals(wall.wallOrientation))
            {
                return wall;
            }
        }
        return null;
    }

    public void exportRetrait()
    {
        Optional<String> file = FilePathGetter.getSTLDirPath("Export Finished");

        if(file.isEmpty())
            return;

        int i = 1;
        for(WallDTO wall : getWallDTOs()) {
            String type = "";
            switch (wall.wallOrientation) {
                case FRONT:
                    type = "/Projet_Retrait_F";
                    break;
                case LEFT:
                    type = "/Projet_Retrait_G";
                    break;
                case BACK:
                    type = "/Projet_Retrait_A";
                    break;
                case RIGHT:
                    type = "/Projet_Retrait_D";
                    break;
            }
            if (isSTLBinary) {
                //gauche
                STLBuilder droit_stlBuilder = new STLBuilder();
                List<STLTriangle> d_triangles = STLBuilder.generateMeshTriangles(MeshFactory.createRetraitGauche(wall));
                Transform transform = new Transform();

                int[] normal = wall.wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);

                float x = wall.x.getRawInchValueFloat() - normal[0] * wall.thickness.minus(wall.imprecision).divide(2).getRawInchValueFloat();
                float y = wall.y.getRawInchValueFloat();
                float z = wall.z.getRawInchValueFloat() - normal[2] * wall.thickness.minus(wall.imprecision).divide(2).getRawInchValueFloat();

                if(!wall.isOvertaking)
                {
                    x = wall.x.getRawInchValueFloat() - (normal[2] * wall.imprecision.getRawInchValueFloat());
                    y = wall.y.getRawInchValueFloat();
                    z = wall.z.getRawInchValueFloat() - (normal[0] * wall.imprecision.getRawInchValueFloat());
                }

                transform.position.set(x, y, z);
                transform.rotation.set(Quaternion.fromDirection(normalVec));


                for (STLTriangle triangle :
                        STLBuilder.transformTriangles(d_triangles, transform)) {
                    droit_stlBuilder.addTriangle(triangle);
                }
                droit_stlBuilder.writeToFile(file.get() + type + "_" + i++ + ".stl");

                //droit
                STLBuilder gauche_stlBuilder = new STLBuilder();
                List<STLTriangle> g_triangles = STLBuilder.generateMeshTriangles(MeshFactory.createRetraitDroit(wall));
                transform = new Transform();

                float x2 = x - normal[2] * wall.realWidth().minus(wall.thickness.plus(wall.imprecision).divide(2)).getRawInchValueFloat();
                float y2 = wall.y.getRawInchValueFloat();
                float z2 = z + normal[0] * wall.realWidth().minus(wall.thickness.plus(wall.imprecision).divide(2)).getRawInchValueFloat();

                transform.position.set(x2, y2, z2);
                transform.rotation.set(Quaternion.fromDirection(normalVec));


                for (STLTriangle triangle :
                        STLBuilder.transformTriangles(g_triangles, transform)) {
                    gauche_stlBuilder.addTriangle(triangle);
                }
                gauche_stlBuilder.writeToFile(file.get() + type + "_" + i++ + ".stl");

                //accessoires
                for (AccessoryDTO acc : wall.accessories) {
                    if (acc.isValid) {
                        STLBuilder stlBuilder = new STLBuilder();
                        List<STLTriangle> triangles = STLBuilder.generateMeshTriangles(MeshFactory.createMesh(acc, wall.thickness.getRawInchValueFloat()));

                        transform = new Transform();

                        double accX = -normal[2] * acc.x.getRawInchValue() - normal[0] * wall.thickness.getRawInchValue() + wall.x.getRawInchValue();
                        double accY = -acc.y.getRawInchValue();
                        double accZ = normal[0] * acc.x.getRawInchValue() - normal[2] * wall.thickness.getRawInchValue() + wall.z.getRawInchValue();

                        transform.position.set((float) accX, (float) accY, (float) accZ);
                        transform.rotation.set(Quaternion.fromDirection(normalVec));

                        for (STLTriangle triangle :
                                STLBuilder.transformTriangles(triangles, transform)) {
                            stlBuilder.addTriangle(triangle);
                        }
                        stlBuilder.writeToFile(file.get() + type + "_" + i + ".stl");
                        i++;
                    }
                }

                i = 1;
            } else if (!isSTLBinary) {
                //gauche
                STLBuilderASCII droit_stlBuilder = new STLBuilderASCII();
                List<STLTriangle> d_triangles = STLBuilder.generateMeshTriangles(MeshFactory.createRetraitDroit(wall));
                for (STLTriangle triangle :
                        d_triangles) {
                    droit_stlBuilder.addTriangle(triangle);
                }
                droit_stlBuilder.writeToFile(file.get() + type + "_" + i++ + ".stl");
                //gauche
                STLBuilderASCII gauche_stlBuilder = new STLBuilderASCII();
                List<STLTriangle> g_triangles = STLBuilder.generateMeshTriangles(MeshFactory.createRetraitDroit(wall));
                for (STLTriangle triangle :
                        g_triangles) {
                    droit_stlBuilder.addTriangle(triangle);
                }
                gauche_stlBuilder.writeToFile(file.get() + type + "_" + i++ + ".stl");
                for (AccessoryDTO acc : wall.accessories) {
                    if (acc.isValid) {
                        STLBuilder stlBuilder = new STLBuilder();
                        List<STLTriangle> triangles = STLBuilder.generateMeshTriangles(MeshFactory.createMesh(acc, wall.thickness.getRawInchValueFloat()));
                        for (STLTriangle triangle :
                                triangles) {
                            stlBuilder.addTriangle(triangle);
                        }
                        stlBuilder.writeToFile(file.get() + type + "_" + i + ".stl");
                        i++;
                    }
                }
                i = 1;
            }
        }
        //retrait Rallong
        RoofDTO roofDTO = getRoofDTO();
        try {
            WallDTO leftWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.LEFT));
            WallDTO rightWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.RIGHT));
            WallDTO backWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.BACK));
            WallDTO frontWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.FRONT));
            //left gable
            List<STLTriangle> leftTriangles = null;
            //right GABLE
            List<STLTriangle> rightTriangles = null;
            //Extension
            List<STLTriangle> extensionTriangles = null;
            //Slope
            List<STLTriangle> slopeTriangles = null;
            switch (roofDTO.orientation) {
                case LEFT:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(backWall,getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(backWall,getLeftGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionRetrait(frontWall, leftWall, getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeRetrait(leftWall,backWall,getSlopeDTO()));
                    break;
                case BACK:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(Objects.requireNonNull(getOrientedWallDTO(Orientation.RIGHT)), getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(Objects.requireNonNull(getOrientedWallDTO(Orientation.RIGHT)), getLeftGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionRetrait(leftWall, backWall, getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeRetrait(backWall,rightWall,getSlopeDTO()));
                    break;
                case RIGHT:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(Objects.requireNonNull(getOrientedWallDTO(Orientation.FRONT)), getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(Objects.requireNonNull(getOrientedWallDTO(Orientation.FRONT)), getLeftGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionRetrait(backWall, rightWall, getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeRetrait(rightWall,frontWall,getSlopeDTO()));
                    break;
                case FRONT:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(Objects.requireNonNull(getOrientedWallDTO(Orientation.LEFT)), getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableRetrait(Objects.requireNonNull(getOrientedWallDTO(Orientation.LEFT)), getLeftGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionRetrait(rightWall, frontWall, getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeRetrait(frontWall,leftWall,getSlopeDTO()));
                    break;
            }

            //Extension
            {
                STLBuilder extensionSTLBuilder = new STLBuilder();
                Transform transform = new Transform();
                Vec3 position = new Vec3(this.getWallDTOCorrespondence(getRoofOrientation())[2].x.getRawInchValueFloat(), -getSlopeDTO().getY().getRawInchValueFloat(), this.getWallDTOCorrespondence(getRoofOrientation())[2].z.getRawInchValueFloat());
                transform.position.set(position);
                int[] normal = this.getWallDTOCorrespondence(getRoofOrientation())[2].wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                transform.rotation.set(Quaternion.fromDirection(normalVec));


                for (STLTriangle triangle : STLBuilder.transformTriangles(extensionTriangles, transform)) {
                    extensionSTLBuilder.addTriangle(triangle);
                }
                extensionSTLBuilder.writeToFile(file.get() + "/Projet_Retrait_R.stl");
            }
            // Slope
            {
                STLBuilder slopeSTLBuilder = new STLBuilder();
                Transform transform = new Transform();
                Vec3 position = new Vec3(this.getWallDTOFromOrientation(getRoofOrientation()).x.getRawInchValueFloat(), -getSlopeDTO().getY().getRawInchValueFloat(), this.getWallDTOFromOrientation(getRoofOrientation()).z.getRawInchValueFloat());
                transform.translate(position, Space.SELF);
                int[] normal = this.getWallDTOFromOrientation(getRoofOrientation()).wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                transform.rotation.set(Quaternion.fromDirection(normalVec));

                for (STLTriangle triangle : STLBuilder.transformTriangles(slopeTriangles, transform)) {
                    slopeSTLBuilder.addTriangle(triangle);
                }
                slopeSTLBuilder.writeToFile(file.get() + "/Projet_Retrait_T.stl");
            }

            // Left Gable
            {
                STLBuilder leftGableSTLBuilder = new STLBuilder();
                Transform transform = new Transform();
                Vec3 position = new Vec3(getLeftGableDTO().getX().getRawInchValueFloat(), -getLeftGableDTO().getY().getRawInchValueFloat(), getLeftGableDTO().getZ().getRawInchValueFloat());
                transform.position.set(position);

                int[] normal = this.getWallDTOCorrespondence(this.getRoofOrientation())[1].wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                transform.rotation.set(Quaternion.fromDirection(normalVec));

                for (STLTriangle triangle : STLBuilder.transformTriangles(leftTriangles, transform)) {
                    leftGableSTLBuilder.addTriangle(triangle);
                }
                leftGableSTLBuilder.writeToFile(file.get() + "/Projet_Retrait_PG.stl");
            }

            // Right Gable
            {
                STLBuilder rightGableSTLBuilder = new STLBuilder();
                Transform transform = new Transform();
                Vec3 position = new Vec3(getRightGableDTO().getX().getRawInchValueFloat(), -getRightGableDTO().getY().getRawInchValueFloat(), getRightGableDTO().getZ().getRawInchValueFloat());
                position.add(new Vec3(0, 0, -(this.getWallDTOCorrespondence(this.getRoofOrientation())[1].thickness.plus(this.getImprecision())).getRawInchValueFloat()));
                transform.position.set(position);

                int[] normal = this.getWallDTOCorrespondence(this.getRoofOrientation())[3].wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                transform.rotation.set(Quaternion.mult(Quaternion.fromDirection(normalVec), Quaternion.fromEuler(new Vec3(0,(float)Math.toRadians(180),0))));

                for (STLTriangle triangle : STLBuilder.transformTriangles(rightTriangles, transform)) {
                    rightGableSTLBuilder.addTriangle(triangle);
                }
                rightGableSTLBuilder.writeToFile(file.get() + "/Projet_Retrait_PD.stl");
            }
        } catch (Exception e)
        {
            System.err.println("Something wrong happened while generate STL mesh file for left gable");
        }
    }

    public void exportFinished()
    {
        Optional<String> file = FilePathGetter.getSTLDirPath("Export Finished");

        if(file.isEmpty())
            return;

        System.out.println(file.get());
//        Triangulator triangulator = new Triangulator(getWallDTOs(),getImprecision());
        for(WallDTO wall : getWallDTOs()) {
            String type = "";
            switch(wall.wallOrientation)
            {
                case FRONT:
                    type = "/Projet_Fini_F.stl";
                    break;
                case LEFT:
                    type = "/Projet_Fini_G.stl";
                    break;
                case BACK:
                    type = "/Projet_Fini_A.stl";
                    break;
                case RIGHT:
                    type = "/Projet_Fini_D.stl";
                    break;
            }
            if(isSTLBinary) {
                STLBuilder stlBuilder = new STLBuilder();
                List<STLTriangle> triangles = STLBuilder.generateMeshTriangles(MeshFactory.createCutoutMesh(wall, getImprecision()));

                Transform transform = new Transform();

                float totalToRemove = wall.thickness.divide(2).plus(wall.imprecision.divide(2)).getRawInchValueFloat();
                int[] normal = wall.wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                float x = wall.x.getRawInchValueFloat() + (wall.isOvertaking ? 0 : -normal[2] * totalToRemove);
                float y = wall.y.getRawInchValueFloat();
                float z = wall.z.getRawInchValueFloat() + (wall.isOvertaking ? 0 : normal[0] * totalToRemove);

                transform.position.set(x, y, z);
                transform.rotation.set(Quaternion.fromDirection(normalVec));


                for (STLTriangle triangle :
                        STLBuilder.transformTriangles(triangles, transform)) {
                    stlBuilder.addTriangle(triangle);
                }
                stlBuilder.writeToFile(file.get() + type);
            }
            else if(!isSTLBinary) {
                STLBuilderASCII stlBuilder = new STLBuilderASCII();
                List<STLTriangle> triangles = STLBuilder.generateMeshTriangles(MeshFactory.createCutoutMesh(wall, getImprecision()));
                for (STLTriangle triangle :
                        triangles) {
                    stlBuilder.addTriangle(triangle);
                }
                stlBuilder.writeToFile(file.get() + type);
            }

//        stlBuilder.addTriangle(new STLTriangle(new Vec3(0,10,0),new Vec3(0,0,0), new Vec3(10,0,0)));
        }
        //LEFT GABLE
        RoofDTO roofDTO = getRoofDTO();
        //left gable
        List<STLTriangle> leftTriangles = null;
        //right GABLE
        List<STLTriangle> rightTriangles = null;
        //Extension
        List<STLTriangle> extensionTriangles = null;
        //Slope
        List<STLTriangle> slopeTriangles = null;
        try {
            WallDTO leftWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.LEFT));
            WallDTO rightWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.RIGHT));
            WallDTO backWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.BACK));
            WallDTO frontWall = Objects.requireNonNull(getOrientedWallDTO(Orientation.FRONT));
            switch (roofDTO.orientation) {
                case LEFT:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableMesh(backWall,getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createRightGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.FRONT)), getRightGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionMesh(frontWall,leftWall,getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeMesh(leftWall,backWall,getSlopeDTO()));
                    break;
                case BACK:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.RIGHT)), getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createRightGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.LEFT)), getRightGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionMesh(leftWall,backWall,getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeMesh(backWall,rightWall,getSlopeDTO()));
                    break;
                case RIGHT:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.FRONT)), getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createRightGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.BACK)), getRightGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionMesh(backWall,rightWall,getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeMesh(rightWall,frontWall,getSlopeDTO()));
                    break;
                case FRONT:
                    leftTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createLeftGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.LEFT)), getLeftGableDTO()));
                    rightTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createRightGableMesh(Objects.requireNonNull(getOrientedWallDTO(Orientation.RIGHT)), getRightGableDTO()));
                    extensionTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createExtensionMesh(rightWall,frontWall,getExtensionDTO()));
                    slopeTriangles = STLBuilder.generateMeshTriangles(MeshFactory.createSlopeMesh(frontWall,leftWall,getSlopeDTO()));
                    break;
            }
            if(isSTLBinary) {
                // Left gable
                {
                STLBuilder leftStlBuilder = new STLBuilder();
                Transform transform = new Transform();

                WallDTO leftGableWall = this.getWallDTOFromOrientation(Orientation.BACK);
                switch (roofDTO.orientation) {
                    case LEFT:
                        leftGableWall = this.getWallDTOFromOrientation(Orientation.BACK);
                        break;
                    case BACK:
                        leftGableWall = this.getWallDTOFromOrientation(Orientation.RIGHT);
                        break;
                    case RIGHT:
                        leftGableWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                        break;
                    case FRONT:
                        leftGableWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                        break;
                }

                Vec3 position = new Vec3(getLeftGableDTO().getX().getRawInchValueFloat(), -getLeftGableDTO().getY().getRawInchValueFloat(), getLeftGableDTO().getZ().getRawInchValueFloat());
                transform.position.set(position);

                int[] normal = leftGableWall.wallOrientation.getNormal();
                Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                transform.rotation.set(Quaternion.fromDirection(normalVec));

                for (STLTriangle triangle :
                        STLBuilder.transformTriangles(leftTriangles, transform)) {
                    leftStlBuilder.addTriangle(triangle);
                }
                leftStlBuilder.writeToFile(file.get() + "/Projet_Fini_PG.stl");
                }

                //Right gable
                {
                    STLBuilder rightStlBuilder = new STLBuilder();
                    Transform transform = new Transform();

                    WallDTO rightGableWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                    switch (roofDTO.orientation) {
                        case LEFT:
                            rightGableWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                            break;
                        case BACK:
                            rightGableWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                            break;
                        case RIGHT:
                            rightGableWall = this.getWallDTOFromOrientation(Orientation.BACK);
                            break;
                        case FRONT:
                            rightGableWall = this.getWallDTOFromOrientation(Orientation.RIGHT);
                            break;
                    }

                    Vec3 position = new Vec3(getRightGableDTO().getX().getRawInchValueFloat(), -getRightGableDTO().getY().getRawInchValueFloat(), getRightGableDTO().getZ().getRawInchValueFloat());
                    position.add(new Vec3(0, 0, -(rightGableWall.thickness.plus(this.getImprecision())).getRawInchValueFloat()));
                    transform.position.set(position);

                    int[] normal = rightGableWall.wallOrientation.getNormal();
                    Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                    transform.rotation.set(Quaternion.mult(Quaternion.fromDirection(normalVec), Quaternion.fromEuler(new Vec3(0,(float)Math.toRadians(180),0))));

                    for (STLTriangle trinagle : STLBuilder.transformTriangles(rightTriangles, transform)) {
                        rightStlBuilder.addTriangle(trinagle);
                    }
                    rightStlBuilder.writeToFile(file.get() + "/Projet_Fini_PD.stl");
                }

                // Extension
                {
                    STLBuilder extensionStlBuilder = new STLBuilder();
                    Transform transform = new Transform();

                    WallDTO frontSlopeWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                    WallDTO leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.BACK);
                    switch (roofDTO.orientation) {
                        case LEFT:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.BACK);
                            break;
                        case BACK:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.BACK);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.RIGHT);
                            break;
                        case RIGHT:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.RIGHT);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                            break;
                        case FRONT:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                            break;
                    }

                    Vec3 position = new Vec3(this.getWallDTOCorrespondence(frontSlopeWall.wallOrientation)[2].x.getRawInchValueFloat(), -getSlopeDTO().getY().getRawInchValueFloat(), this.getWallDTOCorrespondence(frontSlopeWall.wallOrientation)[2].z.getRawInchValueFloat());
                    transform.position.set(position);
                    int[] normal = this.getWallDTOCorrespondence(frontSlopeWall.wallOrientation)[2].wallOrientation.getNormal();
                    Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                    transform.rotation.set(Quaternion.fromDirection(normalVec));

                    for (STLTriangle triangle :
                            STLBuilder.transformTriangles(extensionTriangles, transform)) {
                        extensionStlBuilder.addTriangle(triangle);
                    }
                    extensionStlBuilder.writeToFile(file.get() + "/Projet_Fini_R.stl");
                }

                // Slope
                {
                    STLBuilder slopeStlBuilder = new STLBuilder();
                    Transform transform = new Transform();

                    WallDTO frontSlopeWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                    WallDTO leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.BACK);
                    switch (roofDTO.orientation) {
                        case LEFT:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.BACK);
                            break;
                        case BACK:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.BACK);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.RIGHT);
                            break;
                        case RIGHT:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.RIGHT);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                            break;
                        case FRONT:
                            frontSlopeWall = this.getWallDTOFromOrientation(Orientation.FRONT);
                            leftOfSlopeWall = this.getWallDTOFromOrientation(Orientation.LEFT);
                            break;
                    }

                    Vec3 position = new Vec3(frontSlopeWall.x.getRawInchValueFloat(), -getSlopeDTO().getY().getRawInchValueFloat(), frontSlopeWall.z.getRawInchValueFloat());
                    transform.translate(position, Space.SELF);
                    int[] normal = frontSlopeWall.wallOrientation.getNormal();
                    Vec3 normalVec = new Vec3(normal[0], normal[1], normal[2]);
                    transform.rotation.set(Quaternion.fromDirection(normalVec));


                    for (STLTriangle triangle : STLBuilder.transformTriangles(slopeTriangles, transform)) {
                        slopeStlBuilder.addTriangle(triangle);
                    }
                    slopeStlBuilder.writeToFile(file.get() + "/Projet_Fini_T.stl");
                }

            } else if (!isSTLBinary) {
                STLBuilderASCII leftStlBuilderASCII = new STLBuilderASCII();
                for (STLTriangle triangle :
                        leftTriangles) {
                    leftStlBuilderASCII.addTriangle(triangle);
                }
                leftStlBuilderASCII.writeToFile(file.get() + "/Projet_Fini_PG.stl");

                STLBuilderASCII rightStlBuilderASCII = new STLBuilderASCII();
                for(STLTriangle trinagle: rightTriangles) {
                    rightStlBuilderASCII.addTriangle(trinagle);
                }
                rightStlBuilderASCII.writeToFile(file.get() + "/Projet_Fini_PD.stl");

                STLBuilderASCII extensionStlBuilder = new STLBuilderASCII();
                for(STLTriangle triangle :
                        extensionTriangles) {
                    extensionStlBuilder.addTriangle(triangle);
                }
                extensionStlBuilder.writeToFile(file.get() + "/Projet_Fini_R.stl");

                STLBuilderASCII slopeStlBuilder = new STLBuilderASCII();
                for(STLTriangle triangle : slopeTriangles){
                    slopeStlBuilder.addTriangle(triangle);
                }
                slopeStlBuilder.writeToFile(file.get() + "/Projet_Fini_T.stl");
            }
        }
        catch (Exception e)
        {
            System.err.println("Something wrong happened while generate STL mesh file for left gable");
        }
    }

    /**
     * Creates a selectable from a UUID.
     * @param uuid_ : The UUID of the selectable.
     * @return The selectable.
     */
    public Selectable getSelectableFromUUID(UUID uuid_) {
        // Check if the UUID corresponds to a wall
        Optional<Wall> wallOpt = this.chalet.getWallFromUUID(uuid_);

        if (wallOpt.isPresent()) {
            return new Selectable(uuid_, wallOpt.get().getWallOrientation().toString(), SelectionType.WALL);
        }

        // Check if the UUID corresponds to an accessory
        Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(uuid_);

        if (accessoryOpt.isPresent()) {
            return new Selectable(uuid_, accessoryOpt.get().getName(), SelectionType.ACCESSORY);
        }

        Optional<Extension> extensionOpt = this.chalet.getExtensionFromUUID(uuid_);

        if(extensionOpt.isPresent())
        {
            return new Selectable(uuid_, "Extension", SelectionType.EXTENSION);
        }

        Optional<Gable> gableOpt = this.chalet.getGableFromUUID(uuid_);

        if(gableOpt.isPresent())
        {
            return new Selectable(uuid_, gableOpt.get().getOrientation().toString() + " Gable", SelectionType.GABLE);
        }

        Optional<Slope> slopeOpt = this.chalet.getSlopeFromUUID(uuid_);

        if(slopeOpt.isPresent())
        {
            return new Selectable(uuid_, "Slope", SelectionType.SLOPE);
        }

        // If the UUID does not correspond to anything, return a selectable with the type NONE
        return new Selectable(uuid_, "None", SelectionType.NONE);
    }

    /**
     * Executes the command to change the minimum distance between accessories.
     *
     * @param minDistance_ : The new minimum distance between accessories.
     */
    public void changeMinDistance(Imperial minDistance_) {
        if(!minDistance_.equals(this.getMinDistance())){
            this.commandManager.executeCommand(new ChangeMinDistanceCommand(this, minDistance_));
        }
    }

    /**
     * Updates the minimum distance between accessories in the chalet.
     *
     * @param minDistance_ : The new minimum distance between accessories.
     */
    public void updateMinDistance(Imperial minDistance_) {
        this.chalet.updateMinDistanceBetweenAccessories((Imperial) minDistance_.clone());
    }

    /**
     * Gets the minimum distance between accessories.
     *
     * @return The minimum distance between accessories.
     */
    public Imperial getMinDistance() {
        return (Imperial) this.chalet.getMinDistanceBetweenAccessories().clone();
    }

    /**
     * Updates the position of all the doors in the chalet to make the y position of the door
     * match the minimum distance between accessories.
     */
    public void updateDoorPositions() {
        this.chalet.updateDoorPositions();
    }

    /**
     * Updates the position of all the accessories in the chalet to make sure they keep their relative position to the wall.
     * @param oldDimensions The old dimensions of the walls.
     * @param newDimensions The new dimensions of the walls.
     */
    public void updateAccessoriesRelativePositions(Dimensions<Imperial> oldDimensions, Dimensions<Imperial> newDimensions, Orientation modifiedWallOrientation) {
        Wall[] walls = this.chalet.getWallCorrespondence(modifiedWallOrientation);
        for (int i = 0; i < walls.length; i++) {
            for (IAccessory accessory : walls[i].getAccessories()) {
                // We only want to update the x position of the accessories on the modified wall and the one facing it.
                if(i % 2 == 0) {
                    accessory.setX(Chalet.getNewRelativePosition(accessory.getX(), oldDimensions.getWidth(), newDimensions.getWidth()));
                }

                // The y position of accessories on all walls can be updated
                // since updating the height of a wall changes the height of all the walls.
                if (!accessory.shouldAlignWithFloor()) {
                    accessory.setY(Chalet.getNewRelativePosition(accessory.getY(), oldDimensions.getHeight(), newDimensions.getHeight()));
                }
            }
        }
    }

    /**
     * Updates the position of all the accessories in the chalet to make sure they keep their relative position to the wall.
     * Uses the oldAccessories_ to get the old relative position of the accessories instead of getting the current position.
     *
     * @param oldDimensions
     * @param newDimensions
     * @param oldCorrespondingAccessories_ The accessories that were on the modified wall or the wall facing it.
     * @param oldOtherAccessories_         The accessories that were on the other walls.
     */
    public void updateAccessoriesRelativePositions(Dimensions<Imperial> oldDimensions, Dimensions<Imperial> newDimensions, List<AccessoryDTO> oldCorrespondingAccessories_, List<AccessoryDTO> oldOtherAccessories_) {
        if (oldCorrespondingAccessories_ != null) {
            for (AccessoryDTO accessoryDTO : oldCorrespondingAccessories_) {
                Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(accessoryDTO.uuid);
                accessoryOpt.ifPresent(accessory -> {
                    accessory.setX(Chalet.getNewRelativePosition(accessoryDTO.x, oldDimensions.getWidth(), newDimensions.getWidth()));

                    if (!accessory.shouldAlignWithFloor()) {
                        accessory.setY(Chalet.getNewRelativePosition(accessoryDTO.y, oldDimensions.getHeight(), newDimensions.getHeight()));
                    }
                });
            }
        }

        if (oldOtherAccessories_ != null) {
            for (AccessoryDTO accessoryDTO : oldOtherAccessories_) {
                Optional<IAccessory> accessoryOpt = this.chalet.getAccessoryFromUUID(accessoryDTO.uuid);
                accessoryOpt.ifPresent(accessory -> {
                    if (!accessory.shouldAlignWithFloor()) {
                        accessory.setY(Chalet.getNewRelativePosition(accessoryDTO.y, oldDimensions.getHeight(), newDimensions.getHeight()));
                    }
                });
            }
        }
    }

    public List<AccessoryDTO> getAllAccessoryDTOs() {
        List<AccessoryDTO> accessoryDTOs = new ArrayList<>();
        for (WallDTO wall : getWallDTOs()) {
            accessoryDTOs.addAll(wall.accessories);
        }

        return accessoryDTOs;
    }

    public void zoomIn() {
        this.notify(EventType.ZOOM_IN);
    }

    public void zoomOut() {
        this.notify(EventType.ZOOM_OUT);
    }

    public Optional<String> getProjectPath() {
        return this.applicationState.getProjectPath();
    }

    public void setProjectPath(String path_) {
        this.applicationState.setProjectPath(path_);
    }

    /**
     * Save the project to the current project path.
     * Serializes the chalet object to a file.
     */
    public void saveProject() {
        try
        {
            if(this.getProjectPath().isPresent()) {
                FileOutputStream fileOut = new FileOutputStream(this.getProjectPath().get());
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(this.chalet);
                out.close();
                fileOut.close();
            }
        }
        catch (IOException e)
        {
            System.err.println("Something went wrong while saving the project");
        }
    }

    /**
     * Chooses a file path to save the project to.
     * Saves the project to the chosen file path.
     */
    public void saveProjectAs() {
        Optional<String> path = FilePathGetter.getFilePath("Save Project", ".chalclt");

        if(path.isPresent()) {
            if(!path.get().endsWith(".chalclt")) {
                path = Optional.of(path.get() + ".chalclt");
            }

            this.setProjectPath(path.get());
            this.saveProject();
        }
        else {
            System.err.println("Problem while selecting the save file path");
        }
    }

    /**
     * Loads a project from a file.
     * Deserializes the chalet object from the file.
     */
    public void loadProject() {
        Optional<String> path = FilePathGetter.getFilePath("Load Project", ".chalclt");

        if (path.isPresent()) {
            try {
                FileInputStream fileIn = new FileInputStream(path.get());
                ObjectInputStream in = new ObjectInputStream(fileIn);
                this.chalet = (Chalet) in.readObject();
                in.close();
                fileIn.close();
                this.applicationState = new ApplicationState();
                this.applicationState.setProjectPath(path.get());
                this.eventManager.notify(EventType.NEW_PROJECT);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Something went wrong while loading the project");
            }
        }
    }

    public void changeShowAllPanels(boolean selected) {
        this.applicationState.setShowAllPanels(selected);

        this.notify(EventType.SHOW_ALL_PANELS_CHANGED);
    }

    public boolean isShowAllPanels() {
        return this.applicationState.isShowAllPanels();
    }
}
