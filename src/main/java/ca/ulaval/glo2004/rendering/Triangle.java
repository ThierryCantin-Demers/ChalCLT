package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.Vec3;

public class Triangle {
    public Vec3 v1, v2, v3;
    public int color;

    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3, int color)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    public Vec3 normal()
    {
        return Vec3.cross(Vec3.sub(v2, v1), Vec3.sub(v3, v1)).normalized();
    }

    public float getDepth()
    {
        return (v1.z + v2.z + v3.z) / 3.0f;
    }

    public int getAlpha()
    {
        return ColorUtils.getAlpha(color);
    }

    @Override
    public String toString() {
        return "{ " + v1 + ", " + v2 + ", " + v3 + " }";
    }
}
