package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.util.math.Vec3;

public class Bounds {

    public final Vec3 center;
    public final Vec3 extents;
    public final Vec3 min;
    public final Vec3 max;
    public final Vec3 size;

    public Bounds(Vec3 min, Vec3 max) {
        this.center = Vec3.mult(Vec3.add(max, min), 0.5f);
        this.min = min;
        this.max = max;
        this.size = Vec3.sub(max, min);
        this.extents = Vec3.mult(this.size, 0.5f);
    }

}
