package integrity;

import util.Util;

import java.io.*;
import java.security.MessageDigest;

public class Hasher {
    public static String getHash(String input, String algorithm) throws Exception {
        byte[] inputBA = input.getBytes();
        MessageDigest hasher = MessageDigest.getInstance(algorithm);
        hasher.update(inputBA);
        return Util.byteArrayToHexString(hasher.digest(), "");
    }

    public static String getHashFile(String filename, String algorithm) throws Exception {
        MessageDigest hasher = MessageDigest.getInstance(algorithm);
        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        int in;
        while ((in = fis.read(buffer)) != -1) {
            hasher.update(buffer, 0, in);
        }
        fis.close();
        return Util.byteArrayToHexString(hasher.digest(), "");
    }

    public static void generateIntegrityCheckerFile(String inputPath, String outputFilename) throws Exception {
        File input = new File(inputPath);

        if (!input.exists()) {
            System.out.println("La ruta especificada no existe.");
            return;
        }

        FileWriter writer = new FileWriter(outputFilename, false);

        if (input.isFile()) {
            // Caso: un solo archivo
            String hash = getHashFile(input.getPath(), "SHA-256");
            String line = hash + " *" + input.getName() + "\n";
            writer.write(line);
        } else if (input.isDirectory()) {
            // Caso: una carpeta
            File[] files = input.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("La carpeta está vacía.");
                writer.close();
                return;
            }

            for (File file : files) {
                if (file.isFile()) {
                    String hash = getHashFile(file.getPath(), "SHA-256");
                    String line = hash + " *" + file.getName() + "\n";
                    writer.write(line);
                }
            }
        } else {
            System.out.println("Ruta inválida.");
        }

        writer.close();
    }


    public static void checkIntegrityFile(String integrityFilename, String folderName) throws Exception {
        File integrityFile = new File(integrityFilename);
        if (!integrityFile.exists()) {
            System.out.println("shasum: " + integrityFilename + ": No such file or directory");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(integrityFilename));
        String line;
        int failedHashes = 0;
        int unreadableFiles = 0;
        int improperlyFormattedLines = 0;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(" \\*", 2);

            if (parts.length != 2) {
                improperlyFormattedLines++;
                continue;
            }

            String storedHash = parts[0].trim();
            String filename = parts[1].trim();

            // <<<<<< NUEVA VALIDACIÓN >>>>>>>
            if (!storedHash.matches("[0-9a-f]{64}")) {
                improperlyFormattedLines++;
                continue;
            }

            File file = new File(folderName + "/" + filename);

            if (!file.exists()) {
                System.out.println("shasum: " + filename + ": No such file or directory");
                System.out.println(filename + ": FAILED open or read");
                unreadableFiles++;
                continue;
            }

            String currentHash = getHashFile(file.getPath(), "SHA-256");

            if (currentHash.equalsIgnoreCase(storedHash)) {
                System.out.println(filename + ": OK");
            } else {
                System.out.println(filename + ": FAILED");
                failedHashes++;
            }
        }

        reader.close();

        if (improperlyFormattedLines > 0) {
            System.out.println("shasum: WARNING: " + improperlyFormattedLines + " lines are improperly formatted");
        }
        if (unreadableFiles > 0) {
            System.out.println("shasum: WARNING: " + unreadableFiles + " listed file" + (unreadableFiles > 1 ? "s" : "") + " could not be read");
        }
        if (failedHashes > 0) {
            System.out.println("shasum: WARNING: " + failedHashes + " computed checksum" + (failedHashes > 1 ? "s" : "") + " did NOT match");
        }
    }



}
