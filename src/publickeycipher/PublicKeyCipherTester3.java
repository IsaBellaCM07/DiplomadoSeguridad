package publickeycipher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PublicKeyCipherTester3 {
    public static void main(String[] args) throws Exception {
        // 1. Generar par de llaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 2. Cifrar un mensaje con la llave pública
        String mensaje = "Lab02: API de Java - Criptografía de llave pública.";
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
        byte[] encrypted = cipher.encryptMessage(mensaje, publicKey);

        // 3. Guardar llaves y mensaje cifrado en archivos
        saveToFile("src/publickeycipher/archivos/public.key", publicKey.getEncoded());
        saveToFile("src/publickeycipher/archivos/private.key", privateKey.getEncoded());
        saveToFile("src/publickeycipher/archivos/mensaje.enc", encrypted);

        System.out.println("Llaves y mensaje cifrado guardados correctamente.");
    }

    private static void saveToFile(String fileName, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(data);
        fos.close();
    }
}
