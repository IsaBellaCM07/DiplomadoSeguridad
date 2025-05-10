package rsarestrictions;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Random;

public class RSAMaxEncryptSize {

    public static int getMaxEncryptableSize(int keySize) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        KeyPair keyPair = keyGen.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        int maxSuccessSize = 0;

        for (int size = 1; size < 1000; size++) {
            try {
                String randomText = generateRandomString(size);
                byte[] data = randomText.getBytes();
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                cipher.doFinal(data);
                maxSuccessSize = size;
            } catch (Exception e) {
                break;
            }
        }

        return maxSuccessSize;
    }

    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
