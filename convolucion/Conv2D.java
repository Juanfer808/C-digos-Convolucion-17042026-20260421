package convolucion;

import util.Matrix;
import util.Timer;
import java.io.IOException;
import java.util.concurrent.*;

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

    public static float[][] convolveParalelo(float[][] input, float[][] kernel) {
        int filas = input.length;
        int columnas = input[0].length;
        float[][] output = new float[filas][columnas];
        
        int kernelCenterX = kernel.length / 2;
        int kernelCenterY = kernel[0].length / 2;

        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                cores, 
                cores * 2, 
                60L, TimeUnit.SECONDS, 
                new LinkedBlockingQueue<Runnable>()
        );

        for (int i = 0; i < filas; i++) {
            final int filaActual = i;
            pool.execute(() -> {
                for (int j = 0; j < columnas; j++) {
                    float sum = 0;
                    for (int k = 0; k < kernel.length; k++) {
                        for (int l = 0; l < kernel[0].length; l++) {
                            int inputX = filaActual + k - kernelCenterX;
                            int inputY = j + l - kernelCenterY;
                            
                            if (inputX >= 0 && inputX < filas && inputY >= 0 && inputY < columnas) {
                                sum += input[inputX][inputY] * kernel[k][l];
                            }
                        }
                    }
                    output[filaActual][j] = sum;
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static void main(String[] args) throws IOException {
        int[][][] pixels = util.Image.loadImage("resources/etsii.png");
        int[][] pixels_gray = util.Image.toGray(pixels);
        float[][] pixels_normalized = Matrix.normalize(Matrix.toFloat(pixels_gray), 0, 255);
        
        float[][] kernel = createBoxKernel(3);
        Timer t = new Timer();
        int cores = 8;

        t.start();
        float[][] output_normalized = convolve(pixels_normalized, kernel);
        t.stop();
        long timeSecuencial = t.getElapsedTime();
        System.out.println("Elapsed time (sequential): " + timeSecuencial + " ms");

        t.start();
        float[][] output_normalized_paralelo = convolveParalelo(pixels_normalized, kernel);
        t.stop();
        long timeParalelo = t.getElapsedTime();
        System.out.println("Elapsed time (parallel pool): " + timeParalelo + " ms");

        double speedup = (double) timeSecuencial / timeParalelo;
        System.out.println("Speedup: " + speedup);
        System.out.println("Efficiency: " + (speedup / cores) * 100 + "%");

        float[][] outParNorm = Matrix.truncate(output_normalized_paralelo, 0, 1);
        int[][] outParInt = Matrix.toInt(Matrix.scale(outParNorm, 255));
        util.Image.saveImage("resources/etsii-blur-paralelo.png", util.Image.fromGray(outParInt));
        System.out.println("Image saved to resources/etsii-blur-paralelo.png");
    }
}
