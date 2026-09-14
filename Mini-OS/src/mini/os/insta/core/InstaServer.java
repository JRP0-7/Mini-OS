package mini.os.insta.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import mini.os.core.Sistema;
import mini.os.insta.model.gestorPublicacion;

public class InstaServer {

    public static final String IROOT = pathRaiz();
    public static final int PORT = 1500;
    public static final gestorPublicacion gestorPub = new gestorPublicacion();

    private static boolean servidorActivo = false;

    public static String pathRaiz() {
        return Sistema.ROOT + "/insta";
    }

    public static void iniciar() {
        ServicioInsta.inicializarDatos();
    }

    public static void iniciarServidorEnSegundoPlano() {
        if (servidorActivo) {
            return;
        }
        servidorActivo = true;
        Thread hilo = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("Servidor INSTA+ escuchando en puerto " + PORT);
                while (true) {
                    Socket cliente = server.accept();
                    ManejoConexion manejo = new ManejoConexion(cliente);
                    new Thread(manejo).start();
                }
            } catch (IOException e) {
                System.out.println("Servidor INSTA+ no disponible: " + e.getMessage());
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    public static gestorPublicacion getGestorPub() {
        return gestorPub;
    }

    public static void main(String[] args) {
        iniciar();
        iniciarServidorEnSegundoPlano();
        while (true) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}