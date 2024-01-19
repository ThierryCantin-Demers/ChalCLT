package ca.ulaval.glo2004.domaine.chalet.STLChaletExporter;


import ca.ulaval.glo2004.rendering.Face;
import ca.ulaval.glo2004.rendering.Mesh2;
import ca.ulaval.glo2004.util.math.Vec3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class STLBuilderASCII {
    private List<STLTriangle> triangles;

    public STLBuilderASCII() {
        triangles = new ArrayList<>();
    }

    public static List<STLTriangle> generateMeshTriangles(Mesh2 mesh) {
        List<STLTriangle> triangleList = new ArrayList<>();
        //might have to change winding later on maybe perhaps, perchance, mayhaps
        Vec3 zAdjustment = new Vec3(1, 1, -1);
        for (Face face : mesh.faces) {
            Vec3 v1 = Vec3.mult(mesh.vertices[face.vi1].position, zAdjustment);
            Vec3 v2 = Vec3.mult(mesh.vertices[face.vi2].position, zAdjustment);
            Vec3 v3 = Vec3.mult(mesh.vertices[face.vi3].position, zAdjustment);

            triangleList.add(new STLTriangle(v1, v2, v3));
        }
        return triangleList;
    }

    public void addTriangle(STLTriangle triangle) {
        triangles.add(triangle);
    }

    public void addTriangles(List<STLTriangle> additionalTriangles) {
        triangles.addAll(additionalTriangles);
    }

    public void writeToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write ASCII STL header
            writer.write("solid GeneratedSTL\n");

            // Write each triangle in ASCII format
            for (STLTriangle triangle : triangles) {
                writer.write("  facet normal ");
                writeFloat(writer, triangle.normal().x);
                writeFloat(writer, triangle.normal().y);
                writeFloat(writer, triangle.normal().z);
                writer.write("\n");

                writer.write("    outer loop\n");
                writeVertex(writer, triangle.v1);
                writeVertex(writer, triangle.v2);
                writeVertex(writer, triangle.v3);
                writer.write("    endloop\n");

                writer.write("  endfacet\n");
            }

            // Write ASCII STL footer
            writer.write("endsolid GeneratedSTL\n");

            System.out.println("STL file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write STL file.");
        }
    }

    private void writeFloat(BufferedWriter writer, float value) throws IOException {
        writer.write(String.format(" %f", value));
    }

    private void writeVertex(BufferedWriter writer, Vec3 vertex) throws IOException {
        writer.write("      vertex");
        writeFloat(writer, vertex.x);
        writeFloat(writer, vertex.y);
        writeFloat(writer, vertex.z);
        writer.write("\n");
    }
}

