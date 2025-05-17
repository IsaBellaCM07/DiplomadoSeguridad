package signature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;

public class Signer {
    public static byte[] signMessage(String message, String algorithm, PrivateKey privateKey) throws
            NoSuchAlgorithmException,
            InvalidKeyException,
            SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }

    public static boolean verifyMessageSignature(String message, String algorithm, PublicKey publicKey, byte[] digitalSignature) throws
            NoSuchAlgorithmException,
            InvalidKeyException,
            SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(message.getBytes());


        return signature.verify(digitalSignature);
    }

    public static byte[] signFile(String filename, String algorithm, PrivateKey privateKey) throws
            IOException,
            NoSuchAlgorithmException,
            SignatureException,
            InvalidKeyException {
        File file = new File(filename);
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(fileBytes);


        return signature.sign();
    }

    public static boolean verifyFileSignature(String filename, String algorithm, PublicKey publicKey, byte[] digitalSignature) throws
            IOException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            SignatureException  {
        File file = new File(filename);
        byte[] fileBytes = Files.readAllBytes(file.toPath());


        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(fileBytes);


        return signature.verify(digitalSignature);
    }



}
