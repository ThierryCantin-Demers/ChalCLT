package ca.ulaval.glo2004.rendering;

public class Mesh {

    public Triangle[] triangles;
    public int color;

    public Mesh() {
        this.triangles = new Triangle[]{};
    }
    public Mesh(Triangle[] triangles)
    {
        this.triangles = triangles;
    }

    public Mesh(Triangle[] triangles, int color)
    {
        this.triangles = triangles;
        this.color = color;
    }

    public void setColor(int color)
    {
        this.color = color;

        for (Triangle triangle:
             triangles) {
            triangle.color = color;
        }
    }
}
