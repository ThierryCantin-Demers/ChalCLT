package ca.ulaval.glo2004.domaine.chalet;

import ca.ulaval.glo2004.domaine.Orientation;
import ca.ulaval.glo2004.util.math.Imperial;

import java.io.Serializable;

public class Roof implements Serializable {
    private Gable leftGable;
    private Gable rightGable;
    private Extension extension;

    private Slope slope;
    private float angle;
    private Orientation orientation;

    private Imperial width; // x axis
    private Imperial depth; // z axis
    private Imperial thickness;

    private Imperial x;
    private Imperial z;
    private Imperial y;

    public Roof()
    {
        this(new Gable(), new Gable(), new Extension(),new Slope(), 0, new Imperial(), new Imperial(), new Imperial(), new Imperial(), new Imperial(), new Imperial(), Orientation.FRONT);
    }
    public Roof(Gable leftGable, Gable rightGable, Extension extension,Slope slope, float angle, Imperial width, Imperial depth,Imperial thickness, Imperial x, Imperial y, Imperial z, Orientation orientation)
    {
        this.leftGable = leftGable;
        this.rightGable = rightGable;
        this.extension = extension;
        this.angle = angle;
        this.thickness = thickness;
        this.depth = depth;
        this.width = width;
        this.slope = slope;
        this.x = x;
        this.y = y;
        this.z = z;
        this.orientation = orientation;
    }


    public Gable getLeftGable() {
        return leftGable;
    }

    public void setLeftGable(Gable leftGable) {
        this.leftGable = leftGable;
    }

    public Gable getRightGable() {
        return rightGable;
    }

    public void setRightGable(Gable rightGable) {
        this.rightGable = rightGable;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        this.slope.setAngle(angle);
        this.leftGable.setAngle(angle);
        this.rightGable.setAngle(angle);
        this.extension.setAngle(angle);
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Imperial getThickness() {
        return thickness;
    }

    public void setThickness(Imperial thickness) {
        this.thickness = thickness;
    }

    public Slope getSlope() {
        return slope;
    }

    public void setSlope(Slope slope) {
        this.slope = slope;
    }

    public Imperial getWidth() {
        return width;
    }

    public void setWidth(Imperial width) {
        this.width = width;
    }

    public Imperial getDepth() {
        return depth;
    }

    public void setDepth(Imperial depth) {
        this.depth = depth;
    }

    public Imperial getX() {
        return x;
    }

    public void setX(Imperial x) {
        this.x = x;
    }

    public Imperial getZ() {
        return z;
    }

    public void setZ(Imperial z) {
        this.z = z;
    }

    public Imperial getY() {
        return y;
    }

    public void setY(Imperial y) {
        this.y = y;
    }
}
