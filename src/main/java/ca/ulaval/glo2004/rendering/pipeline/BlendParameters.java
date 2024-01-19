package ca.ulaval.glo2004.rendering.pipeline;

public class BlendParameters {
    BlendFunction blendFuncRgb = BlendFunction.ADD;
    BlendFactor blendSrcRgb = BlendFactor.ONE;
    BlendFactor blendDstRgb = BlendFactor.ZERO;

    BlendFunction blendFuncAlpha = BlendFunction.ADD;
    BlendFactor blendSrcAlpha = BlendFactor.ONE;
    BlendFactor blendDstAlpha = BlendFactor.ZERO;

    public BlendParameters() {}

    public BlendParameters(BlendParameters original) {
        this.blendFuncRgb = original.blendFuncRgb;
        this.blendSrcRgb = original.blendSrcRgb;
        this.blendDstRgb = original.blendDstRgb;
        this.blendFuncAlpha = original.blendFuncAlpha;
        this.blendSrcAlpha = original.blendSrcAlpha;
        this.blendDstAlpha = original.blendDstAlpha;
    }

    public void setBlendFactor(BlendFactor src, BlendFactor dst) {
        blendSrcRgb = src;
        blendSrcAlpha = src;
        blendDstRgb = dst;
        blendDstAlpha = dst;
    }

    public void setBlendFunc(BlendFunction func) {
        blendFuncRgb = func;
        blendFuncAlpha = func;
    }
}
