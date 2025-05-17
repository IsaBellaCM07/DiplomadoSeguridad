package analisisdesempeño;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.util.Arrays;

public class RSACipher {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Cipher cipher;

    public RSACipher() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
        this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    }

    public void encrypt(File inputFile, File outputFile) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[245];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] block = Arrays.copyOf(buffer, bytesRead);
                byte[] encrypted = cipher.doFinal(block);
                fos.write(encrypted);
            }
        }
    }

    public void decrypt(File inputFile, File outputFile) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[256];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] block = Arrays.copyOf(buffer, bytesRead);
                byte[] decrypted = cipher.doFinal(block);
                fos.write(decrypted);
            }
        }
    }
}
