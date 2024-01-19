package ca.ulaval.glo2004.rendering.pipeline;

public class PrimitiveHolder {

    VertexHolder[] vertices;

    public PrimitiveHolder(VertexHolder[] vertices) {
        this.vertices = vertices;
    }

    public PrimitiveHolder(int count) {
        this.vertices = new VertexHolder[count];
    }
}
