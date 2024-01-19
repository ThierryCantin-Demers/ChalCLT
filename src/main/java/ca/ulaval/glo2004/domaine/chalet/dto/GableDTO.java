package ca.ulaval.glo2004.domaine.chalet.dto;

import ca.ulaval.glo2004.domaine.chalet.Gable;
import ca.ulaval.glo2004.util.math.Imperial;

import java.util.UUID;

public class GableDTO {
    private UUID uuid;
    private Imperial x;
    private Imperial y;
    private Imperial z;
    private Imperial width;
    private Imperial height;
    private Gable.GableOrientation orientation;
    private float angle;

    public GableDTO(Gable gable)
    {
        this.uuid = gable.getUuid();
        this.x = (Imperial) gable.getX().clone();
        this.y = (Imperial) gable.getY().clone();
        this.z = (Imperial) gable.getZ().clone();
        this.width = (Imperial) gable.getWidth().clone();
        this.height = (Imperial) gable.getHeight().clone();
        this.orientation = gable.getOrientation();
        this.angle = gable.getAngle();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Imperial getX() {
        return x;
    }

    public void setX(Imperial x) {
        this.x = x;
    }

    public Imperial getY() {
        return y;
    }

    public void setY(Imperial y) {
        this.y = y;
    }

    public Imperial getZ() {
        return z;
    }

    public void setZ(Imperial z) {
        this.z = z;
    }

    public Imperial getWidth() {
        return width;
    }

    public void setWidth(Imperial width) {
        this.width = width;
    }

    public Imperial getHeight() {
        return height;
    }

    public void setHeight(Imperial height) {
        this.height = height;
    }

    public Gable.GableOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Gable.GableOrientation orientation) {
        this.orientation = orientation;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
