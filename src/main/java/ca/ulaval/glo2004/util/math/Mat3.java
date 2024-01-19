package ca.ulaval.glo2004.util.math;


public class Mat3 {
    float[][] mat;

    public Mat3() {
        this.mat = new float[][]{
                {0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f}
        };
    }

    public Mat3(float[][] mat) {
        if (mat.length != 3 || mat[0].length != 3) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.mat = mat;
    }

    public Mat3 multiply(Mat3 _mat) {
        float[][] temp = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                temp[i][j] = dot_row_col(i, j, this, _mat);
            }
        }
        return new Mat3(temp);
    }

    public float getDeterminant() {
        return Matrix.getDeterminant(this.mat);
    }

    public Mat3 transpose() {
        return new Mat3(Matrix.transpose(this.mat));
    }

    public Mat3 inverse() {
        return new Mat3(Matrix.inverse(this.mat));
    }

    public Vec3 transform(Vec3 vec) {
        return new Vec3(vec.x * mat[0][0] + vec.y * mat[0][1] + vec.z * mat[0][2]
                , vec.x * mat[1][0] + vec.y * mat[1][1] + vec.z * mat[1][2]
                , vec.x * mat[2][0] + vec.y * mat[2][1] + vec.z * mat[2][2]
        );
    }


    public float[] transform(float[] vec) {
        if (vec.length != 3 || mat.length != 3 || mat[0].length != 3) {
            throw new IllegalArgumentException("Invalid vector or matrix dimensions");
        }

        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = vec[0] * mat[i][0] + vec[1] * mat[i][1] + vec[2] * mat[i][2];
        }
        return result;
    }

    static private float dot_row_col(int row, int col, Mat3 m_row, Mat3 m_col) {
        return m_row.mat[row][0] * m_col.mat[0][col]
                + m_row.mat[row][1] * m_col.mat[1][col]
                + m_row.mat[row][2] * m_col.mat[2][col];
    }

    public static Mat3 identity() {
        return new Mat3(new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1},
        });
    }
//coordonnée non homogène
    public static Mat3 rotation(Vec3 rotation) {
        float cosX = (float) Math.cos(rotation.x);
        float sinX = (float) Math.sin(rotation.x);

        float cosY = (float) Math.cos(rotation.y);
        float sinY = (float) Math.sin(rotation.y);

        float cosZ = (float) Math.cos(rotation.z);
        float sinZ = (float) Math.sin(rotation.z);

        // Create rotation matrices for each axis
        Mat3 rotationX = new Mat3(new float[][]{
                {1, 0, 0},
                {0, cosX, -sinX},
                {0, sinX, cosX},
        });

        Mat3 rotationY = new Mat3(new float[][]{
                {cosY, 0, sinY},
                {0, 1, 0},
                {-sinY, 0, cosY},
        });

        Mat3 rotationZ = new Mat3(new float[][]{
                {cosZ, -sinZ, 0},
                {sinZ, cosZ, 0},
                {0, 0, 1},
        });


        return rotationZ.multiply(rotationY.multiply(rotationX));
    }


    public static Mat3 scale(Vec3 scale) {
        return new Mat3(new float[][]{
                {scale.x, 0, 0},
                {0, scale.y, 0},
                {0, 0, scale.z},
        });
    }

    public static Mat3 lerp(Mat3 mat1, Mat3 mat2, float t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("Interpolation factor t must be between 0 and 1.");
        }

        float[][] result = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = mat1.mat[i][j] * (1 - t) + mat2.mat[i][j] * t;
            }
        }

        return new Mat3(result);
    }

}
