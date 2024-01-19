package ca.ulaval.glo2004.domaine.chalet.dto;

import ca.ulaval.glo2004.domaine.chalet.Slope;
import ca.ulaval.glo2004.util.math.Imperial;

import java.util.UUID;

public class SlopeDTO {
    private UUID uuid;
    private Imperial x;
    private Imperial y;
    private Imperial z;
    private Imperial width;

    private float angle;

    public SlopeDTO(Slope slope)
    {
        this.uuid = slope.getUuid();
        this.x = (Imperial) slope.getX().clone();
        this.y = (Imperial) slope.getY().clone();
        this.z = (Imperial) slope.getZ().clone();
        this.width = (Imperial) slope.getWidth().clone();
        this.angle = slope.getAngle();
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

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
