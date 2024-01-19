package ca.ulaval.glo2004.rendering.utils;

import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec4;

public class ColorUtils {

    private static final float RECIPROCAL_255 = 1.0f / 255f;

    public static int setAlpha(int argb, int alpha)
    {
        alpha = clamp(alpha);
        int alphaMask = 0xFF000000;
        int clearedColor = argb & ~alphaMask;
        int newAlpha = (alpha << 24) & alphaMask;
        return clearedColor | newAlpha;
    }

    public static int setRed(int argb, int red) {
        red = clamp(red);
        int redMask = 0x00FF0000;
        int clearedColor = argb & ~redMask;
        int newRed = (red << 16) & redMask;
        return clearedColor | newRed;
    }

    public static int setGreen(int argb, int green) {
        green = clamp(green);
        int greenMask = 0x0000FF00;
        int clearedColor = argb & ~greenMask;
        int newGreen = (green << 8) & greenMask;
        return clearedColor | newGreen;
    }

    public static int setBlue(int argb, int blue) {
        blue = clamp(blue);
        int blueMask = 0x000000FF;
        int clearedColor = argb & ~blueMask;
        int newBlue = blue & blueMask;
        return clearedColor | newBlue;
    }

    public static int rgba(int red, int green, int blue, int alpha) {
        return clamp(alpha) << 24 | clamp(red) << 16 | clamp(green) << 8 | clamp(blue);
    }

    public static int rgb(int red, int green, int blue) {
        return rgba(red, green, blue, 255);
    }

    public static int rgba(float r, float g, float b, float a)
    {
        return rgba((int)(r*255), (int)(g*255), (int)(b*255), (int)(a*255));
    }

    public static int rgba(float[] rgba)
    {
        float r = rgba[0];
        float g = rgba[1];
        float b = rgba[2];
        float a = rgba[3];
        return rgba((int) (r*255), (int) (g*255), (int) (b*255), (int) (a*255));
    }

    public static int interpolateBarycentric(int color1, int color2, int color3, float u, float v, float w) {
        int alpha = Math.round(u * getAlpha(color1) + v * getAlpha(color2) + w * getAlpha(color3));
        int red = Math.round(u * getRed(color1) + v * getRed(color2) + w * getRed(color3));
        int green = Math.round(u * getGreen(color1) + v * getGreen(color2) + w * getGreen(color3));
        int blue = Math.round(u * getBlue(color1) + v * getBlue(color2) + w * getBlue(color3));

        return rgba(red, green, blue, alpha);
    }

    public static int lerp(int color1, int color2, float t) {
        int alpha = Math.round(MathUtils.lerp(getAlpha(color1), getAlpha(color2), t));
        int red = Math.round(MathUtils.lerp(getRed(color1), getRed(color2), t));
        int green = Math.round(MathUtils.lerp(getGreen(color1), getGreen(color2), t));
        int blue = Math.round(MathUtils.lerp(getBlue(color1), getBlue(color2), t));

        return rgba(red, green, blue, alpha);
    }

    public static int mult(int argb, float factor)
    {
        int alpha = (int) (getAlpha(argb) * factor);
        int red = (int) (getRed(argb) * factor);
        int green = (int) (getGreen(argb) * factor);
        int blue = (int) (getBlue(argb) * factor);

        return rgba(red, green, blue, alpha);
    }

    public static int multRGB(int argb, float factor)
    {
        int alpha = getAlpha(argb);
        int red = ((int) (getRed(argb) * factor));
        int green = ((int) (getGreen(argb) * factor));
        int blue = ((int) (getBlue(argb) * factor));

        return rgba(red, green, blue, alpha);
    }

    public static int multRGBA(int argb, float factor)
    {
        int alpha = ((int) (getAlpha(argb) * factor));
        int red = ((int) (getRed(argb) * factor));
        int green = ((int) (getGreen(argb) * factor));
        int blue = ((int) (getBlue(argb) * factor));

        return rgba(red, green, blue, alpha);
    }

    public static float[] float4(int argb)
    {
        return new float[]{(float)getRed(argb)*RECIPROCAL_255, (float)getGreen(argb)*RECIPROCAL_255, (float)getBlue(argb)*RECIPROCAL_255, (float)getAlpha(argb)*RECIPROCAL_255};
    }

    public static void float4(int argb, float[] out)
    {
        out[0] = (float)getRed(argb)*RECIPROCAL_255;
        out[1] = (float)getGreen(argb)*RECIPROCAL_255;
        out[2] = (float)getBlue(argb)*RECIPROCAL_255;
        out[3] = (float)getAlpha(argb)*RECIPROCAL_255;
    }

    public static Vec4 vec4(int argb)
    {
        return new Vec4(((float)getRed(argb))*RECIPROCAL_255, ((float)getGreen(argb))*RECIPROCAL_255, ((float)getBlue(argb))*RECIPROCAL_255, ((float)getAlpha(argb))*RECIPROCAL_255);
    }

    public static void vec4(int argb, Vec4 out)
    {
        out.x = (float)getRed(argb)*RECIPROCAL_255;
        out.y = (float)getGreen(argb)*RECIPROCAL_255;
        out.z = (float)getBlue(argb)*RECIPROCAL_255;
        out.w = (float)getAlpha(argb)*RECIPROCAL_255;
    }

    public static int getBrightness(int argb)
    {
        return (int) (0.299f * getRed(argb) + 0.587f * getGreen(argb) + 0.114f * getBlue(argb));
    }

    public static int getContrast(int color1, int color2)
    {
        return Math.abs(getBrightness(color1) - getBrightness(color2));
    }

    public static int calculateInversion(int color, int reference)
    {
        int contrast = getContrast(color, reference);
        int threshold = 30;

        if (contrast < threshold)
        {
            int newR = (getRed(color) - getRed(reference))/2;
            int newG = (getGreen(color) - getGreen(reference))/2;
            int newB = (getBlue(color) - getBlue(reference))/2;
            return rgba(newR, newG, newB, 255);
        }
        return color;
    }

    public static int getAlpha(int argb) {
        return (argb >> 24) & 0xFF;
    }

    public static int getRed(int argb) {
        return (argb >> 16) & 0xFF;
    }

    public static int getGreen(int argb) {
        return (argb >> 8) & 0xFF;
    }

    public static int getBlue(int argb) {
        return argb & 0xFF;
    }

    public static int clamp(int c)
    {
        return MathUtils.clamp(c, 0, 255);
    }

}
