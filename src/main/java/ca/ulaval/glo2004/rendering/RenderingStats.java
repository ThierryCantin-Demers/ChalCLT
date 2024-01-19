package ca.ulaval.glo2004.rendering;

public class RenderingStats {

    int nbTriangles = 0;
    int nbLines = 0;
    int nbPixelOperations = 0;

    public RenderingStats()
    {

    }

    public void reset()
    {
        nbTriangles = 0;
        nbLines = 0;
        nbPixelOperations = 0;
    }

    @Override
    public String toString() {
        return "Triangles: " + nbTriangles + ",\n Lines: " + nbLines + ",\n Pixels: " + nbPixelOperations;
    }
}
