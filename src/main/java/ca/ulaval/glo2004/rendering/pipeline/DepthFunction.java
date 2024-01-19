package ca.ulaval.glo2004.rendering.pipeline;

import ca.ulaval.glo2004.util.math.MathUtils;

public enum DepthFunction {
    NEVER,
    LESS,
    EQUAL,
    LEQUAL,
    GREATER,
    NOTEQUAL,
    GEQUAL,
    ALWAYS;

    public boolean test(float a, float b)
    {
//        return a < b;
// TODO: for now this is more performant, but evaluate the possibility of adding this back in the future
        return switch (this) {
            case NEVER -> false;
            case LESS -> a < b;
            case EQUAL -> Math.abs(a - b) <= MathUtils.FLOAT_ROUNDING_ERROR;
            case LEQUAL -> a <= b;
            case GREATER -> a > b;
            case NOTEQUAL -> Math.abs(a - b) > MathUtils.FLOAT_ROUNDING_ERROR;
            case GEQUAL -> a >= b;
            case ALWAYS -> true;
        };
    }
}
