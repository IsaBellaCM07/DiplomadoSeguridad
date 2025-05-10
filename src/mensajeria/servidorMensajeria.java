package mensajeria;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class servidorMensajeria {
    // Puerto en el que el servidor escuchará
    private static final int PUERTO = 5000;

    // HashMap para almacenar usuarios y sus llaves públicas
    private static Map<String, String> usuarios = new ConcurrentHashMap<>();

    // HashMap para almacenar mensajes de cada usuario
    private static Map<String, List<String>> buzon = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            // Crear el socket del servidor
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

            while (true) {
                // Esperar por una conexión
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                // Crear un nuevo hilo para manejar esta conexión
                ClienteHandler clienteHandler = new ClienteHandler(clientSocket);
                new Thread(clienteHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna para manejar cada cliente en un hilo separado
    private static class ClienteHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader entrada;
        private PrintWriter salida;

        public ClienteHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                salida = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String mensajeCliente;
                while ((mensajeCliente = entrada.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + mensajeCliente);
                    procesarMensaje(mensajeCliente);
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void procesarMensaje(String mensaje) {
            String[] partes = mensaje.split(" ", 3); // Dividir en máximo 3 partes

            if (partes.length < 2) {
                salida.println("ERROR: Formato de mensaje incorrecto");
                return;
            }

            String comando = partes[0];
            String usuario = partes[1];

            switch (comando) {
                case "REGISTRAR":
                    if (partes.length < 3) {
                        salida.println("ERROR: Falta la llave pública");
                        return;
                    }
                    registrarUsuario(usuario, partes[2]);
                    break;

                case "OBTENER_LLAVE_PUBLICA":
                    obtenerLlavePublica(usuario);
                    break;

                case "ENVIAR":
                    if (partes.length < 3) {
                        salida.println("ERROR: Falta el mensaje");
                        return;
                    }
                    enviarMensaje(usuario, partes[2]);
                    break;

                case "LEER":
                    leerMensajes(usuario);
                    break;

                default:
                    salida.println("ERROR: Comando desconocido");
            }
        }

        private void registrarUsuario(String nombre, String llavePublica) {
            if (usuarios.containsKey(nombre)) {
                salida.println("El usuario " + nombre + " ya está registrado");
            } else {
                usuarios.put(nombre, llavePublica);
                // Inicializar buzón del usuario
                buzon.put(nombre, new ArrayList<>());
                salida.println("Bienvenido " + nombre);
            }
        }

        private void obtenerLlavePublica(String nombre) {
            if (usuarios.containsKey(nombre)) {
                salida.println("Llave pública de " + nombre + ": " + usuarios.get(nombre));
            } else {
                salida.println("ERROR. El usuario " + nombre + " no está registrado");
            }
        }

        private void enviarMensaje(String destinatario, String mensajeCifrado) {
            if (usuarios.containsKey(destinatario)) {
                // Agregar mensaje al buzón del destinatario
                buzon.get(destinatario).add(mensajeCifrado);
                salida.println("Mensaje enviado a " + destinatario);
            } else {
                salida.println("ERROR. El usuario " + destinatario + " no está registrado");
            }
        }

        private void leerMensajes(String nombre) {
            if (!usuarios.containsKey(nombre)) {
                salida.println("ERROR. El usuario " + nombre + " no está registrado");
                return;
            }

            List<String> mensajes = buzon.get(nombre);
            int cantidadMensajes = mensajes.size();

            // Envía la cantidad de mensajes
            if (cantidadMensajes == 1) {
                salida.println("El usuario " + nombre + " tiene 1 mensaje");
            } else {
                salida.println("El usuario " + nombre + " tiene " + cantidadMensajes + " mensajes");
            }

            // Envía cada mensaje en una línea separada
            for (String mensaje : mensajes) {
                salida.println(mensaje);
            }

            // Limpia el buzón después de leer los mensajes
            mensajes.clear();
        }
    }
}