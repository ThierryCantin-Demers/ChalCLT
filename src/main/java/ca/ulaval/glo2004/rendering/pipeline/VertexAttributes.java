package ca.ulaval.glo2004.rendering.pipeline;

import ca.ulaval.glo2004.rendering.shaders.DataType;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

import java.util.Arrays;

public class VertexAttributes {
    private float[] data;

    public VertexAttributes(int vertexSize) {
        this.data = new float[vertexSize];
    }

    public VertexAttributes(float[] data) {
        this.data = data;
    }

    public void setAttribute(int offset, DataType dataType, float[] attributeData) {
        int size = dataType.size;
        System.arraycopy(attributeData, 0, data, offset, size);
    }

    public float[] getAttribute(int offset, DataType dataType) {
//        int size = dataType.size;
//        float[] attributeData = new float[size];
//        System.arraycopy(data, offset, attributeData, 0, size);
//        return attributeData;
        return Arrays.copyOfRange(data, offset, offset + dataType.size);
    }

    public void getAttribute(int offset, int size, float[] out) {
        System.arraycopy(data, offset, out, 0, size);
    }

    public float[] getData() {
        float[] attributeData = new float[this.data.length];
        System.arraycopy(data, 0, attributeData, 0, attributeData.length);
        return attributeData;
    }

    public float[] getDataRef() {
        return this.data;
    }

    public void setData(float[] newData) {
        this.data = newData;
    }

    public void putData(float[] newData) {
        System.arraycopy(newData, 0, data, 0, data.length);
    }

    public float getFloat(int offset) {
        return data[offset];
    }

    public Vec2 getFloat2(int offset) {
        return new Vec2(data[offset], data[offset + 1]);
    }

    public Vec3 getFloat3(int offset) {
        return new Vec3(data[offset], data[offset + 1], data[offset+2]);
    }

    public Vec4 getFloat4(int offset) {
        return new Vec4(data[offset], data[offset + 1], data[offset+2], data[offset+3]);
    }


    public void putFloat(int offset, float float1) {
        data[offset] = float1;
    }

    public void putFloat2(int offset, Vec2 float2) {
        data[offset] = float2.x;
        data[offset + 1] = float2.y;
    }

    public void putFloat3(int offset, Vec3 float3) {
        data[offset] = float3.x;
        data[offset + 1] = float3.y;
        data[offset + 2] = float3.z;
    }

    public void putFloat4(int offset, Vec4 float4) {
        data[offset] = float4.x;
        data[offset + 1] = float4.y;
        data[offset + 2] = float4.z;
        data[offset + 3] = float4.w;
    }

    public static float[] interpolateBarycentric(float[] d1, float[] d2, float[] d3, float u, float v, float w, float perspCorrection) {
        if (d1.length != d2.length || d1.length != d3.length) {
            throw new IllegalArgumentException("Input arrays must be of the same length");
        }

        int dimension = d1.length;
        float[] interpolated = new float[dimension];

        for (int i = 0; i < dimension; i++) {
            interpolated[i] = (u * d1[i] + v * d2[i] + w * d3[i]) * perspCorrection;
        }

        return interpolated;
    }

    public static void interpolateBarycentric2(float[] d1, float[] d2, float[] d3, float u, float v, float w, float perspCorrection, float[] out) {
        int length = d1.length;
        int i = 0;

        for (; i < length - 3; i += 4) {
            out[i] = (d1[i] * u + d2[i] * v + d3[i] * w) * perspCorrection;
            out[i + 1] = (d1[i + 1] * u + d2[i + 1] * v + d3[i + 1] * w) * perspCorrection;
            out[i + 2] = (d1[i + 2] * u + d2[i + 2] * v + d3[i + 2] * w) * perspCorrection;
            out[i + 3] = (d1[i + 3] * u + d2[i + 3] * v + d3[i + 3] * w) * perspCorrection;
        }

        for (; i < length; i++) {
            out[i] = (u * d1[i] + v * d2[i] + w * d3[i]) * perspCorrection;
        }
    }


    public static void interpolateBarycentric(float[] d1, float[] d2, float[] d3, float u, float v, float w, float perspCorrection, float[] out) {

        int length = d1.length;
        int i = 0;


        for (; i < length - 3; i += 4) {
            float u1 = d1[i] * u;
            float u2 = d1[i + 1]*u;
            float u3 = d1[i + 2]*u;
            float u4 = d1[i + 3]*u;

            float v1 = d2[i]*v;
            float v2 = d2[i + 1]*v;
            float v3 = d2[i + 2]*v;
            float v4 = d2[i + 3]*v;

            float w1 = d3[i]*w;
            float w2 = d3[i + 1]*w;
            float w3 = d3[i + 2]*w;
            float w4 = d3[i + 3]*w;

            out[i] = (u1 + v1 + w1) * perspCorrection;
            out[i + 1] = (u2 + v2 + w2) * perspCorrection;
            out[i + 2] = (u3 + v3 + w3) * perspCorrection;
            out[i + 3] = (u4 + v4 + w4) * perspCorrection;
        }

        for (; i < length; i++) {
            out[i] = (u * d1[i] + v * d2[i] + w * d3[i]) * perspCorrection;
        }
    }
}
