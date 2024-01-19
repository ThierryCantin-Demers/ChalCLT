package ca.ulaval.glo2004.util.math;

public class Quaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Quaternion set(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
        return this;
    }

    public Quaternion add(float qx, float qy, float qz, float qw) {
        this.x += qx;
        this.y += qy;
        this.z += qz;
        this.w += qw;
        return this;
    }

    public Quaternion add(Quaternion other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }

    public Vec3 transform(Vec3 point)
    {
        float x = this.x * 2f;
        float y = this.y * 2f;
        float z = this.z * 2f;
        float xx = this.x * x;
        float yy = this.y * y;
        float zz = this.z * z;
        float xy = this.x * y;
        float xz = this.x * z;
        float yz = this.y * z;
        float wx = this.w * x;
        float wy = this.w * y;
        float wz = this.w * z;

        Vec3 res = new Vec3(0,0,0);
        res.x = (1F - (yy + zz)) * point.x + (xy - wz) * point.y + (xz + wy) * point.z;
        res.y = (xy + wz) * point.x + (1F - (xx + zz)) * point.y + (yz - wx) * point.z;
        res.z = (xz - wy) * point.x + (yz + wx) * point.y + (1F - (xx + yy)) * point.z;
        return res;
    }

    public static Quaternion mult(Quaternion q0, Quaternion q1)
    {
        float nw = q0.w * q1.w - q0.x * q1.x - q0.y * q1.y - q0.z * q1.z;
        float nx = q0.w * q1.x + q0.x * q1.w + q0.y * q1.z - q0.z * q1.y;
        float ny = q0.w * q1.y - q0.x * q1.z + q0.y * q1.w + q0.z * q1.x;
        float nz = q0.w * q1.z + q0.x * q1.y - q0.y * q1.x + q0.z * q1.w;
        return new Quaternion(nx, ny, nz, nw);
    }

    public static Quaternion mult(Quaternion q, float factor)
    {
        return new Quaternion(q.x*factor, q.y*factor, q.z*factor, q.w*factor);
    }

    /**
     * Source: <a href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java">...</a>
     */
    public Quaternion mult(float x, float y, float z, float w) {
        final float newX = this.w * x + this.x * w + this.y * z - this.z * y;
        final float newY = this.w * y + this.y * w + this.z * x - this.x * z;
        final float newZ = this.w * z + this.z * w + this.x * y - this.y * x;
        final float newW = this.w * w - this.x * x - this.y * y - this.z * z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public Quaternion mult(float factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        this.w *= factor;
        return this;
    }


    public Quaternion mult(Quaternion other) {
        final float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
        final float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
        final float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
        final float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }

    public static float dot(Quaternion a, Quaternion b)
    {
        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }

    public float norm2() {
        return w * w + x * x + y * y + z * z;
    }

    /**
     * Source: <a href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java">...</a>
     */
    public Quaternion slerp(Quaternion end, float alpha) {
        final float d = this.x * end.x + this.y * end.y + this.z * end.z + this.w * end.w;
        float absDot = d < 0.f ? -d : d;

        // Set the first and second scale for the interpolation
        float scale0 = 1f - alpha;
        float scale1 = alpha;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - absDot) > 0.1) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            final float angle = (float)Math.acos(absDot);
            final float invSinTheta = 1f / (float)Math.sin(angle);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = ((float)Math.sin((1f - alpha) * angle) * invSinTheta);
            scale1 = ((float)Math.sin((alpha * angle)) * invSinTheta);
        }

        if (d < 0.f) scale1 = -scale1;

        // Calculate the x, y, z and w values for the quaternion by using a
        // special form of linear interpolation for quaternions.
        float qx = (scale0 * x) + (scale1 * end.x);
        float qy = (scale0 * y) + (scale1 * end.y);
        float qz = (scale0 * z) + (scale1 * end.z);
        float qw = (scale0 * w) + (scale1 * end.w);

        // Return the interpolated quaternion
        return new Quaternion(qx, qy, qz, qw);
    }

    public Quaternion normalize() {
        float len = norm2();
        if (len != 0.f && !MathUtils.isEqual(len, 1f)) {
            len = (float)Math.sqrt(len);
            w /= len;
            x /= len;
            y /= len;
            z /= len;
        }
        return this;
    }

    public Quaternion inverse() {
        float norm = norm2();
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            return new Quaternion(-x * invNorm, -y * invNorm, -z * invNorm, w
                    * invNorm);
        }
        // return an invalid result to flag the error
        return null;
    }

    public Mat4 toRotationMatrix() {

        float norm = norm2();
        float s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;

        float xs = x * s;
        float ys = y * s;
        float zs = z * s;
        float xx = x * xs;
        float xy = x * ys;
        float xz = x * zs;
        float xw = w * xs;
        float yy = y * ys;
        float yz = y * zs;
        float yw = w * ys;
        float zz = z * zs;
        float zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        return new Mat4(new float[][]{
                {1 - (yy + zz), (xy - zw), (xz + yw), 0},
                {(xy + zw), 1 - (xx + zz), (yz - xw), 0},
                {(xz - yw), (yz + xw), 1 - (xx + yy), 0},
                {0, 0, 0, 1}
        });
    }

    public static Quaternion fromEuler(Vec3 rotation)
    {
        float angle;
        float sinRoll, sinPitch, sinYaw, cosRoll, cosPitch, cosYaw;
        angle = rotation.z * 0.5f;
        sinPitch = (float) Math.sin(angle);
        cosPitch = (float) Math.cos(angle);
        angle = rotation.y * 0.5f;
        sinRoll = (float) Math.sin(angle);
        cosRoll = (float) Math.cos(angle);
        angle = rotation.x * 0.5f;
        sinYaw = (float) Math.sin(angle);
        cosYaw = (float) Math.cos(angle);

        float cosRollXcosPitch = cosRoll * cosPitch;
        float sinRollXsinPitch = sinRoll * sinPitch;
        float cosRollXsinPitch = cosRoll * sinPitch;
        float sinRollXcosPitch = sinRoll * cosPitch;

        float w = (cosRollXcosPitch * cosYaw - sinRollXsinPitch * sinYaw);
        float x = (cosRollXcosPitch * sinYaw + sinRollXsinPitch * cosYaw);
        float y = (sinRollXcosPitch * cosYaw + cosRollXsinPitch * sinYaw);
        float z = (cosRollXsinPitch * cosYaw - sinRollXcosPitch * sinYaw);

        return new Quaternion(x, y, z, w);
    }

    public void setEulerRotation(Vec3 rotation)
    {
        Quaternion quat = Quaternion.fromEuler(rotation);
        this.set(quat);
    }

    /**
     * Source: <a href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java">...</a>
     */
    public Quaternion setFromAxis(Vec3 axis, float radians) {
        float d = axis.magnitude();
        float pi2 = (float) (2f*Math.PI);
        if (d == 0f) return identity();
        d = 1f / d;
        float l_ang = radians < 0 ? pi2 - (-radians % pi2) : radians % pi2;
        float l_sin = (float)Math.sin(l_ang / 2);
        float l_cos = (float)Math.cos(l_ang / 2);
        Quaternion q = this.set(d * axis.x * l_sin, d * axis.y * l_sin, d * axis.z * l_sin, l_cos).normalize();
        return q;
    }

    public static Quaternion fromAxis(Vec3 axis, float radians)
    {
        Quaternion q = Quaternion.identity();
        return q.setFromAxis(axis, radians);
    }

    /**
     * Source: <a href="https://stackoverflow.com/a/52551983">Look-at quaternion using up vector</a>
     */
    public static Quaternion lookAt(Vec3 origin, Vec3 target, Vec3 up)
    {

        Vec3 F = Vec3.sub(target, origin).normalized();   // lookAt

        if (Math.abs(Vec3.dot(up, F)) == 1)
        {
            float pitch = (float)Math.asin(-F.y);
            float yaw = (float)Math.atan2(F.x, F.z);

            return Quaternion.fromEuler(new Vec3(0, (float) Math.toRadians(89), 0));
        }


        Vec3 R = Vec3.cross(up, F).normalized();//normalize(cross(F, worldUp)); // sideaxis
        Vec3 U = Vec3.cross(F, R);                  // rotatedup


        float x, y, z, w;

        double trace = R.x + U.y + F.z;
        if (trace > 0.0) {
            float s = (float) (0.5 / Math.sqrt(trace + 1.0));
            w = 0.25f / s;
            x = (U.z - F.y) * s;
            y = (F.x - R.z) * s;
            z = (R.y - U.x) * s;
        } else {
            if (R.x > U.y && R.x > F.z) {
                float s = (float) (2.0 * Math.sqrt(1.0 + R.x - U.y - F.z));
                w = (U.z - F.y) / s;
                x = 0.25f * s;
                y = (U.x + R.y) / s;
                z = (F.x + R.z) / s;
            } else if (U.y > F.z) {
                float s = (float) (2.0 * Math.sqrt(1.0 + U.y - R.x - F.z));
                w = (F.x - R.z) / s;
                x = (U.x + R.y) / s;
                y = 0.25f * s;
                z = (F.y + U.z) / s;
            } else {
                float s = (float) (2.0 * Math.sqrt(1.0 + F.z - R.x - U.y));
                w = (R.y - U.x) / s;
                x = (F.x + R.z) / s;
                y = (F.y + U.z) / s;
                z = 0.25f * s;
            }
        }

        return new Quaternion(x, y, z, w);
    }

    /**
     * Source : <a href="https://android.googlesource.com/platform/external/jmonkeyengine/+/59b2e6871c65f58fdad78cd7229c292f6a177578/engine/src/core/com/jme3/math/Quaternion.java">JMonkey engine Quaternion class</a>
     */
    public Vec3 toEuler()
    {
        Vec3 angles = new Vec3(0,0,0);
        float sqw = w * w;
        float sqx = x * x;
        float sqy = y * y;
        float sqz = z * z;
        float unit = sqx + sqy + sqz + sqw; // if normalized is one, otherwise
        // is correction factor
        float test = x * y + z * w;
        if (test > 0.499 * unit) { // singularity at north pole
            angles.y = (float) (2 * Math.atan2(x, w));
            angles.z = (float) (Math.PI/2.0f);
            angles.x = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            angles.y = (float) (-2 * Math.atan2(x, w));
            angles.z = (float) (-Math.PI/2.0f);
            angles.x = 0;
        } else {
            angles.y = (float) Math.atan2(2 * y * w - 2 * x * z, sqx - sqy - sqz + sqw); // roll or heading
            angles.z = (float) Math.asin(2 * test / unit); // pitch or attitude
            angles.x = (float) Math.atan2(2 * x * w - 2 * y * z, -sqx + sqy - sqz + sqw); // yaw or bank
        }
        return angles;
    }

    public static Quaternion fromDirection(Vec3 direction)
    {
        return lookAt(new Vec3(0,0,0), direction, Vec3.up());
    }

    public static Quaternion identity()
    {
        return new Quaternion(0f, 0f, 0f, 1f);
    }

    public Quaternion copy()
    {
        return new Quaternion(this.x, this.y, this.z, this.w);
    }

    @Override
    public boolean equals(Object obj) {
        float dot = Quaternion.dot(this, (Quaternion) obj);
        return dot > 1.0f - 0.000001F;
    }
}
