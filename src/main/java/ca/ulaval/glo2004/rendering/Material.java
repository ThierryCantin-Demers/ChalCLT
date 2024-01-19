package ca.ulaval.glo2004.rendering;

import ca.ulaval.glo2004.rendering.pipeline.RenderStates;
import ca.ulaval.glo2004.rendering.shaders.BasicShader;
import ca.ulaval.glo2004.rendering.shaders.Shader;

public class Material {
    public int baseColor;
    //public Texture mainTexture;
    public Shader shader;

    private RenderStates renderStates;

    private RenderPass renderPass = RenderPass.OPAQUE;

    private OnApplyMaterial onApplyMaterial = (material) -> {};

    public Material()
    {
        this.shader = new BasicShader();
        this.renderStates = new RenderStates();
    }

    public void setOnApplyMaterial(OnApplyMaterial onApplyMaterial) {
        this.onApplyMaterial = onApplyMaterial;
    }

    public void setRenderStates(RenderStates renderStates) {
        this.renderStates = renderStates;
    }

    public RenderStates getRenderStates() {
        return renderStates;
    }

    public void setTwoSided(boolean twoSided)
    {
        this.renderStates.cullFace = !twoSided;
    }

    public void enableZTest(boolean enabled)
    {
        this.renderStates.depthTest = enabled;
    }

    void enableZWrite(boolean enabled)
    {
        this.renderStates.depthMask = enabled;
    }

    void enableBlend(boolean enabled)
    {
        this.renderStates.blend = enabled;
    }

    public RenderPass getRenderPass()
    {
        return renderPass;
    }

    public void setRenderPass(RenderPass renderPass)
    {
        this.renderPass = renderPass;
    }

    public void applyMaterial()
    {
        this.onApplyMaterial.onApplyMaterial(this);
    }

    public Material copy() {
        Material material = new Material();

        material.baseColor = this.baseColor;
        material.shader = this.shader;
        material.renderStates = new RenderStates(this.renderStates);

        material.onApplyMaterial = this.onApplyMaterial;

        return material;
    }
}
