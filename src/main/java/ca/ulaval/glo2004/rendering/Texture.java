package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.util.math.Vec2;

public interface Texture {
    int sample(Vec2 uv);
    int sample(float[] uv);
    int getPixel(int x, int y);

    int getWidth();
    int getHeight();
}
