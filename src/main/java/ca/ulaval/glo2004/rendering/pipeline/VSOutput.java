package ca.ulaval.glo2004.rendering.pipeline;

import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

public class VSOutput {
    public Vec4 position;
    public Vec2 uv;
    public Vec3 normal;
    public int color;
    boolean normalized = false;

    public static VSOutput interpolate(VSOutput v1, VSOutput v2, float t)
    {
        VSOutput v = new VSOutput();
        v.position = Vec4.lerp(v1.position, v2.position, t);
        v.uv = Vec2.lerp(v1.uv, v2.uv, t);
        v.normal = Vec3.lerp(v1.normal, v2.normal, t);
        v.color = ColorUtils.lerp(v1.color, v2.color, t);
        return v;
    }

    public VSOutput mult(float f)
    {
        this.uv.mult(f);
        this.normal.mult(f);
//        this.color = ColorUtils.mult(this.color, MathUtils.clampf(f, 0f, 1f));
        return this;
    }
}
