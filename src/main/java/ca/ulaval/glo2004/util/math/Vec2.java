package ca.ulaval.glo2004.util.math;

public class Vec2 {

    public float x, y;

    public Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vec2 set(Vec2 other)
    {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vec2 set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }


    public static Vec2 add(Vec2 a, Vec2 b)
    {
        return new Vec2(a.x + b.x, a.y + b.y);
    }

    public void add(Vec2 other)
    {
        this.x += other.x;
        this.y += other.y;
    }

    public static Vec2 sub(Vec2 a, Vec2 b)
    {
        return new Vec2(a.x - b.x, a.y - b.y);
    }

    public void sub(Vec2 other)
    {
        this.x -= other.x;
        this.y -= other.y;
    }

    public static Vec2 mult(Vec2 a, float factor)
    {
        return new Vec2(a.x * factor, a.y * factor);
    }

    public static Vec2 mult(Vec2 a, Vec2 b)
    {
        return new Vec2(a.x * b.x, a.y * b.y);
    }

    public static Vec2 div(Vec2 a, Vec2 b)
    {
        return new Vec2(a.x / b.x, a.y / b.y);
    }

    public void mult(float scale)
    {
        this.x *= scale;
        this.y *= scale;
    }

    public void mult(Vec2 scale)
    {
        this.x *= scale.x;
        this.y *= scale.y;
    }

    public static float dot(Vec2 a, Vec2 b)
    {
        return a.x*b.x + a.y*b.y;
    }

    public static Vec2 lerp(Vec2 from, Vec2 to, float t)
    {
        t = Math.max(0.0f, Math.min(1.0f, t));

        float x = from.x + (to.x - from.x) * t;
        float y = from.y + (to.y - from.y) * t;

        return new Vec2(x, y);
    }

    public static Vec2 interpolateBarycentric(Vec2 v1, Vec2 v2, Vec2 v3, float u, float v, float w)
    {
        float x = u * v1.x + v * v2.x + w * v3.x;
        float y = u * v1.y + v * v2.y + w * v3.y;
        return new Vec2(x, y);
    }

    public static Vec2 round(Vec2 v)
    {
        return new Vec2(Math.round(v.x), Math.round(v.y));
    }

    public void normalizeInPlace()
    {
        // todo
    }

    public Vec2 normalized()
    {
        float mag = magnitude();
        return new Vec2(x/mag, y/mag);
    }

    public static Vec2 smoothStep(Vec2 edge0, Vec2 edge1, Vec2 x) {
        Vec2 t = clamp(div(sub(x, edge0),sub(edge1, edge0)), new Vec2(0.0f, 0.0f), new Vec2(1.0f, 1.0f));
        return mult(t, mult(t, sub(new Vec2(3.0f, 3.0f), mult(t,2.0f))));
    }

    public static Vec2 clamp(Vec2 value, Vec2 min, Vec2 max) {
        float clampedX = Math.max(min.x, Math.min(max.x, value.x));
        float clampedY = Math.max(min.y, Math.min(max.y, value.y));
        return new Vec2(clampedX, clampedY);
    }

    public Vec2 floor()
    {
        return new Vec2((float) Math.floor(x), (float) Math.floor(y));
    }

    public float magnitude()
    {
        return (float) Math.sqrt(x*x + y*y);
    }

    public Vec3 toVec3()
    {
        return new Vec3(x, y, 0.0f);
    }

    public float[] toArray()
    {
        return new float[]{x, y};
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }
}
