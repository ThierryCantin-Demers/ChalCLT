package ca.ulaval.glo2004.rendering;

public interface IView {
    void init();
    void clearColorBuffer(int argb);
    void clearDepthBuffer(float depth);
    void clearStencilBuffer(byte value);
    void setDepth(int x, int y, float depth);
    void setDepth(int index, float depth);
    float getDepth(int x, int y);
    float getDepth(int index);
    void setPixel(int x, int y, float z, int argb);
    void setPixel(int index, int argb);
    int getPixel(int x, int y, int z);
    int getPixel(int index);
    byte getStencil(int index);
    byte getStencil(int x, int y);
    void setStencil(int x, int y, byte value);
    void setStencil(int index, byte value);
    void incrementStencil(int x, int y, boolean wrap);
    void decrementStencil(int x, int y, boolean wrap);
    int getWidth();
    int getHeight();
}
