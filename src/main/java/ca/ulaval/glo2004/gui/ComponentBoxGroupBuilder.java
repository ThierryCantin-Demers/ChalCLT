package ca.ulaval.glo2004.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * A builder for a JPanel with a BoxLayout.
 */
public class ComponentBoxGroupBuilder {
    /**
     * An enum representing the different types of BoxLayouts.
     * This enum is basically a copy of the BoxLayout class constants, but with an enum instead of an int.
     */
    public enum BoxLayoutType
    {
        X_AXIS(BoxLayout.X_AXIS), Y_AXIS(BoxLayout.Y_AXIS), LINE_AXIS(BoxLayout.LINE_AXIS), PAGE_AXIS(BoxLayout.PAGE_AXIS);

        private final int val;
        BoxLayoutType(int val_)
        {
            this.val = val_;
        }
    }

    /**
     * The JPanel that is being built.
     */
    private final JPanel box;

    /**
     * The current component group that is being built.
     * Components are added to this group until a new group is added.
     */
    private JPanel componentGroup;

    /**
     * Creates a new builder with the default layout (BoxLayout.Y_AXIS).
     */
    public ComponentBoxGroupBuilder()
    {
        this.box = new JPanel();
        this.box.setLayout(new BoxLayout(this.box, BoxLayoutType.Y_AXIS.val));
    }

    /**
     * Creates a new builder with the specified layout.
     * @param boxLayout_ : The layout to use.
     */
    public ComponentBoxGroupBuilder(BoxLayoutType boxLayout_)
    {
        this.box = new JPanel();
        this.box.setLayout(new BoxLayout(this.box, boxLayout_.val));
    }

    /**
     * Adds a new component group to the builder.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder addGroup()
    {
        return addGroup(BoxLayoutType.X_AXIS);
    }

    /**
     * Adds a new component group to the builder with the specified layout.
     * @param boxLayout_ : The layout to use.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder addGroup(BoxLayoutType boxLayout_)
    {
        if(this.componentGroup != null) {
            this.box.add(componentGroup);
        }

        this.componentGroup = new JPanel();
        this.componentGroup.setLayout(new BoxLayout(this.componentGroup, boxLayout_.val));

        return this;
    }

    /**
     * Adds a new component group to the builder with the base group layout (BoxLayoutType.X_AXIS) and title.
     * @param groupTitle_ : The title of the group.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder addGroup(String groupTitle_)
    {
        return addGroup(BoxLayoutType.X_AXIS, groupTitle_);
    }

    /**
     * Adds a new component group to the builder with the specified layout and title.
     * @param boxLayout_ : The layout to use.
     * @param groupTitle_ : The title of the group.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder addGroup(BoxLayoutType boxLayout_, String groupTitle_)
    {
        if(this.componentGroup != null) {
            this.box.add(componentGroup);
        }

        this.componentGroup = new JPanel();
        this.componentGroup.setLayout(new BoxLayout(this.componentGroup, boxLayout_.val));

        this.addComponent(new JLabel(groupTitle_));

        return this;
    }

    /**
     * Adds a component to the current component group.
     * @param component_ : The component to add.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder addComponent(Component component_)
    {
        this.componentGroup.add(component_);

        return this;
    }

    /**
     * Adds two components to the current group: 1 JTextField specified in parameter
     * and 1 JLabel with the text being the tooltip text of the JTextField
     * @param textField_ : The text field to add.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder addTextFieldLabelPair(JTextField textField_)
    {
        this.addComponent(textField_);
        this.addComponent(new JLabel(textField_.getToolTipText()));

        return this;
    }

    /**
     * Sets the preferred size of the current component group.
     * @param size_ : The size to set.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder setGroupPreferredSize(Dimension size_)
    {
        this.componentGroup.setPreferredSize(size_);

        return this;
    }

    /**
     * Sets the preferred size of the current component group.
     * @param width_ : The width to set.
     * @param height_ : The height to set.
     * @return The builder.
     */
    public ComponentBoxGroupBuilder setGroupPreferredSize(int width_, int height_)
    {
        return this.setGroupPreferredSize(new Dimension(width_, height_));
    }

    /**
     * Generates the JPanel.
     * Adds the current (and last) component group to the panel if it is not already added.
     * @return The built JPanel.
     */
    public JPanel generate()
    {
        if(this.componentGroup != null && Arrays.stream(this.box.getComponents()).noneMatch((c) -> c.equals(this.componentGroup)))
        {
            this.box.add(this.componentGroup);
        }

        return this.box;
    }
}
