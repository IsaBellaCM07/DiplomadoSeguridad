package rsarestrictions;

import publickeycipher.PublicKeyCipher;
import util.Util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tester3 {

    public static void main(String[] args) throws Exception {
        // 1. Crear par de claves
        int keySize = 2048;
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 2. Crear instancia del cifrador
        PublicKeyCipher pkCipher = new PublicKeyCipher("RSA/ECB/PKCS1Padding");

        // 3. Crear mensaje largo
        int maxRSABytes = RSAMaxEncryptSize.getMaxEncryptableSize(keySize);
        String texto = RSAMaxEncryptSize.generateRandomString(maxRSABytes * 3 + 20);
        System.out.println("Texto original:\n" + texto);
        byte[] original = Util.objectToByteArray(texto);

        // 4. Dividir el mensaje en fragmentos
        byte[][] partes = Util.split(original, maxRSABytes);

        // 5. Cifrar cada parte usando PublicKeyCipher
        List<byte[]> partesCifradas = new ArrayList<>();
        for (byte[] parte : partes) {
            byte[] cifrado = pkCipher.encryptByteArray(parte, publicKey);
            partesCifradas.add(cifrado);
        }

        // 6. Descifrar cada parte usando PublicKeyCipher
        List<byte[]> partesDescifradas = new ArrayList<>();
        for (byte[] parteCifrada : partesCifradas) {
            Object textoDescifrado = pkCipher.decryptByteArray(parteCifrada, privateKey);
            partesDescifradas.add((byte[]) textoDescifrado);
        }

        // 7. Unir las partes descifradas
        byte[][] arregloDescifrado = partesDescifradas.toArray(new byte[0][]);
        byte[] reconstruido = Util.join(arregloDescifrado);
        String reconstruidoTexto = Util.byteArrayToObject(reconstruido).toString();
        System.out.println("Texto reconstruido:\n" + reconstruidoTexto);

        // 8. Verificar si son iguales
        boolean iguales = Arrays.equals(original, reconstruido);
        System.out.println("¿Reconstrucción correcta? " + (iguales ? "Sí" : "No"));
    }


}
