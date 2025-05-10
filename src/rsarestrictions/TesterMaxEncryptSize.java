package rsarestrictions;

import rsarestrictions.RSAMaxEncryptSize;
import util.Util;

import java.util.Arrays;

public class TesterMaxEncryptSize {
    public static void main(String[] args) throws Exception {
        int[] keySizes = {1024, 2048, 3072, 4096};

        for (int keySize : keySizes) {
            int maxBlockSize = RSAMaxEncryptSize.getMaxEncryptableSize(keySize);
            System.out.println("Clave RSA " + keySize + " bits: máximo tamaño cifrable = " + maxBlockSize + " bytes");
        }
    }
}
