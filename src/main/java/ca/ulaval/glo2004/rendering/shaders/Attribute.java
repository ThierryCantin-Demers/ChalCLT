package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;

public class Attribute {
    public final int offset;
    public final DataType dataType;
    public final int size;

    public Attribute(int offset, DataType dataType) {
        this.offset = offset;
        this.dataType = dataType;
        this.size = dataType.size;
    }

    public float[] getAttribute(VertexAttributes attributes) {
        return attributes.getAttribute(offset, dataType);
    }

    public void getAttribute(VertexAttributes attributes, float[] out) {
        attributes.getAttribute(offset, size, out);
    }

    public void setAttribute(VertexAttributes attributes, float[] value) {
        attributes.setAttribute(offset, dataType, value);
    }
}