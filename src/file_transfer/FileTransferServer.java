package file_transfer;

import integrity.Hasher;
import util.Files;

import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferServer {
    public static final int PORT = 4000;

    private ServerSocket listener;
    private Socket serverSideSocket;

    private int port;

    public FileTransferServer() {
        this.port = PORT;
        System.out.println("Isabella Cardozo Marín - May 2/2025");
        System.out.println("File transfer server is running on port: " + this.port);
    }

    public FileTransferServer(int port) {
        this.port = port;
        System.out.println("Isabella Cardozo Marín - May 2/2025");
        System.out.println("File transfer server is running on port: " + this.port);
    }

    private void init() throws Exception {
        listener = new ServerSocket(PORT);

        while (true) {
            serverSideSocket = listener.accept();

            protocol(serverSideSocket);
        }
    }

    public void protocol(Socket socket) throws Exception {
//        Files.receiveFile("Docs", socket);
//        Files.sendFile("Jinx.jpeg", socket);
        Files.receiveFolder("Docs", socket);
        Hasher.generateIntegrityCheckerFile("Docs", "Docs/sha256sumServer.txt");
        Hasher.checkIntegrityFile("integridad/sha256sumCliente.txt", "Docs");
        //Files.compareHashes("integridad");
    }

    public static void main(String[] args) throws Exception{

        FileTransferServer fts = null;
        if (args.length == 0) {
            fts = new FileTransferServer();
        } else {
            int port = Integer.parseInt(args[0]);
            fts = new FileTransferServer(port);
        }
        fts.init();


    }
}