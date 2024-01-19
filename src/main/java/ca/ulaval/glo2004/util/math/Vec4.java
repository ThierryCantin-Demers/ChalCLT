package ca.ulaval.glo2004.util.math;

public class Vec4 {

    public float x, y, z, w;

    public Vec4(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(float[] xyzw)
    {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public void set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(Vec4 other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public void set(float[] xyzw)
    {
        this.x = xyzw[0];
        this.y = xyzw[1];
        this.z = xyzw[2];
        this.w = xyzw[3];
    }

    public void add(Vec4 other)
    {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
    }

    public static Vec4 add(Vec4 a, Vec4 b)
    {
        return new Vec4(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
    }

    public void sub(Vec4 other)
    {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
    }

    public static Vec4 sub(Vec4 a, Vec4 b)
    {
        return new Vec4(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
    }

    public void mult(float scale)
    {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        this.w *= scale;
    }

    public void mult(Vec4 scale)
    {
        this.x *= scale.x;
        this.y *= scale.y;
        this.z *= scale.z;
        this.w *= scale.w;
    }

    public static Vec4 mult(Vec4 a, float scale) {
        return new Vec4(a.x*scale, a.y*scale, a.z*scale, a.w*scale);
    }

    public static Vec4 mult(Vec4 a, Vec4 b) {
        return new Vec4(a.x*b.x, a.y*b.y, a.z*b.z, a.w*b.w);
    }


    public static float dot(Vec4 a, Vec4 b)
    {
        return a.x*b.x + a.y*b.y + a.z*b.z + a.w*b.w;
    }

    public static Vec4 lerp(Vec4 from, Vec4 to, float t)
    {
        // Ensure t is clamped between 0 and 1 to stay within the valid range
        t = Math.max(0.0f, Math.min(1.0f, t));

        // Interpolate between the 'from' and 'to' vectors
        float x = from.x + (to.x - from.x) * t;
        float y = from.y + (to.y - from.y) * t;
        float z = from.z + (to.z - from.z) * t;
        float w = from.w + (to.w - from.w) * t;

        return new Vec4(x, y, z, w);
    }

    public void normalizeInPlace()
    {
        // todo
    }

    public Vec4 normalized()
    {
        float mag = magnitude();
        return new Vec4(x/mag, y/mag, z/mag, w/mag);
    }

    public float magnitude()
    {
        return (float) java.lang.Math.sqrt(x*x + y*y + z*z + w*w);
    }

    public void normalizeHomogeneous()
    {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w /= this.w;
    }

    public Vec3 toVec3()
    {
        return new Vec3(x, y, z);
    }

    public float[] toArray()
    {
        return new float[]{x, y, z, w};
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + ", " + z + ", " + w + "}";
    }

}
