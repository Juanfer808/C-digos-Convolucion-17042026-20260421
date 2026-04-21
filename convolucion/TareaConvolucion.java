package convolucion;

public class TareaConvolucion extends Thread {
    private float[][] input;   // Matriz de entrada (lectura)
    private float[][] kernel;  // Filtro (lectura)
    private float[][] output;  // Matriz de salida (escritura)
    private int filaInicio;    // primer índice que procesa esta hebra
    private int filaFin;       // último índice

    public TareaConvolucion(float[][] input, float[][] kernel, float[][] output, int filaInicio, int filaFin) {
        this.input = input;
        this.kernel = kernel;
        this.output = output;
        this.filaInicio = filaInicio;
        this.filaFin = filaFin;
    }

    @Override
    public void run() {
        // Recorresmos una hebra
        for (int i = filaInicio; i < filaFin; i++) {
            // Recorremos una fila de la matriz de entrada
            for (int j = 0; j < input[0].length; j++) {
                float sum = 0; // Variable auxiliar para sumar los resultados de la convolución
                // Alineación del kernel
                int centerx = kernel.length/2; // Centro eje x del kernel
                int centery = kernel[0].length/2; // Centro eje y del kernel
                for (int k = 0; k < kernel.length; k++) {           
                    for (int l = 0; l < kernel[0].length; l++) {
                        int x = i + k - centerx;
                        int y = j + l - centery;
                        if (x >= 0 && x < input.length && y >= 0 && y < input[0].length) {
                            sum += input[x][y] * kernel[k][l];
                        }
                    }
                }                
                output[i][j] = sum;
            } 
        }
    }
    
}
