package ca.ulaval.glo2004.rendering.awt;

import ca.ulaval.glo2004.rendering.Texture;
import ca.ulaval.glo2004.util.math.MathUtils;
import ca.ulaval.glo2004.util.math.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class AWTARGBTexture implements Texture {

    BufferedImage textureImage;
    int[] dataBuffer;
    int width;
    int height;

    public static AWTARGBTexture createFromFile(String filePath)
    {
        try {
            InputStream inputStream = AWTARGBTexture.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream != null) {
                BufferedImage image = ImageIO.read(inputStream);
                inputStream.close();
                if (image != null) {
                    return new AWTARGBTexture(image);
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

    public AWTARGBTexture(BufferedImage bufferedImage)
    {
        this.textureImage = bufferedImage;
        this.dataBuffer = convertToPixels(bufferedImage);
        this.width = this.textureImage.getWidth();
        this.height = this.textureImage.getHeight();
    }

    @Override
    public int sample(Vec2 uv) {
        float uvX = uv.x;
        float uvY = uv.y;

        uvX = uvX - (float)Math.floor(uvX);
        uvY = uvY - (float)Math.floor(uvY);

        int texX = MathUtils.fastFloor(uvX * (width - 1));
        int texY = MathUtils.fastFloor((1.0f - uvY) * (height - 1));

        return dataBuffer[texY * width + texX];
    }

    @Override
    public int sample(float[] uv) {
        float uvX = uv[0];
        float uvY = uv[1];

        uvX = uvX > 1.0f ? (float) Math.floorMod((int) uvX, 1) : uvX;
        uvY = uvY > 1.0f ? (float) Math.floorMod((int) uvY, 1) : uvY;

        uvX = uvX < 0.0f ? 1 - (float) Math.floorMod((int) -uvX, 1) : uvX;
        uvY = uvY < 0.0f ? 1 - (float) Math.floorMod((int) -uvY, 1) : uvY;

        int texX = MathUtils.fastFloor(uvX * (width - 1));
        int texY = MathUtils.fastFloor((1.0f - uvY) * (height - 1));

        return dataBuffer[texY * width + texX];
    }

    @Override
    public int getPixel(int x, int y) {

        x = x > width-1 ? Math.floorMod(x, width-1) : x;
        y = y > width-1 ? Math.floorMod(y, height-1) : y;

        x = x < 0.0f ? width-1 - Math.floorMod(-x, width-1) : x;
        y = y < 0.0f ? width-1 - Math.floorMod(-y, height-1) : y;

        return dataBuffer[y * width + x];
    }

    private static int[] convertToPixels(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixelArray = new int[width * height];
        image.getRGB(0, 0, width, height, pixelArray,0,width);
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
