package ca.ulaval.glo2004.rendering.pipeline;

public enum StencilFunction {
    NEVER,
    LESS,
    LEQUAL,
    GREATER,
    GEQUAL,
    EQUAL,
    NOTEQUAL,
    ALWAYS;

    public static boolean test(int ref, int stencil, int mask, StencilFunction func)
    {
        return switch (func) {
            case NEVER -> false; // Stencil test always fails
            case LESS -> (ref & mask) < (stencil & mask); // Stencil test passes if ref < stencil
            case LEQUAL -> (ref & mask) <= (stencil & mask); // Stencil test passes if ref <= stencil
            case GREATER -> (ref & mask) > (stencil & mask); // Stencil test passes if ref > stencil
            case GEQUAL -> (ref & mask) >= (stencil & mask); // Stencil test passes if ref >= stencil
            case EQUAL -> (ref & mask) == (stencil & mask); // Stencil test passes if ref == stencil
            case NOTEQUAL -> (ref & mask) != (stencil & mask); // Stencil test passes if ref != stencil
            case ALWAYS -> true; // Stencil test always passes
        };
    }
}
