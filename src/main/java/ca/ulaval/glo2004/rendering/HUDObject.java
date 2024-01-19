package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.pipeline.*;
import ca.ulaval.glo2004.rendering.shaders.OutlineShader;
import ca.ulaval.glo2004.rendering.shaders.Shader;
import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.rendering.utils.GeometryUtils;
import ca.ulaval.glo2004.rendering.utils.Ray;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.Optional;

public class HUDObject extends SceneObject implements Renderable {

    public HUDObject(String descriptor)
    {
        super(descriptor);
    }

    public HUDObject(String descriptor, Mesh2 mesh)
    {
        super(descriptor, mesh);
    }

    @Override
    public void render(RenderAPI renderer, Camera mainCamera) {

        Mat4 viewMatrix = mainCamera != null ? mainCamera.getViewMatrix() : Mat4.identity();
        Mat4 projectionMatrix = mainCamera != null ? mainCamera.getProjectionMatrix() : Mat4.identity();

        Mesh2 mesh = this.mesh_;
        Material material = mesh.material;
        Shader shaderProgram = material.shader;
        material.applyMaterial();

        if (super.selected)
        {
            material.baseColor = ColorUtils.multRGB(material.baseColor, 1.25f);
        }

        Mat4 modelMatrix = transform.getTransform();
        shaderProgram.setModelMatrixUniform(modelMatrix);
        shaderProgram.setViewMatrixUniform(viewMatrix);
        shaderProgram.setProjectionMatrixUniform(projectionMatrix);
        try {
            shaderProgram.setNormalMatrixUniform(modelMatrix.inverse().transpose());
        }
        catch (Exception ignored)
        {
        }
        shaderProgram.setBaseColorUniform(material.baseColor);

        VertexAttributes[] va = new VertexAttributes[mesh.vertices.length];

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
