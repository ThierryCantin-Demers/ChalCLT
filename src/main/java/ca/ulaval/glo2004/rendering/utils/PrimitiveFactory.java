package ca.ulaval.glo2004.rendering.utils;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveFactory {

    public static Mesh createPlane(int argb)
    {
        List<Triangle> triangleList = new ArrayList<>();

        // Define the vertices of the plane
        Vec3 v0 = new Vec3(-0.5f, 0.5f, 0.0f);
        Vec3 v1 = new Vec3(-0.5f, -0.5f, 0.0f);
        Vec3 v2 = new Vec3(0.5f, -0.5f, 0.0f);
        Vec3 v3 = new Vec3(0.5f, 0.5f, 0.0f);

        // Create two triangles to form the plane
        Triangle triangle1 = new Triangle(v0, v1, v2, argb);
        Triangle triangle2 = new Triangle(v0, v2, v3, argb);

        // Add the triangles to the list
        triangleList.add(triangle1);
        triangleList.add(triangle2);

        // Create and return the mesh
        return new Mesh(triangleList.toArray(new Triangle[0]));
    }

    public static Mesh2 createPlane2(int argb)
    {
        Vec3 v0 = new Vec3(-0.5f, 0.5f, 0.0f);
        Vec3 v1 = new Vec3(-0.5f, -0.5f, 0.0f);
        Vec3 v2 = new Vec3(0.5f, -0.5f, 0.0f);
        Vec3 v3 = new Vec3(0.5f, 0.5f, 0.0f);

        Mesh2 mesh = new Mesh2(new Vertex[]{
                new Vertex(v0, new Vec2(0,1), new Vec3(0,0,0), 0xFFFFFFFF),
                new Vertex(v1, new Vec2(0,0), new Vec3(0,0,0), 0xFFFFFFFF),
                new Vertex(v2, new Vec2(1,0), new Vec3(0,0,0), 0xFFFFFFFF),
                new Vertex(v3, new Vec2(1,1), new Vec3(0,0,0), 0xFFFFFFFF)
        }, new Face[]{
                new Face(0, 1, 2), new Face(0, 2, 3)
        });
        mesh.material.getRenderStates().cullFace = false;
        return mesh;
    }

    public static Mesh2 createQuad(float size, int argb) {
        float halfSize = size / 2;

        Vec3 topLeft = new Vec3(-halfSize, halfSize, 0);
        Vec3 topRight = new Vec3(halfSize, halfSize, 0);
        Vec3 bottomRight = new Vec3(halfSize, -halfSize, 0);
        Vec3 bottomLeft = new Vec3(-halfSize, -halfSize, 0);

        return createQuad(topLeft, bottomLeft, bottomRight, topRight, argb);
    }

    public static Mesh2 createQuad_(float size, int argb) {
        float halfSize = size / 2;

        return createQuad(-halfSize, halfSize, halfSize, -halfSize, argb);
    }

    public static Mesh2 createQuad(float left, float top, float right, float bottom, int argb) {
        Vec3 topLeft = new Vec3(left, top, 0);
        Vec3 bottomLeft = new Vec3(left, bottom, 0);
        Vec3 bottomRight = new Vec3(right, bottom, 0);
        Vec3 topRight = new Vec3(right, top, 0);

        Vec2 texTopLeft = new Vec2(0, 0);
        Vec2 texBottomLeft = new Vec2(0, 1);
        Vec2 texBottomRight = new Vec2(1, 1);
        Vec2 texTopRight = new Vec2(1, 0);

        return new Mesh2(new Vertex[]{
                new Vertex(topLeft, texTopLeft, new Vec3(0, 0, 0), argb),
                new Vertex(bottomLeft, texBottomLeft, new Vec3(0, 0, 0), argb),
                new Vertex(bottomRight, texBottomRight, new Vec3(0, 0, 0), argb),
                new Vertex(topRight, texTopRight, new Vec3(0, 0, 0), argb)
        }, new Face[]{
                new Face(0, 1, 2), new Face(0, 2, 3)
        });
    }

    public static Mesh2 createQuad(Vec3 topLeft, Vec3 bottomLeft, Vec3 bottomRight, Vec3 topRight, int argb)
    {
        // Define the vertices of the plane in counterclockwise order
        Vec3 v0 = topLeft.copy();       // Top Left
        Vec3 v1 = topRight.copy();      // Top Right
        Vec3 v2 = bottomRight.copy();   // Bottom Right
        Vec3 v3 = bottomLeft.copy();    // Bottom Left

        // Create and return the mesh
        return new Mesh2(new Vertex[]{
                new Vertex(v0, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v1, new Vec2(1, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(0, 0), new Vec3(0, 0, 0), argb)
        }, new Face[]{
                new Face(0, 1, 2), new Face(0, 2, 3)
        });
    }
    //TODO: MIGHT HAVE TO FIX THIS
    public static Mesh2 createLeftTriangle(Vec3 topLeft, Vec3 bottomLeft, Vec3 bottomRight, int argb)
    {
        // Define the vertices of the plane in counterclockwise order
        Vec3 v0 = topLeft.copy();       // Top Left
        Vec3 v2 = bottomRight.copy();   // Bottom Right
        Vec3 v3 = bottomLeft.copy();    // Bottom Left

        // Create and return the mesh
        return new Mesh2(new Vertex[]{
                new Vertex(v0, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
        }, new Face[]{
                new Face(0, 1, 2)
        });
    }
    public static Mesh2 createRightTriangle(Vec3 bottomLeft, Vec3 bottomRight, Vec3 topRight, int argb)
    {
        // Define the vertices of the plane in counterclockwise order
        Vec3 v1 = topRight.copy();      // Top Right
        Vec3 v2 = bottomRight.copy();   // Bottom Right
        Vec3 v3 = bottomLeft.copy();    // Bottom Left

        // Create and return the mesh
        return new Mesh2(new Vertex[]{
                new Vertex(v1, new Vec2(1, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(0, 0), new Vec3(0, 0, 0), argb)
        }, new Face[]{
                new Face(0, 1, 2)
        });
    }

    public static Mesh2 createQuad(Vec3 topLeft, Vec3 bottomLeft, Vec3 bottomRight, Vec3 topRight, int argb, Vec3 worldUVMin, Vec3 worldUVMax)
    {
        Vec3 uvScale = Vec3.sub(worldUVMax, worldUVMin);

        Vec3 v0 = topLeft;       // Top Left
        Vec3 v1 = topRight;      // Top Right
        Vec3 v2 = bottomRight;   // Bottom Right
        Vec3 v3 = bottomLeft;    // Bottom Left

        Vec3 uv0 = Vec3.div(Vec3.sub(v0, worldUVMin), uvScale);
        Vec3 uv1 = Vec3.div(Vec3.sub(v1, worldUVMin), uvScale);
        Vec3 uv2 = Vec3.div(Vec3.sub(v2, worldUVMin), uvScale);
        Vec3 uv3 = Vec3.div(Vec3.sub(v3, worldUVMin), uvScale);

        return new Mesh2(new Vertex[]{
                new Vertex(v0, new Vec2(uv0.x, uv0.y), new Vec3(0, 0, 0), argb),
                new Vertex(v1, new Vec2(uv1.x, uv1.y), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(uv2.x, uv2.y), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(uv3.x, uv3.y), new Vec3(0, 0, 0), argb)
        }, new Face[]{
                new Face(0, 1, 2), new Face(0, 2, 3)
        });
    }

    public static Mesh2 createQuadTiled(Vec3 topLeft, Vec3 bottomLeft, Vec3 bottomRight, Vec3 topRight, int argb, Vec3 worldUVMin, Vec3 worldUVMax, float tileSize)
    {
        Vec3 uvScale = Vec3.sub(worldUVMax, worldUVMin);

        float numTilesX = uvScale.x / tileSize;
        float numTilesY = uvScale.y / tileSize;

        Vec3 v0 = topLeft;       // Top Left
        Vec3 v1 = topRight;      // Top Right
        Vec3 v2 = bottomRight;   // Bottom Right
        Vec3 v3 = bottomLeft;    // Bottom Left

        Vec3 uv0 = Vec3.mult(Vec3.div(Vec3.sub(v0, worldUVMin), uvScale), new Vec3(numTilesX, numTilesY, 1));
        Vec3 uv1 = Vec3.mult(Vec3.div(Vec3.sub(v1, worldUVMin), uvScale), new Vec3(numTilesX, numTilesY, 1));
        Vec3 uv2 = Vec3.mult(Vec3.div(Vec3.sub(v2, worldUVMin), uvScale), new Vec3(numTilesX, numTilesY, 1));
        Vec3 uv3 = Vec3.mult(Vec3.div(Vec3.sub(v3, worldUVMin), uvScale), new Vec3(numTilesX, numTilesY, 1));

        return new Mesh2(new Vertex[]{
                new Vertex(v0, new Vec2(uv0.x, uv0.y), new Vec3(0, 0, 0), argb),
                new Vertex(v1, new Vec2(uv1.x, uv1.y), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(uv2.x, uv2.y), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(uv3.x, uv3.y), new Vec3(0, 0, 0), argb)
        }, new Face[]{
                new Face(0, 1, 2), new Face(0, 2, 3)
        });
    }



    public static Mesh2 createCube(float size, int argb) {
        float halfSize = size / 2;

        Vec3 v0 = new Vec3(-halfSize, halfSize, halfSize);
        Vec3 v1 = new Vec3(-halfSize, -halfSize, halfSize);
        Vec3 v2 = new Vec3(halfSize, -halfSize, halfSize);
        Vec3 v3 = new Vec3(halfSize, halfSize, halfSize);

        Vec3 v4 = new Vec3(halfSize, halfSize, -halfSize);
        Vec3 v5 = new Vec3(halfSize, -halfSize, -halfSize);
        Vec3 v6 = new Vec3(-halfSize, -halfSize, -halfSize);
        Vec3 v7 = new Vec3(-halfSize, halfSize, -halfSize);

        Mesh2 mesh = new Mesh2(new Vertex[]{
                new Vertex(v0, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v1, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(1, 1), new Vec3(0, 0, 0), argb),

                new Vertex(v4, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v5, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v6, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v7, new Vec2(1, 1), new Vec3(0, 0, 0), argb),

                new Vertex(v3, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v5, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v4, new Vec2(1, 1), new Vec3(0, 0, 0), argb),

                new Vertex(v7, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v6, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v1, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v0, new Vec2(1, 1), new Vec3(0, 0, 0), argb),

                new Vertex(v7, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v0, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v3, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v4, new Vec2(1, 1), new Vec3(0, 0, 0), argb),

                new Vertex(v1, new Vec2(0, 1), new Vec3(0, 0, 0), argb),
                new Vertex(v6, new Vec2(0, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v5, new Vec2(1, 0), new Vec3(0, 0, 0), argb),
                new Vertex(v2, new Vec2(1, 1), new Vec3(0, 0, 0), argb)
        }, new Face[]{
                new Face(0, 1, 2), new Face(0, 2, 3),
                new Face(4, 5, 6), new Face(4, 6, 7),
                new Face(8, 9, 10), new Face(8, 10, 11),
                new Face(12, 13, 14), new Face(12, 14, 15),
                new Face(16, 17, 18), new Face(16, 18, 19),
                new Face(20, 21, 22), new Face(20, 22, 23)
        });

        return mesh;
    }


}
