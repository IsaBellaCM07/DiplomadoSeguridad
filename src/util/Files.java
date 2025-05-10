package util;

import java.io.*;
import java.net.Socket;

//Lab03 - Control de integridad en archivos enviados por la red
public class Files {
    public static void sendFile(String filename, Socket socket) throws Exception {
        System.out.println("File to send: " + filename);
        File localFile = new File(filename);
        BufferedInputStream fromFile = new BufferedInputStream(new FileInputStream(localFile));

        // send the size of the file (in bytes)
        long size = localFile.length();
        System.out.println("Size: " + size);

        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        printWriter.println(filename);

        printWriter.println("Size:" + size);

        BufferedOutputStream toNetwork = new BufferedOutputStream(socket.getOutputStream());

        pause(50);

        // the file is sent one block at a time
        byte[] blockToSend = new byte[1024];
        int in;
        while ((in = fromFile.read(blockToSend)) != -1) {
            toNetwork.write(blockToSend, 0, in);
        }
        // the stream linked to the socket is flushed and closed
        toNetwork.flush();
        fromFile.close();

        pause(50);
    }

    public static String receiveFile(String folder, Socket socket) throws Exception {
        File fd = new File (folder);
        if (fd.exists()==false) {
            fd.mkdir();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedInputStream fromNetwork = new BufferedInputStream(socket.getInputStream());

        String filename = reader.readLine();
//        filename = folder + File.separator + filename;
        filename = folder + File.separator + new File(filename).getName();


        BufferedOutputStream toFile = new BufferedOutputStream(new FileOutputStream(filename));

        System.out.println("File to receive: " + filename);

        String sizeString = reader.readLine();

        // the sender sends "Size:" + size, so here it is separated
        // long size = Long.parseLong(sizeString.subtring(5));
        long size = Long.parseLong(sizeString.split(":")[1]);
        System.out.println("Size: " + size);

        // the file is received one block at a time
        byte[] blockToReceive = new byte[512];
        int in;
        long remainder = size; // the remaining part of the file
        while ((in = fromNetwork.read(blockToReceive)) != -1) {
            toFile.write(blockToReceive, 0, in);
            remainder -= in;
            if (remainder == 0)
                break;
        }

        pause(50);

        // the stream linked to the file is flushed and closed
        toFile.flush();
        toFile.close();
        System.out.println("File received: " + filename);

        return filename;
    }

    public static void pause(int miliseconds) throws Exception {
        Thread.sleep(miliseconds);
    }

    public static void sendFolder(String folderName, Socket socket) throws Exception {
        File folder = new File(folderName);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("La carpeta no existe o no es un directorio");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("La carpeta está vacía");
        }

        // Enviar la cantidad de archivos
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(files.length);

        // Enviar todos los archivos
        for (File file : files) {
            String fileNameOnly = file.getName();  // Solo el nombre del archivo sin la ruta
            sendFile(file.getPath(), socket);
        }
    }

    public static void receiveFolder(String destFolderName, Socket socket) throws Exception {
        File destFolder = new File(destFolderName);
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        int numFiles = Integer.parseInt(reader.readLine());
        for (int i = 0; i < numFiles; i++) {
            receiveFile(destFolderName, socket);
        }
    }

    public static void compareHashes(String integrityFolderPath) throws Exception {
        File clientFile = new File(integrityFolderPath + File.separator + "sha256sumCliente.txt");
        File serverFile = new File(integrityFolderPath + File.separator + "sha256sumServer.txt");

        if (!clientFile.exists() || !serverFile.exists()) {
            System.out.println("No se encontraron ambos archivos de integridad en la carpeta: " + integrityFolderPath);
            return;
        }

        BufferedReader clientReader = new BufferedReader(new InputStreamReader(new FileInputStream(clientFile)));
        BufferedReader serverReader = new BufferedReader(new InputStreamReader(new FileInputStream(serverFile)));

        String clientHash = null;
        String serverHash = null;

        // Buscar hash de sha256sum.txt en archivo cliente
        String line;
        while ((line = clientReader.readLine()) != null) {
            if (line.contains("*sha256sum.txt")) {
                clientHash = line.split(" ")[0];
                break;
            }
        }

        // Buscar hash de sha256sum.txt en archivo servidor
        while ((line = serverReader.readLine()) != null) {
            if (line.contains("*sha256sum.txt")) {
                serverHash = line.split(" ")[0];
                break;
            }
        }

        clientReader.close();
        serverReader.close();

        if (clientHash == null || serverHash == null) {
            System.out.println("No se encontró una entrada para sha256sum.txt en uno de los archivos.");
            return;
        }

        if (clientHash.equalsIgnoreCase(serverHash)) {
            System.out.println("sha256sum.txt: OK");
        } else {
            System.out.println("sha256sum.txt: FAILED");
            System.out.println("shasum: WARNING: 1 computed checksum did NOT match");
        }
    }




}