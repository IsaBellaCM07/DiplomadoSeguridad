package rsarestrictions;

import util.Util;

import java.util.Arrays;
import java.util.Random;

public class TesterSplitJoin {
    public static void main(String[] args) {
        // Paso 1: Crear un arreglo de bytes original con datos aleatorios
        byte[] original = new byte[50];
        new Random().nextBytes(original);
        System.out.println("Arreglo original:");
        System.out.println(Arrays.toString(original));

        // Paso 2: Dividir el arreglo en fragmentos de tamaño definido
        int blockSize = 12;
        byte[][] partes = Util.split(original, blockSize);
        System.out.println("\nFragmentos después de split():");

        for (int i = 0; i < partes.length; i++) {
            System.out.println("Parte " + (i + 1) + ": " + Arrays.toString(partes[i]));
        }

        // Paso 3: Unir los fragmentos con join()
        byte[] reconstruido = Util.join(partes);
        System.out.println("\nArreglo reconstruido con join():");
        System.out.println(Arrays.toString(reconstruido));

        // Paso 4: Comparar si el arreglo reconstruido es igual al original
        boolean iguales = Arrays.equals(original, reconstruido);
        System.out.println("\n¿Los arreglos son iguales? " + (iguales ? "Sí" : "No"));
    }
}
