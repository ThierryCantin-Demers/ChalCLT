package ca.ulaval.glo2004.rendering.shaders;

public enum DataType {
    FLOAT(1),
    FLOAT2(2),
    FLOAT3(3),
    FLOAT4(4),
    MAT3(9),
    MAT4(16);

    public final int size;

    DataType(int size) {
        this.size = size;
    }
}