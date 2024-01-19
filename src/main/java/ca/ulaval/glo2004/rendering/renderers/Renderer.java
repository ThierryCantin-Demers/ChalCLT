package ca.ulaval.glo2004.rendering.renderers;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.pipeline.ClearStates;
import ca.ulaval.glo2004.rendering.pipeline.RenderAPI;
import ca.ulaval.glo2004.rendering.rasterizing.FloatRasterizer;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.*;
import java.util.List;

public class Renderer {


    Camera mainCamera;
    Scene scene;
    Scene overlayScene;
    IView view;
    FloatRasterizer ndcRasterizer;
    RenderAPI renderer;

    Map<RenderPass, List<SceneObject>> passMap;

    public Vec3 mousePos;


    public Renderer(Scene scene)
    {
        this.scene = scene;
        this.mainCamera = new Camera("Default Camera");
        this.ndcRasterizer = new FloatRasterizer(new RenderingStats());
        this.passMap = new HashMap<>();
        this.renderer = new RenderAPI();
    }

    public Renderer(Scene scene, Camera camera)
    {
        this.scene = scene;
        this.mainCamera = camera;
        this.ndcRasterizer = new FloatRasterizer(new RenderingStats());
        this.passMap = new HashMap<>();
        this.renderer = new RenderAPI();
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public void setView(IView view)
    {
        this.view = view;
        this.ndcRasterizer.bindView(view);
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
    }

    public void render(ClearStates clearStates)
    {
        renderer.mousePos = mousePos;
        this.renderer.camera = mainCamera;
        passMap.clear();

        for (SceneObject sceneObject: scene.getSceneObjects()) {
            RenderPass passId = sceneObject.mesh_.material.getRenderPass();
            passMap.computeIfAbsent(passId, renderPass -> new ArrayList<>()).add(sceneObject);
        }


        this.renderer.beginRenderPass(this.view, clearStates);

        Comparator<RenderPass> passComparator = Comparator.comparingInt(Enum::ordinal);
        List<Map.Entry<RenderPass, List<SceneObject>>> sortedEntries = new ArrayList<>(passMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey(passComparator));

        for (Map.Entry<RenderPass, List<SceneObject>> entry : sortedEntries)
        {
//            RenderPass passId = entry.getKey();
            List<SceneObject> currentPassObjects = entry.getValue();

            for (SceneObject sceneObject : currentPassObjects) {
//                Mesh2 mesh = sceneObject.mesh_;
//                Material material = mesh.material;
//                material.onPrepareMaterial.onPrepareMaterial();
//                Shader shaderProgram = material.shader;
//
//                Mat4 modelMatrix = sceneObject.transform.getTransform();
//                shaderProgram.setModelMatrixUniform(modelMatrix);
//                shaderProgram.setViewMatrixUniform(mainCamera.transform.getTransform().inverse());
//                shaderProgram.setProjectionMatrixUniform(mainCamera.getProjectionMatrix());
//                shaderProgram.setNormalMatrixUniform(modelMatrix.inverse().transpose());
//                shaderProgram.setBaseColorUniform(material.baseColor);
//
//                VertexAttributes[] va = new VertexAttributes[mesh.vertices.length];
//                for (int i = 0; i < mesh.vertices.length; i++) {
//                    va[i] = mesh.vertices[i].generateVertexAttribs();
//                }
//
//                this.renderer.setVertexBuffer(mesh.vertices);
//                this.renderer.setVertexBuffer(va);
//                this.renderer.setIndexBuffer(mesh.faces);
//                this.renderer.setShaderProgram(shaderProgram);
//                this.renderer.setRenderState(material.getRenderStates());
//
//                this.renderer.draw_();

                sceneObject.render(renderer, mainCamera);
            }
        }


    }

}
