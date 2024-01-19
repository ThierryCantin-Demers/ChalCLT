package ca.ulaval.glo2004.rendering.pipeline;

import ca.ulaval.glo2004.rendering.*;
import ca.ulaval.glo2004.rendering.rasterizing.FloatRasterizer;
import ca.ulaval.glo2004.rendering.shaders.Shader;
import ca.ulaval.glo2004.rendering.utils.ColorUtils;
import ca.ulaval.glo2004.rendering.utils.GeometryUtils;
import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec2;
import ca.ulaval.glo2004.util.math.Vec3;
import ca.ulaval.glo2004.util.math.Vec4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RenderAPI {
    private IView view;

    // TODO: unneeded
    private final FloatRasterizer rasterizer;

    private boolean earlyZ = true;

    private RenderStates renderState;

    private Vertex[] vertexBuffer = null;
    private VertexAttributes[] vertexBuffer_ = null;
    private Face[] indexBuffer = null;

    private Shader shaderProgram = null;

    private VSOutput[] vsOutput;
    private VertexHolder[] vsOutput_;

    private Vec4 viewScale = new Vec4(1,1,1,1);

    public Camera camera;

    List<VSOutput[]> vsOutputTriangles;
    List<PrimitiveHolder> vsOutputTriangles_;

    public Vec3 mousePos;


    public RenderAPI()
    {
        this.rasterizer = new FloatRasterizer(new RenderingStats());
    }

    public void setRenderState(RenderStates renderState) {
        this.renderState = renderState;
    }

    public void setShaderProgram(Shader shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    public void setEarlyZ(boolean earlyZ) {
        this.earlyZ = earlyZ;
    }

    public void setVertexBuffer(Vertex[] vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }
    public void setVertexBuffer(VertexAttributes[] vertexBuffer) {
        this.vertexBuffer_ = vertexBuffer;
    }

    public void setIndexBuffer(Face[] indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public void beginRenderPass(IView framebuffer, ClearStates clearStates)
    {
        this.view = framebuffer;

        if (clearStates.colorFlag)
        {
            this.view.clearColorBuffer(clearStates.clearColor);
        }

        if (clearStates.depthFlag)
        {
            this.view.clearDepthBuffer(clearStates.clearDepth);
        }

        if (clearStates.stencilFlag)
        {
            this.view.clearStencilBuffer(clearStates.clearStencil);
        }
    }

    /**
     * TODO: deprecated pipeline, we should use {@link VertexAttributes}
     * This will be replaced by draw_ and all of the underscore functions
     */
    public void draw()
    {
        if (this.view == null)
        {
            return;
        }

        processVertices();
        processPrimitiveAssembly();
        processClipSpaceToScreenSpace();
        processRasterization();
    }

    public void draw_()
    {
        if (this.view == null)
        {
            return;
        }
//        this.rasterizer.bindView(view);

        processVertices_();
        processPrimitiveAssembly_();
        processClipSpaceToScreenSpace_();
        processRasterization_();
    }

    private void processVertices_()
    {
        this.vsOutput_ = new VertexHolder[this.vertexBuffer_.length];
        for (int i = 0; i < this.vertexBuffer_.length; i++) {
            VertexHolder out = new VertexHolder(new Vec4(0f,0f,0f,0f), shaderProgram.createOutputVertexAttribs());
            shaderProgram.vertex(this.vertexBuffer_[i], out.position, out.verticesAttributes);
            this.vsOutput_[i] = out;
        }
    }

    private void processVertices()
    {
        this.vsOutput = new VSOutput[this.vertexBuffer.length];
        for (int i = 0; i < this.vertexBuffer.length; i++) {
            this.vsOutput[i] = shaderProgram.vertex(this.vertexBuffer[i]);
        }
    }

    private void processPrimitiveAssembly_()
    {
        this.vsOutputTriangles_ = new ArrayList<>();
        for (Face face: this.indexBuffer) {
            PrimitiveHolder trianglePrimitive = new PrimitiveHolder(3);
            int[] indices = face.asArray();
            for (int j = 0; j < 3; j++) {
                trianglePrimitive.vertices[j] = this.vsOutput_[indices[j]];
            }

            VertexHolder v1 = trianglePrimitive.vertices[0];
            VertexHolder v2 = trianglePrimitive.vertices[1];
            VertexHolder v3 = trianglePrimitive.vertices[2];
//            v1.position.w = -v1.position.w;
//            v2.position.w = -v2.position.w;
//            v3.position.w = -v3.position.w;
            // Cull
            if (renderState.cullPlanes)
            {
                if (v1.position.x > v1.position.w &&
                        v2.position.x > v2.position.w &&
                        v3.position.x > v3.position.w)
                    continue;

                if (v1.position.x < -v1.position.w &&
                        v2.position.x < -v2.position.w &&
                        v3.position.x < -v3.position.w)
                    continue;

                if (v1.position.y > v1.position.w &&
                        v2.position.y > v2.position.w &&
                        v3.position.y > v3.position.w)
                    continue;

                if (v1.position.y < -v1.position.w &&
                        v2.position.y < -v2.position.w &&
                        v3.position.y < -v3.position.w)
                    continue;

                if (v1.position.z > v1.position.w &&
                        v2.position.z > v2.position.w &&
                        v3.position.z > v3.position.w)
                    continue;
            }

//            if (v1.position.z < -v1.position.w &&
//                    v2.position.z < -v2.position.w &&
//                    v3.position.z < -v3.position.w)
//                continue;


            if (v1.position.z > v1.position.w)
            {
                if (v2.position.z > v2.position.w)
                    clipRoutine2_(v1, v2, v3);
                else if (v3.position.z > v3.position.w)
                    clipRoutine2_(v1, v3, v2);
                else
                    clipRoutine1_(v1, v2, v3);
            }
            else if (v2.position.z > v2.position.w)
            {
                if (v3.position.z > v3.position.w)
                    clipRoutine2_(v2, v3, v1);
                else
                    clipRoutine1_(v2, v1, v3);
            }
            else if (v3.position.z > v3.position.w)
                clipRoutine1_(v3, v1, v2);
            else // no near clipping necessary
                this.vsOutputTriangles_.add(trianglePrimitive);
        }

        vsOutputTriangles_.sort((t1, t2) -> {
            float z1 = (t1.vertices[0].position.z + t1.vertices[1].position.z + t1.vertices[2].position.z) / 3.0f;
            float z2 = (t2.vertices[0].position.z + t2.vertices[1].position.z + t2.vertices[2].position.z) / 3.0f;

            return Float.compare(z2, z1);
        });
    }

    private void processPrimitiveAssembly()
    {
        this.vsOutputTriangles = new ArrayList<>();
        for (Face face: this.indexBuffer) {
            VSOutput[] trianglePrimitive = new VSOutput[3];
            int[] indices = face.asArray();
            for (int j = 0; j < 3; j++) {
                int i = indices[j];
                trianglePrimitive[j] = this.vsOutput[i];
            }

            VSOutput v1 = trianglePrimitive[0];
            VSOutput v2 = trianglePrimitive[1];
            VSOutput v3 = trianglePrimitive[2];

            // Cull
            if (v1.position.x > v1.position.w &&
                    v2.position.x > v2.position.w &&
                    v3.position.x > v3.position.w)
                continue;

            if (v1.position.x < -v1.position.w &&
                    v2.position.x < -v2.position.w &&
                    v3.position.x < -v3.position.w)
                continue;

            if (v1.position.y > v1.position.w &&
                    v2.position.y > v2.position.w &&
                    v3.position.y > v3.position.w)
                continue;

            if (v1.position.y < -v1.position.w &&
                    v2.position.y < -v2.position.w &&
                    v3.position.y < -v3.position.w)
                continue;

            if (v1.position.z > v1.position.w &&
                    v2.position.z > v2.position.w &&
                    v3.position.z > v3.position.w)
                continue;
//
//
//            if (v1.position.z < 0.0f &&
//                    v2.position.z < 0.0f &&
//                    v3.position.z < 0.0f)
//                continue;


            // TODO FIX TRIANGLE CLIPPING NOT WORKING RIGHT NOW
            if (v1.position.z > v1.position.w)
            {
                if (v2.position.z > v2.position.w)
                    clipRoutine2(v1, v2, v3);
                else if (v3.position.z > v3.position.w)
                    clipRoutine2(v1, v3, v2);
                else
                    clipRoutine1(v1, v2, v3);
            }
            else if (v2.position.z > v2.position.w)
            {
                if (v3.position.z > v3.position.w)
                    clipRoutine2(v2, v3, v1);
                else
                    clipRoutine1(v2, v1, v3);
            }
            else if (v3.position.z > v3.position.w)
                clipRoutine1(v3, v1, v2);
            else // no near clipping necessary
                this.vsOutputTriangles.add(trianglePrimitive);
        }

        vsOutputTriangles.sort((t1, t2) -> {
            float z1 = (t1[0].position.z + t1[1].position.z + t1[2].position.z) / 3.0f;
            float z2 = (t2[0].position.z + t2[1].position.z + t2[2].position.z) / 3.0f;

            return Float.compare(z2, z1);
        });
    }

    private void clipRoutine1_(VertexHolder v1, VertexHolder v2, VertexHolder v3)
    {
        float alphaA = (v1.position.z - v1.position.w) / (v2.position.w - v1.position.w);
        float alphaB = (v1.position.z - v1.position.w) / (v3.position.w - v1.position.w);

        VertexHolder v0a = VertexHolder.interpolate(v1, v2, alphaA);
        VertexHolder v0b = VertexHolder.interpolate(v1, v3, alphaB);

        // new vertices need to be perspective divided and transformed to screen space
        // as they are not in the vertex list
        processVertexClipToScreenSpace(v0a);

        processVertexClipToScreenSpace(v0b);

        this.vsOutputTriangles_.add(new PrimitiveHolder(new VertexHolder[]{ v0a, v2, v3 }));
        this.vsOutputTriangles_.add(new PrimitiveHolder(new VertexHolder[]{ v0b, v0a, v3 }));
    }

    private void clipRoutine2_(VertexHolder v1, VertexHolder v2, VertexHolder v3)
    {
        float alphaA = (v1.position.z - v1.position.w) / (v3.position.w - v1.position.w);
        float alphaB = (v2.position.z - v2.position.w) / (v3.position.w - v2.position.w);

        v1 = VertexHolder.interpolate(v1, v2, alphaA);
        v2 = VertexHolder.interpolate(v2, v3, alphaB);

        // new vertices need to be perspective divided and transformed to screen space
        // as they are not in the vertex list
        processVertexClipToScreenSpace(v1);

        processVertexClipToScreenSpace(v2);


        this.vsOutputTriangles_.add(new PrimitiveHolder(new VertexHolder[]{ v1, v2, v3 }));
    }

    private void clipRoutine1(VSOutput v1, VSOutput v2, VSOutput v3)
    {
        float alphaA = (v1.position.z - v1.position.w) / (v2.position.w - v1.position.w);
        float alphaB = (v1.position.z - v1.position.w) / (v3.position.w - v1.position.w);

        VSOutput v0a = VSOutput.interpolate(v1, v2, alphaA);
        VSOutput v0b = VSOutput.interpolate(v1, v3, alphaB);

        this.vsOutputTriangles.add(new VSOutput[]{ v0a, v2, v3 });
        this.vsOutputTriangles.add(new VSOutput[]{ v0b, v0a, v3 });
    }

    private void clipRoutine2(VSOutput v1, VSOutput v2, VSOutput v3)
    {
        float alphaA = (v1.position.z - v1.position.w) / (v3.position.w - v1.position.w);
        float alphaB = (v2.position.z - v2.position.w) / (v3.position.w - v2.position.w);

        v1 = VSOutput.interpolate(v1, v2, alphaA);
        v2 = VSOutput.interpolate(v2, v3, alphaB);

        this.vsOutputTriangles.add(new VSOutput[]{ v1, v2, v3 });
    }

    private void processClipSpaceToScreenSpace_() {
        this.viewScale = new Vec4(0.5f*this.view.getWidth(),0.5f*this.view.getHeight(),1f,1f);
        for (VertexHolder vertexHolder : this.vsOutput_) {
            processVertexClipToScreenSpace(vertexHolder);
        }
    }

    private void processVertexClipToScreenSpace(VertexHolder vertex)
    {
        // perspective divide
        float invW = 1/vertex.position.w;
        vertex.position.x *= invW;
        vertex.position.y *= invW;
        vertex.position.z *= invW;
        vertex.position.w = invW;

        if (!renderState.skipSpaceConversion) {        // flip
            vertex.position.mult(new Vec4(-1, -1, 1, 1));

            // offset to center
            vertex.position.add(new Vec4(1, 1, 0, 0));
            // scale to viewport
            vertex.position.mult(this.viewScale);
        }

        float[] attribsDataRef = vertex.verticesAttributes.getDataRef();
        for (int i = 0; i < attribsDataRef.length; i++) {
            attribsDataRef[i] *= invW;
        }
    }

    private void processClipSpaceToScreenSpace() {

        Vec4 viewScale = new Vec4(0.5f*this.view.getWidth(),0.5f*this.view.getHeight(),1f,1f);
        for (int i = 0; i < this.vsOutputTriangles.size(); i++) {
            VSOutput[] triangle = this.vsOutputTriangles.get(i);

            for (VSOutput vertex : triangle)
            {
                if (vertex.normalized)
                    continue;
                //to NDC

                float invW = 1/vertex.position.w;
                vertex.position.x *= invW;
                vertex.position.y *= invW;
                vertex.position.z *= invW;
                vertex.position.w = invW;


                vertex.position.mult(new Vec4(-1,-1,1,1));

                // to screen space
                // offset
                vertex.position.add(new Vec4(1,1,0,0));

                // scale
                vertex.position.mult(viewScale);

                // perspective divide varying attributes
                vertex.mult(invW);

                vertex.normalized = true;
            }

        }
    }

    private static float edgeFunction(Vec3 v1, Vec3 v2, Vec3 v3)
    {
        return (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
    };

    private static float edgeFunction(float v1x, float v1y, float v2x, float v2y, float v3x, float v3y)
    {
        return (v2x - v1x) * (v3y - v1y) - (v2y - v1y) * (v3x - v1x);
    };

    private boolean depthTest(int x, int y, float z, boolean write)
    {
        int index = y * view.getWidth() + x;
        float currZ = this.view.getDepth(index);
        if (this.renderState.depthFunc.test(z, currZ))
        {
            if (write && renderState.depthMask)
            {
                this.view.setDepth(index, z);
            }
            return true;
        }
        return false;
    }

    private void stencilOperation(int x, int y, StencilOperation stencilOperation)
    {
        if (stencilOperation == StencilOperation.KEEP)
            return;


        int newValue = 0;
        int index = y * view.getWidth() + x;

        switch (stencilOperation) {
            case ZERO -> {
                this.view.setStencil(x, y, (byte) 0x00);
                newValue = 0x00;
            }
            case REPLACE -> {
                newValue = this.renderState.stencilRef;
            }
            case INCR -> {
                newValue = view.getStencil(index) & 0xFF;
                if (newValue < 255)
                    ++newValue;
            }
            case INCR_WRAP -> {
                newValue = ((view.getStencil(index) & 0xFF) + 1) & 0xFF;
            }
            case DECR -> {
                newValue = view.getStencil(index) & 0xFF;
                if (newValue > 0)
                    --newValue;
            }
            case DECR_WRAP -> {
                newValue = ((view.getStencil(index) & 0xFF) - 1) & 0xFF;
            }
            case INVERT -> {
                newValue = ~(this.view.getStencil(index) & 0xFF);
            }
        }

        view.setStencil(index, (byte) (((view.getStencil(index) & 0xFF) & ~renderState.stencilWriteMask) | (newValue & renderState.stencilWriteMask)));
    }

    private void processRasterization_()
    {
        for (PrimitiveHolder triangle : this.vsOutputTriangles_) {
            VertexHolder v1 = triangle.vertices[0];
            VertexHolder v2 = triangle.vertices[1];
            VertexHolder v3 = triangle.vertices[2];
            if (this.renderState.cullFace) {
                Vec4 ab = Vec4.sub(v2.position, v1.position);
                Vec4 ac = Vec4.sub(v3.position, v1.position);
                float sign = ab.x * ac.y - ac.x * ab.y;
                if (sign <= 0)
                    continue;
            }
            this.rasterizeTriangle_(v1, v2, v3);
        }
    }

    private void processRasterization()
    {
//        this.vsOutputTriangles.forEach(triangle -> {
        //            this.rasterizer.bindView(view);
        //        });
        this.vsOutputTriangles.parallelStream().forEach(triangle -> {
            VSOutput v1 = triangle[0];
            VSOutput v2 = triangle[1];
            VSOutput v3 = triangle[2];
            if (this.renderState.cullFace) {
                Vec4 ab = Vec4.sub(v2.position, v1.position);
                Vec4 ac = Vec4.sub(v3.position, v1.position);
                float sign = ab.x * ac.y - ac.x * ab.y;
                if (sign <= 0)
                    return;
            }
            this.rasterizeTriangle(v1, v2, v3);
        });
    }

    private void processFragment_(Vec4 fragCoord, VertexAttributes vertexAttributes)
    {
//        if (fragCoord.x < 0 || fragCoord.x >= view.getWidth()-1)
//            return;
//
//        if (fragCoord.y < 0 || fragCoord.y >= view.getHeight()-1)
//            return;


        if (this.earlyZ) {
            if (!depthTest((int) fragCoord.x, (int) fragCoord.y, fragCoord.z, false))
                return;
        }

//
//        if (fragCoord.x % 5 == 0 && fragCoord.y % 5 == 0)
//        {
//            Vec3 normals = vertexAttributes.getFloat3(2);
//            Vec3 normalPtV1 = camera.worldToScreenPoint(Vec3.add(camera.screenToWorldPoint(fragCoord.toVec3()), normals));
//            rasterizer.drawLine3D((int) fragCoord.x, (int) fragCoord.y, fragCoord.z, (int) normalPtV1.x, (int) normalPtV1.y, normalPtV1.z, ColorUtils.mult(0xFFFF0000, 2));
//        }

        int index = (int)fragCoord.y * view.getWidth() + (int)fragCoord.x;

        if (this.renderState.stencilTest)
        {
            int stencilValue = this.view.getStencil(index) & 0xFF;

            if (!StencilFunction.test(this.renderState.stencilRef, stencilValue, this.renderState.stencilMask, this.renderState.stencilParams.stencilFunction))
            {
                stencilOperation((int) fragCoord.x, (int) fragCoord.y, this.renderState.stencilParams.stencilFail);
                return;
            }
        }

        Vec4 fragColor4 = new Vec4(0,0,0,0);

        shaderProgram.fragment(fragCoord, vertexAttributes, fragColor4);
        int fragColor = ColorUtils.rgba(fragColor4.x, fragColor4.y, fragColor4.z, fragColor4.w);
//        fragColor = ColorUtils.mult(fragColor, -Vec3.dot(camera.transform.getForward(), vertexAttributes.getFloat3(2)));

        if (this.renderState.depthTest) {
            if (!depthTest((int) fragCoord.x, (int) fragCoord.y, fragCoord.z, true))
            {
                if (renderState.stencilTest)
                {
                    stencilOperation((int) fragCoord.x, (int) fragCoord.y, this.renderState.stencilParams.stencilPassAndDepthFail);
                }
                return;
            }
            if (renderState.stencilTest)
            {
                stencilOperation((int) fragCoord.x, (int) fragCoord.y, this.renderState.stencilParams.stencilPassAndDepthPass);
            }
        }



        if (this.renderState.blend) {
            int alpha = (fragColor >>> 24);

            if (alpha == 0)
            {
                return;
            }

            int existingColor = this.view.getPixel(index);
            int existingAlpha = (existingColor >>> 24);

            if (alpha == 255 || existingAlpha == 0) {
            } else if (alpha > 0) {


                float normalizedAlpha = alpha / 255.0f;
                float normalizedExistingAlpha = existingAlpha / 255.0f;

                float finalAlpha = normalizedAlpha + normalizedExistingAlpha * (1.0f - normalizedAlpha);

                if (finalAlpha != 0) {

                    int red = (fragColor >> 16) & 0xFF;
                    int green = (fragColor >> 8) & 0xFF;
                    int blue = fragColor & 0xFF;

                    float invFinalAlpha = 1.0f / finalAlpha;

                    int blendedRed = (int)((red * normalizedAlpha + ((existingColor >> 16) & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
                    int blendedGreen = (int)((green * normalizedAlpha + ((existingColor >> 8) & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
                    int blendedBlue = (int)((blue * normalizedAlpha + (existingColor & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);

                    fragColor = ((int)(finalAlpha * 255.0f) << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
                }
            }
        }

        this.view.setPixel(index, fragColor);
    }

    private void processFragment(Vec4 fragCoord, Varyings varyings)
    {
        if (fragCoord.x < 0 || fragCoord.x >= view.getWidth()-1)
            return;

        if (fragCoord.y < 0 || fragCoord.y >= view.getHeight()-1)
            return;


        if (this.earlyZ) {
            if (!depthTest((int) fragCoord.x, (int) fragCoord.y, fragCoord.z, false))
                return;
        }


        if (fragCoord.x == mousePos.x && fragCoord.y == mousePos.y)
        {
//            Vec3 normalPtV1 = /camera.worldToScreenPoint(Vec3.add(camera.screenToWorldPoint(fragCoord), interpolatedVertex.normal));
//            rasterizer.drawLine3D((int) fragCoord.x, (int) fragCoord.y, fragCoord.z, (int) normalPtV1.x, (int) normalPtV1.y, normalPtV1.z, ColorUtils.mult(interpolatedVertex.color, 2));
        }

        int fragColor = shaderProgram.fragment(fragCoord, varyings);


        if (renderState.depthTest) {
            if (!depthTest((int) fragCoord.x, (int) fragCoord.y, fragCoord.z, true))
                return;
        }

        int index = (int)fragCoord.y * view.getWidth() + (int)fragCoord.x;

        if (this.renderState.blend) {
            int alpha = (fragColor >>> 24);

            if (alpha == 0)
            {
                return;
            }

            int existingColor = this.view.getPixel(index);
            int existingAlpha = (existingColor >>> 24);

            if (alpha == 255 || existingAlpha == 0) {
            } else if (alpha > 0) {


                float normalizedAlpha = alpha / 255.0f;
                float normalizedExistingAlpha = existingAlpha / 255.0f;

                float finalAlpha = normalizedAlpha + normalizedExistingAlpha * (1.0f - normalizedAlpha);

                if (finalAlpha != 0) {

                    int red = (fragColor >> 16) & 0xFF;
                    int green = (fragColor >> 8) & 0xFF;
                    int blue = fragColor & 0xFF;

                    float invFinalAlpha = 1.0f / finalAlpha;

                    int blendedRed = (int)((red * normalizedAlpha + ((existingColor >> 16) & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
                    int blendedGreen = (int)((green * normalizedAlpha + ((existingColor >> 8) & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
                    int blendedBlue = (int)((blue * normalizedAlpha + (existingColor & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);

                    fragColor = ((int)(finalAlpha * 255.0f) << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
                }
            }
        }

        this.view.setPixel(index, fragColor);
    }

    public void rasterizeTriangle_(VertexHolder v1, VertexHolder v2, VertexHolder v3) {

        if (v1.position.y > v2.position.y) {
            VertexHolder temp = v2;
            v2 = v1;
            v1 = temp;
        }

        if (v2.position.y > v3.position.y) {
            VertexHolder temp = v2;
            v2 = v3;
            v3 = temp;
        }

        if (v1.position.y > v2.position.y) {
            VertexHolder temp = v2;
            v2 = v1;
            v1 = temp;
        }

        float y1 = v1.position.y;
        float y2 = v2.position.y;
        float y3 = v3.position.y;


        float z1 = v1.position.z;
        float z2 = v2.position.z;
        float z3 = v3.position.z;

        float x1 = v1.position.x;
        float x2 = v2.position.x;
        float x3 = v3.position.x;


        // Determine winding order
        boolean isClockwise = lineSide2D(x2, y2, z2, x1, y1, z1, x3, y3, z3) > 0;

        // Define the vertices for scanline processing
        VertexHolder va, vb, vc, vd, va2, vb2, vc2, vd2;
        if (isClockwise) {
            va = v1;
            vb = v3;
            vc = v1;
            vd = v2;
            va2 = v1;
            vb2 = v3;
            vc2 = v2;
            vd2 = v3;
        } else {
            va = v1;
            vb = v2;
            vc = v1;
            vd = v3;
            va2 = v2;
            vb2 = v3;
            vc2 = v1;
            vd2 = v3;
        }

        float area = edgeFunction(v1.position.x, v1.position.y, v2.position.x, v2.position.y, v3.position.x, v3.position.y);

        // Using IntStream to parallelize the outer loop
        VertexHolder finalV2_ = v2;
        VertexHolder finalV3_ = v3;
        VertexHolder finalV1_ = v1;

        final float[] v1AttribDataRef = finalV1_.verticesAttributes.getDataRef();
        final float[] v2AttribDataRef = finalV2_.verticesAttributes.getDataRef();
        final float[] v3AttribDataRef = finalV3_.verticesAttributes.getDataRef();

        int bound = (int) y3;
        for (int y = (int) y1; y <= bound; y++) {
            float[] interpolatedVADataPreinit = new float[v1AttribDataRef.length];
            VertexAttributes interpolatedVertexAttributes = new VertexAttributes(interpolatedVADataPreinit);

            float gradient1, gradient2, zStart, zEnd;
            int startX, endX;

            if (y < y2) {
                if (y < Math.min(va.position.y, Math.min(vb.position.y, Math.min(vc.position.y, vd.position.y))) || y > Math.max(va.position.y, Math.max(vb.position.y, Math.max(vc.position.y, vd.position.y))))
                    continue;


                gradient1 = va.position.y != vb.position.y ? (y - va.position.y) / (vb.position.y - va.position.y) : 1;
                gradient2 = vc.position.y != vd.position.y ? (y - vc.position.y) / (vd.position.y - vc.position.y) : 1;

                startX = (int) MathUtils.lerp(va.position.x, vb.position.x, gradient1);
                endX = (int) MathUtils.lerp(vc.position.x, vd.position.x, gradient2);

            } else {
                if (y < Math.min(va2.position.y, Math.min(vb2.position.y, Math.min(vc2.position.y, vd2.position.y))) || y > Math.max(va2.position.y, Math.max(vb2.position.y, Math.max(vc2.position.y, vd2.position.y))))
                    continue;

                gradient1 = va2.position.y != vb2.position.y ? (y - va2.position.y) / (vb2.position.y - va2.position.y) : 1;
                gradient2 = vc2.position.y != vd2.position.y ? (y - vc2.position.y) / (vd2.position.y - vc2.position.y) : 1;

                startX = (int) MathUtils.lerp(va2.position.x, vb2.position.x, gradient1);
                endX = (int) MathUtils.lerp(vc2.position.x, vd2.position.x, gradient2);

            }


            if (y < 0 || y >= view.getHeight() - 1)
                continue;

            for (int x = startX; x < endX; x++) {
                if (x < 0 || x >= view.getWidth() - 1)
                    continue;


                float w1 = edgeFunction(finalV2_.position.x, finalV2_.position.y, finalV3_.position.x, finalV3_.position.y, x + 0.5f, y + 0.5f) / area;
                float w2 = edgeFunction(finalV3_.position.x, finalV3_.position.y, finalV1_.position.x, finalV1_.position.y, x + 0.5f, y + 0.5f) / area;
                float w3 = edgeFunction(finalV1_.position.x, finalV1_.position.y, finalV2_.position.x, finalV2_.position.y, x + 0.5f, y + 0.5f) / area;


                float w = 1.0f / (w1 * finalV1_.position.w + w2 * finalV2_.position.w + w3 * finalV3_.position.w);
                float z = (w1 * finalV1_.position.z + w2 * finalV2_.position.z + w3 * finalV3_.position.z);

//                VertexAttributes.interpolateBarycentric(
//                        v1AttribDataRef,
//                        v2AttribDataRef,
//                        v3AttribDataRef,
//                        w1, w2, w3,
//                        w, interpolatedVADataPreinit);
                VertexAttributes.interpolateBarycentric2(
                        v1AttribDataRef,
                        v2AttribDataRef,
                        v3AttribDataRef,
                        w1, w2, w3,
                        w, interpolatedVADataPreinit);

//                VertexAttributes interpolatedVA = new VertexAttributes(interpolatedVADataPreinit);
                interpolatedVertexAttributes.setData(interpolatedVADataPreinit);
                this.processFragment_(new Vec4(x, y, z, w), interpolatedVertexAttributes);

            }
        }
    }

    public void rasterizeTriangle(VSOutput v1, VSOutput v2, VSOutput v3)
    {
        if (v1.position.y > v2.position.y) {
            VSOutput temp = v2;
            v2 = v1;
            v1 = temp;
        }

        if (v2.position.y > v3.position.y) {
            VSOutput temp = v2;
            v2 = v3;
            v3 = temp;
        }

        if (v1.position.y > v2.position.y) {
            VSOutput temp = v2;
            v2 = v1;
            v1 = temp;
        }

        float y1 = v1.position.y;
        float y2 = v2.position.y;
        float y3 = v3.position.y;

        float z1 = v1.position.z;
        float z2 = v2.position.z;
        float z3 = v3.position.z;

        float x1 = v1.position.x;
        float x2 = v2.position.x;
        float x3 = v3.position.x;


        // Determine winding order
        boolean isClockwise = lineSide2D(x2, y2, z2, x1, y1, z1, x3, y3, z3) > 0;

        // Define the vertices for scanline processing
        VSOutput va, vb, vc, vd, va2, vb2, vc2, vd2;
        if (isClockwise) {
            va = v1;
            vb = v3;
            vc = v1;
            vd = v2;
            va2 = v1;
            vb2 = v3;
            vc2 = v2;
            vd2 = v3;
        } else {
            va = v1;
            vb = v2;
            vc = v1;
            vd = v3;
            va2 = v2;
            vb2 = v3;
            vc2 = v1;
            vd2 = v3;
        }

        // Define variables outside the loop
//        float gradient1, gradient2, zStart, zEnd;
//        int startX, endX;
        VSOutput startVertex, endVertex;


        float area = edgeFunction(v1.position.x, v1.position.y, v2.position.x, v2.position.y, v3.position.x, v3.position.y);

        // Using IntStream to parallelize the outer loop
        VSOutput finalV = v2;
        VSOutput finalV1 = v3;
        VSOutput finalV2 = v1;
        IntStream.rangeClosed((int) y1, (int) y3).parallel().forEach(y -> {

            float gradient1, gradient2, zStart, zEnd;
            int startX, endX;

            if (y < y2) {
                if (y < Math.min(va.position.y, Math.min(vb.position.y, Math.min(vc.position.y, vd.position.y))) || y > Math.max(va.position.y, Math.max(vb.position.y, Math.max(vc.position.y, vd.position.y))))
                    return;


                gradient1 = va.position.y != vb.position.y ? (y - va.position.y) / (vb.position.y - va.position.y) : 1;
                gradient2 = vc.position.y != vd.position.y ? (y - vc.position.y) / (vd.position.y - vc.position.y) : 1;

                startX = (int) MathUtils.lerp(va.position.x, vb.position.x, gradient1);
                endX = (int) MathUtils.lerp(vc.position.x, vd.position.x, gradient2);

            } else {
                if (y < Math.min(va2.position.y, Math.min(vb2.position.y, Math.min(vc2.position.y, vd2.position.y))) || y > Math.max(va2.position.y, Math.max(vb2.position.y, Math.max(vc2.position.y, vd2.position.y))))
                    return;

                gradient1 = va2.position.y != vb2.position.y ? (y - va2.position.y) / (vb2.position.y - va2.position.y) : 1;
                gradient2 = vc2.position.y != vd2.position.y ? (y - vc2.position.y) / (vd2.position.y - vc2.position.y) : 1;

                startX = (int) MathUtils.lerp(va2.position.x, vb2.position.x, gradient1);
                endX = (int) MathUtils.lerp(vc2.position.x, vd2.position.x, gradient2);

            }

            for (int x = startX; x < endX; x++) {

//                Vec4 p = new Vec4(x + 0.5f, y + 0.5f, 0.0f, 0.0f);
                float w1 = edgeFunction(finalV.position.x, finalV.position.y, finalV1.position.x, finalV1.position.y, x+0.5f, y+0.5f) / area;
                float w2 = edgeFunction(finalV1.position.x, finalV1.position.y, finalV2.position.x, finalV2.position.y, x+0.5f, y+0.5f) / area;
                float w3 = edgeFunction(finalV2.position.x, finalV2.position.y, finalV.position.x, finalV.position.y, x+0.5f, y+0.5f) / area;
//                float w2 = edgeFunction(v3.position, v1.position, p);
//                float w3 = edgeFunction(v1.position, v2.position, p);
                //                if (w1 < 0 || w2 < 0 || w3 < 0)
                //                    continue;
//                w1 /= area;
//                w2 /= area;
//                w3 /= area;

                float w = 1.0f / (w1 * finalV2.position.w + w2 * finalV.position.w + w3 * finalV1.position.w);
                float z = (w1 * finalV2.position.z + w2 * finalV.position.z + w3 * finalV1.position.z);
                //                VSOutput vert = VSOutput.interpolate(startVertex, endVertex, gradient);
                Varyings vert = new Varyings(
                        Vec2.interpolateBarycentric(finalV2.uv, finalV.uv, finalV1.uv, w1, w2, w3),
                        Vec3.interpolateBarycentric(finalV2.normal, finalV.normal, finalV1.normal, w1, w2, w3),
                        ColorUtils.interpolateBarycentric(finalV2.color, finalV.color, finalV1.color, w1, w2, w3)
                );

                vert.mult(w);
                this.processFragment(new Vec4(x, y, z, w), vert);

            }
        });

//        for (int y = (int) y1; y <= (int) y3; y++) {
//
//            if (y < y2) {
//                if (y < Math.min(va.position.y, Math.min(vb.position.y, Math.min(vc.position.y, vd.position.y))) || y > Math.max(va.position.y, Math.max(vb.position.y, Math.max(vc.position.y, vd.position.y))))
//                    continue;
//
//
//                gradient1 = va.position.y != vb.position.y ? (y - va.position.y) / (vb.position.y - va.position.y) : 1;
//                gradient2 = vc.position.y != vd.position.y ? (y - vc.position.y) / (vd.position.y - vc.position.y) : 1;
//
//                startX = (int) MathUtils.lerp(va.position.x, vb.position.x, gradient1);
//                endX = (int) MathUtils.lerp(vc.position.x, vd.position.x, gradient2);
//
//            } else {
//                if (y < Math.min(va2.position.y, Math.min(vb2.position.y, Math.min(vc2.position.y, vd2.position.y))) || y > Math.max(va2.position.y, Math.max(vb2.position.y, Math.max(vc2.position.y, vd2.position.y))))
//                    continue;
//
//                gradient1 = va2.position.y != vb2.position.y ? (y - va2.position.y) / (vb2.position.y - va2.position.y) : 1;
//                gradient2 = vc2.position.y != vd2.position.y ? (y - vc2.position.y) / (vd2.position.y - vc2.position.y) : 1;
//
//                startX = (int) MathUtils.lerp(va2.position.x, vb2.position.x, gradient1);
//                endX = (int) MathUtils.lerp(vc2.position.x, vd2.position.x, gradient2);
//
//            }
//            for (int x = startX; x < endX; x++) {
//
////                Vec4 p = new Vec4(x + 0.5f, y + 0.5f, 0.0f, 0.0f);
//                float w1 = edgeFunction(v2.position.x, v2.position.y, v3.position.x, v3.position.y, x+0.5f, y+0.5f) / area;
//                float w2 = edgeFunction(v3.position.x, v3.position.y, v1.position.x, v1.position.y, x+0.5f, y+0.5f) / area;
//                float w3 = edgeFunction(v1.position.x, v1.position.y, v2.position.x, v2.position.y, x+0.5f, y+0.5f) / area;
////                float w2 = edgeFunction(v3.position, v1.position, p);
////                float w3 = edgeFunction(v1.position, v2.position, p);
//                //                if (w1 < 0 || w2 < 0 || w3 < 0)
//                //                    continue;
////                w1 /= area;
////                w2 /= area;
////                w3 /= area;
//
//                float w = 1.0f / (w1 * v1.position.w + w2 * v2.position.w + w3 * v3.position.w);
//                float z = (w1 * v1.position.z + w2 * v2.position.z + w3 * v3.position.z);
//                //                VSOutput vert = VSOutput.interpolate(startVertex, endVertex, gradient);
//                Varyings vert = new Varyings(
//                        Vec2.interpolateBarycentric(v1.uv, v2.uv, v3.uv, w1, w2, w3),
//                        Vec3.interpolateBarycentric(v1.normal, v2.normal, v3.normal, w1, w2, w3),
//                        ColorUtils.interpolateBarycentric(v1.color, v2.color, v3.color, w1, w2, w3)
//                );
//
//                vert.mult(w);
//                this.processFragment(new Vec4(x, y, z, w), vert);
//
//            }
//        }
    }

    float cross2d(float x0, float y0, float x1, float y1)
    {
        return x0 * y1 - x1 * y0;
    }

    float lineSide2D(float px, float py, float pz, float sx, float sy, float sz, float ex, float ey, float ez)
    {
        return cross2d(px - sx, py - sy, ex - sx, ey - sy);
    }

}