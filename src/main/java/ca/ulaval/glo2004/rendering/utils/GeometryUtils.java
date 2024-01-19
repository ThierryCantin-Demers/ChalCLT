package ca.ulaval.glo2004.rendering.utils;

import ca.ulaval.glo2004.rendering.Triangle;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GeometryUtils {

    public static boolean intersectTriangle(Triangle triangle, Vec3 lineStart, Vec3 lineEnd)
    {
        Vec3 normal = triangle.normal();
        Vec3 pointOnPlane = triangle.v1;

        Vec3 lineDir = Vec3.sub(lineEnd, lineStart);

        float nDotD = Vec3.dot(normal, lineDir);
        if (Math.abs(nDotD) < 0.0001f)
            return false;

        float nDotPs = Vec3.dot(normal, Vec3.sub(pointOnPlane, lineStart));
        float t = nDotPs / nDotD;

        Vec3 planePoint = Vec3.add(lineStart, Vec3.mult(lineDir, t));

        Vec3 v1ToV2 = Vec3.sub(triangle.v2, triangle.v1);
        Vec3 v2ToV3 = Vec3.sub(triangle.v3, triangle.v2);
        Vec3 v3ToV1 = Vec3.sub(triangle.v1, triangle.v3);

        Vec3 AToPoint = Vec3.sub(planePoint, triangle.v1);
        Vec3 BToPoint = Vec3.sub(planePoint, triangle.v2);
        Vec3 CToPoint = Vec3.sub(planePoint, triangle.v3);

        Vec3 ATestVec = Vec3.cross(v1ToV2, AToPoint);
        Vec3 BTestVec = Vec3.cross(v2ToV3, BToPoint);
        Vec3 CTestVec = Vec3.cross(v3ToV1, CToPoint);

        boolean ATestVecMatchesNormal = Vec3.dot(ATestVec, normal) > 0.0f;
        boolean BTestVecMatchesNormal = Vec3.dot(BTestVec, normal) > 0.0f;
        boolean CTestVecMatchesNormal = Vec3.dot(CTestVec, normal) > 0.0f;

        return ATestVecMatchesNormal && BTestVecMatchesNormal && CTestVecMatchesNormal;
    }

    public static boolean intersectTriangle(Triangle triangle, Vec3 lineStart, Vec3 lineEnd, Vec3 outResult)
    {
        Vec3 normal = triangle.normal();
        Vec3 pointOnPlane = triangle.v1;

        Vec3 lineDir = Vec3.sub(lineEnd, lineStart);

        float nDotD = Vec3.dot(normal, lineDir);
        if (Math.abs(nDotD) < 0.0001f)
            return false;

        float nDotPs = Vec3.dot(normal, Vec3.sub(pointOnPlane, lineStart));
        float t = nDotPs / nDotD;

        Vec3 planePoint = Vec3.add(lineStart, Vec3.mult(lineDir, t));

        Vec3 v1ToV2 = Vec3.sub(triangle.v2, triangle.v1);
        Vec3 v2ToV3 = Vec3.sub(triangle.v3, triangle.v2);
        Vec3 v3ToV1 = Vec3.sub(triangle.v1, triangle.v3);

        Vec3 AToPoint = Vec3.sub(planePoint, triangle.v1);
        Vec3 BToPoint = Vec3.sub(planePoint, triangle.v2);
        Vec3 CToPoint = Vec3.sub(planePoint, triangle.v3);

        Vec3 ATestVec = Vec3.cross(v1ToV2, AToPoint);
        Vec3 BTestVec = Vec3.cross(v2ToV3, BToPoint);
        Vec3 CTestVec = Vec3.cross(v3ToV1, CToPoint);

        boolean ATestVecMatchesNormal = Vec3.dot(ATestVec, normal) > 0.0f;
        boolean BTestVecMatchesNormal = Vec3.dot(BTestVec, normal) > 0.0f;
        boolean CTestVecMatchesNormal = Vec3.dot(CTestVec, normal) > 0.0f;

        boolean hasHit = ATestVecMatchesNormal && BTestVecMatchesNormal && CTestVecMatchesNormal;

        if (hasHit)
            outResult.set(planePoint);

        return hasHit;
    }

    public static float distanceToPlane(Vec3 planePoint, Vec3 planeNorm, Vec3 point)
    {
        Vec3 n = point.normalized();
        return (planeNorm.x * point.x + planeNorm.y * point.y + planeNorm.z * point.z - Vec3.dot(planeNorm, planePoint));
    }

    public static Triangle[] clipTriangleAgainstPlane(Vec3 planePoint, Vec3 planeNorm, Triangle triangle)
    {
        planeNorm = planeNorm.normalized();

        List<Vec3> insidePoints = new ArrayList<>();
        List<Vec3> outsidePoints = new ArrayList<>();

        float d0 = distanceToPlane(planePoint, planeNorm, triangle.v1);
        float d1 = distanceToPlane(planePoint, planeNorm, triangle.v2);
        float d2 = distanceToPlane(planePoint, planeNorm, triangle.v3);

        if (d0 >= 0) {
            insidePoints.add(triangle.v1);
        }
        else {
            outsidePoints.add(triangle.v1);
        }
        if (d1 >= 0) {
            insidePoints.add(triangle.v2);
        }
        else {
            outsidePoints.add(triangle.v2);
        }
        if (d2 >= 0) {
            insidePoints.add(triangle.v3);
        }
        else {
            outsidePoints.add(triangle.v3);
        }

        if (outsidePoints.size() == 0)
        {
            return new Triangle[]{triangle};
        }

        if (outsidePoints.size() == 3)
        {
            return new Triangle[]{};
        }

        // todo triangle clipping

        return new Triangle[]{};
    }
}
