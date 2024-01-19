package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.Texture;
import ca.ulaval.glo2004.rendering.Vertex;
import ca.ulaval.glo2004.rendering.awt.AWTARGBTexture;
import ca.ulaval.glo2004.rendering.pipeline.VSOutput;
import ca.ulaval.glo2004.rendering.pipeline.Varyings;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

public class OutlineShader extends Shader {

    static final int IN_POSITION_OFFSET = 0;
    static final Attribute in_position = new Attribute(IN_POSITION_OFFSET, DataType.FLOAT3);


    public float outlining = 2f;

    @Override
    public VSOutput vertex(Vertex vertex) {
        VSOutput v = new VSOutput();
        v.position = projectionMatrix.transform(viewMatrix.transform(modelMatrix.transform(vertex.position.toVec4())));
        v.uv = vertex.uv;
        v.normal = normalMatrix.transform(vertex.normal.toVec4()).toVec3().normalized();
        v.color = vertex.color;
        return v;
    }

    @Override
    public void vertex(final VertexAttributes in, final Vec4 gl_Position, final VertexAttributes out) {
        Vec3 position = in.getFloat3(0);//in.getAttribute(0, DataType.FLOAT3);
        Vec3 normal = in.getFloat3(3);//in.getAttribute(0, DataType.FLOAT3);

        Mat4 scale = Mat4.scale(new Vec3(outlining,outlining,outlining));

        Vec4 newPosition = projectionMatrix.transform(viewMatrix.transform(modelMatrix.transform((Vec3.add(position, Vec3.mult(normal, outlining))).toVec4())));
//        Vec3 newNormal = normalMatrix.transform(normal.toVec4()).toVec3().normalized();
//        out.putFloat2(0, uv);//out_uv.setAttribute(out, uv);
//        out.putFloat3(2, newNormal);//out_normal.setAttribute(out, newNormal.toVec3());
//        out_color.setAttribute(out, new float[]{1f,1f,1f,1f});

        gl_Position.set(newPosition);
    }

    @Override
    public int fragment(Vec4 fragCoord, Varyings varyings) {
        return 0;//ColorUtils.rgba((int) (checker*red), (int) (checker*green), (int) (checker*blue), 255);
    }

    @Override
    public VertexAttributes createInputVertexAttribs() {
        return new VertexAttributes(in_position.dataType.size + 3);
    }

    @Override
    public VertexAttributes createOutputVertexAttribs() {
        return new VertexAttributes(0);
    }

    @Override
    public void fragment(Vec4 gl_FragCoord, final VertexAttributes out, final Vec4 gl_FragColor) {
        gl_FragColor.set(ColorUtils.float4(baseColor));
    }
}
