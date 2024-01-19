package ca.ulaval.glo2004.util.math.animation;

public class EaseInEaseOut implements EasingFunction {
    @Override
    public float ease(float x) {
        return x == 0
                ? 0
                : (float) (x == 1
                ? 1
                : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2
                : (2 - Math.pow(2, -20 * x + 10)) / 2);

    }
}