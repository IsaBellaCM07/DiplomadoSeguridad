package file_transfer;

import integrity.Hasher;
import util.Files;

import java.net.Socket;

public class FileTransferClient {
    public static final int PORT = 4000;
    public static final String SERVER = "localhost";

    private Socket clientSideSocket;

    private String server;
    private int port;

    public FileTransferClient() {
        this.server = SERVER;
        this.port = PORT;
        System.out.println("Isabella Cardozo Marín - May 2/2025");
        System.out.println("File transfer client is running ... connecting the server in "+ this.server + ":" + this.port);
        System.out.println("Other usage: FileTransferClient host port. Ex: FileTransferClient localhost 3800");
    }

    public FileTransferClient(String server, int port) {
        this.server = server;
        this.port = port;
        System.out.println("Isabella Cardozo Marín - May 2/2025");
        System.out.println("Echo client is running ... connecting the server in "+ this.server + ":" + this.port);
    }

    public void init() throws Exception {
        clientSideSocket = new Socket(SERVER, PORT);

        protocol(clientSideSocket);
        clientSideSocket.close();
    }

    public void protocol(Socket socket) throws Exception {
//        Files.sendFile("Matricula.pdf", socket);
//        Files.receiveFile("Download", socket);
        Hasher.generateIntegrityCheckerFile("archivos", "archivos/sha256sum.txt");
        Hasher.generateIntegrityCheckerFile("archivos/sha256sum.txt", "integridad/sha256sumCliente.txt");
        Files.sendFolder("archivos", socket);
    }

    public static void main(String[] args) throws Exception {
        FileTransferClient ftc = null;
        if (args.length == 0) {
            ftc = new FileTransferClient();

        } else {
            String server = args[0];
            int port = Integer.parseInt(args[1]);
            ftc = new FileTransferClient(server, port);
        }
        ftc.init();
    }
}