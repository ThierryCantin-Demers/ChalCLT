package ca.ulaval.glo2004.rendering.pipeline;

import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec4;

public class VertexHolder {
    public final Vec4 position;
    public final VertexAttributes verticesAttributes;

    public VertexHolder(Vec4 gl_Position, VertexAttributes verticesAttributes) {
        this.position = gl_Position;
        this.verticesAttributes = verticesAttributes;
    }

    public static VertexHolder interpolate(VertexHolder v1, VertexHolder v2, float t)
    {
        Vec4 pos = Vec4.lerp(v1.position, v2.position, t);
        float[] data = MathUtils.lerp(v1.verticesAttributes.getData(), v2.verticesAttributes.getData(), t);

        return new VertexHolder(pos, new VertexAttributes(data));
    }

}
