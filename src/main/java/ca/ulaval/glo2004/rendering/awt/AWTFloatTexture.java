package ca.ulaval.glo2004.rendering.awt;

import ca.ulaval.glo2004.rendering.Texture;
import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferFloat;
import java.io.IOException;
import java.io.InputStream;

public class AWTFloatTexture implements Texture {

    BufferedImage textureImage;
    float[] dataBuffer;
    int width;
    int height;

    public static AWTFloatTexture createFromFile(String filePath)
    {
        try {
            InputStream inputStream = AWTFloatTexture.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);
                inputStream.close();
                if (image != null) {
                    return new AWTFloatTexture(image);
                } else {
                    System.err.println("Failed to load image from resources: " + filePath);
                }
            } else {
                System.err.println("Resource not found: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AWTFloatTexture(BufferedImage bufferedImage)
    {
        this.textureImage = bufferedImage;
        this.dataBuffer = ((DataBufferFloat)bufferedImage.getData().getDataBuffer()).getData();//convertToPixels(bufferedImage);
        this.width = this.textureImage.getWidth();
        this.height = this.textureImage.getHeight();
    }

    public float sampleFloat(Vec2 uv)
    {
        float uvX = uv.x;
        float uvY = uv.y;

        uvX = uvX > 1.0f ? (float) Math.floorMod((int) uvX, 1) : uvX;
        uvY = uvY > 1.0f ? (float) Math.floorMod((int) uvY, 1) : uvY;

        uvX = uvX < 0.0f ? 1 - (float) Math.floorMod((int) -uvX, 1) : uvX;
        uvY = uvY < 0.0f ? 1 - (float) Math.floorMod((int) -uvY, 1) : uvY;

        int texX = MathUtils.fastFloor(uvX * (width - 1));
        int texY = MathUtils.fastFloor((1.0f - uvY) * (height - 1));

        return dataBuffer[texY * width + texX];
    }

    @Override
    public int sample(Vec2 uv) {
        return 0;
    }

    @Override
    public int sample(float[] uv) {
        return 0;
    }

    @Override
    public int getPixel(int x, int y) {
        return 0;
    }

    public float getPixelFloat(int x, int y) {
        x = MathUtils.wrap(x, width-1);
        y = MathUtils.wrap(y, height-1);
        return dataBuffer[y * width + x];
    }

    private static float[] convertToPixels(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float[] pixelArray = new float[width * height];
        image.getSampleModel().getPixels(0, 0, width, height, pixelArray, image.getData().getDataBuffer());
        return pixelArray;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
