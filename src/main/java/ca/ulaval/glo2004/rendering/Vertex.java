package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.rendering.shaders.DataType;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;

public class Vertex implements IVertex {
    public Vec3 position;
    public Vec2 uv;
    public Vec3 normal;
    public Vec3 avgNormal;
    public int color;

    public Vertex(Vec3 position)
    {
        this.position = position;
        this.uv = new Vec2(0,0);
        this.normal = Vec3.forward();
        this.avgNormal = Vec3.forward();
        this.color = 0xFFFFFFFF;
    }

    public Vertex()
    {
        this.position = new Vec3(0,0,0);
        this.uv = new Vec2(0,0);
        this.normal = Vec3.forward();
        this.avgNormal = Vec3.forward();
        this.color = 0xFFFFFFFF;
    }

    public Vertex(Vec3 position, Vec3 normal, int color)
    {
        this.position = position;
        this.uv = new Vec2(0,0);
        this.normal = normal;
        this.avgNormal = normal;
        this.color = color;
    }

    public Vertex(Vec3 position, Vec2 uv, Vec3 normal, int color)
    {
        this.position = position;
        this.uv = uv;
        this.normal = normal;
        this.avgNormal = normal;
        this.color = color;
    }

    @Override
    public VertexAttributes generateVertexAttribs()
    {
        VertexAttributes attributes = new VertexAttributes(DataType.FLOAT3.size + DataType.FLOAT2.size + DataType.FLOAT3.size);
        attributes.putFloat3(0, position);
        attributes.putFloat2(3, uv);
        attributes.putFloat3(5, normal);
        return attributes;
    }
}
