package publickeycipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyCipherTester4 {
    public static void main(String[] args) throws Exception {
        // 1. Leer llaves desde archivo
        byte[] publicBytes = readFromFile("public.key");
        byte[] privateBytes = readFromFile("private.key");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));

        // 2. Leer mensaje cifrado
        byte[] encrypted = readFromFile("mensaje.enc");

        // 3. Descifrar mensaje con la llave privada
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
        String mensajeDescifrado = cipher.decryptMessage(encrypted, privateKey);

        System.out.println("Mensaje descifrado: " + mensajeDescifrado);
    }

    private static byte[] readFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(data);
        fis.close();
        return data;
    }
}
