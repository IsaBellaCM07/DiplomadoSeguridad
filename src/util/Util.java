package util;

import base64.Base64;

import java.io.*;
import java.security.Key;

public class Util {
    //Lab02 - API de Java para el cálculo de Funciones Hash
    public static String byteArrayToHexString(byte[] bytes, String separator) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            result += String.format("%02x", bytes[i]) + separator;
        }
        return result;
    }

    //Lab02 - API de Java - Criptografía de llave simétrica
    public static void saveObject(Object o, String fileName) throws
            IOException {
        FileOutputStream fileOut;
        ObjectOutputStream out;

        fileOut = new FileOutputStream(fileName);
        out = new ObjectOutputStream(fileOut);
        out.writeObject(o);
        out.flush();
        out.close();
    }

    //Lab02 - API de Java - Criptografía de llave simétrica
    public static Object loadObject (String fileName) throws
            IOException,
            ClassNotFoundException,
            InterruptedException {
        FileInputStream fileIn;
        ObjectInputStream in;

        fileIn = new FileInputStream(fileName);
        in = new ObjectInputStream(fileIn);

        Thread.sleep(100);

        Object o = in.readObject();

        fileIn.close();
        in.close();

        return o;
    }

    //Lab02 - API de Java - Criptografía de llave simétrica
    public static byte[] objectToByteArray(Object o) throws
            IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(o);
        out.close();
        byte[] buffer = bos.toByteArray();

        return buffer;
    }

    //Lab02 - API de Java - Criptografía de llave simétrica
    public static Object byteArrayToObject(byte[] byteArray) throws
            IOException,
            ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        Object o = in.readObject();
        in.close();

        return o;
    }

    //Lab02: API de Java - Criptografía de llave pública
    public static void printKeyInPEMFormat(Key key, String keyType) {
        String encodedKey = Base64.encode(key.getEncoded());

        String header = "";
        String footer = "";

        if (keyType.equalsIgnoreCase("public")) {
            header = "-----BEGIN PUBLIC KEY-----";
            footer = "-----END PUBLIC KEY-----";
        } else if (keyType.equalsIgnoreCase("private")) {
            header = "-----BEGIN PRIVATE KEY-----";
            footer = "-----END PRIVATE KEY-----";
        } else {
            throw new IllegalArgumentException("Tipo de llave no reconocido: " + keyType);
        }

        System.out.println(header);

        // Imprime en líneas de 64 caracteres como lo exige el formato PEM
        int index = 0;
        while (index < encodedKey.length()) {
            int endIndex = Math.min(index + 64, encodedKey.length());
            System.out.println(encodedKey.substring(index, endIndex));
            index = endIndex;
        }

        System.out.println(footer);
    }

    public static byte[][] split(byte[] input, int blockSize) {
        if (blockSize <= 0) {
            throw new IllegalArgumentException("El tamaño del bloque debe ser mayor que 0");
        }

        int totalLength = input.length;
        int fullBlocks = totalLength / blockSize;
        int remainder = totalLength % blockSize;
        int numBlocks = (remainder == 0) ? fullBlocks : fullBlocks + 1;

        byte[][] output = new byte[numBlocks][];

        for (int i = 0; i < fullBlocks; i++) {
            output[i] = new byte[blockSize];
            for (int j = 0; j < blockSize; j++) {
                output[i][j] = input[i * blockSize + j];
            }
        }

        // Último bloque (si hay resto)
        if (remainder != 0) {
            output[numBlocks - 1] = new byte[remainder];
            for (int j = 0; j < remainder; j++) {
                output[numBlocks - 1][j] = input[fullBlocks * blockSize + j];
            }
        }

        return output;
    }

    public static byte[] join(byte[][] input) {
        // Paso 1: Calcular la longitud total del arreglo de salida
        int totalLength = 0;
        for (byte[] fragment : input) {
            totalLength += fragment.length;
        }

        // Paso 2: Crear arreglo destino y copiar los valores
        byte[] result = new byte[totalLength];
        int currentPos = 0;

        for (byte[] fragment : input) {
            for (byte b : fragment) {
                result[currentPos++] = b;
            }
        }

        return result;
    }
}
