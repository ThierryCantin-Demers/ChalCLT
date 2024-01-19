package ca.ulaval.glo2004.rendering.pipeline;

import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;

public class Varyings {
    public Vec2 uv;
    public Vec3 normal;
    public int color;

    public Varyings(Vec2 uv, Vec3 normal, int color) {
        this.uv = uv;
        this.normal = normal;
        this.color = color;
    }

//    public static Varyings interpolate(Varyings v1, Varyings v2, float t)
//    {
//        Varyings v = new Varyings();
//        v.uv = Vec2.lerp(v1.uv, v2.uv, t);
//        v.normal = Vec3.lerp(v1.normal, v2.normal, t);
//        v.color = ColorUtils.lerpColor(v1.color, v2.color, t);
//        return v;
//    }

    public Varyings mult(float f)
    {
        this.uv.mult(f);
        this.normal.mult(f);
        this.color = ColorUtils.mult(this.color, MathUtils.clampf(f, 0f, 1f));
        return this;
    }
}
