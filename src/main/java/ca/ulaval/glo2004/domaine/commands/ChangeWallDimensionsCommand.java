package ca.ulaval.glo2004.domaine.commands;

import ca.ulaval.glo2004.domaine.Controller;
import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.domaine.chalet.dto.AccessoryDTO;
import ca.ulaval.glo2004.domaine.chalet.dto.WallDTO;
import ca.ulaval.glo2004.gui.events.EventType;
import ca.ulaval.glo2004.util.math.Dimensions;
import ca.ulaval.glo2004.util.math.Imperial;

import java.util.ArrayList;
import java.util.List;

/**
 * Command that changes the dimensions of a wall.
 */
public class ChangeWallDimensionsCommand implements ICommand {
    /**
     * The controller of the application.
     */
    Controller controller;

    /**
     * The wall that will be modified.
     */
    Orientation wallOrientation;

    /**
     * The new dimensions of the wall.
     */
    Dimensions<Imperial> newDimensions;

    /**
     * The old dimensions of the wall.
     */
    Dimensions<Imperial> oldDimensions;

    /**
     * The old accessories (DTOs) that are on the wall which dimensions are being changed and the wall facing it.
     */
    List<AccessoryDTO> oldCorrespondingAccessories = null;

    /**
     * The old accessories (DTOs) that are on the other walls.
     */
    List<AccessoryDTO> oldOtherAccessories = null;

    /**
     * constructor
     *
     * @param controller_      : The controller of the application.
     * @param wallOrientation_ : The wall that will be modified.
     * @param newDimensions_   : The new dimensions of the wall.
     */
    public ChangeWallDimensionsCommand(Controller controller_, Orientation wallOrientation_, Dimensions<Imperial> newDimensions_) {
        this.controller = controller_;
        this.wallOrientation = wallOrientation_;
        this.newDimensions = new Dimensions<>((Imperial) newDimensions_.getWidth().clone(),
                (Imperial) newDimensions_.getHeight().clone());
        WallDTO wall = this.controller.getWallDTOFromOrientation(wallOrientation_);
        this.oldDimensions = new Dimensions<>(wall.width, wall.height);
    }

    /**
     * constructor for if the command was executed from a drag
     *
     * @param controller_
     * @param wallOrientation_
     * @param oldDimensions_
     * @param newDimensions_
     */
    public ChangeWallDimensionsCommand(Controller controller_, Orientation wallOrientation_, Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_) {
        this.controller = controller_;
        this.wallOrientation = wallOrientation_;
        this.newDimensions = new Dimensions<>((Imperial) newDimensions_.getWidth().clone(),
                (Imperial) newDimensions_.getHeight().clone());
        this.oldDimensions = new Dimensions<>((Imperial) oldDimensions_.getWidth().clone(),
                (Imperial) oldDimensions_.getHeight().clone());
    }

    /**
     * constructor for if the command was executed from a drag
     * keeps the old accessories (DTOs) to be able to do the correct relative position change
     *
     * @param controller_
     * @param wallOrientation_
     * @param oldDimensions_
     * @param newDimensions_
     */
    public ChangeWallDimensionsCommand(Controller controller_, Orientation wallOrientation_, Dimensions<Imperial> oldDimensions_, Dimensions<Imperial> newDimensions_, List<AccessoryDTO> oldCorrespondingAccessories_, List<AccessoryDTO> oldOtherAccessories_) {
        this.controller = controller_;
        this.wallOrientation = wallOrientation_;
        this.newDimensions = new Dimensions<>((Imperial) newDimensions_.getWidth().clone(),
                (Imperial) newDimensions_.getHeight().clone());
        this.oldDimensions = new Dimensions<>((Imperial) oldDimensions_.getWidth().clone(),
                (Imperial) oldDimensions_.getHeight().clone());
        this.oldCorrespondingAccessories = oldCorrespondingAccessories_;
        this.oldOtherAccessories = oldOtherAccessories_;
    }

    /**
     * Changes the dimensions of the wall to the new dimensions.
     */
    @Override
    public void execute() {
        this.controller.updatePanelDimensions(this.wallOrientation, this.newDimensions);
        if (oldCorrespondingAccessories == null && oldOtherAccessories == null) {
            this.controller.updateAccessoriesRelativePositions(this.oldDimensions, this.newDimensions, this.wallOrientation);
        } else {
            this.controller.updateAccessoriesRelativePositions(this.oldDimensions, this.newDimensions, this.oldCorrespondingAccessories, this.oldOtherAccessories);
        }

        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.WALL_DIMENSIONS_CHANGED);
        this.controller.notify(EventType.ACCESSORY_MODIFIED);
    }

    /**
     * Reverts the dimensions of the wall to the old dimensions.
     */
    @Override
    public void undo() {
        this.controller.updatePanelDimensions(this.wallOrientation, this.oldDimensions);
        this.controller.updateAccessoriesRelativePositions(this.newDimensions, this.oldDimensions, this.wallOrientation);

        this.controller.revalidateAllAccessories();

        this.controller.notify(EventType.WALL_DIMENSIONS_CHANGED);
        this.controller.notify(EventType.ACCESSORY_MODIFIED);
    }
}
