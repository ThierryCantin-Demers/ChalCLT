package ca.ulaval.glo2004.domaine.chalet.STLChaletExporter;

import ca.ulaval.glo2004.util.math.Vec3;

public class STLTriangle {

    Vec3 v1;
    Vec3 v2;
    Vec3 v3;

    public STLTriangle(Vec3 _v1, Vec3 _v2, Vec3 _v3)
    {
        v1 = _v1;
        v2 = _v2;
        v3 = _v3;
    }

    public Vec3 normal()
    {
        Vec3 u = Vec3.sub(v3,v2);
        Vec3 v = Vec3.sub(v1,v2);
        Vec3 normal = Vec3.cross(v,u);
        normal.normalizeInPlace();
        return normal;
    }


}
