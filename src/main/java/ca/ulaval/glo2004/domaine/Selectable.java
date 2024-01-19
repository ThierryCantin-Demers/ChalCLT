package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.gui.selection.SelectionType;

import java.util.UUID;

/**
 * This class represents an overview an object that can be selected.
 */
public final class Selectable implements Cloneable {
    /**
     * The UUID of the object.
     */
    private final UUID uuid;

    /**
     * The name of the object.
     */
    private final String name;

    /**
     * The type of the object.
     */
    private final SelectionType selectionType;

    /**
     * Creates a new default Selectable.
     */
    public Selectable() {
        this.uuid = null;
        this.name = "";
        this.selectionType = SelectionType.NONE;
    }

    /**
     * Creates a new Selectable.
     * @param uuid_ : The UUID of the object.
     * @param name_ : The name of the object.
     * @param selectionType : The type of the object.
     */
    public Selectable(UUID uuid_, String name_, SelectionType selectionType) {
        this.uuid = uuid_;
        this.name = name_;
        this.selectionType = selectionType;
    }

    /**
     * Gets the UUID of the object.
     * @return The UUID of the object.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Gets the name of the object.
     * @return The name of the object.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the type of the object.
     * @return The type of the object.
     */
    public SelectionType getSelectionType() {
        return selectionType;
    }

    /**
     * Checks if the object is equal to another object.
     * @param obj : The object to compare to.
     * @return True if the object is equal to the other object, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    /**
     * Gets the string representation of the object.
     * @return The string representation of the object.
     */
    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Object clone() {
        Selectable copy;

        try {
            copy = (Selectable) super.clone();
        } catch (CloneNotSupportedException e) {
            copy = new Selectable();
        }

        return copy;
    }
}
