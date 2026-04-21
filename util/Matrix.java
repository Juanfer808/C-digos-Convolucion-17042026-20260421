package util;

public class Matrix {
    public static float[][] normalize(float[][] matrix, float min, float max) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = (matrix[i][j] - min) / (max - min);
            }
        }
        return matrix;
    }

    public static float[][] scale(float[][] matrix, float factor) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = matrix[i][j] * factor;
            }
        }
        return matrix;
    }

    public static float[][] truncate(float[][] matrix, float min, float max) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = Math.max(min, Math.min(matrix[i][j], max));
            }
        }
        return matrix;
    }

    public static int[][] toInt(float[][] matrix) {
        int[][] result = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = (int) matrix[i][j];
            }
        }
        return result;
    }

    public static float[][] toFloat(int[][] matrix) {
        float[][] result = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = matrix[i][j];
            }
        }
        return result;
    }

    public static float[][] copy(float[][] matrix) {
        float [][] newMatrix = new float[matrix.length][];
        for(int i = 0; i < matrix.length; i++)
            newMatrix[i] = matrix[i].clone();
        return newMatrix;
    }
}
