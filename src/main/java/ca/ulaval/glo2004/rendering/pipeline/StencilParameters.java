package ca.ulaval.glo2004.rendering.pipeline;

public class StencilParameters {

    StencilOperation stencilFail = StencilOperation.KEEP;
    StencilOperation stencilPassAndDepthFail = StencilOperation.KEEP;
    StencilOperation stencilPassAndDepthPass = StencilOperation.REPLACE;

    StencilFunction stencilFunction = StencilFunction.ALWAYS;

    public StencilParameters() {}

    public StencilParameters(StencilParameters original)
    {
        this.stencilFail = original.stencilFail;
        this.stencilPassAndDepthFail = original.stencilPassAndDepthFail;
        this.stencilPassAndDepthPass = original.stencilPassAndDepthPass;
        this.stencilFunction = original.stencilFunction;
    }

    public void setStencilOps(StencilOperation stencilFail, StencilOperation depthFail, StencilOperation depthPass)
    {
        this.stencilFail = stencilFail;
        this.stencilPassAndDepthFail = depthFail;
        this.stencilPassAndDepthPass = depthPass;
    }

    public void setStencilFunc(StencilFunction stencilFunc)
    {
        this.stencilFunction = stencilFunc;
    }
}
