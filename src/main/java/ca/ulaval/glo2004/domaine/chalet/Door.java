package ca.ulaval.glo2004.domaine.chalet;

import ca.ulaval.glo2004.util.math.Imperial;

import java.util.UUID;

public class Door implements IAccessory {
    static final Imperial DEFAULT_WIDTH = new Imperial(0,38);
    static final Imperial DEFAULT_HEIGHT = new Imperial(0,88);

    private Imperial y = new Imperial();
    private final UUID uuid;
    private Imperial height;
    private Imperial width;
    private Imperial x;
    private boolean isValid;

    public Door()
    {
        this.uuid = UUID.randomUUID();
        this.x = new Imperial();
        this.width = Door.DEFAULT_WIDTH;
        this.height = Door.DEFAULT_HEIGHT;

        this.isValid = false;
    }
    public Door(Imperial x_)
    {
        this.uuid = UUID.randomUUID();
        this.x = x_;
        this.width = Door.DEFAULT_WIDTH;
        this.height = Door.DEFAULT_HEIGHT;

        this.isValid = false;
    }

    public Door(Imperial x_, Imperial width_, Imperial height_)
    {
        this.uuid = UUID.randomUUID();
        this.x = x_;
        this.width = width_;
        this.height = height_;

        this.isValid = false;
    }

    @Override
    public Imperial getX() {
        return this.x;
    }

    @Override
    public void setX(Imperial x_) {
        this.x = x_;
    }

    @Override
    public Imperial getY() {
        return this.y;
    }

    @Override
    public void setY(Imperial y_) {
        this.y = y_;
    }

    @Override
    public Imperial getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(Imperial width_)
    {
        this.width = width_;
    }

    @Override
    public Imperial getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(Imperial height_)
    {
        this.height = height_;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void setIsValid(boolean valid_) {
        this.isValid = valid_;
    }

    @Override
    public boolean intersectsWith(IAccessory other, Imperial minDistance) {
        Imperial halfMinDistance = minDistance.divide(2);
        double leftX1 = 0.0d;
        double leftX2 = 0.0d;
        double rightX = Math.min(this.getX().plus(this.getWidth()).plus(halfMinDistance).getRawInchValue(), other.getX().plus(other.getWidth()).plus(halfMinDistance).getRawInchValue());
        double botY1 = 0.0d;
        double botY2 = 0.0d;
        double topY = Math.min(this.getY().plus(this.getHeight()).plus(halfMinDistance).getRawInchValue(), other.getY().plus(other.getHeight()).plus(halfMinDistance).getRawInchValue());
        try
        {
            leftX1 = this.getX().minus(halfMinDistance).getRawInchValue();
        }
        catch(IllegalArgumentException ignored)
        {

        }

        try
        {
            leftX2 = other.getX().minus(halfMinDistance).getRawInchValue();
        }
        catch(IllegalArgumentException ignored)
        {

        }

        double leftX = Math.max(leftX1, leftX2);

        try
        {
            botY1= this.getY().minus(halfMinDistance).getRawInchValue();
        }
        catch(IllegalArgumentException ignored)
        {

        }

        try{
            botY2 = other.getY().minus(halfMinDistance).getRawInchValue();
        }
        catch(IllegalArgumentException ignored)
        {

        }

        double botY = Math.max(botY1, botY2);


        return rightX >= leftX && topY >= botY;
    }

    @Override
    public String toString()
    {
        return "Door(x = " + this.x.getRawInchValue() + ", y = " + this.y.getRawInchValue() + ", width = " + this.width.getRawInchValue() + ", height = " + this.height.getRawInchValue() + ")";
    }

    @Override
    public String getName()
    {
        return "Door";
    }

    @Override
    public boolean shouldAlignWithFloor() {
        return true;
    }
}
