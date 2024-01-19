package ca.ulaval.glo2004.rendering.utils;

import ca.ulaval.glo2004.rendering.Texture;

public class FixedTextureQueue {

    private Texture[] array;
    private int size;

    public FixedTextureQueue(int capacity) {
        array = new Texture[capacity];
        size = 0;
    }

    public void push(Texture element) {
        if (size < array.length) {
            size++;
        }
        for (int i = size - 1; i > 0; i--) {
            array[i] = array[i - 1];
        }
        array[0] = element;
    }

    public Texture[] getData()
    {
        return array;
    }
}
