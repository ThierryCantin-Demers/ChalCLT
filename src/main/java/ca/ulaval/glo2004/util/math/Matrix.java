package ca.ulaval.glo2004.util.math;

/**
 * Class with package level helper functions for 4x4 and 3x3 matrix
 *
 * @author Patrick
 */
public class Matrix {
    static float getDeterminant(float[][] mat) {
        if (mat.length < 1) {
            throw new IllegalArgumentException("empty matrix");
        }
        if (mat.length != mat[0].length) {
            throw new IllegalArgumentException("rectangular matrix");
        }
        if (mat.length == 1) {
            return mat[0][0];
        }
        float det = 0.0f;
        for (int i = 0; i < mat.length; i++) {
            det += mat[0][i] * cofactor(mat, 0, i);
        }
        return det;
    }

    static float[][] transpose(float[][] mat) {
        float[][] temp = new float[mat[0].length][mat.length];
        for (int i = 0; i < mat[0].length; i++) {
            for (int j = 0; j < mat.length; j++) {
                temp[i][j] = mat[j][i];
            }
        }
        return temp;
    }

    public static float[][] fastInverse(float[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        float[][] augmentedMatrix = augmentMatrix(matrix);

        // Gauss-jordan eletric boogaloo
        for (int i = 0; i < rows; i++) {
            if (augmentedMatrix[i][i] == 0.0f) {
                int swapRow = findNonZeroRow(augmentedMatrix, i);

                if (swapRow == -1) {
                    throw new IllegalArgumentException("Matrice singulière, too bad!");
                }

                swapRows(augmentedMatrix, i, swapRow);
            }

            float diagonalElement = augmentedMatrix[i][i];
            scaleRow(augmentedMatrix, i, 1 / diagonalElement);

            for (int j = 0; j < rows; j++) {
                if (j != i) {
                    float factor = augmentedMatrix[j][i];
                    rowOperation(augmentedMatrix, j, i, -factor);
                }
            }
        }

        float[][] invertedMatrix = new float[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(augmentedMatrix[i], cols, invertedMatrix[i], 0, cols);
        }

        return invertedMatrix;
    }

    private static int findNonZeroRow(float[][] matrix, int col) {
        int rows = matrix.length;
        for (int i = col + 1; i < rows; i++) {
            if (matrix[i][col] != 0.0f) {
                return i;
            }
        }
        return -1;
    }

    private static void swapRows(float[][] matrix, int row1, int row2) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            float temp = matrix[row1][i];
            matrix[row1][i] = matrix[row2][i];
            matrix[row2][i] = temp;
        }
    }


    private static float[][] augmentMatrix(float[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        float[][] augmentedMatrix = new float[rows][2 * cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, augmentedMatrix[i], 0, cols);
            augmentedMatrix[i][cols + i] = 1.0f;  // Identity matrix part
        }

        return augmentedMatrix;
    }

    private static void scaleRow(float[][] matrix, int rowIndex, float scalar) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[rowIndex][i] *= scalar;
        }
    }

    private static void rowOperation(float[][] matrix, int targetRowIndex, int sourceRowIndex, float factor) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[targetRowIndex][i] += factor * matrix[sourceRowIndex][i];
        }
    }

    static float[][] inverse(float[][] mat) {
        float[][] adjointMatrix = new float[mat.length][mat.length];
        float det = getDeterminant(mat);
        if (det == 0) {
            throw new IllegalArgumentException("singular matrix, not invertible");
        }
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                int cofactor = (i + j) % 2 == 0 ? 1 : -1;
                adjointMatrix[j][i] = cofactor * minor(mat, i, j) / det;
            }
        }
        return adjointMatrix;
    }

    static float[][] multiply(float[][] mat1, float[][] mat2) {
        float[][] temp = new float[mat1.length][mat2[0].length];
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat2[0].length; j++) {
                temp[i][j] = dot_row_col(i, j, mat1, mat2);
            }
        }
        return temp;
    }

    private static float dot_row_col(int row, int col, float[][] mat_r, float[][] mat_c) {
        float temp = 0;
        for (int i = 0; i < mat_r[0].length; i++) {
            temp += mat_r[row][i] * mat_c[i][col];
        }
        return temp;
    }

    private static float cofactor(float[][] mat, int row, int col) {
        int sign = ((row + col) % 2 == 0) ? 1 : -1;
        return sign * minor(mat, row, col);
    }

    private static float minor(float[][] mat, int row, int col) {
        return getDeterminant(minorMatrix(mat, row, col));
    }

    private static float[][] minorMatrix(float[][] mat, int row, int col) {
        float[][] minorMatrix = new float[mat.length - 1][mat.length - 1];
        int minorRow, minorCol;
        for (int i = 0; i < mat.length; i++) {
            if (i == row) continue;
            minorRow = (i < row) ? i : i - 1;
            for (int j = 0; j < mat.length; j++) {
                if (j == col) continue;
                minorCol = (j < col) ? j : j - 1;
                minorMatrix[minorRow][minorCol] = mat[i][j];
            }
        }
        return minorMatrix;
    }
}
