package ca.ulaval.glo2004.domaine;

public enum Orientation {
    FRONT("Front", new int[]{1,0,0}),
    LEFT("Left", new int[]{0,0,-1}),
    BACK("Back", new int[]{-1,0,0}),
    RIGHT("Right", new int[]{0,0,1}),
    TOP("Top", new int[]{0,1,0});

    private final String val;

    /**
     * The normal is with respect to the middle of the chalet
     */
    private final int[] normal;

    Orientation(String val_, int[] normal_)
    {
        this.val = val_;
        this.normal = normal_;
    }

    @Override
    public String toString() {
        return this.val;
    }

    public int[] getNormal()
    {
        return normal;
    }
}
