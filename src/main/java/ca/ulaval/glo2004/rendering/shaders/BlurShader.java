package ca.ulaval.glo2004.rendering.shaders;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.pipeline.VSOutput;
import ca.ulaval.glo2004.rendering.pipeline.Varyings;
import ca.ulaval.glo2004.rendering.pipeline.VertexAttributes;
import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

public class BlurShader extends Shader {
    static final int IN_POSITION_OFFSET = 0, IN_POSITION_SIZE = 3;
    static final int IN_UV_OFFSET = 3, IN_UV_SIZE = 2;
    static final int IN_NORMAL_OFFSET = 5, IN_NORMAL_SIZE = 3;
    static final int IN_COLOR_OFFSET = 8, IN_COLOR_SIZE = 4;

    static final int OUT_UV_OFFSET = 0, OUT_UV_SIZE = 2;
    static final int OUT_NORMAL_OFFSET = 2, OUT_NORMAL_SIZE = 3;
    static final int OUT_COLOR_OFFSET = 5, OUT_COLOR_SIZE = 4;

    Vec3 globalLightDirection = new Vec3(0,1,-1).normalized();
    public Texture[] samplers = new Texture[8];
    public Texture depthTexture;

    @Override
    public VSOutput vertex(Vertex vertex) {
        VSOutput v = new VSOutput();
        v.position = projectionMatrix.transform(viewMatrix.transform(modelMatrix.transform(vertex.position.toVec4())));
        v.uv = vertex.uv;
        v.normal = normalMatrix.transform(vertex.normal.toVec4()).toVec3().normalized();
        v.color = baseColor;
        return v;
    }

    @Override
    public void vertex(VertexAttributes in, Vec4 gl_Position, VertexAttributes out) {
        Vec3 position = in.getFloat3(IN_POSITION_OFFSET);//in.getAttribute(0, DataType.FLOAT3);
        Vec2 uv = in.getFloat2(IN_UV_OFFSET);//in.getAttribute(3, DataType.FLOAT2);
        Vec3 normal = in.getFloat3(IN_NORMAL_OFFSET);//in.getAttribute(5, DataType.FLOAT3);
//        Vec4 color = in.getFloat4(IN_COLOR_OFFSET);//in.getAttribute(5, DataType.FLOAT3);

        Vec4 newPosition = projectionMatrix.transform(viewMatrix.transform(modelMatrix.transform(position.toVec4())));
        Vec3 newNormal = normalMatrix.transform(normal.toVec4()).toVec3().normalized();
        out.putFloat2(OUT_UV_OFFSET, uv);//out_uv.setAttribute(out, uv);
        out.putFloat3(OUT_NORMAL_OFFSET, newNormal);//out_normal.setAttribute(out, newNormal.toVec3());
        out.putFloat4(OUT_COLOR_OFFSET, ColorUtils.vec4(baseColor));

        gl_Position.set(newPosition);
    }

    @Override
    public int fragment(Vec4 fragCoord, Varyings varyings) {

        float shading = Math.max(0.1f, Vec3.dot(globalLightDirection, varyings.normal));

        shading = (float) Math.sqrt(shading);

        float red = ColorUtils.getRed(varyings.color) * shading;
        float green = ColorUtils.getGreen(varyings.color) * shading;
        float blue = ColorUtils.getBlue(varyings.color) * shading;

        int newRed, newGreen, newBlue;
        {

            // Apply simple blurring by averaging nearby pixels
            int blurRadius = 0; // Adjust blur radius as needed
            int avgRed = 0, avgGreen = 0, avgBlue = 0;
            int count = 0;

            int x = (int) fragCoord.x;
            int y = (int) fragCoord.y;
            for (int i = -blurRadius; i <= blurRadius; i++) {
                for (int j = -blurRadius; j <= blurRadius; j++) {
                    int pixelX = x + i;
                    int pixelY = y + j;

                    if (pixelX >= 0 && pixelX < samplers[0].getWidth() && pixelY >= 0 && pixelY < samplers[0].getHeight()) {
                        int pixel = this.samplers[0].getPixel(pixelX, pixelY);
                        avgRed += (pixel >> 16) & 0xFF;
                        avgGreen += (pixel >> 8) & 0xFF;
                        avgBlue += pixel & 0xFF;
                        count++;
                    }
                }
            }

            newRed = avgRed / count;
            newGreen = avgGreen / count;
            newBlue = avgBlue / count;
        }

        int blurredColor = ColorUtils.rgba((int) newRed, (int) newGreen, (int) newBlue, 255);
        int originalColor = ColorUtils.rgba((int) red, (int) green, (int) blue, 255);
        return ColorUtils.lerp(blurredColor, originalColor, ColorUtils.getAlpha(varyings.color)/255f);
    }

    @Override
    public VertexAttributes createInputVertexAttribs() {
        return new VertexAttributes(IN_POSITION_SIZE+IN_UV_SIZE+IN_NORMAL_SIZE+IN_COLOR_SIZE);
    }

    @Override
    public VertexAttributes createOutputVertexAttribs() {
        return new VertexAttributes(OUT_UV_SIZE+OUT_NORMAL_SIZE+OUT_COLOR_SIZE);
    }

    @Override
    public void fragment(Vec4 gl_FragCoord, VertexAttributes out, Vec4 gl_FragColor) {

        Vec2 vTexcoord = out.getFloat2(OUT_UV_OFFSET);
        Vec3 vNormal = out.getFloat3(OUT_NORMAL_OFFSET);
        Vec4 vColor = out.getFloat4(OUT_COLOR_OFFSET);

        float shading = Math.max(0.1f, Vec3.dot(globalLightDirection, vNormal));

        shading = (float) Math.sqrt(shading);

        float red = vColor.x * shading;
        float green = vColor.y * shading;
        float blue = vColor.z * shading;

        float newRed, newGreen, newBlue;
        {

            // Apply simple blurring by averaging nearby pixels
            int blurRadius = 2; // Adjust blur radius as needed
            float avgRed = 0, avgGreen = 0, avgBlue = 0;
            int count = 0;

            int x = (int) gl_FragCoord.x;
            int y = (int) gl_FragCoord.y;
            for (int i = -blurRadius; i <= blurRadius; i++) {
                for (int j = -blurRadius; j <= blurRadius; j++) {
                    int pixelX = x + i;
                    int pixelY = y + j;

                    if (pixelX >= 0 && pixelX < samplers[0].getWidth() && pixelY >= 0 && pixelY < samplers[0].getHeight()) {
                        Vec4 pixel = ColorUtils.vec4(this.samplers[0].getPixel(pixelX, pixelY));
                        avgRed += pixel.x;
                        avgGreen += pixel.y;
                        avgBlue += pixel.z;
                        count++;
                    }
                }
            }

            newRed = avgRed / count;
            newGreen = avgGreen / count;
            newBlue = avgBlue / count;
        }


        Vec4 blurredColor = new Vec4(newRed, newGreen, newBlue, 1.0f);
        Vec4 originalColor = new Vec4(red, green, blue, 1.0f);
        Vec4 fragColor = Vec4.lerp(Vec4.mult(blurredColor, 0.8f), originalColor, vColor.w);//ColorUtils.lerp(blurredColor, originalColor, ColorUtils.getAlpha(varyings.color)/255f);

//        // Get the depth buffer value at this pixel.
//        float zOverW = tex2D(depthTexture, texCoord);
//        // H is the viewport position at this pixel in the range -1 to 1.
//        float4 H = float4(texCoord.x * 2 - 1, (1 - texCoord.y) * 2 - 1, zOverW, 1);
//        // Transform by the view-projection inverse.
//        float4 D = mul(H, g_ViewProjectionInverseMatrix);
//        // Divide by w to get the world position.
//        float4 worldPos = D / D.w;
//
//        // Current viewport position
//        float4 currentPos = H;
//        // Use the world position, and transform by the previous view-
//        // projection matrix.
//        float4 previousPos = mul(worldPos, g_previousViewProjectionMatrix);
//        // Convert to nonhomogeneous points [-1,1] by dividing by w. previousPos /= previousPos.w;
//        // Use this frame's position and last frame's to compute the pixel
//        // velocity.
//        float2 velocity = (currentPos - previousPos)/2.f;


//        Vec4 accumulatedColor = new Vec4(0.0f, 0.0f, 0.0f, 0.0f);
//
//        Vec2 fragCoord = new Vec2(gl_FragCoord.x, gl_FragCoord.y);
//        float blurStrength = 0.05f;
//
//        int i;
//
//        Vec4 texelCurrentFrame = ColorUtils.vec4(samplers[0].getPixel((int) gl_FragCoord.x, (int) gl_FragCoord.y));
//        for (i = 0; i < samplers.length; i++) {
//            float weight = (float)(i + 1) / (float)(samplers.length); // Weighted contribution of each frame
//            // Assuming previousTexture is the texture from the previous frame
//            Vec4 texelPreviousFrame = ColorUtils.vec4(samplers[i].getPixel((int) gl_FragCoord.x, (int) gl_FragCoord.y));
//
//            Vec2 offset = new Vec2((texelCurrentFrame.x - texelPreviousFrame.x)*blurStrength, (texelCurrentFrame.y - texelPreviousFrame.y)*blurStrength) ;
//
//            Vec2 sampleTexcoord = Vec2.add(vTexcoord, offset);
//
//            Vec4 sampledColor = ColorUtils.vec4(samplers[i].getPixel((int) sampleTexcoord.x, (int) sampleTexcoord.y));
//            accumulatedColor.add(Vec4.mult(sampledColor, weight));
//        }
//        accumulatedColor.mult((float) 1 /i);

//        gl_FragColor.set(Vec4.lerp(accumulatedColor, new Vec4(0f,0f,0f,1f), 0.10f));

//        if (this.depthTexture != null){
//            float depth = (((AWTFloatTexture)this.depthTexture).sampleFloat(vTexcoord)+2)/2;
//            gl_FragColor.set(new Vec4(depth, depth, depth, 1f));
//        }

        gl_FragColor.set(fragColor);
    }
}
