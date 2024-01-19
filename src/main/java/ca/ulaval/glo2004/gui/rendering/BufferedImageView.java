package ca.ulaval.glo2004.gui.rendering;

import ca.ulaval.glo2004.rendering.IView;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Arrays;

public class BufferedImageView implements IView {

    private BufferedImage backBuffer;
    private Graphics backGraphics;
    private BufferedImage zBuffer;
    private BufferedImage stencilBuffer;

    private int[] backBufferData;
    private float[] zBufferData;
    private byte[] stencilBufferData;

    private int width;
    private int height;

    public boolean postProcessingTest = false;

    public BufferedImageView(int width, int height)
    {
        this.width = width;
        this.height = height;
        init();
    }

    public BufferedImage getBufferedImage()
    {
        return backBuffer;
    }

    public BufferedImage getZBufferImage()
    {
        return zBuffer;
    }

    public BufferedImage getStencilBufferImage()
    {
        return stencilBuffer;
    }

    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;
        init();
    }

    @Override
    public void init() {
        this.backBuffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        this.backGraphics = this.backBuffer.getGraphics();
        this.backBufferData = ((DataBufferInt)this.backBuffer.getRaster().getDataBuffer()).getData();

        this.zBuffer = createZBufferImage(width, height);
        this.zBufferData = ((DataBufferFloat)zBuffer.getRaster().getDataBuffer()).getData();

        this.stencilBuffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_GRAY);
        this.stencilBufferData = ((DataBufferByte)this.stencilBuffer.getRaster().getDataBuffer()).getData();
    }

    @Override
    public void clearColorBuffer(int argb) {
        this.backGraphics.setColor(new Color(argb, true));
//        this.backGraphics.fillRect(0, 0, width, height);
//        this.backGraphics.clearRect(0,0,width,height);
//        Arrays.fill(this.backBufferData, 0x0);

        Graphics2D g2d = (Graphics2D)this.backGraphics;
        g2d.setComposite(AlphaComposite.Src);
        g2d.fillRect(0, 0, (width), (height));
        g2d.setComposite(AlphaComposite.SrcOver);
    }

    @Override
    public void clearDepthBuffer(float depth) {
        Arrays.fill(zBufferData, depth);
    }

    @Override
    public void clearStencilBuffer(byte value) {
        Arrays.fill(this.stencilBufferData, value);
    }

    @Override
    public void setDepth(int x, int y, float depth) {
        if (x < 0 || x >= width-1)
            return;

        if (y < 0 || y >= height-1)
            return;

        int index = y * width + x;
        this.zBufferData[index] = depth;
    }

    @Override
    public void setDepth(int index, float depth) {
        this.zBufferData[index] = depth;
    }

    @Override
    public float getDepth(int x, int y) {
        int index = y * width + x;
        return this.zBufferData[index];
    }

    @Override
    public float getDepth(int index) {
        return this.zBufferData[index];
    }

    @Override
    public void setPixel(int x, int y, float z, int argb) {
        if (x < 0 || x >= width-1)
            return;

        if (y < 0 || y >= height-1)
            return;

        int index = y * width + x;
        this.backBufferData[index] = argb;

//
//        if (this.depthTesting) {
//            float depth = zBufferData[index];
//
//            if (depth <= z) {
//                return;
//            }
//            else
//            {
//                int alpha = (argb >>> 24);
//
//                if (alpha == 255)
//                    this.zBufferData[index] = z;
//            }
//        }
//
//        if (this.alphaBlending) {
//            int alpha = (argb >>> 24);
//
//            if (alpha == 0)
//            {
//                return;
//            }
//
//            int existingColor = this.backBufferData[index];
//            int existingAlpha = (existingColor >>> 24);
//
//            if (alpha == 255 || existingAlpha == 0) {
//            } else if (alpha > 0) {
//
//
//                float normalizedAlpha = alpha / 255.0f;
//                float normalizedExistingAlpha = existingAlpha / 255.0f;
//
//                float finalAlpha = normalizedAlpha + normalizedExistingAlpha * (1.0f - normalizedAlpha);
//
//                if (finalAlpha != 0) {
//
//                    int red = (argb >> 16) & 0xFF;
//                    int green = (argb >> 8) & 0xFF;
//                    int blue = argb & 0xFF;
//
//                    float invFinalAlpha = 1.0f / finalAlpha;
//
//                    int blendedRed = (int)((red * normalizedAlpha + ((existingColor >> 16) & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
//                    int blendedGreen = (int)((green * normalizedAlpha + ((existingColor >> 8) & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
//                    int blendedBlue = (int)((blue * normalizedAlpha + (existingColor & 0xFF) * normalizedExistingAlpha * (1.0f - normalizedAlpha)) * invFinalAlpha);
//
//                    argb = ((int)(finalAlpha * 255.0f) << 24) | (blendedRed << 16) | (blendedGreen << 8) | blendedBlue;
//                }
//            }
//        }
//
//        this.backBufferData[index] = argb;
//
//        int newRed = ColorUtils.getRed(argb);
//        int newGreen = ColorUtils.getGreen(argb);
//        int newBlue = ColorUtils.getBlue(argb);
//
//
//        if (postProcessingTest)
//        {
//
//            // Apply simple blurring by averaging nearby pixels
//            int blurRadius = 2; // Adjust blur radius as needed
//            int avgRed = 0, avgGreen = 0, avgBlue = 0;
//            int count = 0;
//
//            for (int i = -blurRadius; i <= blurRadius; i++) {
//                for (int j = -blurRadius; j <= blurRadius; j++) {
//                    int pixelX = x + i;
//                    int pixelY = y + j;
//
//                    if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
//                        int pixel = backBufferData[pixelY * width + pixelX];
//                        avgRed += (pixel >> 16) & 0xFF;
//                        avgGreen += (pixel >> 8) & 0xFF;
//                        avgBlue += pixel & 0xFF;
//                        count++;
//                    }
//                }
//            }
//
//            newRed = avgRed / count;
//            newGreen = avgGreen / count;
//            newBlue = avgBlue / count;
//        }
//
//
//        // Recombine color channels and set the modified pixel
//        int newARGB = ColorUtils.rgba(newRed, newGreen, newBlue, ColorUtils.getAlpha(argb));
//        backBufferData[y * width + x] = newARGB;

    }

    @Override
    public void setPixel(int index, int argb) {
        this.backBufferData[index] = argb;
    }

    private int getClampedColorValue(int value) {
        return Math.min(Math.max(value, 0), 255); // Clamp value between 0 and 255
    }


    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getPixel(int x, int y, int z) {
        int index = y * width + x;
        return this.backBufferData[index];
    }

    @Override
    public int getPixel(int index) {
        return this.backBufferData[index];
    }

    @Override
    public byte getStencil(int index) {
        return this.stencilBufferData[index];
    }

    @Override
    public byte getStencil(int x, int y) {
        return this.stencilBufferData[y * width + x];
    }

    @Override
    public void setStencil(int x, int y, byte value) {
        this.stencilBufferData[y * width + x] = value;
    }

    @Override
    public void setStencil(int index, byte value) {
        this.stencilBufferData[index] = value;
    }

    @Override
    public void incrementStencil(int x, int y, boolean wrap) {
        int index = y * width + x;

        if (wrap) {
            stencilBufferData[index] = (byte) ((stencilBufferData[index] + 1) & 0xFF); // Wrapped increment using bitwise AND with 0xFF
        } else {
            if (stencilBufferData[index] < (byte)0xFF) {
                ++stencilBufferData[index]; // Normal increment up to 255
            }
        }
    }

    @Override
    public void decrementStencil(int x, int y, boolean wrap) {
        int index = y * width + x;

        if (wrap) {
            stencilBufferData[index] = (byte) ((stencilBufferData[index] - 1) & 0xFF); // Wrapped increment using bitwise AND with 0xFF
        } else {
            if (stencilBufferData[index] > (byte)0x00) {
                --stencilBufferData[index]; // Normal increment up to 255
            }
        }
    }

    private BufferedImage createZBufferImage(int width, int height)
    {
        int bands = 1;
        int[] bandOffsets = {0};

        SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, width, height, bands, width * bands, bandOffsets);
        DataBuffer buffer = new DataBufferFloat(width * height * bands);

        WritableRaster raster = Raster.createWritableRaster(sampleModel, buffer, null);

        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorModel colorModel = new ComponentColorModel(colorSpace, false, false, Transparency.OPAQUE, DataBuffer.TYPE_FLOAT);

        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }
}
