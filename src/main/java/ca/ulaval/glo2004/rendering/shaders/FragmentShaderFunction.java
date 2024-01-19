package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.pipeline.Varyings;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.util.math.Vec4;

public interface FragmentShaderFunction {
    int fragment(Vec4 fragCoord, Varyings varyings);
    void fragment(Vec4 gl_FragCoord, final VertexAttributes out, Vec4 gl_FragColor);
}
