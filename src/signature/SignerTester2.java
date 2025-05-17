package signature;

import util.Util;

import java.io.IOException;
import java.security.*;

public class SignerTester2 {
    public static void main(String[] args) throws
            NoSuchAlgorithmException,
            InvalidKeyException,
            SignatureException,
            IOException {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String filename = "archivos/pacr42.pdf";
        byte[] digitalSignature = Signer.signFile(filename, "SHA256withRSA", privateKey);
        System.out.println(Util.byteArrayToHexString(digitalSignature, ""));

        boolean isVerified = Signer.verifyFileSignature(filename, "SHA256withRSA", publicKey, digitalSignature);
        System.out.println("Firma verificada: " + isVerified);
    }

}
