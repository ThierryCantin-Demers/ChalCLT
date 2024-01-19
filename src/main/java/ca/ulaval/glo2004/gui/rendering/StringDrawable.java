package ca.ulaval.glo2004.gui.rendering;

import ca.ulaval.glo2004.util.math.Vec3;

import java.awt.*;

public class StringDrawable {
    Vec3 screenPosition;
    String text;
    int argb;

    public StringDrawable(Vec3 screenPosition, String text, int argb)
    {
        this.screenPosition = screenPosition;
        this.text = text;
        this.argb = argb;
    }

    public StringDrawable(Vec3 screenPosition, String text)
    {
        this(screenPosition, text, 0xFFFFFFFF);
    }
}
