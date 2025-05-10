package publickeycipher;

import util.Util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PublicKeyCipherTester5 {
    public static void main(String[] args) throws Exception {
        // 1. Crear par de llaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 2. Crear un objeto de prueba
        Persona objetoOriginal = new Persona("Isabella Cardozo", 21);

        // 3. Cifrar el objeto con la llave pública
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
        byte[] objetoCifrado = cipher.encryptObject(objetoOriginal, publicKey);

        // 4. Descifrar el objeto con la llave privada
        Object objetoRecuperado = cipher.decryptObject(objetoCifrado, privateKey);

        // 5. Mostrar el resultado
        System.out.println("Objeto original: " + objetoOriginal);
        System.out.println("Objeto recuperado: " + objetoRecuperado);
    }
}
