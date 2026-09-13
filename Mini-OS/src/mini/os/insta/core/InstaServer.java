package mini.os.insta.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import mini.os.core.Sistema;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.model.gestorPublicacion;
import mini.os.model.ListaEnlazada;
import mini.os.io.ArchivoUtil;

public class InstaServer {

    public static final String IROOT = pathRaiz();
    public static final int PORT = 1500;
    public static final gestorPublicacion gestorPub = new gestorPublicacion();

    public static String pathRaiz() {
        String cd = Sistema.ROOT + "/insta";
        return cd;
    }

    public static void iniciar() {
        File raizInsta = new File(pathRaiz());

        if (!raizInsta.exists()) {
            raizInsta.mkdir();
        }

        File datosUsers = new File(raizInsta, "users.xr");
        if (!datosUsers.exists()) {
            ListaEnlazada<String> lista = new ListaEnlazada<>();
            ArchivoUtil.guardar(lista, datosUsers.getPath());
        }
    }

    public static void crearAdmin() throws NoSuchAlgorithmException, UsuarioDuplicadoException {
        GestorInstaUser.registrar("admin", "1234", "admin", '0', 0, null);
    }

    

    public static gestorPublicacion getGestorPub() {
        return gestorPub;
    }

    public static void main(String[] args) throws IOException {
        iniciar();

        try {
            crearAdmin();
        } catch (NoSuchAlgorithmException | UsuarioDuplicadoException e) {
            // ya existe o falla hash, no bloquea el arranque
        }

        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Servidor INSTA+ escuchando en puerto " + PORT);

        while (true) {
            Socket cliente = server.accept();
            ManejoConexion manejo = new ManejoConexion(cliente);
            new Thread(manejo).start();
        }
    }

}