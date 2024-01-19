package ca.ulaval.glo2004.rendering;

public class Face {

    public int vi1, vi2, vi3;

    public Face(int vi1, int vi2, int vi3)
    {
        this.vi1 = vi1;
        this.vi2 = vi2;
        this.vi3 = vi3;
    }

    public int[] asArray()
    {
        return new int[]{vi1, vi2, vi3};
    }
}
