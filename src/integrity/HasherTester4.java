package integrity;

public class HasherTester4 {
    public static void main(String[] args) throws Exception {
        Hasher.checkIntegrityFile("sha256suma.txt", "archivos");
    }
}
