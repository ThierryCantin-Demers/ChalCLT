package ca.ulaval.glo2004.gui.selection;

/**
 * This enum represents the different types of objects that can be selected.
 */
public enum SelectionType {
    SLOPE("Slope"),EXTENSION("Extension"),GABLE("Gable"), WALL("Wall"), ACCESSORY("Accessory"), NONE("None");

    /**
     * The string representation of the selection type.
     */
    private final String val;

    /**
     * Creates a selection type with a string representation.
     * @param val_ : The string representation of the selection type.
     */
    SelectionType(String val_)
    {
        this.val = val_;
    }

    /**
     * Returns the string representation of the selection type.
     * @return : The string representation of the selection type.
     */
    @Override
    public String toString()
    {
        return this.val;
    }
}
