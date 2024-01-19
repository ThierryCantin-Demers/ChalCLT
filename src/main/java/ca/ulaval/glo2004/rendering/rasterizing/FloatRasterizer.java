package ca.ulaval.glo2004.rendering.rasterizing;

import ca.ulaval.glo2004.rendering.IView;
import ca.ulaval.glo2004.rendering.RenderingStats;
import ca.ulaval.glo2004.util.math.MathUtils;

public class FloatRasterizer {

    private IView view;
    private RenderingStats renderingStats;

    public FloatRasterizer(RenderingStats renderingStats) {
        this.renderingStats = renderingStats;
    }

    public void bindView(IView view) {
        this.view = view;
    }

    public void drawPixel(float x, float y, float z, int argb) {
        if (x < 0 || x >= view.getWidth()-1)
            return;

        if (y < 0 || y >= view.getHeight()-1)
            return;

        this.view.setPixel((int) x, (int) y, z, argb);
//        this.view.setDepth((int) x, (int) y, z);
    }

    public void drawHorizontalLine(float x1, float x2, float y, float z1, float z2, int argb) {
        float difX = x2 - x1;
        float difZ = z2 - z1;
        float px, pz;

        drawPixel(x1, y, z1, argb);

        if (x1 > x2) {
            for (px = x2 + 1; px < x1; px++) {
                pz = z1 + ((px - x1) * difZ / difX);
                drawPixel(px, y, pz, argb);
            }
        } else {
            for (px = x1 + 1; px < x2; px++) {
                pz = z1 + ((px - x1) * difZ / difX);
                drawPixel(px, y, pz, argb);
            }
        }

        drawPixel(x2, y, z2, argb);
    }

    private void drawBottomFlatTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int argb) {
        float slope1 = (x2 - x1) / (y2 - y1);
        float slope2 = (x3 - x1) / (y3 - y1);
        float zSlope1 = (z2 - z1) / (y2 - y1);
        float zSlope2 = (z3 - z1) / (y3 - y1);

        float curx1 = x1;
        float curx2 = x1;
        float curz1 = z1;
        float curz2 = z1;

        for (float scanlineY = y1; scanlineY <= y2; scanlineY++) {
            drawHorizontalLine(curx1, curx2, scanlineY, curz1, curz2, argb);
            curx1 += slope1;
            curx2 += slope2;
            curz1 += zSlope1;
            curz2 += zSlope2;
        }
    }

    private void drawTopFlatTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int argb) {
        float slope1 = (x3 - x1) / (y3 - y1);
        float slope2 = (x3 - x2) / (y3 - y2);
        float zSlope1 = (z3 - z1) / (y3 - y1);
        float zSlope2 = (z3 - z2) / (y3 - y2);

        float curx1 = x3;
        float curx2 = x3;
        float curz1 = z3;
        float curz2 = z3;

        for (float scanlineY = y3; scanlineY > y1; scanlineY--) {
            drawHorizontalLine(curx1, curx2, scanlineY, curz1, curz2, argb);
            curx1 -= slope1;
            curx2 -= slope2;
            curz1 -= zSlope1;
            curz2 -= zSlope2;
        }
    }

    public void drawTriangle2(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int argb) {
        if (y1 > y2) {
            float tempX = x2;
            float tempY = y2;
            float tempZ = z2;

            x2 = x1;
            y2 = y1;
            z2 = z1;

            x1 = tempX;
            y1 = tempY;
            z1 = tempZ;
        }

        if (y2 > y3) {
            float tempX = x2;
            float tempY = y2;
            float tempZ = z2;

            x2 = x3;
            y2 = y3;
            z2 = z3;

            x3 = tempX;
            y3 = tempY;
            z3 = tempZ;
        }

        if (y1 > y2) {
            float tempX = x2;
            float tempY = y2;
            float tempZ = z2;

            x2 = x1;
            y2 = y1;
            z2 = z1;

            x1 = tempX;
            y1 = tempY;
            z1 = tempZ;
        }

        if (y2 == y3) {
            drawBottomFlatTriangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, argb);
        } else if (y1 == y2) {
            drawTopFlatTriangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, argb);
        } else {
            float x4 = x1 + ((y2 - y1) * (x3 - x1)) / (y3 - y1);
            float y4 = y2;
            float z4 = z1 + ((y2 - y1) * (z3 - z1)) / (y3 - y1);

            drawBottomFlatTriangle(x1, y1, z1, x2, y2, z2, x4, y4, z4, argb);
            drawTopFlatTriangle(x2, y2, z2, x4, y4, z4, x3, y3, z3, argb);
        }
    }

    float cross2d(float x0, float y0, float x1, float y1)
    {
        return x0 * y1 - x1 * y0;
    }

    float lineSide2D(float px, float py, float pz, float sx, float sy, float sz, float ex, float ey, float ez)
    {
        return cross2d(px - sx, py - sy, ex - sx, ey - sy);
    }

    public void drawTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int argb)
    {
        if (y1 > y2) {
            float tempX = x2;
            float tempY = y2;
            float tempZ = z2;

            x2 = x1;
            y2 = y1;
            z2 = z1;

            x1 = tempX;
            y1 = tempY;
            z1 = tempZ;
        }

        if (y2 > y3) {
            float tempX = x2;
            float tempY = y2;
            float tempZ = z2;

            x2 = x3;
            y2 = y3;
            z2 = z3;

            x3 = tempX;
            y3 = tempY;
            z3 = tempZ;
        }

        if (y1 > y2) {
            float tempX = x2;
            float tempY = y2;
            float tempZ = z2;

            x2 = x1;
            y2 = y1;
            z2 = z1;

            x1 = tempX;
            y1 = tempY;
            z1 = tempZ;
        }

        // First case where triangles are like that:
        // P1
        // -
        // --
        // - -
        // -  -
        // -   - P2
        // -  -
        // - -
        // -
        // P3
//        if (dP1P2 > dP1P3)
        if (lineSide2D(x2, y2, z2, x1, y1, z1, x3, y3, z3) > 0)
        {
            for (int y = (int)y1; y <= (int)y3; y++)
            {
                if (y < y2)
                {
                    processScanline(y, x1, y1, z1, x3, y3, z3, x1, y1, z1, x2, y2, z2, argb);
                }
                else
                {
                    processScanline(y, x1, y1, z1, x3, y3, z3, x2, y2, z2, x3, y3, z3, argb);
                }
            }
        }
        // First case where triangles are like that:
        //       P1
        //        -
        //       --
        //      - -
        //     -  -
        // P2 -   -
        //     -  -
        //      - -
        //        -
        //       P3
        else
        {
            for (int y = (int)y1; y <= (int)y3; y++)
            {
                if (y < y2)
                {
                    processScanline(y, x1, y1, z1, x2, y2, z2, x1, y1, z1, x3, y3, z3, argb);
                }
                else
                {
                    processScanline(y, x2, y2, z2, x3, y3, z3, x1, y1, z1, x3, y3, z3, argb);
                }
            }
        }
    }


    void processScanline(int y, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz, int color)
    {
        // Check if y is within the Y range of the triangle
        if (y < Math.min(ay, Math.min(by, Math.min(cy, dy))) || y > Math.max(ay, Math.max(by, Math.max(cy, dy))))
            return;

        // Thanks to current Y, we can compute the gradient to compute others values like
        // the starting X (sx) and ending X (ex) to draw between
        // if pa.Y == pb.Y or pc.Y == pd.Y, gradient is forced to 1
        float gradient1 = ay != by ? (y - ay) / (by - ay) : 1;
        float gradient2 = cy != dy ? (y - cy) / (dy - cy) : 1;

        int sx = (int)MathUtils.lerp(ax, bx, gradient1);
        int ex = (int)MathUtils.lerp(cx, dx, gradient2);

        // starting Z & ending Z
        float z1 = MathUtils.lerp(az, bz, gradient1);
        float z2 = MathUtils.lerp(cz, dz, gradient2);

        // drawing a line from left (sx) to right (ex)
        for (float x = sx; x < ex; x++)
        {
            float gradient = (x - sx) / (float)(ex - sx);

            float z = MathUtils.lerp(z1, z2, gradient);
//            fragmentProcessor.onFragmentRasterized(new Vec3(x, y, z));
            this.drawPixel(x, y, z, color);
        }

    }

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
