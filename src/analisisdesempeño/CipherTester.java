package analisisdesempeño;

import java.io.File;

public class CipherTester {
    public static void main(String[] args) throws Exception {

        String method = "DES";

        File inputFile = new File("archivos/Imagenes.zip");

        // Carpeta de salida según el método
        String baseOutputDir = "archivos/" + method + "/";
        File outputDir = new File(baseOutputDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();  // Crear carpeta si no existe
        }

        File encryptedFile = new File(outputDir, "ImagenesEncriptado.bin");
        File decryptedFile = new File(outputDir, "ImagenesDesencriptado.zip");

        System.out.println("Buscando archivo: " + inputFile.getCanonicalPath());
        if (!inputFile.exists()) {
            System.out.println("ERROR: archivo no encontrado!");
            return;
        }

        long startEnc, endEnc, startDec, endDec;

        switch (method) {
            case "DES":
                DESCipher desCipher = new DESCipher();

                startEnc = System.nanoTime();
                desCipher.encrypt(inputFile, encryptedFile);
                endEnc = System.nanoTime();

                startDec = System.nanoTime();
                desCipher.decrypt(encryptedFile, decryptedFile);
                endDec = System.nanoTime();

                System.out.printf("DES Encriptar: %.3f segundos%n", (endEnc - startEnc) / 1e9);
                System.out.printf("DES Desencriptar: %.3f segundos%n", (endDec - startDec) / 1e9);
                break;

            case "RSA":
                RSACipher rsaCipher = new RSACipher();

                startEnc = System.nanoTime();
                rsaCipher.encrypt(inputFile, encryptedFile);
                endEnc = System.nanoTime();

                startDec = System.nanoTime();
                rsaCipher.decrypt(encryptedFile, decryptedFile);
                endDec = System.nanoTime();

                System.out.printf("RSA Encriptar: %.3f segundos%n", (endEnc - startEnc) / 1e9);
                System.out.printf("RSA Desencriptar: %.3f segundos%n", (endDec - startDec) / 1e9);
                break;

            default:
                System.out.println("Método no válido. Use DES o RSA.");
        }
    }
}
