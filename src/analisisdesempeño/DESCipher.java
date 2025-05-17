package analisisdesempeño;

import javax.crypto.*;
import java.io.*;
import java.security.*;

public class DESCipher {
    private SecretKey secretKey;
    private Cipher cipher;

    public DESCipher() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        this.secretKey = keyGen.generateKey();
        this.cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    }

    public void encrypt(File inputFile, File outputFile) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    public void decrypt(File inputFile, File outputFile) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        try (FileInputStream fis = new FileInputStream(inputFile);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }
}
