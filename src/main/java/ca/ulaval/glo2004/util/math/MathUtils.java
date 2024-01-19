package ca.ulaval.glo2004.util.math;

import java.nio.ByteBuffer;

public class MathUtils {

    static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits

    public static int gcd(int a, int b) {
        if (b==0) return a;
        return gcd(b,a%b);
    }

    public static float lerp(float from, float to, float t)
    {
        return from + t * (to - from);
    }

    public static float[] lerp(float[] from, float[] to, float t)
    {
        if (from.length != to.length) {
            throw new IllegalArgumentException("Input arrays must be of the same length");
        }

        int dimension = from.length;
        float[] interpolated = new float[dimension];

        for (int i = 0; i < dimension; i++) {
            interpolated[i] = from[i] * (1 - t) + to[i] * t;
        }

        return interpolated;
    }

    public static void perspectiveDivide(ByteBuffer buffer, float perspectiveValue) {
        buffer.rewind();

        while (buffer.hasRemaining()) {
            float value = buffer.getFloat();
            float perspectiveDivided = value * perspectiveValue;
            buffer.putFloat(perspectiveDivided);
        }

        buffer.flip();
    }

    public static void interpolateBarycentric(ByteBuffer d1, ByteBuffer d2, ByteBuffer d3, float u, float v, float w, float perspCorrection, ByteBuffer out) {
        int numFloats = d1.capacity() / Float.BYTES;
        if (numFloats != d2.capacity() / Float.BYTES || numFloats != d3.capacity() / Float.BYTES || numFloats != out.capacity() / Float.BYTES) {
            throw new IllegalArgumentException("Input and output buffers must have the same capacity");
        }

        d1.rewind();
        d2.rewind();
        d3.rewind();
        out.rewind();

        for (int i = 0; i < numFloats; i++) {
            float val1 = d1.getFloat();
            float val2 = d2.getFloat();
            float val3 = d3.getFloat();

            float interpolatedValue = (u * val1 + v * val2 + w * val3) * perspCorrection;

            out.putFloat(interpolatedValue);
        }
    }

    public static int replaceByte(int index, int value, byte replaceByte)
    {
        return (value & ~(0xFF << (index * 8))) | (replaceByte << (index * 8));
    }

    public static float clampf(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    public static float wrap(float value, float max) {
        if (value < 0) {
            return max - (Math.abs(value) % max);
        }
        return value % max;
    }

    public static int wrap(int value, int max) {
        if (value < 0) {
            return max - (Math.abs(value) % max);
        }
        return value % max;
    }

    public static int fastFloor(float x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    public static float snapToMultiple(float value, float multiple) {
        return Math.round(value / multiple) * multiple;
    }

    public static float snapToMultiple(float value, float multiple, float offset) {
        float correctedValue = value - offset;
        float snappedValue = Math.round(correctedValue / multiple) * multiple;
        return snappedValue + offset;
    }

    static public boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    public static Pair<Integer, Integer> findClosestFraction(double decimalNumber_, int maxDenominator_) {
        int bestNumerator = 0;
        int bestDenominator = 1;
        double bestError = Math.abs(decimalNumber_);

        for (int denominator = 1; denominator <= maxDenominator_; denominator++) {
            int numerator = (int) (decimalNumber_ * denominator);
            double error = Math.abs(decimalNumber_ - (double) numerator / denominator);

            if (error < bestError) {
                bestNumerator = numerator;
                bestDenominator = denominator;
                bestError = error;
            }
        }

        return new Pair<>(bestNumerator, bestDenominator);
    }
}
