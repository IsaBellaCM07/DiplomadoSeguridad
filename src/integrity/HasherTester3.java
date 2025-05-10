package integrity;

public class HasherTester3 {
    public static void main(String[] args) throws Exception {
        Hasher.generateIntegrityCheckerFile("archivos", "sha256sum.txt");
    }
}
