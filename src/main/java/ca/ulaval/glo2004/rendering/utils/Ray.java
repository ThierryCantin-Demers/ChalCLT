package ca.ulaval.glo2004.rendering.utils;

import ca.ulaval.glo2004.util.math.Vec3;

public class Ray {
    public Vec3 startPoint;
    public Vec3 direction;
    public float t;

    public Ray(Vec3 startPoint, Vec3 direction, float t)
    {
        this.startPoint = startPoint;
        this.direction = direction;
        this.t = t;
    }

    public Vec3 getPoint()
    {
        return Vec3.add(startPoint, Vec3.mult(direction, t));
    }

    public Vec3 getPoint(float distance)
    {
        return Vec3.add(startPoint, Vec3.mult(direction, distance));
    }
}