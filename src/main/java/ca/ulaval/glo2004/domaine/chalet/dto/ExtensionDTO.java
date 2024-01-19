package ca.ulaval.glo2004.domaine.chalet.dto;

import ca.ulaval.glo2004.domaine.chalet.Extension;
import ca.ulaval.glo2004.util.math.Imperial;

import java.util.UUID;

public class ExtensionDTO {
    private UUID uuid;
    private Imperial x;
    private Imperial y;
    private Imperial z;
    private Imperial width;

    private Imperial height;

    private float angle;


    public ExtensionDTO(Extension extension)
    {
        this.uuid = extension.getUuid();
        this.x = (Imperial) extension.getX().clone();
        this.y = (Imperial) extension.getY().clone();
        this.z = (Imperial) extension.getZ().clone();
        this.width = (Imperial) extension.getWidth().clone();
        this.height = (Imperial) extension.getHeight().clone();
        this.angle = extension.getAngle();
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

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
