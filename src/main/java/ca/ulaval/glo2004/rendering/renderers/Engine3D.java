package ca.ulaval.glo2004.rendering.renderers;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.rasterizing.FloatRasterizer;
import ca.ulaval.glo2004.rendering.rasterizing.Rasterizer;
import ca.ulaval.glo2004.rendering.utils.Ray;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Engine3D {

    Camera mainCamera;
    Scene scene;
    IView view;
    Rasterizer rasterizer;
    FloatRasterizer ndcRasterizer;

    boolean wireframeEnabled = false;

    public Vec3 mousePos = new Vec3(0,0,0);

    public Engine3D(Scene scene)
    {
        this.scene = scene;
        this.mainCamera = new Camera("Default Camera");
        this.rasterizer = new Rasterizer(new RenderingStats());
        this.ndcRasterizer = new FloatRasterizer(new RenderingStats());
    }

    public Engine3D(Scene scene, Camera camera)
    {
        this.scene = scene;
        this.mainCamera = camera;
        this.rasterizer = new Rasterizer(new RenderingStats());
        this.ndcRasterizer = new FloatRasterizer(new RenderingStats());
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public void setWireframeEnabled(boolean wireframeEnabled) {
        this.wireframeEnabled = wireframeEnabled;
    }

    public void setView(IView view)
    {
        this.view = view;
        this.rasterizer.bindView(view);
        this.ndcRasterizer.bindView(view);
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
    }

    public void render()
    {
        List<Triangle> trianglesToRaster = Collections.synchronizedList(new ArrayList<>());//new ArrayList<>();

        Mat4 cameraTransform = mainCamera.transform.getTransform();
        Mat4 viewTransform = cameraTransform.inverse();

        Mat4 projectionTransform = mainCamera.getProjectionMatrix();


        Ray castRay = mainCamera.screenPointToRay(mousePos, 30f);
        Vec3 rayStart = castRay.startPoint;
        Vec3 rayEnd = castRay.getPoint();


        Vec3 lineStart = rayStart;//Vec3.add(Vec3.add(mainCamera.transform.position, new Vec3(0, 0, 0)), mainCamera.transform.getForward());
        Vec3 lineEnd = rayEnd;//Vec3.add(lineStart, Vec3.mult(mainCamera.transform.getForward(), 10));


        Vec3 screenLineStart = mainCamera.worldToScreenPoint(lineStart);
        Vec3 screenLineEnd = mainCamera.worldToScreenPoint(lineEnd);

        rasterizer.drawLine3D((int) screenLineStart.x, (int) screenLineStart.y, screenLineStart.z, (int) screenLineEnd.x, (int) screenLineEnd.y, screenLineEnd.z, 0xFFFF0000);

//        for (SceneObject sceneObject : scene.getSceneObjects()) {

        scene.getSceneObjects().parallelStream().forEach(sceneObject -> {

            Mat4 objectTransform = sceneObject.transform.getTransform();

//            Arrays.stream(sceneObject.mesh.triangles).parallel().forEach(triangle -> {
//
            for (Triangle triangle : sceneObject.mesh.triangles) {


                Triangle triTransformed = new Triangle(
                        objectTransform.transform(triangle.v1.toVec4()).toVec3(),
                        objectTransform.transform(triangle.v2.toVec4()).toVec3(),
                        objectTransform.transform(triangle.v3.toVec4()).toVec3(),
                        triangle.color
                );


                Vec3 normal = Vec3.mult(triTransformed.normal(), new Vec3(1,1,1));
                Vec3 cameraRay = Vec3.sub(triTransformed.v1, mainCamera.transform.position);

                float dot = Vec3.dot(normal, cameraRay);
//                if (dot < 0.0f)
                {
                    Vec3 lightDirection = new Vec3(0.0f, 1.0f, -1.0f).normalized();

                    float shading = Math.max(0.1f, Vec3.dot(lightDirection, normal));

                    shading = (float) Math.sqrt(shading);

                    Color color = new Color(triTransformed.color, true);
                    float red = color.getRed() * shading;
                    float green = color.getGreen() * shading;
                    float blue = color.getBlue() * shading;
                    triTransformed.color = new Color(red / 255, green / 255, blue / 255, color.getAlpha()/255f).getRGB();

//
//                    boolean rayHitsTriangle = intersectTriangle(triTransformed, lineStart, lineEnd);
//                    if (rayHitsTriangle)
//                    {
//                        triTransformed.color = 0xFF00FF00;
//                    }

                    Triangle triViewed = new Triangle(
                            viewTransform.transform(triTransformed.v1.toVec4()).toVec3(),
                            viewTransform.transform(triTransformed.v2.toVec4()).toVec3(),
                            viewTransform.transform(triTransformed.v3.toVec4()).toVec3(),
                            (int) (triTransformed.color)
                    );

                    Triangle[] clippedTriangles = clipTriangleAgainstPlane(new Vec3(0, 0, 1f), Vec3.forward, triViewed);

                    if (clippedTriangles.length == 0)
                    {
                        continue;
                        //triViewed.color = 0xFFFF0000;
                    }

//                    if (mainCamera.getProjectionType() == ProjectionType.ORTHOGRAPHIC)
//                    {
//                        int _ = 0;
//
//                    }
//                    else
//                    {
//                        int _ = 0;
//                    }

                    Vec4 projectedV1 = projectionTransform.transform(triViewed.v1.toVec4());
                    Vec4 projectedV2 = projectionTransform.transform(triViewed.v2.toVec4());
                    Vec4 projectedV3 = projectionTransform.transform(triViewed.v3.toVec4());

                    projectedV1.normalizeHomogeneous();
                    projectedV2.normalizeHomogeneous();
                    projectedV3.normalizeHomogeneous();

                    Triangle triProjected = new Triangle(
                            projectedV1.toVec3(),
                            projectedV2.toVec3(),
                            projectedV3.toVec3(),
                            triViewed.color
                    );

                    triProjected.v1.mult(new Vec3(-1, -1, 1));
                    triProjected.v2.mult(new Vec3(-1, -1, 1));
                    triProjected.v3.mult(new Vec3(-1, -1, 1));

                    Vec3 offsetView = new Vec3(1, 1, 0);
                    triProjected.v1.add(offsetView);
                    triProjected.v2.add(offsetView);
                    triProjected.v3.add(offsetView);

                    float widthScale = 0.5f * view.getWidth();
                    float heightScale = 0.5f * view.getHeight();
                    triProjected.v1.mult(new Vec3(widthScale, heightScale, 1));
                    triProjected.v2.mult(new Vec3(widthScale, heightScale, 1));
                    triProjected.v3.mult(new Vec3(widthScale, heightScale, 1));


                    Vec3 ab = Vec3.sub(triProjected.v2, triProjected.v1);
                    Vec3 ac = Vec3.sub(triProjected.v3, triProjected.v1);

                    float sign = ab.x*ac.y - ac.x*ab.y;
                    if (sign > 0)
                    {
                        trianglesToRaster.add(triProjected);
                    }
                }

            }
//            });
        });


        List<Triangle> opaqueTriangles = new ArrayList<>();
        List<Triangle> transparentTriangles = new ArrayList<>();

        trianglesToRaster.forEach(triangle -> {
            if (triangle.getAlpha() == 255)
            {
                opaqueTriangles.add(triangle);
            }
            else
            {
                transparentTriangles.add(triangle);
            }
        });


        // Sort triangles by depth
//        trianglesToRaster.sort(Comparator.comparingDouble(Triangle::getDepth));

        transparentTriangles.sort(Comparator.comparingDouble(Triangle::getDepth).reversed());

        // Then, sort by alpha for transparency
//        trianglesToRaster.sort(Comparator.comparingDouble(Triangle::getAlpha).reversed());

//        trianglesToRaster.sort((t1, t2) -> {
//            float z1 = (t1.v1.z + t1.v2.z + t1.v3.z) / 3.0f;
//            float z2 = (t2.v1.z + t2.v2.z + t2.v3.z) / 3.0f;
//
//            int a1 = (t1.color >> 24) & 0xFF;
//            int a2 = (t2.color >> 24) & 0xFF;
//
//            if (z1 != z2) {
//                return Float.compare(z2, z1); // Sort by z-value
//            } else {
//                return Integer.compare(a2, a1); // If z-values are equal, sort by alpha
//            }
//        });

        if (wireframeEnabled)
        {
//            for (Triangle triangle : trianglesToRaster) {
//                this.rasterizer.drawTriangleWireframe((int) triangle.v1.x, (int) triangle.v1.y, triangle.v1.z, (int) triangle.v2.x, (int) triangle.v2.y, triangle.v2.z, (int) triangle.v3.x, (int) triangle.v3.y, triangle.v3.z, triangle.color);
//            }
            trianglesToRaster.parallelStream().forEach(triangle -> {
                this.rasterizer.drawTriangleWireframe((int) triangle.v1.x, (int) triangle.v1.y, triangle.v1.z, (int) triangle.v2.x, (int) triangle.v2.y, triangle.v2.z, (int) triangle.v3.x, (int) triangle.v3.y, triangle.v3.z, triangle.color);
            });
        }
        else
        {
            for (Triangle triangle : opaqueTriangles) {
                this.ndcRasterizer.drawTriangle(triangle.v1.x, triangle.v1.y, triangle.v1.z, triangle.v2.x, triangle.v2.y, triangle.v2.z, triangle.v3.x, triangle.v3.y, triangle.v3.z, triangle.color);
            }
            for (Triangle triangle : transparentTriangles) {
                this.ndcRasterizer.drawTriangle(triangle.v1.x, triangle.v1.y, triangle.v1.z, triangle.v2.x, triangle.v2.y, triangle.v2.z, triangle.v3.x, triangle.v3.y, triangle.v3.z, triangle.color);
            }
//            trianglesToRaster.parallelStream().forEach(triangle -> {
//                this.rasterizer.drawTriangle2((int) triangle.v1.x, (int) triangle.v1.y, triangle.v1.z, (int) triangle.v2.x, (int) triangle.v2.y, triangle.v2.z, (int) triangle.v3.x, (int) triangle.v3.y, triangle.v3.z, triangle.color);
//            });
        }
    }


    private static Vec3 intersectPlane(Vec3 planePoint, Vec3 planeNormal, Vec3 lineStart, Vec3 lineEnd)
    {
        planePoint = planePoint.normalized();
        float planeDot = -Vec3.dot(planeNormal, planePoint);
        float ad = Vec3.dot(lineStart, planeNormal);
        float bd = Vec3.dot(lineEnd, planeNormal);
        float t = (-planeDot - ad) / (bd - ad);
        Vec3 lineStartToEnd = Vec3.sub(lineEnd, lineStart);
        Vec3 lineToIntersect = Vec3.mult(lineStartToEnd, t);
        return Vec3.add(lineStart, lineToIntersect);
    }

    private static boolean intersectTriangle(Triangle triangle, Vec3 lineStart, Vec3 lineEnd)
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

    private static float distanceToPlane(Vec3 planePoint, Vec3 planeNorm, Vec3 point)
    {
        Vec3 n = point.normalized();
        return (planeNorm.x * point.x + planeNorm.y * point.y + planeNorm.z * point.z - Vec3.dot(planeNorm, planePoint));
    }

    private static Triangle[] clipTriangleAgainstPlane(Vec3 planePoint, Vec3 planeNorm, Triangle triangle)
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

//    public void renderObject(SceneObject sceneObject)
//    {
//        Transform transform = sceneObject.transform;
//        Mat4 matrix = transform.getTransform();
//
//        for (Triangle triangle : sceneObject.mesh.triangles) {
//            renderTriangle(triangle);
//        }
//    }
//
//    public void renderTriangle(Triangle triangle)
//    {
//        Triangle triProjected, triTransformed, tiViewed;
//
//        triTransformed.v1 =
//    }
}
