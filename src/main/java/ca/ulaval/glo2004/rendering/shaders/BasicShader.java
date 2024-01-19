package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.awt.AWTARGBTexture;
import ca.ulaval.glo2004.rendering.pipeline.VSOutput;
import ca.ulaval.glo2004.rendering.pipeline.Varyings;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

public class BasicShader extends Shader {

    static final int IN_POSITION_OFFSET = 0;
    static final Attribute in_position = new Attribute(IN_POSITION_OFFSET, DataType.FLOAT3);
    static final int IN_UV_OFFSET = 3;
    static final Attribute in_uv = new Attribute(IN_UV_OFFSET, DataType.FLOAT2);
    static final int IN_NORMAL_OFFSET = 5;
    static final Attribute in_normal = new Attribute(IN_NORMAL_OFFSET, DataType.FLOAT3);

    static final int OUT_UV_OFFSET = 0;
    static final Attribute out_uv = new Attribute(OUT_UV_OFFSET, DataType.FLOAT2);
    static final int OUT_NORMAL_OFFSET = 2;
    static final Attribute out_normal = new Attribute(OUT_NORMAL_OFFSET, DataType.FLOAT3);


    Vec3 globalLightDirection = new Vec3(0,0.5f,-1).normalized();
    static Texture sampler = AWTARGBTexture.createFromFile("cross-laminated-timber-clt-end-grain-327-mm-architextures(1).png");


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
        Vec2 uv = in.getFloat2(3);//in.getAttribute(3, DataType.FLOAT2);
        Vec3 normal = in.getFloat3(5);//in.getAttribute(5, DataType.FLOAT3);

//        Vec4 newPosition = projectionMatrix.transform(viewMatrix.transform(modelMatrix.transform(position.toVec4())));
        Vec4 newPosition = projectionMatrix.multiply(viewMatrix.multiply(modelMatrix)).transform(position.toVec4());
        Vec3 newNormal = normalMatrix.transform(normal.toVec4()).toVec3().normalized();
        out.putFloat2(0, uv);//out_uv.setAttribute(out, uv);
        out.putFloat3(2, newNormal);//out_normal.setAttribute(out, newNormal.toVec3());
//        out_color.setAttribute(out, new float[]{1f,1f,1f,1f});

        gl_Position.set(newPosition);
    }

    @Override
    public int fragment(Vec4 fragCoord, Varyings varyings) {

        float shading = Math.max(0.1f, Vec3.dot(globalLightDirection, varyings.normal));

        shading = (float) Math.sqrt(shading);

        float red = ColorUtils.getRed(varyings.color) * shading;
        float green = ColorUtils.getGreen(varyings.color) * shading;
        float blue = ColorUtils.getBlue(varyings.color) * shading;

        Vec2 uv = varyings.uv;

        int color = sampler.sample(uv);

        // Checkerboard pattern
        float checkerSize = 10.0f;
        Vec2 checkerCoord = Vec2.mult(uv, checkerSize).floor();
        int checker = (int) ((checkerCoord.x + checkerCoord.y) % 2.0f);

        return ColorUtils.multRGB(color, shading);//ColorUtils.rgba((int) (checker*red), (int) (checker*green), (int) (checker*blue), 255);
    }

    @Override
    public VertexAttributes createInputVertexAttribs() {
        return new VertexAttributes(in_position.dataType.size + in_uv.dataType.size + in_normal.dataType.size);
    }

    @Override
    public VertexAttributes createOutputVertexAttribs() {
        return new VertexAttributes(out_uv.dataType.size + out_normal.dataType.size);
    }

    @Override
    public void fragment(Vec4 gl_FragCoord, final VertexAttributes out, final Vec4 gl_FragColor) {
        Vec2 uv = out.getFloat2(OUT_UV_OFFSET);
        Vec3 normal = out.getFloat3(OUT_NORMAL_OFFSET);

        float shading = Math.max(0.1f, Vec3.dot(globalLightDirection, normal));

        shading = (float) StrictMath.sqrt(shading);

//        System.out.println(uv);

//        int sample = sampler.sample(uv);
//        ColorUtils.vec4(sample, gl_FragColor);
//        gl_FragColor.mult(new Vec4(shading, shading, shading, 1f));

        // Checkerboard pattern
        float checkerSize = 3f;
        Vec2 checkerCoord = Vec2.mult(uv, checkerSize).floor();
        int checker = (int) ((checkerCoord.x + checkerCoord.y) % 2.0f);

        float checkerMultiplier = checker == 0 ? 0.96f : 1.0f; // Adjust checker intensity

        gl_FragColor.set(ColorUtils.vec4(ColorUtils.multRGB(baseColor, shading*checkerMultiplier)));

//        gl_FragColor.set(uv.x * shading, uv.y *shading, 0, 1f);

//        // Checkerboard pattern
//        float checkerSize = 10.0f;
//        float[] checkerCoord = Vec.floor(Vec.mult(uv.toArray(), checkerSize));
//        int checker = (int) ((Vec.x(checkerCoord) + Vec.y(checkerCoord)) % 2.0f);
////        gl_FragColor.set(color);
    }
}
