package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.util.math.Vec3;

public class RaycastResult {
    public final SceneObject target;
    public final Vec3 hitPoint;

    public RaycastResult(SceneObject target, Vec3 hitPoint)
    {
        this.target = target;
        this.hitPoint = hitPoint;
    }

    @Override
    public String toString() {
        return target.descriptor;
    }
}
