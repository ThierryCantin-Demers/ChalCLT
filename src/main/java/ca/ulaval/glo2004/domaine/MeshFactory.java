package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.chalet.STLChaletExporter.STLTriangle;
import ca.ulaval.glo2004.domaine.chalet.Wall;
import ca.ulaval.glo2004.domaine.chalet.dto.*;
import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.utils.PrimitiveFactory;
import ca.ulaval.glo2004.util.math.Imperial;
import ca.ulaval.glo2004.util.math.Vec;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;

import java.util.*;

import static ca.ulaval.glo2004.util.math.Vec2.add;
import static java.util.Collections.sort;

public class MeshFactory {
    private static final int WALL_COLOR = 0xFFFFFFFF;
    private static final int VALID_ACCESSORY_COLOR = 0xFF00FF00;
    private static final int INVALID_ACCESSORY_COLOR = 0xFFFF0000;

    public static Mesh2 createMesh(WallDTO wallDTO_) {
        float totalToRemove = wallDTO_.thickness.divide(2).plus(wallDTO_.imprecision.divide(2)).getRawInchValueFloat();
        float thicknessWithRemoval = wallDTO_.thickness.getRawInchValueFloat() - totalToRemove;

        int[] normal = wallDTO_.wallOrientation.getNormal();
        float x = wallDTO_.x.getRawInchValueFloat() + (wallDTO_.isOvertaking ? 0 : -normal[2] * totalToRemove);
        float y = wallDTO_.y.getRawInchValueFloat();
        float z = wallDTO_.z.getRawInchValueFloat() + (wallDTO_.isOvertaking ? 0 : normal[0] * totalToRemove);

        float width = wallDTO_.width.getRawInchValueFloat() - (wallDTO_.isOvertaking ? 0 : 2 * totalToRemove);

        Vec3 v0 = new Vec3(x, y, z);
        Vec3 v1 = Vec3.add(v0, new Vec3(-normal[0] * thicknessWithRemoval, 0, -normal[2] * thicknessWithRemoval));
        Vec3 v2 = Vec3.add(v1, new Vec3(-normal[2] * totalToRemove, 0, normal[0] * totalToRemove));
        Vec3 v3 = Vec3.add(v2, new Vec3(-normal[0] * totalToRemove, 0, -normal[2] * totalToRemove));

        Vec3 v7 = Vec3.add(v0, new Vec3(-normal[2] * width, 0, normal[0] * width));
        Vec3 v6 = Vec3.add(v7, new Vec3(-normal[0] * thicknessWithRemoval, 0, -normal[2] * thicknessWithRemoval));
        Vec3 v5 = Vec3.add(v6, new Vec3(normal[2] * totalToRemove, 0, -normal[0] * totalToRemove));
        Vec3 v4 = Vec3.add(v5, new Vec3(-normal[0] * totalToRemove, 0, -normal[2] * totalToRemove));

        Vec3 v01 = Vec3.add(v0, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v11 = Vec3.add(v1, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v21 = Vec3.add(v2, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v31 = Vec3.add(v3, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v41 = Vec3.add(v4, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v51 = Vec3.add(v5, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v61 = Vec3.add(v6, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));
        Vec3 v71 = Vec3.add(v7, new Vec3(0, wallDTO_.height.getRawInchValueFloat(), 0));

        int color = WALL_COLOR;
        switch (wallDTO_.wallOrientation) {
            case BACK:
                color = 0xFFFF4040;
                break;
            case FRONT:
                color = 0xFF40FF40;
                break;
            case RIGHT:
                color = 0xFF4040FF;
                break;
            case LEFT:
                color = 0xFFFFFFFF;
                break;
        }


//        List<Vertex> vertices = new ArrayList<>();
//        List<Face> faces = new ArrayList<>();
//
//        vertices.add(new Vertex(v0,  new Vec2(0,0), new Vec3(0,0,0), color)); // 0
//        vertices.add(new Vertex(v1,  new Vec2(0,0), new Vec3(0,0,0), color)); // 1
//        vertices.add(new Vertex(v2,  new Vec2(0,0), new Vec3(0,0,0), color)); // 2
//        vertices.add(new Vertex(v3,  new Vec2(0,0), new Vec3(0,0,0), color)); // 3
//        vertices.add(new Vertex(v4,  new Vec2(0,0), new Vec3(0,0,0), color)); // 4
//        vertices.add(new Vertex(v5,  new Vec2(0,0), new Vec3(0,0,0), color)); // 5
//        vertices.add(new Vertex(v6,  new Vec2(0,0), new Vec3(0,0,0), color)); // 6
//        vertices.add(new Vertex(v7,  new Vec2(0,0), new Vec3(0,0,0), color)); // 7
//        vertices.add(new Vertex(v01, new Vec2(0,0),  new Vec3(0,0,0), color)); // 8
//        vertices.add(new Vertex(v11, new Vec2(0,0),  new Vec3(0,0,0), color)); // 9
//        vertices.add(new Vertex(v21, new Vec2(0,0),  new Vec3(0,0,0), color)); // 10
//        vertices.add(new Vertex(v31, new Vec2(0,0),  new Vec3(0,0,0), color)); // 11
//        vertices.add(new Vertex(v41, new Vec2(0,0),  new Vec3(0,0,0), color)); // 12
//        vertices.add(new Vertex(v51, new Vec2(0,0),  new Vec3(0,0,0), color)); // 13
//        vertices.add(new Vertex(v61, new Vec2(0,0),  new Vec3(0,0,0), color)); // 14
//        vertices.add(new Vertex(v71, new Vec2(0,0),  new Vec3(0,0,0), color)); // 15

        Mesh2 bottom1 = PrimitiveFactory.createQuad(v6, v7, v0, v1, color);
        Mesh2 bottom2 = PrimitiveFactory.createQuad(v4, v5, v2, v3, color);

        Mesh2 top1 = PrimitiveFactory.createQuad(v11, v01, v71, v61, color); //v01, v61, v71, v11
        Mesh2 top2 = PrimitiveFactory.createQuad(v31, v21, v51, v41, color); //v21, v31, v51, v41

        Mesh2 exterior = PrimitiveFactory.createQuad(v01, v0, v7, v71, color); //v0, v71, v7, v01
        Mesh2 interior = PrimitiveFactory.createQuad(v41, v4, v3, v31, color); //v4, v31, v3, v41

        Mesh2 left = PrimitiveFactory.createQuad(v71, v7, v6, v61, color); //v7, v61, v6, v71
        Mesh2 right = PrimitiveFactory.createQuad(v11, v1, v0, v01, color); //v11, v01, v0, v1

        Mesh2 removeLeft = PrimitiveFactory.createQuad(v61, v6, v5, v51, color);
        Mesh2 removeRight = PrimitiveFactory.createQuad(v21, v2, v1, v11, color);

        Mesh2 removeInteriorLeft = PrimitiveFactory.createQuad(v51, v5, v4, v41, color);
        Mesh2 removeInteriorRight = PrimitiveFactory.createQuad(v31, v3, v2, v21, color);

        Mesh2 finalMesh = bottom1.add(bottom2)
                .add(top1).add(top2)
                .add(exterior).add(interior)
                .add(left).add(right)
                .add(removeLeft).add(removeRight)
                .add(removeInteriorLeft).add(removeInteriorRight);

//        // Bottom
//        faces.add(new Face(0, 7, 6));
//        faces.add(new Face(0, 6, 1));
//        faces.add(new Face(2, 5, 3));
//        faces.add(new Face(3, 5, 4));
//
//// Top
//        faces.add(new Face(8, 14, 15)); // Connecting v01, v11, v71 (using index integers)
//        faces.add(new Face(8, 9, 14));  // Connecting v01, v11, v61
//        faces.add(new Face(10, 11, 13)); // Connecting v21, v31, v51
//        faces.add(new Face(11, 12, 13)); // Connecting v31, v41, v51
//
//// Exterior
//        faces.add(new Face(0, 15, 7)); // Connecting v0, v71, v7
//        faces.add(new Face(0, 8, 15)); // Connecting v0, v01, v71
//
//// Interior
//        faces.add(new Face(4, 11, 3)); // Connecting v4, v31, v3
//        faces.add(new Face(4, 12, 11)); // Connecting v4, v41, v31
//
//// Left
//        faces.add(new Face(7, 14, 6)); // Connecting v7, v61, v6
//        faces.add(new Face(7, 15, 14)); // Connecting v7, v71, v61
//
//// Right
//        faces.add(new Face(1, 8, 0)); // Connecting v1, v01, v0
//        faces.add(new Face(1, 9, 8)); // Connecting v1, v11, v01
//
//// Remove left
//        faces.add(new Face(6, 13, 5)); // Connecting v6, v51, v5
//        faces.add(new Face(6, 14, 13)); // Connecting v6, v61, v51
//
//// Remove right
//        faces.add(new Face(2, 9, 1)); // Connecting v2, v11, v1
//        faces.add(new Face(2, 10, 9)); // Connecting v2, v21, v11
//
//// Remove interior left
//        faces.add(new Face(5, 12, 4)); // Connecting v5, v41, v4
//        faces.add(new Face(5, 13, 12)); // Connecting v5, v51, v41
//
//// Remove interior right
//        faces.add(new Face(3, 10, 2)); // Connecting v3, v21, v2
//        faces.add(new Face(3, 11, 10)); // Connecting v3, v31, v21
//
//
//        // Create new lists to hold expanded vertices and faces
//        List<Vertex> expandedVertices = new ArrayList<>();
//        List<Face> expandedFaces = new ArrayList<>();
//
//        Map<Vec3, Integer> vertexIndices = new HashMap<>();
//
//        // Loop through each face to create expanded vertices and faces
//        for (Face face : faces) {
//            int vertexIndex1 = face.vi1;
//            Vec2 uv = vertices.get(vertexIndex1).uv;
//            Vec3 normal_ = vertices.get(vertexIndex1).normal;
//            int color_ = vertices.get(vertexIndex1).color;
//
//            int[] newVertexIndices = new int[3];
//            int[] asArray = face.asArray();
//            for (int i = 0; i < asArray.length; i++) {
//                int faceIndex = asArray[i];
//                Vec3 pos1 = vertices.get(faceIndex).position;
//
////                if (!vertexIndices.containsKey(pos1)) {
//                    int index = expandedVertices.size();
//                    expandedVertices.add(new Vertex(pos1, uv, normal_, color_));
//                    vertexIndices.put(pos1, index);
////                }
//                newVertexIndices[i] = vertexIndices.get(pos1);
//
//            }
//            // Add expanded face using new vertices
//            expandedFaces.add(new Face(newVertexIndices[0], newVertexIndices[1], newVertexIndices[2]));
//        }

//        Mesh2 mesh = new Mesh2(expandedVertices.toArray(new Vertex[0]), expandedFaces.toArray(new Face[0]));

//        return new Mesh(triangles.toArray(new Triangle[0]), color);

//        Mesh2 mesh = new Mesh2(vertices.toArray(new Vertex[0]), faces.toArray(new Face[0]));
//        finalMesh.material.baseColor = color;
        return finalMesh;
    }

    public static Mesh2 createMesh(AccessoryDTO accessoryDTO_, float thickness) {
        Vec3 v4 = new Vec3(0.0f, 0.0f, -0.05f);
        Vec3 v5 = Vec3.add(v4, new Vec3(accessoryDTO_.width.getRawInchValueFloat(), 0f, 0f));
        Vec3 v7 = Vec3.add(v4, new Vec3(0f, accessoryDTO_.height.getRawInchValueFloat(), 0f));
        Vec3 v6 = Vec3.add(v5, new Vec3(0f, accessoryDTO_.height.getRawInchValueFloat(), 0f));

        Vec3 v0 = Vec3.add(v4, new Vec3(0, 0, -thickness));
        Vec3 v1 = Vec3.add(v0, new Vec3(accessoryDTO_.width.getRawInchValueFloat(), 0f, 0f));
        Vec3 v3 = Vec3.add(v0, new Vec3(0f, accessoryDTO_.height.getRawInchValueFloat(), 0f));
        Vec3 v2 = Vec3.add(v1, new Vec3(0f, accessoryDTO_.height.getRawInchValueFloat(), 0f));

        int color = accessoryDTO_.isValid ? VALID_ACCESSORY_COLOR : INVALID_ACCESSORY_COLOR;

//        // Front
//        triangles.add(new Triangle(v0, v1, v2, color));
//        triangles.add(new Triangle(v0, v2, v3, color));
//
//        // Left
//        triangles.add(new Triangle(v4, v0, v3, color));
//        triangles.add(new Triangle(v4, v3, v7, color));
//
//        // Back
//        triangles.add(new Triangle(v5, v4, v7, color));
//        triangles.add(new Triangle(v5, v7, v6, color));
//
//        // Right
//        triangles.add(new Triangle(v1, v5, v6, color));
//        triangles.add(new Triangle(v1, v6, v2, color));
//
//        // Top
//        triangles.add(new Triangle(v3, v2, v6, color));
//        triangles.add(new Triangle(v3, v6, v7, color));
//
//        // Bottom
//        triangles.add(new Triangle(v0, v4, v5, color));
//        triangles.add(new Triangle(v0, v5, v1, color));
//        return new Mesh(triangles.toArray(new Triangle[0]));
        Mesh2 frontMesh = PrimitiveFactory.createQuad(v0, v1, v2, v3, color);
        Mesh2 backMesh = PrimitiveFactory.createQuad(v6, v5, v4, v7, color);
        Mesh2 leftMesh = PrimitiveFactory.createQuad(v4, v0, v3, v7, color);
        Mesh2 rightMesh = PrimitiveFactory.createQuad(v1, v5, v6, v2, color);
        Mesh2 topMesh = PrimitiveFactory.createQuad(v3, v2, v6, v7, color);
        Mesh2 bottomMesh = PrimitiveFactory.createQuad(v0, v4, v5, v1, color);

        Mesh2 finalMesh = frontMesh.add(backMesh).add(leftMesh).add(rightMesh).add(topMesh).add(bottomMesh);

        finalMesh.material.baseColor = color;
        return finalMesh;
    }

    public static Mesh2 createCutoutMesh(WallDTO wall, Imperial imprecision) {
//        // if wall has no accessories, create regular, uncut wall mesh
//        if (wall.accessories.size() == 0) {
//            return createMesh(wall);
//        }

        //https://prnt.sc/lXmAE1cLVQoY
        Vec3 v00 = new Vec3(0, 0, 0);
        Vec3 v10 = Vec3.add(v00, new Vec3(wall.thickness.divide(2).plus(imprecision.divide(2)).getRawInchValueFloat(), 0, 0));
        Vec3 v20 = Vec3.add(v00, new Vec3(0, 0, wall.thickness.divide(2).minus(imprecision.divide(2)).getRawInchValueFloat()));
        Vec3 v30 = Vec3.add(v20, new Vec3(wall.thickness.divide(2).plus(imprecision.divide(2)).getRawInchValueFloat(), 0, 0));
        Vec3 v40 = Vec3.add(v30, new Vec3(0,0,wall.thickness.divide(2).plus(imprecision.divide(2)).getRawInchValueFloat()));
        Vec3 v50 = Vec3.add(v40, new Vec3(wall.realInteriorWidth().getRawInchValueFloat(),0,0));
        Vec3 v60 = Vec3.add(v30, new Vec3(wall.realInteriorWidth().getRawInchValueFloat(),0,0));
        Vec3 v70 = Vec3.add(v60, new Vec3(wall.thickness.divide(2).plus(imprecision.divide(2)).getRawInchValueFloat(),0,0));
        Vec3 v80 = Vec3.add(v10, new Vec3(wall.realInteriorWidth().getRawInchValueFloat(),0,0));
        Vec3 v90 = Vec3.add(v80, new Vec3(wall.thickness.divide(2).plus(imprecision.divide(2)).getRawInchValueFloat(),0,0));


        Vec3 v01 = Vec3.add(v00, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v11 = Vec3.add(v10, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v21 = Vec3.add(v20, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v31 = Vec3.add(v30, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v41 = Vec3.add(v40, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v51 = Vec3.add(v50, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v61 = Vec3.add(v60, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v71 = Vec3.add(v70, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v81 = Vec3.add(v80, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v91 = Vec3.add(v90, new Vec3(0, wall.height.getRawInchValueFloat(), 0));


        Vec3 minUV = v00;
        Vec3 maxUV = v91;

        Vec3 minUVSide = v10;
        Vec3 maxUVSide = v41;

        Vec3 minUVTop = v00;
        Vec3 maxUVTop = v70;

        Mesh2 outputMesh = new Mesh2();
        outputMesh.autoCalculateNormals = false;
        float totalToRemove = wall.thickness.divide(2).plus(wall.imprecision.divide(2)).getRawInchValueFloat();
        float overtakingOffset = -(wall.isOvertaking ? 0 : totalToRemove);

        //inside the chalet
        Vec2 v0 = new Vec2(wall.thickness.divide(2).plus(wall.imprecision.divide(2)).getRawInchValueFloat(), 0);
        Vec2 v1 = new Vec2(wall.thickness.divide(2).plus(wall.imprecision.divide(2)).plus(wall.realInteriorWidth()).getRawInchValueFloat(), 0);
        Vec2 v2 = Vec2.add(v1, new Vec2(0, wall.height.getRawInchValueFloat()));
//        Vec2 v3 = add(v0, new Vec2(0, wall.height.getRawInchValueFloat()));

        List<Float> importantPointsX = new ArrayList<>();
        List<Float> importantPointsY = new ArrayList<>();
        //adding four corners of the face in there
        importantPointsX.add(v0.x);
        importantPointsY.add(v0.y);
        importantPointsX.add(v2.x);
        importantPointsY.add(v2.y);

        HashMap<UUID, ArrayList<ArrayList<Float>>> rectCollisionMap = new HashMap<>();

        List<AccessoryDTO> validAccessories = new ArrayList<>();
        for (AccessoryDTO acc : wall.accessories) {
            if (!acc.isValid)
                continue;

            float a0x = acc.x.getRawInchValueFloat() + overtakingOffset;
            if (!importantPointsX.contains(a0x)) {
                importantPointsX.add(a0x);
            }
            float a0y = acc.y.getRawInchValueFloat();
            if (!importantPointsY.contains(a0y)) {
                importantPointsY.add(a0y);
            }
            float a1x = acc.x.plus(acc.width).getRawInchValueFloat() + overtakingOffset;
            if (!importantPointsX.contains(a1x)) {
                importantPointsX.add(a1x);
            }
            float a1y = acc.y.plus(acc.height).getRawInchValueFloat();
            if (!importantPointsY.contains(a1y)) {
                importantPointsY.add(a1y);
            }

            validAccessories.add(acc);
        }
        sort(importantPointsX);
        sort(importantPointsY);
        for (int y = 0; y < importantPointsY.size() - 1; y++) {
            for (int x = 0; x < importantPointsX.size() - 1; x++) {
                boolean isInsideHole = false;
                float x0 = importantPointsX.get(x);
                float x1 = importantPointsX.get(x + 1);
                float y0 = importantPointsY.get(y);
                float y1 = importantPointsY.get(y + 1);
                for (AccessoryDTO acc : validAccessories) {

                    float a0x = acc.x.getRawInchValueFloat() + overtakingOffset;
                    float a0y = acc.y.getRawInchValueFloat();
                    float a1x = acc.x.plus(acc.width).getRawInchValueFloat() + overtakingOffset;
                    float a1y = acc.y.plus(acc.height).getRawInchValueFloat();

                    Vec2[] collisionRect = isColliding_(x0, x1, y0, y1, a0x, a1x, a0y, a1y);
                    if (collisionRect != null) {
                        isInsideHole = true;
                        ArrayList<ArrayList<Float>> rectArray = rectCollisionMap.getOrDefault(acc.uuid, new ArrayList<>(List.of(new ArrayList<>(), new ArrayList<>())));
                        for (Vec2 vec2 : collisionRect) {
                            rectArray.get(0).add(vec2.x);
                            rectArray.get(1).add(vec2.y);
                        }
                        rectCollisionMap.put(acc.uuid, rectArray);
                        break;
                    }
                }
                if (!isInsideHole) {
                    Vec3 bottom_left_front = new Vec3(x0, y0, 0);
                    Vec3 bottom_left_inside = new Vec3(x0, y0, wall.thickness.getRawInchValueFloat());
                    Vec3 bottom_right_front = new Vec3(x1, y0, 0);
                    Vec3 bottom_right_inside = new Vec3(x1, y0, wall.thickness.getRawInchValueFloat());
                    Vec3 top_left_front = new Vec3(x0, y1, 0);
                    Vec3 top_left_inside = new Vec3(x0, y1, wall.thickness.getRawInchValueFloat());
                    Vec3 top_right_front = new Vec3(x1, y1, 0);
                    Vec3 top_right_inside = new Vec3(x1, y1, wall.thickness.getRawInchValueFloat());

                    // it is possible that winding order is not correct
                    Mesh2 front = PrimitiveFactory.createQuadTiled(top_left_front, bottom_left_front, bottom_right_front, top_right_front, 0, minUV, maxUV, 20);
                    Mesh2 inside = PrimitiveFactory.createQuadTiled(top_right_inside, bottom_right_inside, bottom_left_inside, top_left_inside, 0, minUV, maxUV, 20);
//                    Mesh2 front = PrimitiveFactory.createQuad(top_left_front, bottom_left_front, bottom_right_front, top_right_front, 0);
//                    Mesh2 inside = PrimitiveFactory.createQuad(top_right_inside, bottom_right_inside, bottom_left_inside, top_left_inside, 0);
                    // construct the rest of the mesh using quads

                    // bro think he in javascript!!!!
                    outputMesh
                            .add(front)
                            .add(inside);
                }
                //check if is inside a hole
            }

        }


////        outputMesh.add(PrimitiveFactory.createQuad(v30,v40,v50,v60,0)); // bottom 1
////        outputMesh.add(PrimitiveFactory.createQuad(v10,v30,v60,v80,0)); // bottom 2
//
//        outputMesh.add(PrimitiveFactory.createQuad(v41,v31,v61,v51,0));
//        outputMesh.add(PrimitiveFactory.createQuad(v31,v11,v81,v61,0));

        outputMesh.add(PrimitiveFactory.createQuad(v21,v01,v11,v31,0)); // small square top
        outputMesh.add(PrimitiveFactory.createQuad(v61,v81,v91,v71,0)); // small square top
        outputMesh.add(PrimitiveFactory.createQuad(v00,v20,v30,v10,0)); // small square bottom
        outputMesh.add(PrimitiveFactory.createQuad(v80,v60,v70,v90,0)); // small square bottom
//        outputMesh.add(PrimitiveFactory.createQuad(v21,v20,v00,v01,0)); // outer edges
//        outputMesh.add(PrimitiveFactory.createQuad(v31,v30,v20,v21,0)); // inner edges s
//        outputMesh.add(PrimitiveFactory.createQuad(v41,v40,v30,v31,0)); // inner edges 1
//        outputMesh.add(PrimitiveFactory.createQuad(v01,v00,v10,v11,0)); // outer edges
//        outputMesh.add(PrimitiveFactory.createQuad(v61,v60,v50,v51,0)); // inner edges 1
//        outputMesh.add(PrimitiveFactory.createQuad(v71,v70,v60,v61,0)); // back edges
//        outputMesh.add(PrimitiveFactory.createQuad(v91,v90,v70,v71,0)); // inner edges 2
//        outputMesh.add(PrimitiveFactory.createQuad(v81,v80,v90,v91,0)); // outer edges


        List<Float> wallXValues = new ArrayList<>();
        List<Float> wallYValues = new ArrayList<>();

        wallXValues.add(v10.x);
        wallYValues.add(v00.y);

        //special case, do the bottom part of the mesh considering doors exist
        List<Vec2> doorXValues = new ArrayList<>();
        doorXValues.add(new Vec2(0,wall.thickness.divide(2).getRawInchValueFloat()));
        for (AccessoryDTO acc : validAccessories) {

//            Vec3 fbl = new Vec3(acc.x.getRawInchValueFloat() + overtakingOffset,acc.y.getRawInchValueFloat(),0);
//            Vec3 ftl = Vec3.add(fbl, new Vec3(0,acc.height.getRawInchValueFloat(),0));
//            Vec3 ftr = Vec3.add(ftl, new Vec3(acc.width.getRawInchValueFloat(),0,0));
//            Vec3 fbr = Vec3.add(fbl, new Vec3(acc.width.getRawInchValueFloat(),0,0));
//
//            Vec3 ibl = Vec3.add(fbl,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//            Vec3 itl = Vec3.add(ftl,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//            Vec3 itr = Vec3.add(ftr,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//            Vec3 ibr = Vec3.add(fbr,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));

            List<Float> accXValues = new ArrayList<>();
            List<Float> accYValues = new ArrayList<>();

            float accX1 = acc.x.getRawInchValueFloat() + overtakingOffset;
            float accX2 = accX1 + acc.width.getRawInchValueFloat();
            float accY1 = acc.y.getRawInchValueFloat();
            float accY2 = accY1 + acc.height.getRawInchValueFloat();

            accXValues.add(accX1);
            accYValues.add(accY1);


            var rectCollisions = rectCollisionMap.get(acc.uuid);
            if (rectCollisions != null)
            {
                ArrayList<Float> collisionsX = rectCollisions.get(0);
                ArrayList<Float> collisionsY = rectCollisions.get(1);
                Collections.sort(collisionsX);
                Collections.sort(collisionsY);
                accXValues.addAll(collisionsX);
                accYValues.addAll(collisionsY);
            }
            accXValues.add(accX2);
            accYValues.add(accY2);

            wallXValues.addAll(accXValues);
            wallYValues.addAll(accYValues);


            for (int i = 0; i < accXValues.size() - 1; i++) {
                float currX = accXValues.get(i);
                float nextX = accXValues.get(i + 1);

                Vec3 bot_bl = new Vec3(currX, acc.y.getRawInchValueFloat(), 0);
                Vec3 bot_tl = Vec3.add(bot_bl, new Vec3(0, 0, wall.thickness.getRawInchValueFloat()));
                Vec3 bot_br = new Vec3(nextX, acc.y.getRawInchValueFloat(), 0);
                Vec3 bot_tr = Vec3.add(bot_br, new Vec3(0, 0, wall.thickness.getRawInchValueFloat()));
                Vec3 top_bl = Vec3.add(bot_bl, new Vec3(0, acc.height.getRawInchValueFloat(), 0));
                Vec3 top_tl = Vec3.add(bot_tl, new Vec3(0, acc.height.getRawInchValueFloat(), 0));
                Vec3 top_br = Vec3.add(bot_br, new Vec3(0, acc.height.getRawInchValueFloat(), 0));
                Vec3 top_tr = Vec3.add(bot_tr, new Vec3(0, acc.height.getRawInchValueFloat(), 0));

                outputMesh.add(PrimitiveFactory.createQuad(bot_tl, bot_bl, bot_br, bot_tr, 0));
                outputMesh.add(PrimitiveFactory.createQuad(top_bl, top_tl,  top_tr, top_br, 0));
            }

            for (int i = 0; i < accYValues.size() - 1; i++){
                float currY = accYValues.get(i);
                float nextY = accYValues.get(i + 1);

                Vec3 bot_bl = new Vec3(acc.x.getRawInchValueFloat() + overtakingOffset, currY, 0);
                Vec3 bot_tl = Vec3.add(bot_bl, new Vec3(0, 0, wall.thickness.getRawInchValueFloat()));
                Vec3 bot_br = new Vec3(acc.x.getRawInchValueFloat() + overtakingOffset, nextY, 0);
                Vec3 bot_tr = Vec3.add(bot_br, new Vec3(0, 0, wall.thickness.getRawInchValueFloat()));
                Vec3 top_bl = Vec3.add(bot_bl, new Vec3(acc.width.getRawInchValueFloat(), 0, 0));
                Vec3 top_tl = Vec3.add(bot_tl, new Vec3(acc.width.getRawInchValueFloat(), 0, 0));
                Vec3 top_br = Vec3.add(bot_br, new Vec3(acc.width.getRawInchValueFloat(), 0, 0));
                Vec3 top_tr = Vec3.add(bot_tr, new Vec3(acc.width.getRawInchValueFloat(), 0, 0));

                outputMesh.add(PrimitiveFactory.createQuad(bot_bl, bot_tl, bot_tr, bot_br, 0));
                outputMesh.add(PrimitiveFactory.createQuad(top_tl, top_bl, top_br, top_tr, 0));
            }

            doorXValues.add(new Vec2(acc.x.getRawInchValueFloat() + overtakingOffset, acc.x.plus(acc.width).getRawInchValueFloat() + overtakingOffset));
        }
        doorXValues.add(new Vec2(wall.realWidth().minus(wall.thickness.divide(2)).getRawInchValueFloat(),0));
        doorXValues.sort(new Comparator<Vec2>() {
            @Override
            public int compare(Vec2 o1, Vec2 o2) {
                return Float.compare(o1.x, o2.x);
            }
        });


        Collections.sort(wallXValues);
        Collections.sort(wallYValues);

        wallXValues.add(v80.x);
        wallYValues.add(v01.y);

        for (int i = 0; i < wallXValues.size() - 1; i++) {
            float currX = wallXValues.get(i);
            float nextX = wallXValues.get(i + 1);

            Vec3 bot_bl1 = new Vec3(currX,v10.y,v10.z);
            Vec3 bot_tl1 = new Vec3(currX,v30.y,v30.z);
            Vec3 bot_br1 = new Vec3(nextX,v80.y,v80.z);
            Vec3 bot_tr1 = new Vec3(nextX,v60.y,v60.z);

            Vec3 top_bl1 = Vec3.add(bot_bl1, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
            Vec3 top_tl1 = Vec3.add(bot_tl1, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
            Vec3 top_br1 = Vec3.add(bot_br1, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
            Vec3 top_tr1 = Vec3.add(bot_tr1, new Vec3(0, wall.height.getRawInchValueFloat(), 0));

            outputMesh.add(PrimitiveFactory.createQuad(bot_bl1, bot_tl1, bot_tr1, bot_br1, 0));
            outputMesh.add(PrimitiveFactory.createQuad(top_tl1, top_bl1, top_br1, top_tr1, 0));

            Vec3 bot_bl2 = new Vec3(currX,v30.y,v30.z);
            Vec3 bot_tl2 = new Vec3(currX,v40.y,v40.z);
            Vec3 bot_br2 = new Vec3(nextX,v60.y,v60.z);
            Vec3 bot_tr2 = new Vec3(nextX,v50.y,v50.z);

            Vec3 top_bl2 = Vec3.add(bot_bl2, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
            Vec3 top_tl2 = Vec3.add(bot_tl2, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
            Vec3 top_br2 = Vec3.add(bot_br2, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
            Vec3 top_tr2 = Vec3.add(bot_tr2, new Vec3(0, wall.height.getRawInchValueFloat(), 0));

            outputMesh.add(PrimitiveFactory.createQuad(bot_bl2, bot_tl2, bot_tr2, bot_br2, 0));
            outputMesh.add(PrimitiveFactory.createQuad(top_tl2, top_bl2, top_br2, top_tr2, 0));
        }

        for (int i = 0; i < wallYValues.size() - 1; i++) {
            float currY = wallYValues.get(i);
            float nextY = wallYValues.get(i + 1);

            Vec3 v00_ = new Vec3(v00.x, currY, v00.z);
            Vec3 v01_ = new Vec3(v01.x, nextY, v01.z);
            Vec3 v10_ = new Vec3(v10.x, currY, v10.z);
            Vec3 v11_ = new Vec3(v11.x, nextY, v11.z);
            Vec3 v20_ = new Vec3(v20.x, currY, v20.z);
            Vec3 v21_ = new Vec3(v21.x, nextY, v21.z);
            Vec3 v30_ = new Vec3(v30.x, currY, v30.z);
            Vec3 v31_ = new Vec3(v31.x, nextY, v31.z);
            Vec3 v40_ = new Vec3(v40.x, currY, v40.z);
            Vec3 v41_ = new Vec3(v41.x, nextY, v41.z);
            Vec3 v50_ = new Vec3(v50.x, currY, v50.z);
            Vec3 v51_ = new Vec3(v51.x, nextY, v51.z);
            Vec3 v60_ = new Vec3(v60.x, currY, v60.z);
            Vec3 v61_ = new Vec3(v61.x, nextY, v61.z);
            Vec3 v70_ = new Vec3(v70.x, currY, v70.z);
            Vec3 v71_ = new Vec3(v71.x, nextY, v71.z);
            Vec3 v80_ = new Vec3(v80.x, currY, v80.z);
            Vec3 v81_ = new Vec3(v81.x, nextY, v81.z);
            Vec3 v90_ = new Vec3(v90.x, currY, v90.z);
            Vec3 v91_ = new Vec3(v91.x, nextY, v91.z);

            outputMesh.add(PrimitiveFactory.createQuad(v21_,v20_,v00_,v01_,0)); // outer edges
            outputMesh.add(PrimitiveFactory.createQuad(v31_,v30_,v20_,v21_,0)); // inner edges s
            outputMesh.add(PrimitiveFactory.createQuad(v41_,v40_,v30_,v31_,0)); // inner edges 1
            outputMesh.add(PrimitiveFactory.createQuad(v01_,v00_,v10_,v11_,0)); // outer edges
            outputMesh.add(PrimitiveFactory.createQuad(v61_,v60_,v50_,v51_,0)); // inner edges 1
            outputMesh.add(PrimitiveFactory.createQuad(v71_,v70_,v60_,v61_,0)); // back edges
            outputMesh.add(PrimitiveFactory.createQuad(v91_,v90_,v70_,v71_,0)); // inner edges 2
            outputMesh.add(PrimitiveFactory.createQuad(v81_,v80_,v90_,v91_,0)); // outer edges

        }

        for(int i = 0 ; i < doorXValues.size() - 1 ; i++)
        {
            float curr = doorXValues.get(i).y;
            float next = doorXValues.get(i+1).x;
            Vec3 bot_bl = new Vec3(curr,0,0);
            Vec3 bot_tl = Vec3.add(bot_bl,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
            Vec3 bot_br = new Vec3(next, 0,0);
            Vec3 bot_tr = Vec3.add(bot_br,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));

//            outputMesh.add(PrimitiveFactory.createQuad(bot_bl,bot_tl,bot_tr,bot_br,0));
        }

        for(AccessoryDTO acc : validAccessories)
        {
//            Vec3 fbl = new Vec3(acc.x.getRawInchValueFloat() + overtakingOffset,acc.y.getRawInchValueFloat(),0);
//            Vec3 ftl = Vec3.add(fbl, new Vec3(0,acc.height.getRawInchValueFloat(),0));
//            Vec3 ftr = Vec3.add(ftl, new Vec3(acc.width.getRawInchValueFloat(),0,0));
//            Vec3 fbr = Vec3.add(fbl, new Vec3(acc.width.getRawInchValueFloat(),0,0));
//
//            Vec3 ibl = Vec3.add(fbl,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//            Vec3 itl = Vec3.add(ftl,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//            Vec3 itr = Vec3.add(ftr,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//            Vec3 ibr = Vec3.add(fbr,new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
//
//            if(acc.y.getRawInchValueFloat() != 0)
//            {
//                outputMesh.add(PrimitiveFactory.createQuad(ibr,ibl,fbl,fbr,0));
//            }
//            outputMesh.add(PrimitiveFactory.createQuad(ibl,itl,ftl,fbl,0));
//            outputMesh.add(PrimitiveFactory.createQuad(itl,itr,ftr,ftl,0));
//            outputMesh.add(PrimitiveFactory.createQuad(itr,ibr,fbr,ftr,0));
        }
        outputMesh.calculateVertexNormals();
        return outputMesh;
    }

    public static Mesh2 createPanelMesh(WallDTO wall)
    {

       Mesh2 outputMesh = new Mesh2();
        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(wall.width.getRawInchValueFloat(),0,0));
        Vec3 v2 = Vec3.add(v0, new Vec3(0, wall.height.getRawInchValueFloat(), 0));
        Vec3 v3 = Vec3.add(v2, new Vec3(wall.width.getRawInchValueFloat(),0,0));

        Vec3 v00 = Vec3.add(v0, new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
        Vec3 v11 = Vec3.add(v1, new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
        Vec3 v22 = Vec3.add(v2, new Vec3(0,0,wall.thickness.getRawInchValueFloat()));
        Vec3 v33 = Vec3.add(v3, new Vec3(0,0,wall.thickness.getRawInchValueFloat()));

        outputMesh.add(PrimitiveFactory.createQuad(v2,v0,v1,v3,0));
        outputMesh.add(PrimitiveFactory.createQuad(v22,v00,v0,v2,0));
        outputMesh.add(PrimitiveFactory.createQuad(v3,v1,v11,v33,0));
        outputMesh.add(PrimitiveFactory.createQuad(v33,v11,v00,v22,0));
        outputMesh.add(PrimitiveFactory.createQuad(v22,v2,v3,v33,0));
        outputMesh.add(PrimitiveFactory.createQuad(v00,v0,v1,v11,0));
        return outputMesh;
    }

    public static Mesh2 createRetraitGauche(WallDTO wall)
    {
        Mesh2 outputMush = new Mesh2();
        if(wall.isOvertaking)
        {
            float rainureWidth = wall.thickness.divide(2).plus(wall.imprecision.divide(2)).getRawInchValueFloat();

            Vec3 v0 = new Vec3(0,0,0);
            Vec3 v1 = Vec3.add(v0, new Vec3(rainureWidth,0,0));
            Vec3 v2 = Vec3.add(v0, new Vec3(0,0,rainureWidth));
            Vec3 v3 = Vec3.add(v0, new Vec3(rainureWidth,0,rainureWidth));

            Vec3 v00 = Vec3.add(v0,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v11 = Vec3.add(v1,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v22 = Vec3.add(v2,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v33 = Vec3.add(v3,new Vec3(0, wall.height.getRawInchValueFloat(),0));

            outputMush.add(PrimitiveFactory.createQuad(v00,v0,v1,v11,0));
            outputMush.add(PrimitiveFactory.createQuad(v22,v2,v0,v00,0));
            outputMush.add(PrimitiveFactory.createQuad(v33,v3,v2,v22,0));
            outputMush.add(PrimitiveFactory.createQuad(v11,v1,v3,v33,0));
            outputMush.add(PrimitiveFactory.createQuad(v22,v00,v11,v33,0));
            outputMush.add(PrimitiveFactory.createQuad(v0,v2,v3,v1,0));
        }
        //https://prnt.sc/aO3W4BzlJuoK
        else
        {
            Imperial rainureWidth = wall.thickness.divide(2).plus(wall.imprecision.divide(2));

            Vec3 v0 = new Vec3(0,0,0);
            Vec3 v1 = Vec3.add(v0, new Vec3(wall.thickness.minus(rainureWidth).getRawInchValueFloat(),0,0));
            Vec3 v2 = Vec3.add(v1, new Vec3(0, 0,wall.thickness.minus(rainureWidth).getRawInchValueFloat()));
            Vec3 v3 = Vec3.add(v2, new Vec3(rainureWidth.getRawInchValueFloat(),0,0));
            Vec3 v4 = Vec3.add(v3, new Vec3(0, 0,rainureWidth.getRawInchValueFloat()));
            Vec3 v5 = Vec3.add(v0, new Vec3(0, 0,wall.thickness.getRawInchValueFloat()));
            Vec3 v6 = Vec3.add(v0, new Vec3(0, 0,wall.thickness.minus(rainureWidth).getRawInchValueFloat()));

            Vec3 v00 = Vec3.add(v0,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v11 = Vec3.add(v1,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v22 = Vec3.add(v2,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v33 = Vec3.add(v3,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v44 = Vec3.add(v4,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v55 = Vec3.add(v5,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v66 = Vec3.add(v6,new Vec3(0, wall.height.getRawInchValueFloat(),0));

            outputMush.add(PrimitiveFactory.createQuad(v66,v00,v11,v22,0));
            outputMush.add(PrimitiveFactory.createQuad(v55,v66,v33,v44,0));
            outputMush.add(PrimitiveFactory.createQuad(v2,v1,v0,v6,0));
            outputMush.add(PrimitiveFactory.createQuad(v4,v3,v6,v5,0));
            outputMush.add(PrimitiveFactory.createQuad(v00,v0,v1,v11,0));
            outputMush.add(PrimitiveFactory.createQuad(v55,v5,v0,v00,0));
            outputMush.add(PrimitiveFactory.createQuad(v44,v4,v5,v55,0));
            outputMush.add(PrimitiveFactory.createQuad(v33,v3,v4,v44,0));
            outputMush.add(PrimitiveFactory.createQuad(v22,v2,v3,v33,0));
            outputMush.add(PrimitiveFactory.createQuad(v11,v1,v2,v22,0));

        }
        return outputMush;
    }

    public static Mesh2 createRetraitDroit(WallDTO wall)
    {
        Mesh2 outputMush = new Mesh2();
        if(wall.isOvertaking)
        {
            float rainureWidth = wall.thickness.divide(2).plus(wall.imprecision.divide(2)).getRawInchValueFloat();

            Vec3 v0 = new Vec3(0,0,0);
            Vec3 v1 = Vec3.add(v0, new Vec3(rainureWidth,0,0));
            Vec3 v2 = Vec3.add(v0, new Vec3(0,0,rainureWidth));
            Vec3 v3 = Vec3.add(v0, new Vec3(rainureWidth,0,rainureWidth));

            Vec3 v00 = Vec3.add(v0,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v11 = Vec3.add(v1,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v22 = Vec3.add(v2,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v33 = Vec3.add(v3,new Vec3(0, wall.height.getRawInchValueFloat(),0));

            outputMush.add(PrimitiveFactory.createQuad(v00,v0,v1,v11,0));
            outputMush.add(PrimitiveFactory.createQuad(v22,v2,v0,v00,0));
            outputMush.add(PrimitiveFactory.createQuad(v33,v3,v2,v22,0));
            outputMush.add(PrimitiveFactory.createQuad(v11,v1,v3,v33,0));
            outputMush.add(PrimitiveFactory.createQuad(v22,v00,v11,v33,0));
            outputMush.add(PrimitiveFactory.createQuad(v0,v2,v3,v1,0));
        }
        else
        {

            //  v5              v4
            //  v0      v1      v35
            //  (0,0)   v2      v3
            Imperial rainureWidth = wall.thickness.divide(2).plus(wall.imprecision.divide(2));
            Vec3 v0 = new Vec3(0,0,wall.thickness.minus(rainureWidth).getRawInchValueFloat());
            Vec3 v1 = Vec3.add(v0, new Vec3(rainureWidth.getRawInchValueFloat(),0,0));
            Vec3 v2 = new Vec3(rainureWidth.getRawInchValueFloat(), 0,0);
            Vec3 v3 =  new Vec3(wall.thickness.getRawInchValueFloat(), 0,0);
            Vec3 v35 = new Vec3(wall.thickness.getRawInchValueFloat(),0,wall.thickness.minus(rainureWidth).getRawInchValueFloat());
            Vec3 v4 = new Vec3(wall.thickness.getRawInchValueFloat(), 0,wall.thickness.getRawInchValueFloat());
            Vec3 v5 = new Vec3(0, 0,wall.thickness.getRawInchValueFloat());

            Vec3 v00 = Vec3.add(v0,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v11 = Vec3.add(v1,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v22 = Vec3.add(v2,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v33 = Vec3.add(v3,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v3535 = Vec3.add(v35,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v44 = Vec3.add(v4,new Vec3(0, wall.height.getRawInchValueFloat(),0));
            Vec3 v55 = Vec3.add(v5,new Vec3(0, wall.height.getRawInchValueFloat(),0));

            outputMush.add(PrimitiveFactory.createQuad(v11,v22,v33,v3535,0));
            outputMush.add(PrimitiveFactory.createQuad(v55,v00,v3535,v44,0));
            outputMush.add(PrimitiveFactory.createQuad(v35,v3,v2,v1,0));
            outputMush.add(PrimitiveFactory.createQuad(v4,v35,v0,v5,0));
            outputMush.add(PrimitiveFactory.createQuad(v22,v2,v3,v33,0));
            outputMush.add(PrimitiveFactory.createQuad(v11,v1,v2,v22,0));
            outputMush.add(PrimitiveFactory.createQuad(v00,v0,v1,v11,0));
            outputMush.add(PrimitiveFactory.createQuad(v55,v5,v0,v00,0));
            outputMush.add(PrimitiveFactory.createQuad(v44,v4,v5,v55,0));
            outputMush.add(PrimitiveFactory.createQuad(v33,v3,v4,v44,0));
        }
        return outputMush;
    }
    private static boolean isColliding(float x0, float x1, float y0, float y1, float a0x, float a1x, float a0y, float a1y) {
        if (x1 <= a0x || a1x <= x0) {
            return false;
        }

        // Check for no overlap in y-axis
        if (y1 <= a0y || a1y <= y0) {
            return false;
        }

        return true;
    }

    private static Vec2[] isColliding_(float x0, float x1, float y0, float y1, float a0x, float a1x, float a0y, float a1y) {
        if (x1 <= a0x || a1x <= x0 || y1 <= a0y || a1y <= y0) {
            return null;
        }

        float intersectionX0 = Math.max(x0, a0x);
        float intersectionX1 = Math.min(x1, a1x);
        float intersectionY0 = Math.max(y0, a0y);
        float intersectionY1 = Math.min(y1, a1y);

        Vec2[] intersectionPoints = {
                new Vec2(intersectionX0, intersectionY0),
                new Vec2(intersectionX1, intersectionY1)
        };

        return intersectionPoints;
    }

    public static Mesh2 createLeftGableMesh(WallDTO leftGableWall, GableDTO leftGable)
    {
        Mesh2 outputMesh = new Mesh2();
        double angle = (double)leftGable.getAngle();
        float slope = (float) Math.tan(Math.toRadians(angle));
        float exteriorWidth = leftGableWall.width.minus(leftGableWall.thickness.plus(leftGableWall.imprecision).divide(2)).getRawInchValueFloat();
        float interiorWidth = leftGableWall.realInteriorWidth().getRawInchValueFloat();
        float exteriorHeight = exteriorWidth * slope;
        float interiorHeight = interiorWidth * slope;
        float exteriorThickness = leftGableWall.thickness.minus(leftGableWall.imprecision).divide(2).getRawInchValueFloat();
        float interiorThickess = leftGableWall.thickness.plus(leftGableWall.imprecision).divide(2).getRawInchValueFloat();

        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(exteriorWidth,0,0));
        Vec3 v2 = Vec3.add(v0, new Vec3(0,exteriorHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,0,exteriorThickness));
        Vec3 v4 = Vec3.add(v1, new Vec3(0,0,exteriorThickness));
        Vec3 v5 = Vec3.add(v2, new Vec3(0,0,exteriorThickness));

        Vec3 v6 = Vec3.add(v3,new Vec3(interiorThickess,0,0));
        Vec3 v7 = Vec3.add(v6, new Vec3(interiorWidth, 0,0));
        Vec3 v8 = Vec3.add(v6, new Vec3(0, interiorHeight,0));
        Vec3 v9 = Vec3.add(v6, new Vec3(0,0,interiorThickess));
        Vec3 v10 = Vec3.add(v7, new Vec3(0,0,interiorThickess));
        Vec3 v11 = Vec3.add(v8, new Vec3(0,0,interiorThickess));

        Mesh2 frontFace = PrimitiveFactory.createLeftTriangle(v2,v0,v1,0);
        Mesh2 exteriorSideFace = PrimitiveFactory.createQuad(v5,v3,v0,v2,0);
        Mesh2 exteriorInteriorLeftFace = PrimitiveFactory.createQuad(v8,v6,v3,v5,0);
        Mesh2 interiorSideFace = PrimitiveFactory.createQuad(v11,v9,v6,v8,0);
        Mesh2 backFace = PrimitiveFactory.createRightTriangle(v10,v9,v11,0);
        Mesh2 exteriorTopFace = PrimitiveFactory.createQuad(v2,v1,v4,v5,0);
        Mesh2 interiorTopFace = PrimitiveFactory.createQuad(v8,v7,v10,v11,0);

        //this might be wrong, so might have to fix this
        float heightGap = slope*interiorThickess;
        Vec3 gappedPoint = Vec3.add(v8, new Vec3(0,heightGap,0));
        Mesh2 gapFaceLeftPart = PrimitiveFactory.createQuad(v5,v4,v7,v8,0);
        Mesh2 gapFaceRightPart = PrimitiveFactory.createQuad(v8,v6,v3,v4,0);
        Mesh2 bottomExteriorPart = PrimitiveFactory.createQuad(v1,v0,v3,v4,0);
        Mesh2 bottomInteriorPart = PrimitiveFactory.createQuad(v7,v6,v9,v10,0);

        outputMesh.add(frontFace);
        outputMesh.add(exteriorSideFace);
        outputMesh.add(exteriorInteriorLeftFace);
        outputMesh.add(interiorSideFace);
        outputMesh.add(backFace);
        outputMesh.add(exteriorTopFace);
        outputMesh.add(interiorTopFace);
        outputMesh.add(gapFaceLeftPart);
        outputMesh.add(gapFaceRightPart);
        outputMesh.add(bottomExteriorPart);
        outputMesh.add(bottomInteriorPart);
        return outputMesh;
    }

    public static Mesh2 createRightGableMesh(WallDTO leftGableWall, GableDTO leftGable)
    {
        Mesh2 outputMesh = new Mesh2();
        double angle = (double)leftGable.getAngle();
        float slope = (float) Math.tan(Math.toRadians(angle));
        float exteriorWidth = leftGableWall.width.minus(leftGableWall.thickness.plus(leftGableWall.imprecision).divide(2)).getRawInchValueFloat();
        float interiorWidth = leftGableWall.realInteriorWidth().getRawInchValueFloat();
        float exteriorHeight = exteriorWidth * slope;
        float interiorHeight = interiorWidth * slope;
        float exteriorThickness = leftGableWall.thickness.minus(leftGableWall.imprecision).divide(2).getRawInchValueFloat();
        float interiorThickness = leftGableWall.thickness.plus(leftGableWall.imprecision).divide(2).getRawInchValueFloat();

        Vec3 v0 = new Vec3(interiorThickness,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(interiorWidth,0,0));
        Vec3 v2 = Vec3.add(v0, new Vec3(0,interiorHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,0,interiorThickness));
        Vec3 v4 = Vec3.add(v1, new Vec3(0,0,interiorThickness));
        Vec3 v5 = Vec3.add(v2, new Vec3(0,0,interiorThickness));

        Vec3 v6 = Vec3.add(v3,new Vec3(-interiorThickness,0,0));
        Vec3 v7 = Vec3.add(v6, new Vec3(exteriorWidth, 0,0));
        Vec3 v8 = Vec3.add(v6, new Vec3(0, exteriorHeight,0));
        Vec3 v9 = Vec3.add(v6, new Vec3(0,0,exteriorThickness));
        Vec3 v10 = Vec3.add(v7, new Vec3(0,0,exteriorThickness));
        Vec3 v11 = Vec3.add(v8, new Vec3(0,0,exteriorThickness));

        Mesh2 frontFace = PrimitiveFactory.createLeftTriangle(v2,v0,v1,0);
        Mesh2 interiorSideFace = PrimitiveFactory.createQuad(v5,v3,v0,v2,0);
        Mesh2 exteriorInteriorLeftFace = PrimitiveFactory.createQuad(v8,v6,v3,v5,0);
        Mesh2 exteriorSideFace = PrimitiveFactory.createQuad(v11,v9,v6,v8,0);
        Mesh2 backFace = PrimitiveFactory.createRightTriangle(v10,v9,v11,0);
        Mesh2 exteriorTopFace = PrimitiveFactory.createQuad(v2,v1,v4,v5,0);
        Mesh2 interiorTopFace = PrimitiveFactory.createQuad(v8,v7,v10,v11,0);

        //this might be wrong, so might have to fix this
//        float heightGap = slope*interiorThickess;
//        Vec3 gappedPoint = Vec3.add(v8, new Vec3(0,heightGap,0));
        Mesh2 gapFaceLeftPart = PrimitiveFactory.createQuad(v8,v6,v3,v5,0);
        Mesh2 gapFaceRightPart = PrimitiveFactory.createQuad(v8,v5,v4,v7,0);
        Mesh2 bottomInteriorPart = PrimitiveFactory.createQuad(v1,v0,v3,v4,0);
        Mesh2 bottomExteriorPart = PrimitiveFactory.createQuad(v7,v6,v9,v10,0);

        outputMesh.add(frontFace);
        outputMesh.add(exteriorSideFace);
        outputMesh.add(exteriorInteriorLeftFace);
        outputMesh.add(interiorSideFace);
        outputMesh.add(backFace);
        outputMesh.add(exteriorTopFace);
        outputMesh.add(interiorTopFace);
        outputMesh.add(gapFaceLeftPart);
        outputMesh.add(gapFaceRightPart);
        outputMesh.add(bottomExteriorPart);
        outputMesh.add(bottomInteriorPart);
        return outputMesh;
    }

    public static Mesh2 createExtensionMesh(WallDTO leftWallToExtension,WallDTO extensionWallDTO,ExtensionDTO extensionDTO)
    {
        //TODO: CHANGE HEIGTH FORMULA
        Mesh2 outputMesh = new Mesh2();
        double angle = (double)extensionDTO.getAngle();
        float thickHalf = extensionWallDTO.thickness.divide(2).getRawInchValueFloat();
        float slopeHeight = thickHalf / (float)Math.cos(Math.toRadians(angle));
        float slope = (float) Math.tan(Math.toRadians(angle));
        float exteriorWidth = extensionWallDTO.realWidth().getRawInchValueFloat();
        float interiorWidth = extensionWallDTO.realInteriorWidth().getRawInchValueFloat();
        float interiorThickness = extensionWallDTO.thickness.plus(extensionWallDTO.imprecision).divide(2).getRawInchValueFloat();
        float exteriorThickness = extensionWallDTO.thickness.minus(extensionWallDTO.imprecision).divide(2).getRawInchValueFloat();
//        float exteriorHeight = middleExteriorHeight + exteriorThickness* slope;
        float exteriorHeight = leftWallToExtension.width.minus(leftWallToExtension.imprecision.divide(2)).getRawInchValueFloat() * slope;
        float middleExteriorHeight = exteriorHeight - exteriorThickness * slope;
        float middleInteriorHeight = middleExteriorHeight - slopeHeight;
        float interiorHeight = middleInteriorHeight - interiorThickness * slope;
//        float middleExteriorHeight = leftWallToExtension.realWidth().plus(leftWallToExtension.imprecision).getRawInchValueFloat() * slope;
//        float exteriorHeight = middleExteriorHeight + exteriorThickness* slope;
        float halfThicknessWithImp = extensionWallDTO.thickness.plus(extensionWallDTO.imprecision).divide(2).getRawInchValueFloat();

        //Exterior Face
        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(exteriorWidth,0,0));
        Vec3 v2 = Vec3.add(v1, new Vec3(0,exteriorHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,exteriorHeight,0));

        //Side Exterior Face
        Vec3 v4 = Vec3.add(v0, new Vec3(0,0,exteriorThickness));
        Vec3 v5 = Vec3.add(v1, new Vec3(0,0,exteriorThickness));
        Vec3 v6 = Vec3.add(v5, new Vec3(0,middleExteriorHeight,0));
        Vec3 v7 = Vec3.add(v4, new Vec3(0,middleExteriorHeight,0));

        //Side Interior Face
        Vec3 v8 = Vec3.add(v4, new Vec3(halfThicknessWithImp,0,0));
        Vec3 v9 = Vec3.add(v8, new Vec3(interiorWidth,0,0));
        Vec3 v10 = Vec3.add(v9, new Vec3(0,middleInteriorHeight,0));
        Vec3 v11 = Vec3.add(v8, new Vec3(0,middleInteriorHeight,0));

        //v12... in x should be = to thickness (check this if it's fucked up)
        Vec3 v12 = Vec3.add(v8, new Vec3(0,0,interiorThickness));
        Vec3 v13 = Vec3.add(v9, new Vec3(0,0,interiorThickness));
        Vec3 v14 = Vec3.add(v13, new Vec3(0,interiorHeight,0));
        Vec3 v15 = Vec3.add(v12, new Vec3(0,interiorHeight,0));

        Mesh2 exteriorFace = PrimitiveFactory.createQuad(v3,v0,v1,v2,0);
        Mesh2 leftExteriorSideFace = PrimitiveFactory.createQuad(v7,v4,v0,v3,0);
        Mesh2 rightExteriorSideFace = PrimitiveFactory.createQuad(v2,v1,v5,v6,0);

        Mesh2 topMiddleFace = PrimitiveFactory.createQuad(v6,v10,v11,v7,0);
        Mesh2 leftMiddleFace = PrimitiveFactory.createQuad(v6,v5,v9,v10,0);
        Mesh2 rightMiddleFace = PrimitiveFactory.createQuad(v11,v8,v4,v7,0);

        Mesh2 interiorFace = PrimitiveFactory.createQuad(v14,v13,v12,v15,0);
        Mesh2 leftInteriorFace = PrimitiveFactory.createQuad(v15,v12,v8,v11,0);
        Mesh2 rightInteriorFace = PrimitiveFactory.createQuad(v10,v9,v13,v14,0);

        Mesh2 topExteriorFace = PrimitiveFactory.createQuad(v2,v6,v7,v3,0);
        Mesh2 topInteriorFace = PrimitiveFactory.createQuad(v15,v11,v10,v14,0);

        Mesh2 bottomExteriorFace = PrimitiveFactory.createQuad(v0,v4,v5,v1,0);
        Mesh2 bottomInteriorFace = PrimitiveFactory.createQuad(v8,v12,v13,v9,0);

        outputMesh.add(exteriorFace);
        outputMesh.add(leftExteriorSideFace);
        outputMesh.add(rightExteriorSideFace);
        outputMesh.add(topMiddleFace);
        outputMesh.add(leftMiddleFace);
        outputMesh.add(rightMiddleFace);
        outputMesh.add(interiorFace);
        outputMesh.add(leftInteriorFace);
        outputMesh.add(rightInteriorFace);
        outputMesh.add(topExteriorFace);
        outputMesh.add(topInteriorFace);
        outputMesh.add(bottomExteriorFace);
        outputMesh.add(bottomInteriorFace);

        return outputMesh;
    }

    public static Mesh2 createSlopeMesh(WallDTO frontOfSlopeWall, WallDTO leftOfSlopeWall, SlopeDTO slopeDTO)
    {
        Mesh2 outputMash = new Mesh2();
        float angle = slopeDTO.getAngle();
        float slope = (float)Math.tan(Math.toRadians(angle));
        float slopeWidth = frontOfSlopeWall.width.getRawInchValueFloat();
        float slopeDepth = leftOfSlopeWall.width.getRawInchValueFloat();
        float thickHalf = frontOfSlopeWall.thickness.divide(2).getRawInchValueFloat();
        float thickHalfMinImp = frontOfSlopeWall.thickness.minus(frontOfSlopeWall.imprecision).divide(2).getRawInchValueFloat();
        float slopeHeight = thickHalf / (float)Math.cos(Math.toRadians(angle));
        //front face vertex
        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(slopeWidth,0,0));
        Vec3 v2 = Vec3.add(v1, new Vec3(0,slopeHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,slopeHeight, 0 ));
        float slopeHeightGain = slopeDepth * slope;

        //back face vertex
        Vec3 v4 = Vec3.add(v0, new Vec3(0,slopeHeightGain,slopeDepth));
        Vec3 v5 = Vec3.add(v1, new Vec3(0,slopeHeightGain,slopeDepth));
        Vec3 v6 = Vec3.add(v2, new Vec3(0,slopeHeightGain,slopeDepth));
        Vec3 v7 = Vec3.add(v3, new Vec3(0,slopeHeightGain,slopeDepth));
        //under vertex before the drop
        Vec3 v8 = Vec3.add(v4, new Vec3(thickHalfMinImp,-slope*thickHalfMinImp,-thickHalfMinImp));
        Vec3 v9 = Vec3.add(v5, new Vec3(-thickHalfMinImp,-slope*thickHalfMinImp,-thickHalfMinImp));
        //under vertex after drop
        Vec3 v10 = Vec3.add(v8, new Vec3(0,-slopeHeight,0));
        Vec3 v11 = Vec3.add(v9, new Vec3(0,-slopeHeight,0));
        //under vertex after drop sloped down
        float zDistance = v10.y / slope;
        Vec3 v12 = Vec3.add(v10, new Vec3(0,-v10.y,-zDistance));
        Vec3 v13 = Vec3.add(v11, new Vec3(0,-v10.y,-zDistance));
        //drop downed vertex at the end
        Vec3 v14 = new Vec3(v12.x,0,0);
        Vec3 v15 = new Vec3(v13.x,0,0);



        Mesh2 frontFace = PrimitiveFactory.createQuad(v3,v0,v1,v2,0);
        Mesh2 topFace = PrimitiveFactory.createQuad(v7,v3,v2,v6,0);
        Mesh2 leftFace = PrimitiveFactory.createQuad(v7,v4,v0,v3,0);
        Mesh2 rightFace = PrimitiveFactory.createQuad(v2,v1,v5,v6,0);
        Mesh2 backFace = PrimitiveFactory.createQuad(v6,v5,v4,v7,0);
        Mesh2 underBeforeDropDownFace = PrimitiveFactory.createQuad(v5,v9,v8,v4,0);
        Mesh2 underDropDownFace = PrimitiveFactory.createQuad(v9,v11,v10,v8,0);
        Mesh2 fullUnderFace = PrimitiveFactory.createQuad(v11,v13,v12,v10,0);
        Mesh2 underConnexFace = PrimitiveFactory.createQuad(v14,v12,v13,v15,0);
        Mesh2 leftUnderFace = PrimitiveFactory.createQuad(v8,v10,v12,v14,0);
        Mesh2 rightUnderFace = PrimitiveFactory.createQuad(v15,v13,v11,v9,0);
        Mesh2 leftUnderUnderFace = PrimitiveFactory.createQuad(v8,v14,v0,v4,0);
        Mesh2 rightUnderUnderFace = PrimitiveFactory.createQuad(v5,v1,v15,v9,0);

        outputMash.add(frontFace);
        outputMash.add(topFace);
        outputMash.add(leftFace);
        outputMash.add(rightFace);
        outputMash.add(backFace);
        outputMash.add(underBeforeDropDownFace);
        outputMash.add(underDropDownFace);
        outputMash.add(fullUnderFace);
        outputMash.add(underConnexFace);
        outputMash.add(leftUnderFace);
        outputMash.add(rightUnderFace);
        outputMash.add(leftUnderUnderFace);
        outputMash.add(rightUnderUnderFace);

        return outputMash;
    }

    public static Mesh2 createExtensionRetrait(WallDTO leftWallToExtension,WallDTO extensionWallDTO,ExtensionDTO extensionDTO)
    {
        Mesh2 outputMesh = new Mesh2();
        double angle = (double)extensionDTO.getAngle();
        float thickHalf = extensionWallDTO.thickness.divide(2).getRawInchValueFloat();
        float slopeHeight = thickHalf / (float)Math.cos(Math.toRadians(angle));
        float slope = (float) Math.tan(Math.toRadians(angle));
        float exteriorWidth = extensionWallDTO.realWidth().getRawInchValueFloat();
        float interiorWidth = extensionWallDTO.realInteriorWidth().getRawInchValueFloat();
        float interiorThickness = extensionWallDTO.thickness.divide(2).plus(extensionWallDTO.imprecision).getRawInchValueFloat();
        float exteriorThickness = extensionWallDTO.thickness.divide(2).minus(extensionWallDTO.imprecision).getRawInchValueFloat();
        float exteriorHeight = leftWallToExtension.width.minus(leftWallToExtension.imprecision.divide(2)).getRawInchValueFloat() * slope;
        float middleExteriorHeight = exteriorHeight - exteriorThickness * slope;
        float middleInteriorHeight = middleExteriorHeight - slopeHeight;
        float interiorHeight = middleInteriorHeight - interiorThickness * slope;
        float halfThicknessWithImp = extensionWallDTO.thickness.plus(extensionWallDTO.imprecision).divide(2).getRawInchValueFloat();

        //Exterior Face
        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(exteriorWidth,0,0));
        Vec3 v2 = Vec3.add(v1, new Vec3(0,exteriorHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,exteriorHeight,0));

        //Side Exterior Face
        Vec3 v4 = Vec3.add(v0, new Vec3(0,0,exteriorThickness));
        Vec3 v5 = Vec3.add(v1, new Vec3(0,0,exteriorThickness));
        Vec3 v6 = Vec3.add(v5, new Vec3(0,middleExteriorHeight,0));
        Vec3 v7 = Vec3.add(v4, new Vec3(0,middleExteriorHeight,0));

        //Side Interior Face
        Vec3 v8 = Vec3.add(v4, new Vec3(halfThicknessWithImp,0,0));
        Vec3 v9 = Vec3.add(v8, new Vec3(interiorWidth,0,0));
        Vec3 v10 = Vec3.add(v9, new Vec3(0,middleInteriorHeight,0));
        Vec3 v11 = Vec3.add(v8, new Vec3(0,middleInteriorHeight,0));

        //v12... in x should be = to thickness (check this if it's fucked up)
        Vec3 v12 = Vec3.add(v8, new Vec3(0,0,interiorThickness));
        Vec3 v13 = Vec3.add(v9, new Vec3(0,0,interiorThickness));
        Vec3 v14 = Vec3.add(v13, new Vec3(0,interiorHeight,0));
        Vec3 v15 = Vec3.add(v12, new Vec3(0,interiorHeight,0));

        Vec3 v3f = new Vec3(v3.x,v3.y,exteriorThickness+interiorThickness);
        Vec3 v2f = new Vec3(v2.x, v2.y, exteriorThickness+interiorThickness);
        Vec3 v4f = new Vec3(v4.x,v4.y,exteriorThickness+interiorThickness);
        Vec3 v5f = new Vec3(v5.x,v5.y,exteriorThickness+interiorThickness);
        Vec3 v6f = new Vec3(v6.x,v6.y,exteriorThickness+interiorThickness);
        Vec3 v7f = new Vec3(v7.x,v7.y,exteriorThickness+interiorThickness);
        Vec3 v7r = new Vec3(v7.x+halfThicknessWithImp,v7.y,v7.z);
        Vec3 v7fr = new Vec3(v7r.x,v7.y,exteriorThickness+interiorThickness);
        Vec3 v6l = new Vec3(v6.x-halfThicknessWithImp,v6.y,v6.z);
        Vec3 v6fl = new Vec3(v6.x-halfThicknessWithImp,v6.y,exteriorThickness+interiorThickness);

        Mesh2 topRetrait = PrimitiveFactory.createQuad(v3f,v3,v2,v2f,0);

        Mesh2 bottomRightSquare = PrimitiveFactory.createQuad(v9,v13,v5f,v5,0);
        Mesh2 topRightSquare = PrimitiveFactory.createQuad(v6,v6f,v6fl,v6l,0);
        Mesh2 backRightPillarFace = PrimitiveFactory.createQuad(v6f,v5f,v13,v6fl,0);
        Mesh2 leftRightPillarFace = PrimitiveFactory.createQuad(v6,v5,v5f,v6f,0);
        Mesh2 frontRightPillarFace = PrimitiveFactory.createQuad(v6l,v9,v5,v6,0);
        Mesh2 rightRightPillarFace = PrimitiveFactory.createQuad(v6fl,v13,v9,v6l,0);

        Mesh2 bottomLeftSquare = PrimitiveFactory.createQuad(v4,v4f,v12,v8,0);
        Mesh2 topLeftSquare = PrimitiveFactory.createQuad(v7r,v7fr,v7f,v7,0);
        Mesh2 backLeftPillarFace = PrimitiveFactory.createQuad(v7fr,v12,v4f,v7f,0);
        Mesh2 leftLeftPillarFace = PrimitiveFactory.createQuad(v7r,v8,v12,v7fr,0);
        Mesh2 frontLeftPillarFace = PrimitiveFactory.createQuad(v7,v4,v8,v7r,0);
        Mesh2 rightLeftPillarFace = PrimitiveFactory.createQuad(v7f,v4f,v4,v7,0);

        Mesh2 rightBottomTrapFace = PrimitiveFactory.createQuad(v7fr,v15,v11,v7r,0);
        Mesh2 leftBottomTrapFace = PrimitiveFactory.createQuad(v6fl,v10,v14,v6l,0);
        Mesh2 topBottomTrapFace = PrimitiveFactory.createQuad(v7r,v6l,v6fl,v7fr,0);
        Mesh2 exteriorBottomTrapFace = PrimitiveFactory.createQuad(v7r,v11,v10,v6l,0);
        Mesh2 interiorBottomTrapFace = PrimitiveFactory.createQuad(v6fl,v14,v15,v7fr,0);
        Mesh2 bottomBottomTrapce = PrimitiveFactory.createQuad(v14,v10,v11,v15,0);

        Mesh2 rightTopTrapFace = PrimitiveFactory.createQuad(v2,v6,v6f,v2f,0);
        Mesh2 leftTopTrapFace = PrimitiveFactory.createQuad(v3f,v7f,v7,v3,0);
        Mesh2 topTopTrapFace = PrimitiveFactory.createQuad(v3,v2,v2f,v3f,0);
        Mesh2 bottomTopTrapFace = PrimitiveFactory.createQuad(v6,v7,v7f,v6f,0);
        Mesh2 ExteriorTopTrapFace = PrimitiveFactory.createQuad(v3,v7,v6,v2,0);
        Mesh2 InteriorTopTrapFace = PrimitiveFactory.createQuad(v2f,v6f,v7f,v3f,0);
//        Mesh2 exteriorFace = PrimitiveFactory.createQuad(v3,v0,v1,v2,0);
//        Mesh2 leftExteriorSideFace = PrimitiveFactory.createQuad(v7,v4,v0,v3,0);
//        Mesh2 rightExteriorSideFace = PrimitiveFactory.createQuad(v2,v1,v5,v6,0);
//
//        Mesh2 topMiddleFace = PrimitiveFactory.createQuad(v6,v10,v11,v7,0);
//        Mesh2 leftMiddleFace = PrimitiveFactory.createQuad(v6,v5,v9,v10,0);
//        Mesh2 rightMiddleFace = PrimitiveFactory.createQuad(v11,v8,v4,v7,0);
//
//        Mesh2 interiorFace = PrimitiveFactory.createQuad(v14,v13,v12,v15,0);
//        Mesh2 leftInteriorFace = PrimitiveFactory.createQuad(v15,v12,v8,v11,0);
//        Mesh2 rightInteriorFace = PrimitiveFactory.createQuad(v10,v9,v13,v14,0);
//
//        Mesh2 topExteriorFace = PrimitiveFactory.createQuad(v2,v6,v7,v3,0);
//        Mesh2 topInteriorFace = PrimitiveFactory.createQuad(v15,v11,v10,v14,0);
//
//        Mesh2 bottomExteriorFace = PrimitiveFactory.createQuad(v0,v4,v5,v1,0);
//        Mesh2 bottomInteriorFace = PrimitiveFactory.createQuad(v8,v12,v13,v9,0);
        outputMesh.add(topRetrait);
        outputMesh.add(bottomLeftSquare);
        outputMesh.add(bottomRightSquare);
        outputMesh.add(topRightSquare);
        outputMesh.add(topLeftSquare);
        outputMesh.add(backRightPillarFace);
        outputMesh.add(leftRightPillarFace);
        outputMesh.add(frontRightPillarFace);
        outputMesh.add(rightRightPillarFace);
        outputMesh.add(backLeftPillarFace);
        outputMesh.add(leftLeftPillarFace);
        outputMesh.add(frontLeftPillarFace);
        outputMesh.add(rightLeftPillarFace);

        outputMesh.add(rightBottomTrapFace);
        outputMesh.add(leftBottomTrapFace);
        outputMesh.add(topBottomTrapFace);
        outputMesh.add(exteriorBottomTrapFace);
        outputMesh.add(interiorBottomTrapFace);
        outputMesh.add(bottomBottomTrapce);

        outputMesh.add(leftTopTrapFace);
        outputMesh.add(topTopTrapFace);
        outputMesh.add(bottomTopTrapFace);
        outputMesh.add(ExteriorTopTrapFace);
        outputMesh.add(InteriorTopTrapFace);
        outputMesh.add(rightTopTrapFace);
        return outputMesh;

    }
    public static Mesh2 createSlopeRetrait(WallDTO frontOfSlopeWall, WallDTO leftOfSlopeWall, SlopeDTO slopeDTO)
    {
        Mesh2 outputMash = new Mesh2();
        float angle = slopeDTO.getAngle();
        float slope = (float)Math.tan(Math.toRadians(angle));
        float slopeWidth = frontOfSlopeWall.width.getRawInchValueFloat();
        float slopeDepth = leftOfSlopeWall.width.getRawInchValueFloat();
        float thickHalf = frontOfSlopeWall.thickness.divide(2).getRawInchValueFloat();
        float thickHalfMinImp = frontOfSlopeWall.thickness.minus(frontOfSlopeWall.imprecision).divide(2).getRawInchValueFloat();
        float slopeHeight = thickHalf / (float)Math.cos(Math.toRadians(angle));
        //front face vertex
        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(slopeWidth,0,0));
        Vec3 v2 = Vec3.add(v1, new Vec3(0,slopeHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,slopeHeight, 0 ));
        float slopeHeightGain = slopeDepth * slope;

        //back face vertex
        Vec3 v4 = Vec3.add(v0, new Vec3(0,slopeHeightGain,slopeDepth));
        Vec3 v5 = Vec3.add(v1, new Vec3(0,slopeHeightGain,slopeDepth));
        Vec3 v6 = Vec3.add(v2, new Vec3(0,slopeHeightGain,slopeDepth));
        Vec3 v7 = Vec3.add(v3, new Vec3(0,slopeHeightGain,slopeDepth));
        //under vertex before the drop
        Vec3 v8 = Vec3.add(v4, new Vec3(thickHalfMinImp,-slope*thickHalfMinImp,-thickHalfMinImp));
        Vec3 v9 = Vec3.add(v5, new Vec3(-thickHalfMinImp,-slope*thickHalfMinImp,-thickHalfMinImp));
        //under vertex after drop
        Vec3 v10 = Vec3.add(v8, new Vec3(0,-slopeHeight,0));
        Vec3 v11 = Vec3.add(v9, new Vec3(0,-slopeHeight,0));
        //under vertex after drop sloped down
        float zDistance = v10.y / slope;
        Vec3 v12 = Vec3.add(v10, new Vec3(0,-v10.y,-zDistance));
        Vec3 v13 = Vec3.add(v11, new Vec3(0,-v10.y,-zDistance));
        //drop downed vertex at the end
        Vec3 v14 = new Vec3(v12.x,0,0);
        Vec3 v15 = new Vec3(v13.x,0,0);

        Vec3 v9r = Vec3.add(v9, new Vec3(thickHalfMinImp,0,0));
        Vec3 v8l = Vec3.add(v8, new Vec3(-thickHalfMinImp,0,0));

        Vec3 v11r = Vec3.add(v11, new Vec3(thickHalfMinImp,0,0));
        Vec3 v10l = Vec3.add(v10, new Vec3(-thickHalfMinImp,0,0));

        Vec3 v13r = Vec3.add(v13, new Vec3(thickHalfMinImp,0,0));
        Vec3 v12l = Vec3.add(v12, new Vec3(-thickHalfMinImp,0,0));

        Mesh2 leftPrism = quadPrism(v9r,v11r,v11,v9,v1,v13r,v13,v15);
        Mesh2 rightPrism = quadPrism(v8,v10,v10l,v8l,v14,v12,v12l,v0);

        float h = (float)Math.sin(Math.toRadians(90-angle)) * frontOfSlopeWall.thickness.getRawInchValueFloat();
        float d = (float)Math.cos(Math.toRadians(90-angle)) * frontOfSlopeWall.thickness.getRawInchValueFloat();

        Vec3 v4s = Vec3.add(v4,new Vec3(0,-h,d));
        Vec3 v5s = Vec3.add(v5,new Vec3(0,-h,d));

        Mesh2 leftTriangle = PrimitiveFactory.createRightTriangle(v4s,v4,v7,0);
        Mesh2 rightTriangle = PrimitiveFactory.createRightTriangle(v5s,v6,v5,0);
        Mesh2 topQuadOfTri = PrimitiveFactory.createQuad(v5s,v4s,v4,v5,0);
        Mesh2 rightQuadOfTri = PrimitiveFactory.createQuad(v4,v7,v6,v5,0);
        Mesh2 bottomQuadOfTri = PrimitiveFactory.createQuad(v5s,v6,v7,v4s,0);

        Mesh2 trapPrism = quadPrism(v9r,v11r,v5s,v5,v8l,v10l,v4s,v4);





        Mesh2 frontFace = PrimitiveFactory.createQuad(v3,v0,v1,v2,0);
        Mesh2 topFace = PrimitiveFactory.createQuad(v7,v3,v2,v6,0);
        Mesh2 leftFace = PrimitiveFactory.createQuad(v7,v4,v0,v3,0);
        Mesh2 rightFace = PrimitiveFactory.createQuad(v2,v1,v5,v6,0);
        Mesh2 backFace = PrimitiveFactory.createQuad(v6,v5,v4,v7,0);
        Mesh2 underBeforeDropDownFace = PrimitiveFactory.createQuad(v5,v9,v8,v4,0);
        Mesh2 underDropDownFace = PrimitiveFactory.createQuad(v9,v11,v10,v8,0);
        Mesh2 fullUnderFace = PrimitiveFactory.createQuad(v11,v13,v12,v10,0);
        Mesh2 underConnexFace = PrimitiveFactory.createQuad(v14,v12,v13,v15,0);
        Mesh2 leftUnderFace = PrimitiveFactory.createQuad(v8,v10,v12,v14,0);
        Mesh2 rightUnderFace = PrimitiveFactory.createQuad(v15,v13,v11,v9,0);
        Mesh2 leftUnderUnderFace = PrimitiveFactory.createQuad(v8,v14,v0,v4,0);
        Mesh2 rightUnderUnderFace = PrimitiveFactory.createQuad(v5,v1,v15,v9,0);

        float d2 = thickHalf * (float)Math.cos(Math.toRadians(90-angle));
        float h2 = thickHalf * (float)Math.sin(Math.toRadians(90-angle));
        Vec3 v0s = Vec3.add(v0,new Vec3(0,h2,-d2));
        Vec3 v0ss = Vec3.add(v0,new Vec3(0,-h2,d2));
        Vec3 v4sw = Vec3.add(v4s, new Vec3(slopeWidth,0,0));
        Vec3 v0ssw = Vec3.add(v0ss, new Vec3(slopeWidth,0,0));
        Vec3 v12lw = Vec3.add(v12l, new Vec3(slopeWidth,0,0));
        Vec3 v8lw = Vec3.add(v8l, new Vec3(slopeWidth,0,0));
        Vec3 v0sw = Vec3.add(v0s, new Vec3(slopeWidth,0,0));
        Vec3 v0w = Vec3.add(v0, new Vec3(slopeWidth,0,0));
        Mesh2 testPrism = quadPrism(v8l,v4s,v0ss,v12l,v8lw,v4sw,v0ssw,v12lw);

        Mesh2 leftSide = PrimitiveFactory.createLeftTriangle(v12l,v0ss,v0,0);
        Mesh2 rightSide = PrimitiveFactory.createLeftTriangle(v0w,v0ssw,v12lw,0);
        Mesh2 leftSide2 = PrimitiveFactory.createLeftTriangle(v0,v0s,v3,0);
        Mesh2 rightSide2 = PrimitiveFactory.createRightTriangle(v2,v0sw,v0w,0);
        Mesh2 up2 = PrimitiveFactory.createQuad(v0w,v0,v0s,v0sw,0);
        Mesh2 left2 = PrimitiveFactory.createQuad(v2,v3,v0s,v0sw,0);
        Mesh2 right2 = PrimitiveFactory.createQuad(v2,v3,v0,v0w,0);
        Mesh2 up1 = PrimitiveFactory.createQuad(v12lw,v12l,v0,v0w,0);
        Mesh2 left1 = PrimitiveFactory.createQuad(v0w,v0,v0ss,v0ssw,0);
        Mesh2 right1 = PrimitiveFactory.createQuad(v0ssw,v0ss,v12l,v12lw,0);

        Mesh2 testQuad = PrimitiveFactory.createQuad(v8l,v4s,v0ss,v12l,0);

        outputMash.add(leftPrism);
        outputMash.add(rightPrism);
        outputMash.add(leftTriangle);
        outputMash.add(rightTriangle);
        outputMash.add(topQuadOfTri);
        outputMash.add(rightQuadOfTri);
        outputMash.add(bottomQuadOfTri);
        outputMash.add(trapPrism);
        outputMash.add(testPrism);
        outputMash.add(leftSide);
        outputMash.add(rightSide);
        outputMash.add(leftSide2);
        outputMash.add(rightSide2);
        outputMash.add(up1);
        outputMash.add(left1);
        outputMash.add(right1);
        outputMash.add(up2);
        outputMash.add(left2);
        outputMash.add(right2);


        return outputMash;
    }


    private static Mesh2 quadPrism(Vec3 ttl, Vec3 tbl, Vec3 tbr, Vec3 ttr, Vec3 btl, Vec3 bbl, Vec3 bbr, Vec3 btr)
    {
        Mesh2 outMesh = new Mesh2();
        Mesh2 topFace = PrimitiveFactory.createQuad(ttl,tbl,tbr,ttr,0);
        Mesh2 bottomFace = PrimitiveFactory.createQuad(btr,bbr,bbl,btl, 0);
        Mesh2 frontface = PrimitiveFactory.createQuad(tbl,bbl,bbr,tbr,0);
        Mesh2 leftface = PrimitiveFactory.createQuad(ttl,btl,bbl,tbl,0);
        Mesh2 backface = PrimitiveFactory.createQuad(ttl,btl,btr,ttr,0);
        Mesh2 rightFace = PrimitiveFactory.createQuad(tbr,bbr,btr,ttr,0);
        outMesh.add(topFace);
        outMesh.add(bottomFace);
        outMesh.add(frontface);
        outMesh.add(leftface);
        outMesh.add(backface);
        outMesh.add(rightFace);
        return outMesh;
    }
    public static Mesh2 createLeftGableRetrait(WallDTO leftGableWall, GableDTO leftGable)
    {
        Mesh2 outputMesh = new Mesh2();
        double angle = (double)leftGable.getAngle();
        float slope = (float) Math.tan(Math.toRadians(angle));
        float exteriorWidth = leftGableWall.width.minus(leftGableWall.thickness.plus(leftGableWall.imprecision).divide(2)).getRawInchValueFloat();
        float interiorWidth = leftGableWall.realInteriorWidth().getRawInchValueFloat();
        float exteriorHeight = exteriorWidth * slope;
        float interiorHeight = interiorWidth * slope;
        float exteriorThickness = leftGableWall.thickness.minus(leftGableWall.imprecision).divide(2).getRawInchValueFloat();
        float interiorThickess = leftGableWall.thickness.plus(leftGableWall.imprecision).divide(2).getRawInchValueFloat();

        Vec3 v0 = new Vec3(0,0,0);
        Vec3 v1 = Vec3.add(v0, new Vec3(exteriorWidth,0,0));
        Vec3 v2 = Vec3.add(v0, new Vec3(0,exteriorHeight,0));
        Vec3 v3 = Vec3.add(v0, new Vec3(0,0,exteriorThickness));
        Vec3 v4 = Vec3.add(v1, new Vec3(0,0,exteriorThickness));
        Vec3 v5 = Vec3.add(v2, new Vec3(0,0,exteriorThickness));

        Vec3 v6 = Vec3.add(v3,new Vec3(interiorThickess,0,0));
        Vec3 v7 = Vec3.add(v6, new Vec3(interiorWidth, 0,0));
        Vec3 v8 = Vec3.add(v6, new Vec3(0, interiorHeight,0));
        Vec3 v9 = Vec3.add(v6, new Vec3(0,0,interiorThickess));
        Vec3 v10 = Vec3.add(v7, new Vec3(0,0,interiorThickess));
        Vec3 v11 = Vec3.add(v8, new Vec3(0,0,interiorThickess));

        Mesh2 frontFace = PrimitiveFactory.createLeftTriangle(v2,v0,v1,0);
        Mesh2 exteriorSideFace = PrimitiveFactory.createQuad(v5,v3,v0,v2,0);
        Mesh2 exteriorInteriorLeftFace = PrimitiveFactory.createQuad(v8,v6,v3,v5,0);
        Mesh2 interiorSideFace = PrimitiveFactory.createQuad(v11,v9,v6,v8,0);
        Mesh2 backFace = PrimitiveFactory.createRightTriangle(v10,v9,v11,0);
        Mesh2 exteriorTopFace = PrimitiveFactory.createQuad(v2,v1,v4,v5,0);
        Mesh2 interiorTopFace = PrimitiveFactory.createQuad(v8,v7,v10,v11,0);

        //this might be wrong, so might have to fix this
        float heightGap = slope*interiorThickess;
        Vec3 gappedPoint = Vec3.add(v8, new Vec3(0,heightGap,0));
        Mesh2 gapFaceLeftPart = PrimitiveFactory.createQuad(v5,v4,v7,v8,0);
        Mesh2 gapFaceRightPart = PrimitiveFactory.createQuad(v8,v6,v3,v4,0);
        Mesh2 bottomExteriorPart = PrimitiveFactory.createQuad(v1,v0,v3,v4,0);
        Mesh2 bottomInteriorPart = PrimitiveFactory.createQuad(v7,v6,v9,v10,0);

        Vec3 v4w = Vec3.add(v4,new Vec3(0,0,interiorThickess));
        Vec3 v5w = Vec3.add(v5,new Vec3(0,0,interiorThickess));
        Vec3 v8w = Vec3.add(v8,new Vec3(0,0,interiorThickess));
        Vec3 v7w = Vec3.add(v7,new Vec3(0,0,interiorThickess));
        Vec3 v6w = Vec3.add(v6,new Vec3(0,0,interiorThickess));
        Vec3 v3w = Vec3.add(v3,new Vec3(0,0,interiorThickess));
        Vec3 vc = Vec3.add(v1, new Vec3(0,exteriorHeight,0));
        Vec3 vcw = Vec3.add(vc, new Vec3(0,0,leftGableWall.thickness.getRawInchValueFloat()));
        Vec3 v1w = Vec3.add(v1, new Vec3(0,0,leftGableWall.thickness.getRawInchValueFloat()));
        Vec3 v2w = Vec3.add(v2, new Vec3(0,0,leftGableWall.thickness.getRawInchValueFloat()));
        Mesh2 leftPrism = quadPrism(v5w,v4w,v7w,v8w,v5,v4,v7,v8);
        Mesh2 rightPrism = quadPrism(v8w,v6w,v3w,v5w,v8,v6,v3,v5);

        Mesh2 innerTiangle = PrimitiveFactory.createLeftTriangle(vc,v1,v2,0);
        Mesh2 outerTiangle = PrimitiveFactory.createRightTriangle(v2w,v1w,vcw ,0);
        Mesh2 left = PrimitiveFactory.createQuad(vc,v1,v1w,vcw,0);
        Mesh2 top = PrimitiveFactory.createQuad(v2w,vcw,vc,v2,0);
        Mesh2 bottom = PrimitiveFactory.createQuad(v1w,v2w,v2,v1,0);



        outputMesh.add(leftPrism);
        outputMesh.add(rightPrism);
        outputMesh.add(innerTiangle);
        outputMesh.add(outerTiangle);
        outputMesh.add(left);
        outputMesh.add(top);
        outputMesh.add(bottom);
        return outputMesh;
    }

}

