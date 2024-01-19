package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.pipeline.VSOutput;
import ca.ulaval.glo2004.rendering.pipeline.Varyings;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec4;

public abstract class Shader implements VertexShaderFunction, FragmentShaderFunction {

    Mat4 modelMatrix = Mat4.identity();
    Mat4 normalMatrix = Mat4.identity();
    Mat4 viewMatrix = Mat4.identity();
    Mat4 projectionMatrix = Mat4.identity();

    int baseColor = 0xFFFFFFFF;

    public void setModelMatrixUniform(Mat4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public void setViewMatrixUniform(Mat4 viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void setProjectionMatrixUniform(Mat4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setNormalMatrixUniform(Mat4 normalMatrix) {
        this.normalMatrix = normalMatrix;
    }

    public void setBaseColorUniform(int baseColor) {
        this.baseColor = baseColor;
    }

    @Override
    public abstract VSOutput vertex(Vertex vertex);

    @Override
    public abstract void vertex(final VertexAttributes in, final Vec4 gl_Position, final VertexAttributes out);

    @Override
    public abstract int fragment(Vec4 fragCoord, Varyings varyings);

    @Override
    public abstract void fragment(Vec4 gl_FragCoord, final VertexAttributes out, Vec4 gl_FragColor);

    public abstract VertexAttributes createInputVertexAttribs();
    public abstract VertexAttributes createOutputVertexAttribs();
    
}