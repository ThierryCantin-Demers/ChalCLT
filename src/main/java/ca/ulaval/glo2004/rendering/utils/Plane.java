package ca.ulaval.glo2004.rendering.utils;

import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.Optional;

public class Plane {

    public Vec3 normal;
    public float distance;

    public Plane(Vec3 normal, Vec3 point)
    {
        this.normal = normal.normalized();
        this.distance = -Vec3.dot(this.normal, point);
    }

    public Plane(Vec3 normal, float distance)
    {
        this.normal = normal.normalized();
        this.distance = distance;
    }

    public Plane(Vec3 a, Vec3 b, Vec3 c)
    {
        this.normal = Vec3.cross(Vec3.sub(b, a), Vec3.sub(c, a));
        this.distance = -Vec3.dot(this.normal, a);
    }

    public float distanceToPoint(Vec3 point)
    {
        return Vec3.dot(this.normal, point) + this.distance;
    }

    public Vec3 closestPoint(Vec3 point)
    {
        float pointToplaneDistance = Vec3.dot(this.normal, point) + this.distance;
        return Vec3.sub(point, Vec3.mult(this.normal, pointToplaneDistance));
    }

    public Optional<Float> raycast(Ray ray)
    {
        float vdot = Vec3.dot(ray.direction, this.normal);
        float ndot = -Vec3.dot(ray.startPoint, this.normal) - this.distance;

        if (MathUtils.isEqual(vdot, 0.0f))
        {
            return Optional.empty();
        }

        float enter = ndot / vdot;

        return enter > 0.0f ? Optional.of(enter) : Optional.empty();
    }
}
