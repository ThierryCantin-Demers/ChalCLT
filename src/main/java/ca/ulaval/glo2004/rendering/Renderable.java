package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.pipeline.RenderAPI;

public interface Renderable {
    void render(RenderAPI renderer, Camera mainCamera);
}
