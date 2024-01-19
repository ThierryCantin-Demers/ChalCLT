package ca.ulaval.glo2004.util.math;

import org.junit.Test;

import static org.junit.Assert.*;
import java.lang.Math;

public class MatrixTest {

    @Test
    public void rectangularDeterminant()
    {
        try {
            Matrix.getDeterminant(new float[][]{
                    {0.0f, 0.2f, 0.4f},
                    {0.0f, 0.2f, 0.4f},
                    {0.0f, 0.2f, 0.4f},
                    {0.0f, 0.2f, 0.4f}
            });
            fail("rectangular matrix but exception not caught");
            }
        catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void linearDepedantRows()
    {
        assertEquals(0f, Matrix.getDeterminant(new float[][]{
                {1f, 2f, 3f},
                {2f, 3f, 4f},
                {3f, 5f, 7f}
        }), 0.0);
    }

    @Test
    public void goodOl4x4MatrixDetermianant()
    {
        assertEquals(-1201.148f, Matrix.getDeterminant(new float[][]{
                {1.2f,2.1f,-1f,1.456f},
                {1.23f,1.2f,1.23f, -8.54f},
                {1.123f,12.2f,1.12f,-4f},
                {20.2f,43.2f,-1f,-1f}
        }),0.1);
    }

    @Test
    public void transposingRectangularMatrix()
    {
        assertArrayEquals(new float[][]{
            {3f,2f,4f,1f},
            {2f,1f,3f,2f},
            {5f,2f,1f,0f},
        },Matrix.transpose(new float[][]{
            {3f,2f,5f},
            {2f,1f,2f},
            {4f,3f,1f},
            {1f,2f,0f}
        }));
    }

    @Test
    public void invert2DRotationMatrix()
    {
        assertArrayEquals(
                new float[][]{
                        {0f,1f},
                        {-1f,0f}
                },
                Matrix.inverse(new float[][]{
                {0f,-1f},
                {1f,0f}
        }
        ));
    }
    private static boolean are2DArraysEqual(float[][] expected, float[][] actual, float delta) {
        if (expected.length != actual.length || expected[0].length != actual[0].length) {
            return false;
        }

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[0].length; j++) {
                if (Math.abs(expected[i][j] - actual[i][j]) > delta) {
                    return false;
                }
            }
        }
        return true;
    }
    @Test
    public void inverting3DScalingMatrix() {
        float[][] left = Matrix.fastInverse(new float[][]{
                {1f / 3f, 0f, 0f},
                {0f, 1f / 3f, 0f},
                {0, 0f, 1f / 3f}
        });
        float[][] right = new float[][]{
                {3f, 0f, 0f},
                {0f, 3f, 0f},
                {0f, 0f, 3f}
        };
        assertTrue(are2DArraysEqual(left, right, 0.01f));
    }


    @Test
    public void invertingRandomMatrix(){
        float[][] left = Matrix.fastInverse(new float[][]{
                {2f,3.4f,2.3f,-1.2f},
                {3.2f, 2f , 1.4f, 2f},
                {-1.3f, 2, -4, -1},
                {2f,3f,2f,1f}
        });
        float[][] right = new float[][]{
                {0.33428844317096466083f, 0.59694364851957975163f, 0.009551098376313276027f , -0.78319006685768863407f},
                {-0.05444126074498567332f, -0.19245463228271251194f, 0.12225405921680993313f, 0.44183381088825214897f},
                {-0.01910219675262655203f, -0.27220630372492836673f ,-0.19102196752626552053f, 0.33046800382043935049f},
                {-0.4670487106017191977f, -0.072110792741165233994f, -0.0038204393505253104106f, 0.57994269340974212034f}
        };
        assertTrue(are2DArraysEqual(left, right, 0.01f));
    }

    @Test
    public void invertingSingularMatrix()
    {
        try {
            Matrix.fastInverse(new float[][]{
                    {0.0f, 0.2f, 0.4f},
                    {0.0f, 0.2f, 0.4f},
                    {0.0f, 0.2f, 0.4f},
            });
            fail("singular matrix but exception not caught");
        }
        catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test public void simpleMarixMultiplication()
    {
        assertTrue(are2DArraysEqual(
                new float[][]{
                        {14f,8f,17},
                        {12f,7f,14f},
                        {14,9,17}
                },
                Matrix.multiply(new float[][]{
                        {1f,3f,2f},
                        {1f,2f,2f},
                        {1f,2f,3f}
                }, new float[][]{
                        {4f,1f,2f},
                        {2f,1f,3f},
                        {2f,2f,3f}
                }),0.01f
        ));
    }

}
