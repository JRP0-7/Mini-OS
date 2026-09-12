package mini.os.insta.core;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import mini.os.model.ListaEnlazada;
import mini.os.io.ArchivoUtil;
import mini.os.insta.model.Respuesta;
import mini.os.model.InstaUser;
import mini.os.io.Autentificacion;
import mini.os.error.*;

public class GestorInstaUser {
    public static void registrar(String user, String pass, String nombre, char genero, int age, String rutaI)
            throws UsuarioDuplicadoException, NoSuchAlgorithmException {
        ListaEnlazada<String> lista = ArchivoUtil.leer(InstaServer.IROOT + "/users.xr");
        if (lista.contiene(user)) {
            throw new UsuarioDuplicadoException("El usuario " + user + " ya existe");
        }

        File UserFolder = new File(InstaServer.IROOT, "/" + user);
        UserFolder.mkdir();

        String hashPass = Autentificacion.hash(pass);

        InstaUser nuevo = new InstaUser(user, hashPass, nombre, genero, age, new Date(), rutaI, true);
        ArchivoUtil.guardar(nuevo, InstaServer.IROOT + "/" + user + "/" + user + ".xr");
        lista.agregar(user);
        ArchivoUtil.guardar(lista, InstaServer.IROOT + "/users.xr");
    }

    public static InstaUser login(String user, String pass) throws NoSuchAlgorithmException {
        File folder = new File(InstaServer.IROOT + "/" + user);

        if (!folder.exists()) {
            return null;
        }

        InstaUser iu = ArchivoUtil.leer(folder.getPath() + "/" + user + ".xr");
        if (iu.getPassword().equals(Autentificacion.hash(pass))) {
            return iu;
        } else {
            return null;
        }
    }

}
