package ca.ulaval.glo2004.util.math;


import java.util.Arrays;

public class Mat4 {
    float [][] mat;

    public Mat4() {
        this.mat = new float[][]{
                {0.0f,0.0f,0.0f,0.0f},
                {0.0f,0.0f,0.0f,0.0f},
                {0.0f,0.0f,0.0f,0.0f},
                {0.0f,0.0f,0.0f,0.0f}
        };
    }
    public Mat4(float[][] mat) {
        if(mat.length != 4 || mat[0].length != 4)
        {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.mat = mat;
    }

    public Mat4 multiply(Mat4 _mat)
    {
        float[][] temp = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                temp[i][j] = dot_row_col(i, j, this, _mat);
            }
        }
        return new Mat4(temp);
    }

    public Mat4 multiply(float factor)
    {
        float[][] temp = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                temp[i][j] = mat[i][j]*factor;
            }
        }
        return new Mat4(temp);
    }

    public float getDeterminant(){
        return Matrix.getDeterminant(this.mat);
    }

    public Mat4 transpose(){
        return new Mat4(Matrix.transpose(this.mat));
    }

    public Mat4 inverse(){
        return new Mat4(Matrix.inverse(this.mat));
    }

    public Vec4 transform(Vec4 vec){
       return new Vec4(vec.x*mat[0][0] + vec.y*mat[0][1] + vec.z*mat[0][2] + vec.w*mat[0][3]
               ,vec.x*mat[1][0] + vec.y*mat[1][1] + vec.z*mat[1][2] + vec.w*mat[1][3]
               ,vec.x*mat[2][0] + vec.y*mat[2][1] + vec.z*mat[2][2] + vec.w*mat[2][3]
               ,vec.x*mat[3][0] + vec.y*mat[3][1] + vec.z*mat[3][2] + vec.w*mat[3][3]
       );
    }


    public float[] transform(float[] vec) {
        if (vec.length != 4 || mat.length != 4 || mat[0].length != 4) {
            throw new IllegalArgumentException("Invalid vector or matrix dimensions");
        }

        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            result[i] = vec[0] * mat[i][0] + vec[1] * mat[i][1] + vec[2] * mat[i][2] + vec[3] * mat[i][3];
        }
        return result;
    }

    static private float dot_row_col(int row, int col,Mat4 m_row, Mat4 m_col)
    {
        return m_row.mat[row][0] * m_col.mat[0][col]
                + m_row.mat[row][1] * m_col.mat[1][col]
                + m_row.mat[row][2] * m_col.mat[2][col]
                + m_row.mat[row][3] * m_col.mat[3][col];
    }

    public static Mat4 identity()
    {
        return new Mat4(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Mat4 rotation(Vec3 rotation) {
        float cosX = (float) Math.cos(rotation.x);
        float sinX = (float) Math.sin(rotation.x);

        float cosY = (float) Math.cos(rotation.y);
        float sinY = (float) Math.sin(rotation.y);

        float cosZ = (float) Math.cos(rotation.z);
        float sinZ = (float) Math.sin(rotation.z);

        // Create rotation matrices for each axis
        Mat4 rotationX = new Mat4(new float[][] {
                { 1, 0, 0, 0 },
                { 0, cosX, -sinX, 0 },
                { 0, sinX, cosX, 0 },
                { 0, 0, 0, 1 }
        });

        Mat4 rotationY = new Mat4(new float[][] {
                { cosY, 0, sinY, 0 },
                { 0, 1, 0, 0 },
                { -sinY, 0, cosY, 0 },
                { 0, 0, 0, 1 }
        });

        Mat4 rotationZ = new Mat4(new float[][] {
                { cosZ, -sinZ, 0, 0 },
                { sinZ, cosZ, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
        });


        return rotationZ.multiply(rotationY.multiply(rotationX));
    }

    public static Mat4 translation(Vec3 translation)
    {
        return new Mat4(new float[][]{
                {1, 0, 0, translation.x},
                {0, 1, 0, translation.y},
                {0, 0, 1, translation.z},
                {0, 0, 0, 1}
        });
    }

    public static Mat4 scale(Vec3 scale)
    {
        return new Mat4(new float[][]{
                {scale.x, 0, 0, 0},
                {0, scale.y, 0, 0},
                {0, 0, scale.z, 0},
                {0, 0, 0, 1}
        });
    }

    public static Mat4 lerp(Mat4 mat1, Mat4 mat2, float t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("Interpolation factor t must be between 0 and 1.");
        }

        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = mat1.mat[i][j] * (1 - t) + mat2.mat[i][j] * t;
            }
        }

        return new Mat4(result);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (float[] floats : mat) {
            out.append(Arrays.toString(floats)).append("\n");
        }

        return out.toString();
    }
}
