package ca.ulaval.glo2004.rendering.pipeline;

public enum StencilOperation {
    KEEP, // Keep current value
    ZERO, // Set stencil value to 0
    REPLACE, // Replace stencil value to ref
    INCR, // Increment the current stencil buffer value. Clamps to maximum (255)
    INCR_WRAP, // Increment the current stencil buffer value with wrapping.
    DECR, // Decrement the current stencil buffer value. Clamps to minimum (0)
    DECR_WRAP, // Decrement the current stencil buffer value with wrapping.
    INVERT // Bitwise invert the current stencil buffer value
}
