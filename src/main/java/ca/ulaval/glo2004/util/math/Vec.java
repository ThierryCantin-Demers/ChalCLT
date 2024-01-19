package ca.ulaval.glo2004.util.math;

import java.util.Arrays;

public class Vec {

    public static float[] float2(float x, float y) {
        return new float[]{x, y};
    }

    public static float[] float3(float x, float y, float z) {
        return new float[]{x, y, z};
    }

    public static float[] float4(float x, float y, float z, float w) {
        return new float[]{x, y, z, w};
    }

    public static float[] float4(float[] float3) {
        float[] float4 = new float[]{0f, 0f, 0f, 1f};
        System.arraycopy(float3, 0, float4, 0, 3);
        return float4;
    }

    public static float x(float[] floatVec) {
        return floatVec[0];
    }

    public static float y(float[] floatVec) {
        return floatVec[1];
    }

    public static float z(float[] floatVec) {
        return floatVec[2];
    }

    public static float w(float[] floatVec) {
        return floatVec[3];
    }

    public static float[] xy(float[] floatVec) {
        return slice(floatVec, 0, 2);
    }

    public static float[] xz(float[] floatVec) {
        return slice(floatVec, 0, 3);
    }

    public static float[] yx(float[] floatVec) {
        return slice(floatVec, 1, 3);
    }

    public static float[] yz(float[] floatVec) {
        return slice(floatVec, 1, 4);
    }

    public static float[] zx(float[] floatVec) {
        return slice(floatVec, 2, 4);
    }

    public static float[] zy(float[] floatVec) {
        return slice(floatVec, 2, 5);
    }

    public static float[] xyz(float[] floatVec) {
        return Arrays.copyOf(floatVec, 3);
    }

    public static float[] xzy(float[] floatVec) {
        float[] temp = slice(floatVec, 0, 3);
        float tempVal = temp[1];
        temp[1] = temp[2];
        temp[2] = tempVal;
        return temp;
    }

    public static float[] yxz(float[] floatVec) {
        float[] temp = slice(floatVec, 0, 3);
        float tempVal = temp[0];
        temp[0] = temp[1];
        temp[1] = tempVal;
        return temp;
    }

    public static float[] yzx(float[] floatVec) {
        float[] temp = slice(floatVec, 0, 3);
        float tempVal = temp[0];
        temp[0] = temp[2];
        temp[2] = temp[1];
        temp[1] = tempVal;
        return temp;
    }

    public static float[] zxy(float[] floatVec) {
        float[] temp = slice(floatVec, 0, 3);
        float tempVal = temp[0];
        temp[0] = temp[2];
        temp[2] = temp[1];
        temp[1] = tempVal;
        return temp;
    }

    public static float[] zyx(float[] floatVec) {
        float[] temp = slice(floatVec, 0, 3);
        float tempVal = temp[0];
        temp[0] = temp[2];
        temp[2] = temp[0];
        temp[2] = tempVal;
        return temp;
    }

    public static float[] slice(float[] floatVec, int start, int end) {
        if (start < 0 || end > floatVec.length || start >= end) {
            throw new IllegalArgumentException("Invalid slice parameters");
        }
        return Arrays.copyOfRange(floatVec, start, end);
    }

    public static float[] rgba(float[] floatVec) {
        return Arrays.copyOf(floatVec, Math.min(4, floatVec.length));
    }

    public static float r(float[] floatVec) {
        return floatVec.length > 0 ? floatVec[0] : 0f;
    }

    public static float g(float[] floatVec) {
        return floatVec.length > 1 ? floatVec[1] : 0f;
    }

    public static float b(float[] floatVec) {
        return floatVec.length > 2 ? floatVec[2] : 0f;
    }

    public static float a(float[] floatVec) {
        return floatVec.length > 3 ? floatVec[3] : 1f;
    }

    public static float dot(float[] vec1, float[] vec2) {
        return vec1[0] * vec2[0] + vec1[1] * vec2[1] + vec1[2] * vec2[2];
    }

    public static float[] mult(float[] vec1, float[] vec2)
    {
        float[] result = new float[vec1.length];
        for (int i = 0; i < vec1.length; i++) {
            result[i] = vec1[i]*vec2[i];
        }
        return result;
    }

    public static float[] mult(float[] vec1, float factor)
    {
        float[] result = new float[vec1.length];
        for (int i = 0; i < vec1.length; i++) {
            result[i] = vec1[i]*factor;
        }
        return result;
    }

    public static float[] floor(float[] floatVec)
    {
        float[] result = new float[floatVec.length];
        for (int i = 0; i < floatVec.length; i++) {
            result[i] = (float) Math.floor(floatVec[i]);
        }
        return result;
    }

    public static float[] floorInPlace(float[] floatVec)
    {
        for (int i = 0; i < floatVec.length; i++) {
            floatVec[i] = (float) Math.floor(floatVec[i]);
        }
        return floatVec;
    }
}
