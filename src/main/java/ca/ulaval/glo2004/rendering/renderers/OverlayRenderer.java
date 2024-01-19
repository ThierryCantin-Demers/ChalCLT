package ca.ulaval.glo2004.rendering.renderers;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.rasterizing.FloatRasterizer;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverlayRenderer {

    private FloatRasterizer rasterizer;
    private Scene overlayScene;
    private IView view;

    public OverlayRenderer()
    {
        this.overlayScene = new Scene();

        this.rasterizer = new FloatRasterizer(new RenderingStats());
    }

    public OverlayRenderer(Scene overlayScene)
    {
        this.overlayScene = overlayScene;

        this.rasterizer = new FloatRasterizer(new RenderingStats());
    }

    public void setView(IView view)
    {
        this.view = view;
        this.rasterizer.bindView(view);
    }

    public Scene getOverlayScene() {
        return overlayScene;
    }

    public void setOverlayScene(Scene overlayScene)
    {
        this.overlayScene = overlayScene;
    }

    public void render()
    {
        List<Triangle> trianglesToRaster = Collections.synchronizedList(new ArrayList<>());

        Camera orthoCam = new Camera("");
        orthoCam.setOrthographic(view.getHeight() / 2.0f, (float) view.getWidth(), (float) view.getHeight(), 1.0f, 1000.0f);

        overlayScene.getSceneObjects().parallelStream().forEach(sceneObject -> {

            Mat4 objectTransform = sceneObject.transform.getTransform();

            for (Triangle triangle : sceneObject.mesh.triangles) {


                Triangle triTransformed = new Triangle(
                        objectTransform.transform(triangle.v1.toVec4()).toVec3(),
                        objectTransform.transform(triangle.v2.toVec4()).toVec3(),
                        objectTransform.transform(triangle.v3.toVec4()).toVec3(),
                        triangle.color
                );

                Vec3 normal = Vec3.mult(triTransformed.normal(), new Vec3(1,1,1));

                Vec3 lightDirection = new Vec3(0.0f, 1.0f, -1.0f).normalized();


                if (sceneObject.shaded)
                {

                    float shading = Math.max(0.1f, Vec3.dot(lightDirection, normal));

                    shading = (float) Math.sqrt(shading);

                    int alpha = (triTransformed.color >> 24) & 0xff;
                    int red = (int) (((triTransformed.color >> 16) & 0xff) * shading);
                    int green = (int) (((triTransformed.color >> 8) & 0xff) * shading);
                    int blue = (int) (((triTransformed.color >> 0) & 0xff) * shading);
                    triTransformed.color = ((alpha & 0xFF) << 24) |
                            ((red & 0xFF) << 16) |
                            ((green & 0xFF) << 8)  |
                            ((blue & 0xFF) << 0);
//
                }
//                    boolean rayHitsTriangle = intersectTriangle(triTransformed, lineStart, lineEnd);
//                    if (rayHitsTriangle)
//                    {
//                        triTransformed.color = 0xFF00FF00;
//                    }

                Mat4 projection = orthoCam.getProjectionMatrix();

                Triangle triProjected = new Triangle(
                        projection.transform(triTransformed.v1.toVec4()).toVec3(),
                        projection.transform(triTransformed.v2.toVec4()).toVec3(),
                        projection.transform(triTransformed.v3.toVec4()).toVec3(),
                        triTransformed.color
                );

                triTransformed.v1.mult(new Vec3(1, 1, 1));
                triTransformed.v2.mult(new Vec3(1, 1, 1));
                triTransformed.v3.mult(new Vec3(1, 1, 1));

                float widthScale = 0.5f * view.getWidth();
                float heightScale = 0.5f * view.getHeight();
                triProjected.v1.mult(new Vec3(widthScale, heightScale, 1));
                triProjected.v2.mult(new Vec3(widthScale, heightScale, 1));
                triProjected.v3.mult(new Vec3(widthScale, heightScale, 1));

                Vec3 ab = Vec3.sub(triProjected.v2, triProjected.v1);
                Vec3 ac = Vec3.sub(triProjected.v3, triProjected.v1);

                float sign = ab.x*ac.y - ac.x*ab.y;
//                if (sign > 0)
                {
                    trianglesToRaster.add(triTransformed);
                }

            }
        });


        for (Triangle triangle : trianglesToRaster) {
            this.rasterizer.drawTriangle(triangle.v1.x, triangle.v1.y, triangle.v1.z, triangle.v2.x, triangle.v2.y, triangle.v2.z, triangle.v3.x, triangle.v3.y, triangle.v3.z, triangle.color);
        }
    }


}
