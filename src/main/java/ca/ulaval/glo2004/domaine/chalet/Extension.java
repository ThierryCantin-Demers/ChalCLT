package ca.ulaval.glo2004.domaine.chalet;

import ca.ulaval.glo2004.util.math.Imperial;

import java.io.Serializable;
import java.util.UUID;

public class Extension implements Serializable {
    private UUID uuid;
    private Imperial x;
    private Imperial y;
    private Imperial z;
    private Imperial width;

    private Imperial height;
    private float angle;

    public Extension()
    {
        this(new Imperial(), new Imperial(), new Imperial(), new Imperial(), new Imperial(), 0);
    }
    public Extension(Imperial x, Imperial y, Imperial z, Imperial width, Imperial height, float angle)
    {
        this.uuid = UUID.randomUUID();

        this.x = x;
        this.y = y;
        this.z = z;

        this.width = width;
        this.height = height;

        this.angle = angle;
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
