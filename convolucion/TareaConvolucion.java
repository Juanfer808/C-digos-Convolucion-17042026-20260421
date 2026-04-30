package convolucion;

public class TareaConvolucion implements Runnable {
    private float[][] input;   // Matriz de entrada (lectura)
    private float[][] kernel;  // Filtro (lectura)
    private float[][] output;  // Matriz de salida (escritura)
    private int fila;

    public TareaConvolucion(float[][] input, float[][] kernel, float[][] output, int fila) {
        this.input = input;
        this.kernel = kernel;
        this.output = output;
        this.fila = fila;
    }

    @Override
    public void run() {
        int columnas = input[0].length;
        int centerx = kernel.length / 2;
        int centery = kernel[0].length / 2;

        // Procesa únicamente los píxeles de la fila asignada
        for (int j = 0; j < columnas; j++) {
            float sum = 0; 
            
            for (int k = 0; k < kernel.length; k++) {           
                for (int l = 0; l < kernel[0].length; l++) {
                    int x = fila + k - centerx; 
                    int y = j + l - centery;
                    
                    if (x >= 0 && x < input.length && y >= 0 && y < columnas) {
                        sum += input[x][y] * kernel[k][l];
                    }
                }
            }                
            output[fila][j] = sum; 
        }
    }
    
}
