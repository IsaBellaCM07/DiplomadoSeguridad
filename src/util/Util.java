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
}
