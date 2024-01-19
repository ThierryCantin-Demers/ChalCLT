package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.Vertex;
import ca.ulaval.glo2004.rendering.pipeline.VSOutput;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.util.math.Vec4;

public interface VertexShaderFunction {
    VSOutput vertex(Vertex vertex);
    void vertex(final VertexAttributes in, final Vec4 gl_Position, final VertexAttributes out);
}
