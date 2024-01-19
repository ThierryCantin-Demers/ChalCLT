package ca.ulaval.glo2004.util.math;

public class Vec3 {

    public static final Vec3 right = new Vec3(1.0f, 0.0f, 0.0f);
    public static final Vec3 up = new Vec3(0.0f, 1.0f, 0.0f);
    public static final Vec3 forward = new Vec3(0.0f, 0.0f, 1.0f);

    public float x, y, z;

    public static Vec3 right()
    {
        return new Vec3(1.0f, 0.0f, 0.0f);
    }

    public static Vec3 forward()
    {
        return new Vec3(0.0f, 0.0f, 1.0f);
    }

    public static Vec3 up()
    {
        return new Vec3(0.0f, 1.0f, 0.0f);
    }

    public static Vec3 right(float mag)
    {
        return new Vec3(mag, 0.0f, 0.0f);
    }

    public static Vec3 forward(float mag)
    {
        return new Vec3(0.0f, 0.0f, mag);
    }

    public static Vec3 up(float mag)
    {
        return new Vec3(0.0f, mag, 0.0f);
    }

    public Vec3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 set(Vec3 other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vec3 set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public static Vec3 add(Vec3 a, Vec3 b)
    {
        return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public void add(Vec3 other)
    {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public static Vec3 sub(Vec3 a, Vec3 b)
    {
        return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public void sub(Vec3 other)
    {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
    }

    public static Vec3 mult(Vec3 a, float factor)
    {
        return new Vec3(a.x * factor, a.y * factor, a.z * factor);
    }

    public static Vec3 mult(Vec3 a, Vec3 b)
    {
        return new Vec3(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vec3 div(Vec3 a, Vec3 b)
    {
        return new Vec3(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    public void mult(float scale)
    {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
    }

    public void mult(Vec3 scale)
    {
        this.x *= scale.x;
        this.y *= scale.y;
        this.z *= scale.z;
    }

    public static float dot(Vec3 a, Vec3 b)
    {
        return a.x*b.x + a.y*b.y + a.z*b.z;
    }

    public static Vec3 cross(Vec3 a, Vec3 b)
    {
        return new Vec3(
                a.y*b.z - a.z*b.y,
                a.z*b.x - a.x*b.z,
                a.x*b.y - a.y*b.x
        );
    }

    public static Vec3 lerp(Vec3 from, Vec3 to, float t)
    {
        // Ensure t is clamped between 0 and 1 to stay within the valid range
        t = Math.max(0.0f, Math.min(1.0f, t));

        // Interpolate between the 'from' and 'to' vectors
        float x = from.x + (to.x - from.x) * t;
        float y = from.y + (to.y - from.y) * t;
        float z = from.z + (to.z - from.z) * t;

        return new Vec3(x, y, z);
    }

    public static Vec3 interpolateBarycentric(Vec3 v1, Vec3 v2, Vec3 v3, float u, float v, float w)
    {
        float x = u * v1.x + v * v2.x + w * v3.x;
        float y = u * v1.y + v * v2.y + w * v3.y;
        float z = u * v1.z + v * v2.z + w * v3.z;
        return new Vec3(x, y, z);
    }

    public static Vec3 round(Vec3 v)
    {
        return new Vec3(Math.round(v.x), Math.round(v.y), Math.round(v.z));
    }

    public static Vec3 rotate(Vec3 vector, Vec3 rotation)
    {
        // Calculate the rotation matrix
        Mat4 rotationMatrix = Mat4.rotation(rotation);

        // Apply the rotation to the vector
        return rotationMatrix.transform(vector.toVec4()).toVec3();
    }

    public void normalizeInPlace()
    {
        float mag = magnitude();
        if (mag != 0) {
            x /= mag;
            y /= mag;
            z /= mag;
        }
    }

    public Vec3 normalized() {
        Vec3 normalizedVec = new Vec3(x, y, z);
        normalizedVec.normalizeInPlace();
        return normalizedVec;
    }

    public static Vec3 projectOntoPlane(Vec3 vector, Vec3 planeNormal) {
        Vec3 normalizedNormal = planeNormal.normalized();
        float dotProduct = Vec3.dot(vector, normalizedNormal);
        return Vec3.sub(vector, Vec3.mult(normalizedNormal, dotProduct));
    }

    public static float angleBetween(Vec3 vec1, Vec3 vec2) {
        float dotProduct = Vec3.dot(vec1.normalized(), vec2.normalized());

        dotProduct = MathUtils.clampf(dotProduct, -1.0f, 1.0f);

        float angleInRadians = (float) Math.acos(dotProduct);

        return (float) Math.toDegrees(angleInRadians);
    }

    public Vec3 floor()
    {
        return new Vec3((float) Math.floor(x), (float) Math.floor(y), (float) Math.floor(z));
    }

    public float magnitude()
    {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    public Vec4 toVec4()
    {
        return new Vec4(x, y, z, 1.0f);
    }

    public Vec2 toVec2() {
        return new Vec2(x, y);
    }

    public float[] toArray()
    {
        return new float[]{x, y, z};
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + Float.hashCode(x + 0.0f);
        result = prime * result + Float.hashCode(y + 0.0f);
        result = prime * result + Float.hashCode(z + 0.0f);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Vec3 other = (Vec3) obj;
        return MathUtils.isEqual(x, other.x)
                && MathUtils.isEqual(y, other.y)
                && MathUtils.isEqual(z, other.z);
    }

    public Vec3 copy()
    {
        return new Vec3(x,y,z);
    }
    
    @Override
    public String toString() {
        return "{" + x + ", " + y + ", " + z + "}";
    }
}
