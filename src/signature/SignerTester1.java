package signature;

import util.Util;

import java.security.*;

public class SignerTester1 {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String message = "Fundamentos de seguridad digital";
        byte[] digitalSignature = Signer.signMessage(message, "SHA256withRSA", privateKey);

        System.out.println(Util.byteArrayToHexString(digitalSignature, ""));

        boolean isVerified = Signer.verifyMessageSignature(message, "SHA256withRSA", publicKey, digitalSignature);
        System.out.println("Firma verificada: " + isVerified);
    }
}
