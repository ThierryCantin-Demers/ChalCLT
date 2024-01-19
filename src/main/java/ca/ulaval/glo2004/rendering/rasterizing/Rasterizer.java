package ca.ulaval.glo2004.rendering.rasterizing;

import ca.ulaval.glo2004.rendering.IView;
import ca.ulaval.glo2004.rendering.RenderingStats;
import ca.ulaval.glo2004.util.math.MathUtils;

public class Rasterizer {

    private IView view;

    private RenderingStats renderingStats;

    public Rasterizer(RenderingStats renderingStats)
    {
        this.renderingStats = renderingStats;
    }

    public void bindView(IView view)
    {
        this.view = view;
    }

    public void drawPixel(int x, int y, float z, int argb)
    {
        if (view == null)
            return;

        this.view.setPixel(x, y, z, argb);
    }

    public void drawHorizontalLine(int x1, int x2, int y, float z1, float z2, int argb) {
        float difX, difZ;
        float px, pz;

        this.view.setPixel(x1, y, z1, argb);
        difX = x2 - x1;
        difZ = z2 - z1;

        if (x1 > x2) {
            for (px = x2 + 1; px < x1; px++) {
                pz = z1 + ((px - x1) * difZ / difX);
                this.view.setPixel((int) px, y, pz, argb);
            }
        } else {
            for (px = x1 + 1; px < x2; px++) {
                pz = z1 + ((px - x1) * difZ / difX);
                this.view.setPixel((int) px, y, pz, argb);
            }
        }

        this.view.setPixel(x2, y, z2, argb);
    }

    private void drawBottomFlatTriangle(int x1, int y1, float z1, int x2, int y2, float z2, int x3, int y3, float z3, int argb)
    {
        float slope1 = (float)(x2 - x1) / (float)(y2 - y1);
        float slope2 = (float)(x3 - x1) / (float)(y3 - y1);
        float zSlope1 = (z2 - z1) / (float)(y2 - y1);
        float zSlope2 = (z3 - z1) / (float)(y3 - y1);

        float curx1 = x1;
        float curx2 = x1;
        float curz1 = z1;
        float curz2 = z1;

        for (int scanlineY = y1; scanlineY <= y2; scanlineY++)
        {
            drawHorizontalLine((int)curx1, (int)curx2, scanlineY, curz1, curz2, argb);
            curx1 += slope1;
            curx2 += slope2;
            curz1 += zSlope1;
            curz2 += zSlope2;

        }
    }

    private void drawTopFlatTriangle(int x1, int y1, float z1, int x2, int y2, float z2, int x3, int y3, float z3, int argb) {
        float slope1 = (float)(x3 - x1) / (float)(y3 - y1);
        float slope2 = (float)(x3 - x2) / (float)(y3 - y2);
        float zSlope1 = (z3 - z1) / (float)(y3 - y1);
        float zSlope2 = (z3 - z2) / (float)(y3 - y2);

        float curx1 = x3;
        float curx2 = x3;
        float curz1 = z3;
        float curz2 = z3;

        for (int scanlineY = y3; scanlineY > y1; scanlineY--) {
            drawHorizontalLine((int)curx1, (int)curx2, scanlineY, curz1, curz2, argb);
            curx1 -= slope1;
            curx2 -= slope2;
            curz1 -= zSlope1;
            curz2 -= zSlope2;
        }
    }

    public void drawTriangle2(int x1, int y1, float z1, int x2, int y2, float z2, int x3, int y3, float z3, int argb)
    {
        if (y1 > y2)
        {
            x2 = x2 ^ x1;
            x1 = x2 ^ x1;
            x2 = x2 ^ x1;

            y2 = y2 ^ y1;
            y1 = y2 ^ y1;
            y2 = y2 ^ y1;

            float temp = z2;
            z2 = z1;
            z1 = temp;
        }

        if (y2 > y3)
        {
            x2 = x2 ^ x3;
            x3 = x2 ^ x3;
            x2 = x2 ^ x3;

            y2 = y2 ^ y3;
            y3 = y2 ^ y3;
            y2 = y2 ^ y3;

            float temp = z2;
            z2 = z3;
            z3 = temp;
        }

        if (y1 > y2)
        {
            x2 = x2 ^ x1;
            x1 = x2 ^ x1;
            x2 = x2 ^ x1;

            y2 = y2 ^ y1;
            y1 = y2 ^ y1;
            y2 = y2 ^ y1;

            float temp = z2;
            z2 = z1;
            z1 = temp;
        }

        if (y2 == y3)
        {
            drawBottomFlatTriangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, argb);
        }
        else if (y1 == y2)
        {
            drawTopFlatTriangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, argb);
        }
        else
        {
            int x4 = x1 + ((y2 - y1) * (x3 - x1)) / (y3 - y1);//(int)(x1 + ((float)(y2 - y1) / (float)(y3 - y1)) * (x3 - x1));
            int y4 = y2;
            float z4 = z1 + ((y2 - y1) * (z3 - z1)) / (y3 - y1);//(int)(z1 + ((float)(y2 - y1) / (float)(y3 - y1)) * (z3 - z1));

            drawBottomFlatTriangle(x1, y1, z1, x2, y2, z2, x4, y4, z4, argb);
            drawTopFlatTriangle(x2, y2, z2, x4, y4, z4, x3, y3, z3, argb);
        }
    }

    public void drawTriangleWireframe(int x1, int y1, float z1, int x2, int y2, float z2, int x3, int y3, float z3, int argb)
    {
        drawLine(x1, y1, x2, y2, argb);
        drawLine(x1, y1, x3, y3, argb);
        drawLine(x2, y2, x3, y3, argb);
    }

    /**
     * Source: <a href="https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java">...</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(int x1, int y1, int x2, int y2, int argb) {
        // delta of exact value and rounded value of the dependent variable
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        if (dx >= dy) {
            while (true) {
                drawPixel(x, y, 0, argb);
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (true) {
                drawPixel(x, y, 0, argb);
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
    }

    /**
     * Modified from:
     * Source: <a href="https://rosettacode.org/wiki/Bitmap/Bresenham%27s_line_algorithm#Java">...</a>
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine3D(int x1, int y1, float z1, int x2, int y2, float z2, int argb) {
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx;
        int dy2 = 2 * dy;

        int ix = x1 < x2 ? 1 : -1;
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;
        float z = z1;

        if (dx >= dy) {
            while (true) {
                drawPixel(x, y, z, argb);
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
                // Interpolate z-value
                z = z1 + (z2 - z1) * (x - x1) / (x2 - x1);
            }
        } else {
            while (true) {
                drawPixel(x, y, z, argb);
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
                // Interpolate z-value
                z = z1 + (z2 - z1) * (y - y1) / (y2 - y1);
            }
        }
    }
}
