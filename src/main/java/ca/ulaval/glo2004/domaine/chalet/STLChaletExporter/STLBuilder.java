package ca.ulaval.glo2004.domaine.chalet.STLChaletExporter;

import ca.ulaval.glo2004.rendering.Face;
import ca.ulaval.glo2004.rendering.Mesh2;
import ca.ulaval.glo2004.rendering.Transform;
import ca.ulaval.glo2004.util.math.Mat4;
import ca.ulaval.glo2004.util.math.Vec3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class STLBuilder {
    private List<STLTriangle> triangles;

    public STLBuilder() {
        triangles = new ArrayList<>();
    }

    public static List<STLTriangle> generateMeshTriangles(Mesh2 mesh)
    {
        List<STLTriangle> triangleList = new ArrayList<>();
        //might have to change winding later on maybe perhaps, perchance, mayhaps
        Vec3 zAdjustment = new Vec3(-1,-1,-1);
        for (Face face : mesh.faces) {
            Vec3 v1 = Vec3.mult(mesh.vertices[face.vi1].position, zAdjustment);
            Vec3 v2 = Vec3.mult(mesh.vertices[face.vi2].position, zAdjustment);
            Vec3 v3 = Vec3.mult(mesh.vertices[face.vi3].position, zAdjustment);

            triangleList.add(new STLTriangle(v1, v3, v2));
        }
        return triangleList;
    }

    public void addTriangle(STLTriangle triangle) {
        triangles.add(triangle);
    }

    public void addTriangles(List<STLTriangle> triangles) {
        triangles.addAll(triangles);
    }

    public void writeToFile(String filePath) {
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            // Write STL header (80 bytes reserved for header)
            dos.write(new byte[80]);

            // Write number of triangles (4 bytes)
            writeLittleEndianInt(dos,triangles.size());
            // Write each triangle
            for (STLTriangle triangle : triangles) {
                // Write normal vector (12 bytes)

                writeLittleEndianFloat(dos,triangle.normal().x);
                writeLittleEndianFloat(dos,triangle.normal().y);
                writeLittleEndianFloat(dos,triangle.normal().z);

                // Write vertex 1 (12 bytes)
                writeLittleEndianFloat(dos,triangle.v1.x);
                writeLittleEndianFloat(dos,triangle.v1.y);
                writeLittleEndianFloat(dos,triangle.v1.z);

                // Write vertex 2 (12 bytes)
                writeLittleEndianFloat(dos,triangle.v2.x);
                writeLittleEndianFloat(dos,triangle.v2.y);
                writeLittleEndianFloat(dos, triangle.v2.z);

                // Write vertex 3 (12 bytes)
                writeLittleEndianFloat(dos,triangle.v3.x);
                writeLittleEndianFloat(dos,triangle.v3.y);
                writeLittleEndianFloat(dos,triangle.v3.z);

                // Debugging output
                System.out.println("Normal: " + triangle.normal());
                System.out.println("V1: " + triangle.v1);
                System.out.println("V2: " + triangle.v2);
                System.out.println("V3: " + triangle.v3);

                // Write attribute byte count (2 bytes, set to zero for now)
                writeLittleEndianShort(dos,(short)0);
            }

            System.out.println("STL file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("hi");
        }
    }
    private void writeLittleEndianFloat(DataOutputStream dos, float value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(value);
        dos.write(buffer.array());
    }

    private void writeLittleEndianShort(DataOutputStream dos, short value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);
        dos.write(buffer.array());
    }
    private void writeLittleEndianInt(DataOutputStream dos, int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);
        dos.write(buffer.array());
    }

    public static List<STLTriangle> transformTriangles(List<STLTriangle> triangles, Transform transform) {
        List<STLTriangle> transformedTriangles = new ArrayList<>();
        Mat4 transformMatrix = transform.getTransform();
        for (STLTriangle triangle : triangles) {
            transformedTriangles.add(new STLTriangle(transformMatrix.transform(triangle.v1.toVec4()).toVec3(),
                    transformMatrix.transform(triangle.v2.toVec4()).toVec3(),
                    transformMatrix.transform(triangle.v3.toVec4()).toVec3()));
        }

        return transformedTriangles;
    }
}
