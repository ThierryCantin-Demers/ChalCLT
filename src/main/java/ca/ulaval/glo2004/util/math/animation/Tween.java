package ca.ulaval.glo2004.util.math.animation;

import ca.ulaval.glo2004.util.math.MathUtils;

public class Tween {
    private float currentTime;
    private float duration;
    private EasingFunction easingFunction;

    public Tween(float duration, EasingFunction easingFunction) {
        this.duration = duration;
        this.easingFunction = easingFunction;
        this.currentTime = 0;
    }

    public float update(float deltaTime) {
        currentTime += deltaTime;
        if (currentTime > duration) {
            currentTime = duration;
        }
        if (currentTime <= 0)
        {
            currentTime = 0;
        }

        float progress = currentTime / duration;

        return easingFunction.ease(progress);
    }

    public void reset()
    {
        this.currentTime = 0;
    }
}