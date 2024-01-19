package ca.ulaval.glo2004.rendering.pipeline;

public class RenderStates {
    public boolean blend = false;
    public BlendParameters blendParams = new BlendParameters();

    public boolean depthTest = true;
    public boolean depthMask = true;
    public DepthFunction depthFunc = DepthFunction.LESS;

    public boolean stencilTest = false;
    public int stencilMask = 0xFF;
    public int stencilWriteMask = 0xFF;
    public int stencilRef = 0;
    public StencilParameters stencilParams = new StencilParameters();

    public boolean cullFace = true;
    public PrimitiveType primitiveType = PrimitiveType.TRIANGLES;

    public float lineWidth = 1.f;

    public boolean skipSpaceConversion = false;
    public boolean cullPlanes = true;

    public RenderStates()
    {}

    public RenderStates(RenderStates original) {
        this.blend = original.blend;
        this.blendParams = new BlendParameters(original.blendParams);

        this.depthTest = original.depthTest;
        this.depthMask = original.depthMask;
        this.depthFunc = original.depthFunc;

        this.stencilTest = original.stencilTest;
        this.stencilMask = original.stencilMask;
        this.stencilWriteMask = original.stencilWriteMask;
        this.stencilRef = original.stencilRef;
        this.stencilParams = new StencilParameters(original.stencilParams);

        this.cullFace = original.cullFace;
        this.primitiveType = original.primitiveType;

        this.lineWidth = original.lineWidth;

        this.skipSpaceConversion = original.skipSpaceConversion;
        this.cullPlanes = original.cullPlanes;
    }
}
