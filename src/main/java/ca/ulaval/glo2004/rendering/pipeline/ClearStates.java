package ca.ulaval.glo2004.rendering.pipeline;

public class ClearStates {
    public boolean depthFlag = false;
    public boolean colorFlag = false;
    public boolean stencilFlag = false;

    public int clearColor = 0xFF000000;
    public float clearDepth = 1.0f;
    public byte clearStencil = 0x00;

    public ClearStates()
    {
    }

    public ClearStates(boolean depthFlag, boolean colorFlag, boolean stencilFlag, int clearColor, float clearDepth, byte clearStencil) {
        this.depthFlag = depthFlag;
        this.colorFlag = colorFlag;
        this.clearColor = clearColor;
        this.clearDepth = clearDepth;
        this.stencilFlag = stencilFlag;
        this.clearStencil = clearStencil;
    }
}
