package convolucion;

import util.Matrix;
import util.Timer;

import java.io.IOException;

public class Conv2D {
    public static float[][] convolve(float[][] input, float[][] kernel) {
        float[][] output = new float[input.length][input[0].length];
        int kernelCenterX = kernel.length / 2;
        int kernelCenterY = kernel[0].length / 2;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                float sum = 0;
                for (int k = 0; k < kernel.length; k++) {
                    for (int l = 0; l < kernel[0].length; l++) {
                        int inputX = i + k - kernelCenterX;
                        int inputY = j + l - kernelCenterY;
                        float inputVal = 0;
                        if (inputX >= 0 && inputX < input.length && inputY >= 0 && inputY < input[0].length) {
                            inputVal = input[inputX][inputY];
                        }
                        sum += inputVal * kernel[k][l];
                    }
                }
                output[i][j] = sum;
            }
        }
        return output;
    }

    public static float[][] createGaussianKernel(int size, float sigma) {
        float[][] kernel = new float[size][size];
        int center = size / 2;
        float sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] = (float) Math.exp(-0.5 * (Math.pow((i - center) / sigma, 2.0) + Math.pow((j - center) / sigma, 2.0)));
                sum += kernel[i][j];
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] /= sum;
            }
        }
        return kernel;
    }

    public static float[][] createBoxKernel(int size) {
        float[][] kernel = new float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] = 1.0f / (size * size);
            }
        }
        return kernel;
    }

    public static float[][] createEdgeKernel() {
        float[][] kernel = {
                { 0, -1, 0 },
                { -1, 4, -1 },
                { 0, -1, 0 }
        };

        return kernel;
    }

    public static float[][] createEdgeKernel(int size) {
        float[][] kernel = new float[size][size];
        int center = size / 2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] = (i == center && j == center) ? 4 * center : -1;
            }
        }
        return kernel;
    }

    public static float[][] convolveParalelo(float[][] input, float[][] kernel, int numThreads) {
        // Creamos la matriz de salida y el array de hilos
        float[][] output = new float[input.length][input[0].length];
        Thread[] threads = new Thread[numThreads];
        int rowsPerThread = input.length / numThreads; // Cálculo de filas por hebra
        // Asignamos las filas a cada hilo
        for (int i = 0; i < numThreads; i++){
            int startRow = i * rowsPerThread; // Donde empieza la hebra seleccionada
            int endRow = -1; // Donde termina la hebra seleccionada
            // En caso de ser la última hebra, debe llegar al final de la imagen
            if (i == numThreads - 1) {
                endRow = input.length; // La última hebra procesa hasta el final
            } else {
                endRow = startRow + rowsPerThread; // Hebras intermedias procesan un bloque de filas
            } 
            // Creación y lanzamiento de la hebra
            threads[i] = new TareaConvolucion(input, kernel, output, startRow, endRow);
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    public static void main(String[] args) throws IOException {
        int[][][] pixels = util.Image.loadImage("resources/etsii.png");

        Timer t = new Timer();

        int[][] pixels_gray = util.Image.toGray(pixels);

        float[][] kernel = createBoxKernel(5);
        float[][] pixels_normalized = Matrix.normalize(Matrix.toFloat(pixels_gray), 0, 255);

        int numThreads = Runtime.getRuntime().availableProcessors();

        convolve(pixels_normalized, kernel);
        convolveParalelo(pixels_normalized, kernel, numThreads);

        t.start();
        float[][] output_normalized = convolve(pixels_normalized, kernel);
        t.stop();
        long timeSecuencial = t.getElapsedTime();
        System.out.println("Elapsed time (sequential): " + timeSecuencial + " ms");

        t.start();
        float[][] output_normalized_paralelo = convolveParalelo(pixels_normalized, kernel, numThreads);
        t.stop();
        long timeParalelo = t.getElapsedTime();
        System.out.println("Elapsed time (parallel): " + timeParalelo + " ms");

        float maxError = 0;
        for (int i = 0; i < output_normalized.length; i++) {
            for (int j = 0; j < output_normalized[0].length; j++) {
                maxError = Math.max(maxError, Math.abs(output_normalized[i][j] - output_normalized_paralelo[i][j]));
            }
        }
        System.out.println("Max error: " + maxError);

        double speedup = (double) timeSecuencial / timeParalelo;
        double efficiency = (speedup / numThreads)*100;
        System.out.println("Speedup: " + speedup);
        System.out.println("Efficiency: " + efficiency + "%");

        // Guardado de resultados

        float[][] outParNorm = Matrix.truncate(output_normalized_paralelo, 0, 1);
        int[][] outParInt = Matrix.toInt(Matrix.scale(outParNorm, 255));
        util.Image.saveImage("resources/etsii-blur-paralelo.png", util.Image.fromGray(outParInt));
        System.out.println("Blurred image (parallel) saved to resources/etsii-blur-paralelo.png");

        output_normalized = Matrix.truncate(output_normalized, 0, 1);
        int[][] output = Matrix.toInt(Matrix.scale(output_normalized, 255));
        util.Image.saveImage("resources/etsii-blur.png", util.Image.fromGray(output));
        System.out.println("Blurred image saved to resources/etsii-blur.png");

        kernel = createEdgeKernel();
        float[][] edges_paralelo = convolveParalelo(pixels_normalized, kernel, numThreads);
        float[][] edges_norm = Matrix.truncate(edges_paralelo, 0, 1);
        int[][] edges_int = Matrix.toInt(Matrix.scale(edges_norm, 255));
        
        output_normalized = convolve(pixels_normalized, kernel);
        output_normalized = Matrix.truncate(output_normalized, 0, 1);
        output = Matrix.toInt(Matrix.scale(output_normalized, 255));

        util.Image.saveImage("resources/etsii-edge.png", util.Image.fromGray(output));
        System.out.println("Edges saved to resources/etsii-edge.png");
        util.Image.saveImage("resources/etsii-edge-paralelo.png", util.Image.fromGray(edges_int));
        System.out.println("Edges (parallel) saved to resources/etsii-edge-paralelo.png");
    }
}
