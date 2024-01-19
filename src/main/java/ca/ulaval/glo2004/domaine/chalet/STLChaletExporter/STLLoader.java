package ca.ulaval.glo2004.domaine.chalet.STLChaletExporter;

import ca.ulaval.glo2004.util.math.Vec3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class STLLoader {
    public static List<STLTriangle> readSTLFile(String filePath) {
        List<STLTriangle> triangles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            STLTriangle currentTriangle = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("facet normal")) {
                    // Parse normal vector
                    String[] parts = line.split("\\s+");
                    float nx = Float.parseFloat(parts[2]);
                    float ny = Float.parseFloat(parts[3]);
                    float nz = Float.parseFloat(parts[4]);
                    Vec3 normal = new Vec3(nx, ny, nz);

                    currentTriangle = new STLTriangle(null, null, null);
                } else if (line.startsWith("vertex")) {
                    // Parse vertex coordinates
                    String[] parts = line.split("\\s+");
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    Vec3 vertex = new Vec3(x, y, z);

                    if (currentTriangle != null) {
                        // Assign vertices to the current triangle
                        if (currentTriangle.v1 == null) {
                            currentTriangle.v1 = vertex;
                        } else if (currentTriangle.v2 == null) {
                            currentTriangle.v2 = vertex;
                        } else if (currentTriangle.v3 == null) {
                            currentTriangle.v3 = vertex;
                            // Add the completed triangle to the list
                            triangles.add(currentTriangle);
                            currentTriangle = null;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return triangles;
    }
}
