package mensajeria;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;

public class clienteMensajeria {
    // Configuración del servidor
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    // Nombre de usuario
    private static String nombreUsuario;

    // Par de llaves del usuario
    private static PrivateKey llavePrivada;
    private static PublicKey llavePublica;

    // Almacenamiento de llaves públicas de otros usuarios
    private static Map<String, PublicKey> llavesPublicasOtros = new HashMap<>();

    // Socket y flujos de entrada/salida
    private static Socket socket;
    private static BufferedReader entradaServidor;
    private static PrintWriter salidaServidor;
    private static BufferedReader entradaConsola;

    public static void main(String[] args) {
        try {
            // Configurar entrada por consola
            entradaConsola = new BufferedReader(new InputStreamReader(System.in));

            // Solicitar nombre de usuario
            System.out.print("Ingrese su nombre de usuario: ");
            nombreUsuario = entradaConsola.readLine();

            // Cargar o generar llaves
            cargarOGenerarLlaves();

            // Cargar llaves públicas de otros usuarios
            cargarLlavesPublicasOtros();

            // Conectar al servidor
            socket = new Socket(HOST, PUERTO);
            entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salidaServidor = new PrintWriter(socket.getOutputStream(), true);

            // Registrar el usuario en el servidor
            registrarUsuario();

            // Crear hilo para recibir mensajes del servidor
            Thread hiloRecepcion = new Thread(new ReceptorMensajes());
            hiloRecepcion.start();

            // Menú principal
            mostrarMenu();
            String opcion;
            while (!(opcion = entradaConsola.readLine()).equals("5")) {
                switch (opcion) {
                    case "1":
                        registrarUsuario();
                        break;
                    case "2":
                        obtenerLlavePublica();
                        break;
                    case "3":
                        enviarMensaje();
                        break;
                    case "4":
                        leerMensajes();
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
                mostrarMenu();
            }

            // Guardar llaves públicas antes de salir
            guardarLlavesPublicasOtros();

            // Cerrar conexiones
            socket.close();
            System.out.println("¡Hasta pronto!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA DE MENSAJERÍA ===");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Obtener llave pública de un usuario");
        System.out.println("3. Enviar mensaje");
        System.out.println("4. Leer mensajes");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void cargarOGenerarLlaves() throws Exception {
        File archivoPrivada = new File(nombreUsuario + "_privada.key");
        File archivoPublica = new File(nombreUsuario + "_publica.key");

        if (archivoPrivada.exists() && archivoPublica.exists()) {
            // Cargar llaves desde archivos
            llavePrivada = cargarLlavePrivada(archivoPrivada);
            llavePublica = cargarLlavePublica(archivoPublica);
            System.out.println("Llaves cargadas con éxito");
        } else {
            // Generar nuevo par de llaves
            KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
            generador.initialize(2048);
            KeyPair parLlaves = generador.generateKeyPair();
            llavePublica = parLlaves.getPublic();
            llavePrivada = parLlaves.getPrivate();

            // Guardar llaves en archivos
            guardarLlavePrivada(llavePrivada, archivoPrivada);
            guardarLlavePublica(llavePublica, archivoPublica);
            System.out.println("Nuevo par de llaves generado y guardado");
        }
    }

    private static void guardarLlavePrivada(PrivateKey llave, File archivo) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(llave.getEncoded());
        }
    }

    private static void guardarLlavePublica(PublicKey llave, File archivo) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(llave.getEncoded());
        }
    }

    private static PrivateKey cargarLlavePrivada(File archivo) throws Exception {
        byte[] keyBytes = Files.readAllBytes(archivo.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private static PublicKey cargarLlavePublica(File archivo) throws Exception {
        byte[] keyBytes = Files.readAllBytes(archivo.toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static void cargarLlavesPublicasOtros() {
        try {
            File archivo = new File(nombreUsuario + "_contactos.dat");
            if (archivo.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                    // Cargar el mapa de nombres de usuario a llaves públicas en Base64
                    Map<String, String> mapaBase64 = (Map<String, String>) ois.readObject();

                    // Convertir de Base64 a objetos PublicKey
                    for (Map.Entry<String, String> entry : mapaBase64.entrySet()) {
                        byte[] keyBytes = Base64.getDecoder().decode(entry.getValue());
                        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey publicKey = keyFactory.generatePublic(spec);
                        llavesPublicasOtros.put(entry.getKey(), publicKey);
                    }
                }
                System.out.println("Contactos cargados: " + llavesPublicasOtros.size());
            }
        } catch (Exception e) {
            System.out.println("Error al cargar contactos: " + e.getMessage());
        }
    }

    private static void guardarLlavesPublicasOtros() {
        try {
            // Convertir de objetos PublicKey a Base64 para almacenamiento
            Map<String, String> mapaBase64 = new HashMap<>();
            for (Map.Entry<String, PublicKey> entry : llavesPublicasOtros.entrySet()) {
                String keyBase64 = Base64.getEncoder().encodeToString(entry.getValue().getEncoded());
                mapaBase64.put(entry.getKey(), keyBase64);
            }

            // Guardar el mapa en un archivo
            File archivo = new File(nombreUsuario + "_contactos.dat");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
                oos.writeObject(mapaBase64);
            }
            System.out.println("Contactos guardados: " + llavesPublicasOtros.size());
        } catch (Exception e) {
            System.out.println("Error al guardar contactos: " + e.getMessage());
        }
    }

    private static void registrarUsuario() {
        try {
            // Convertir la llave pública a Base64
            String llavePublicaBase64 = Base64.getEncoder().encodeToString(llavePublica.getEncoded());

            // Enviar comando de registro
            salidaServidor.println("REGISTRAR " + nombreUsuario + " " + llavePublicaBase64);

            // La respuesta será manejada por el hilo receptor
        } catch (Exception e) {
            System.out.println("[" + nombreUsuario + "] Error al registrar: " + e.getMessage());
        }
    }

    private static void obtenerLlavePublica() throws IOException {
        System.out.print("Ingrese el nombre del usuario: ");
        String usuario = entradaConsola.readLine();

        // Enviar solicitud al servidor
        salidaServidor.println("OBTENER_LLAVE_PUBLICA " + usuario);

        // La respuesta será manejada por el hilo receptor
    }

    private static void enviarMensaje() throws Exception {
        System.out.print("Ingrese el nombre del destinatario: ");
        String destinatario = entradaConsola.readLine();

        // Verificar si tenemos la llave pública del destinatario
        if (!llavesPublicasOtros.containsKey(destinatario)) {
            System.out.println("[" + nombreUsuario + "] No tenemos la llave pública de " + destinatario);
            System.out.println("[" + nombreUsuario + "] Solicitando llave pública al servidor...");

            // Solicitar llave pública al servidor
            salidaServidor.println("OBTENER_LLAVE_PUBLICA " + destinatario);

            // Esperar un momento para recibir la llave
            Thread.sleep(1000);

            // Verificar nuevamente
            if (!llavesPublicasOtros.containsKey(destinatario)) {
                System.out.println("[" + nombreUsuario + "] No se pudo obtener la llave pública de " + destinatario);
                return;
            }
        }

        System.out.print("Ingrese el mensaje: ");
        String mensaje = entradaConsola.readLine();

        // Cifrar el mensaje con la llave pública del destinatario
        PublicKey llaveDestinatario = llavesPublicasOtros.get(destinatario);
        byte[] mensajeCifrado = cifrarMensaje(mensaje, llaveDestinatario);
        String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);

        // Enviar mensaje cifrado al servidor
        salidaServidor.println("ENVIAR " + destinatario + " " + mensajeCifradoBase64);

        // La respuesta será manejada por el hilo receptor
    }

    private static void leerMensajes() {
        // Enviar solicitud al servidor
        salidaServidor.println("LEER " + nombreUsuario);

        // La respuesta será manejada por el hilo receptor
    }

    private static byte[] cifrarMensaje(String mensaje, PublicKey llave) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, llave);
        return cipher.doFinal(mensaje.getBytes());
    }

    private static String descifrarMensaje(byte[] mensajeCifrado) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, llavePrivada);
        byte[] bytesDescifrados = cipher.doFinal(mensajeCifrado);
        return new String(bytesDescifrados);
    }

    // Clase interna para recibir mensajes del servidor
    private static class ReceptorMensajes implements Runnable {
        @Override
        public void run() {
            try {
                String mensajeServidor;
                while ((mensajeServidor = entradaServidor.readLine()) != null) {
                    // Procesar respuestas específicas
                    if (mensajeServidor.startsWith("Llave pública de ")) {
                        procesarRespuestaLlavePublica(mensajeServidor);
                    } else if (mensajeServidor.startsWith("El usuario " + nombreUsuario + " tiene ")) {
                        procesarRespuestaMensajes(mensajeServidor);
                    } else {
                        // Mostrar otras respuestas normalmente
                        System.out.println("[Servidor] " + mensajeServidor);
                    }
                }
            } catch (Exception e) {
                System.out.println("[" + nombreUsuario + "] Conexión con el servidor cerrada");
            }
        }

        private void procesarRespuestaLlavePublica(String mensaje) {
            try {
                // Formato: "Llave pública de USUARIO: BASE64"
                String[] partes = mensaje.split(": ");
                if (partes.length != 2) return;

                String nombreConPrefijo = partes[0];
                String nombreUsuario = nombreConPrefijo.substring("Llave pública de ".length());
                String llaveBase64 = partes[1];

                // Convertir Base64 a PublicKey
                byte[] keyBytes = Base64.getDecoder().decode(llaveBase64);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(spec);

                // Guardar en el mapa local
                llavesPublicasOtros.put(nombreUsuario, publicKey);

                System.out.println("[" + clienteMensajeria.nombreUsuario + "] Llave pública de " + nombreUsuario + " recibida y guardada");

            } catch (Exception e) {
                System.out.println("[" + clienteMensajeria.nombreUsuario + "] Error al procesar llave pública: " + e.getMessage());
            }
        }

        private void procesarRespuestaMensajes(String mensajeInicial) throws Exception {
            // Extraer cantidad de mensajes
            System.out.println("[Servidor] " + mensajeInicial);

            // Determinar cuántos mensajes esperar
            String[] partes = mensajeInicial.split(" ");
            int cantidadMensajes = Integer.parseInt(partes[partes.length - 2]);

            // Leer y descifrar cada mensaje
            for (int i = 0; i < cantidadMensajes; i++) {
                String mensajeCifradoBase64 = entradaServidor.readLine();
                try {
                    byte[] mensajeCifrado = Base64.getDecoder().decode(mensajeCifradoBase64);
                    String mensajeDescifrado = descifrarMensaje(mensajeCifrado);
                    System.out.println("[" + clienteMensajeria.nombreUsuario + "] Mensaje " + (i+1) + ": " + mensajeDescifrado);
                } catch (Exception e) {
                    System.out.println("[" + clienteMensajeria.nombreUsuario + "] Error al descifrar mensaje: " + e.getMessage());
                }
            }
        }
    }
}
