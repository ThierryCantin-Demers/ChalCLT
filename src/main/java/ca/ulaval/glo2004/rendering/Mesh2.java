package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.util.math.Vec3;

import java.util.*;

public class Mesh2 {
    public Vertex[] vertices = new Vertex[0];
    public Face[] faces = new Face[0];

    public Material material = new Material();

    public boolean autoCalculateNormals = true;

    public Mesh2() {
        this.vertices = new Vertex[]{};
        this.faces = new Face[]{};
    }
    public Mesh2(Vertex[] vertices, Face[] faces) {
        this.vertices = vertices;
        this.faces = faces;

        if (autoCalculateNormals)
            calculateVertexNormals();
    }

    public void set(Vertex[] vertices, Face[] indices)
    {
        if (indices.length % 3 != 0)
            return;

        this.vertices = vertices;
        this.faces = indices;

        if (autoCalculateNormals)
            calculateVertexNormals();
    }

    public List<Integer> getAdjacentVertices(int vertexIndex) {
        List<Integer> adjacentVertices = new ArrayList<>();

        for (Face face : faces) {
            if (face.vi1 == vertexIndex) {
                adjacentVertices.add(face.vi2);
                adjacentVertices.add(face.vi3);
            } else if (face.vi2 == vertexIndex) {
                adjacentVertices.add(face.vi1);
                adjacentVertices.add(face.vi3);
            } else if (face.vi3 == vertexIndex) {
                adjacentVertices.add(face.vi1);
                adjacentVertices.add(face.vi2);
            }
        }
        return adjacentVertices;
    }

    public void calculateVertexNormals() {
        HashMap<Vec3, Set<Vec3>> vertexNormalsMap = new HashMap<>(); // Map to accumulate normals for each unique vertex position

        // Initialize normals for each vertex
        for (Vertex value : this.vertices) {
            value.normal = new Vec3(0, 0, 0);
            value.avgNormal = new Vec3(0, 0, 0);
        }

        HashMap<Integer, Set<Vec3>> accumulationMap = new HashMap<>();

        // Calculate face normals and accumulate them for each vertex position
        for (Face face : this.faces) {
            int ia = face.vi1;
            int ib = face.vi2;
            int ic = face.vi3;

            Vec3 e1 = Vec3.sub(this.vertices[ia].position, this.vertices[ib].position).normalized();
            Vec3 e2 = Vec3.sub(this.vertices[ic].position, this.vertices[ib].position).normalized();
            Vec3 faceNormal = Vec3.cross(e2, e1).normalized();

            accumulateVertexNormal2(accumulationMap, ia, faceNormal);
            accumulateVertexNormal2(accumulationMap, ib, faceNormal);
            accumulateVertexNormal2(accumulationMap, ic, faceNormal);

//            this.vertices[ia].normal.add(faceNormal);
//            this.vertices[ib].normal.add(faceNormal);
//            this.vertices[ic].normal.add(faceNormal);

            // Accumulate face normals for each vertex position in the map
            accumulateVertexNormal(vertexNormalsMap, this.vertices[ia].position, faceNormal);
            accumulateVertexNormal(vertexNormalsMap, this.vertices[ib].position, faceNormal);
            accumulateVertexNormal(vertexNormalsMap, this.vertices[ic].position, faceNormal);
        }

        // Assign accumulated normals to corresponding vertices
        Vertex[] vertices1 = this.vertices;
        for (int i = 0; i < vertices1.length; i++) {
            Vertex vertex = vertices1[i];
            vertex.normal = calculateAverageNormal(accumulationMap.getOrDefault(i, new HashSet<>()));

            vertex.avgNormal = calculateAverageNormal(vertexNormalsMap.getOrDefault(vertex.position, new HashSet<>()));
//
//            if (accumulatedNormal != null) {
//                vertex.avgNormal = accumulatedNormal.normalized();
//            }
        }
    }

    // Helper function to accumulate unique normals for vertices with the same position
    private void accumulateVertexNormal(HashMap<Vec3, Set<Vec3>> vertexNormalsMap, Vec3 position, Vec3 faceNormal) {
        Set<Vec3> normals = vertexNormalsMap.getOrDefault(position, new HashSet<>());
        normals.add(faceNormal);
        vertexNormalsMap.put(position, normals);
    }
//
//    // Helper function to accumulate normals for vertices with the same position
//    private void accumulateVertexNormal(HashMap<Vec3, Vec3> vertexNormalsMap, Vec3 position, Vec3 faceNormal) {
//        Vec3 accumulatedNormal = vertexNormalsMap.getOrDefault(position, new Vec3(0, 0, 0));
//        accumulatedNormal.add(faceNormal);
//        vertexNormalsMap.put(position, accumulatedNormal);
//    }

    private void accumulateVertexNormal2(HashMap<Integer, Set<Vec3>> vertexNormalsMap, int vertIndex, Vec3 faceNormal) {
        Set<Vec3> accumulatedNormalSet = vertexNormalsMap.getOrDefault(vertIndex, new HashSet<>());
        accumulatedNormalSet.add(faceNormal);
        vertexNormalsMap.put(vertIndex, accumulatedNormalSet);
    }

    private Vec3 calculateAverageNormal(Set<Vec3> normals) {
        Vec3 sum = new Vec3(0, 0, 0);
        for (Vec3 normal : normals) {
            sum.add(normal);
        }
        return sum.normalized();
    }

    public Mesh2 add(Mesh2 otherMesh) {
        int vertexOffset = this.vertices.length;

        // Calculate new array sizes
        int totalVertices = this.vertices.length + otherMesh.vertices.length;
        int totalFaces = this.faces.length + otherMesh.faces.length;

        // Create new arrays with updated sizes
        Vertex[] updatedVertices = new Vertex[totalVertices];
        Face[] updatedFaces = new Face[totalFaces];

        // Copy existing vertices and faces to new arrays
        System.arraycopy(this.vertices, 0, updatedVertices, 0, this.vertices.length);
        System.arraycopy(this.faces, 0, updatedFaces, 0, this.faces.length);

        // Copy other mesh's vertices to new array starting from the end of the existing data
        System.arraycopy(otherMesh.vertices, 0, updatedVertices, this.vertices.length, otherMesh.vertices.length);

        // Copy other mesh's faces to new array and adjust indices
        for (int i = 0; i < otherMesh.faces.length; i++) {
            Face oldFace = otherMesh.faces[i];
            Face newFace = new Face(
                    oldFace.vi1 + vertexOffset,
                    oldFace.vi2 + vertexOffset,
                    oldFace.vi3 + vertexOffset
            );
            updatedFaces[this.faces.length + i] = newFace;
        }

        // Update the mesh with the combined contents
        this.vertices = updatedVertices;
        this.faces = updatedFaces;

        if (autoCalculateNormals)
            calculateVertexNormals();

        return this;
    }

    public Bounds calculateBounds()
    {
        Vec3 min = new Vec3(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);
        Vec3 max = new Vec3(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);

        for (Vertex vertex : this.vertices) {
            Vec3 vertPos = vertex.position;

            min.set(Math.min(min.x, vertPos.x), Math.min(min.y, vertPos.y), Math.min(min.z, vertPos.z));
            max.set(Math.max(max.x, vertPos.x), Math.max(max.y, vertPos.y), Math.max(max.z, vertPos.z));
        }

        return new Bounds(min, max);
    }

    public void setVertexScale(float scale)
    {
        for (Vertex vertex : this.vertices) {
            vertex.position.mult(scale);
        }
    }

    public Mesh2 copy() {
        Mesh2 copiedMesh = new Mesh2();

        Vertex[] copiedVertices = new Vertex[this.vertices.length];
        for (int i = 0; i < this.vertices.length; i++) {
            Vertex originalVertex = this.vertices[i];
            Vertex copiedVertex = new Vertex(originalVertex.position.copy(), originalVertex.normal.copy(), 0);
            copiedVertices[i] = copiedVertex;
        }
        copiedMesh.vertices = copiedVertices;

        Face[] copiedFaces = new Face[this.faces.length];
        for (int i = 0; i < this.faces.length; i++) {
            Face originalFace = this.faces[i];
            Face copiedFace = new Face(originalFace.vi1, originalFace.vi2, originalFace.vi3);
            copiedFaces[i] = copiedFace;
        }
        copiedMesh.faces = copiedFaces;

        copiedMesh.material = this.material.copy();
        copiedMesh.autoCalculateNormals = this.autoCalculateNormals;

        copiedMesh.calculateVertexNormals();

        return copiedMesh;
    }
}
