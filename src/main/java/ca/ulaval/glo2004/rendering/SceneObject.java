package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.pipeline.*;
import ca.ulaval.glo2004.rendering.shaders.OutlineShader;
import ca.ulaval.glo2004.rendering.shaders.Shader;
import ca.ulaval.glo2004.rendering.utils.GeometryUtils;
import ca.ulaval.glo2004.rendering.utils.Ray;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.Optional;

public class SceneObject implements Renderable {

    public String descriptor;
    public Mesh mesh;
    public Mesh2 mesh_ = new Mesh2();
    public Transform transform;

    private Object userData = null;

    public boolean selectable = true;
    public boolean selected = false;

    public boolean shaded = true;

    private ObjectEventListener objectEventListener = () -> {};
    private ObjectHoverListener objectHoverListener = () -> {};

    public void setObjectEventListener(ObjectEventListener objectEventListener) {
        this.objectEventListener = objectEventListener;
    }

    public void setObjectHoverListener(ObjectHoverListener objectHoverListener) {
        this.objectHoverListener = objectHoverListener;
    }

    public SceneObject(String descriptor)
    {
        this.descriptor = descriptor;
        this.mesh = new Mesh();
        this.transform = new Transform();
    }
    public SceneObject(String descriptor, Mesh mesh)
    {
        this.descriptor = descriptor;
        this.mesh = mesh;
        this.transform = new Transform();
    }

    public SceneObject(String descriptor, Mesh2 mesh)
    {
        this.descriptor = descriptor;
        this.mesh_ = mesh;
        this.transform = new Transform();
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public Object getUserData() {
        return userData;
    }

    public Optional<RaycastResult> raycast(Ray ray)
    {
        if (mesh_ == null)
        {
            return Optional.empty();
        }

        Mat4 worldTransform = transform.getTransform();

        for (Face face : mesh_.faces) {
            Vec3 vertex1Pos = mesh_.vertices[face.vi1].position;
            Vec3 vertex2Pos = mesh_.vertices[face.vi2].position;
            Vec3 vertex3Pos = mesh_.vertices[face.vi3].position;

            Triangle triTransformed = new Triangle(
                    worldTransform.transform(vertex1Pos.toVec4()).toVec3(),
                    worldTransform.transform(vertex2Pos.toVec4()).toVec3(),
                    worldTransform.transform(vertex3Pos.toVec4()).toVec3(),
                    0
            );
            Vec3 result = new Vec3(0,0,0);
            boolean triangleHit = GeometryUtils.intersectTriangle(triTransformed, ray.startPoint, ray.getPoint(), result);
            if (triangleHit)
                return Optional.of(new RaycastResult(this, result));
        }
        return Optional.empty();
    }

    @Override
    public void render(RenderAPI renderer, Camera mainCamera) {

        Mat4 viewMatrix = mainCamera != null ? mainCamera.getViewMatrix() : Mat4.identity();
        Mat4 projectionMatrix = mainCamera != null ? mainCamera.getProjectionMatrix() : Mat4.identity();

        Mesh2 mesh = this.mesh_;
        Material material = mesh.material;
        Shader shaderProgram = material.shader;
        material.applyMaterial();

        Mat4 modelMatrix = transform.getTransform();
        shaderProgram.setModelMatrixUniform(modelMatrix);
        shaderProgram.setViewMatrixUniform(viewMatrix);
        shaderProgram.setProjectionMatrixUniform(projectionMatrix);
        shaderProgram.setNormalMatrixUniform(modelMatrix.inverse().transpose());
        shaderProgram.setBaseColorUniform(material.baseColor);

        VertexAttributes[] va = new VertexAttributes[mesh.vertices.length];

        if (selected)
        {
            RenderStates renderStates = new RenderStates();
            renderStates.stencilTest = true;
            renderStates.stencilParams.setStencilFunc(StencilFunction.ALWAYS);
            renderStates.stencilParams.setStencilOps(StencilOperation.KEEP, StencilOperation.KEEP, StencilOperation.REPLACE);
            renderStates.stencilRef = 255;
            renderStates.stencilMask = 0xFF;
            renderStates.stencilWriteMask = 0xFF;

            for (int i = 0; i < mesh.vertices.length; i++) {
                va[i] = mesh.vertices[i].generateVertexAttribs();
            }
            renderer.setVertexBuffer(va);
            renderer.setIndexBuffer(mesh.faces);
            renderer.setShaderProgram(material.shader);
            renderer.setRenderState(renderStates);
            renderer.draw_();


            OutlineShader outlineShader = new OutlineShader();
            outlineShader.setModelMatrixUniform(modelMatrix);
            outlineShader.setViewMatrixUniform(viewMatrix);
            outlineShader.setProjectionMatrixUniform(projectionMatrix);
            outlineShader.setNormalMatrixUniform(Mat4.identity());
            outlineShader.setBaseColorUniform(0x8000FF00);

            renderStates.stencilParams.setStencilFunc(StencilFunction.NOTEQUAL);
            renderStates.stencilRef = 255;
            renderStates.stencilMask = 0xFF;
            renderStates.stencilWriteMask = 0x00;
            renderStates.depthTest = true;
            renderStates.depthMask = false;
            renderStates.blend = true;
            renderStates.cullFace = true;
            for (int i = 0; i < mesh.vertices.length; i++) {
                VertexAttributes vert = outlineShader.createInputVertexAttribs();
                vert.putFloat3(0, mesh.vertices[i].position);
                vert.putFloat3(3, mesh.vertices[i].avgNormal);
                va[i] = vert;
            }
            renderer.setVertexBuffer(va);
            renderer.setIndexBuffer(mesh.faces);
            renderer.setShaderProgram(outlineShader);
            renderer.setRenderState(renderStates);
            renderer.draw_();


        }
        else
        {
            for (int i = 0; i < mesh.vertices.length; i++) {
                va[i] = mesh.vertices[i].generateVertexAttribs();
            }


            renderer.setVertexBuffer(mesh.vertices);
            renderer.setVertexBuffer(va);
            renderer.setIndexBuffer(mesh.faces);
            renderer.setShaderProgram(shaderProgram);
            renderer.setRenderState(material.getRenderStates());

            renderer.draw_();
        }

    }

    public void notifyEvent()
    {
        this.objectEventListener.handle();
    }

    public void notifyHover()
    {
        this.objectHoverListener.handle();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj instanceof SceneObject)
        {
            if (((SceneObject) obj).getUserData() != null && this.getUserData() != null && (((SceneObject) obj).getUserData().equals(this.getUserData()) || ((SceneObject) obj).getUserData() == this.getUserData()))
            {
                return true;
            }
        }
        return super.equals(obj);
    }
}
