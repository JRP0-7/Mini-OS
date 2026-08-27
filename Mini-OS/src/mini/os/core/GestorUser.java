package mini.os.core;

import java.io.File;

import mini.os.error.UsuarioDuplicadoException;
import mini.os.io.ArchivoUtil;
import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;

public class GestorUser {
    public static void crearUser(String user, String pass) throws UsuarioDuplicadoException {
        ListaEnlazada<String> lista = ArchivoUtil.leer(Sistema.ROOT + "/users.xr");
        if (lista.contiene(user)) {
            throw new UsuarioDuplicadoException("El usuario " + user + " ya existe");
        }
        File UserFolder = new File(Sistema.ROOT + "/" + user);
        UserFolder.mkdirs();

        new File(UserFolder, "Documents").mkdirs();
        new File(UserFolder, "Music").mkdirs();
        new File(UserFolder, "Images").mkdirs();

        SystemUser nuevo = new SystemUser(user, pass, false);
        ArchivoUtil.guardar(nuevo, Sistema.ROOT + "/" + user + "/" + user + ".xr");

        lista.agregar(user);
        ArchivoUtil.guardar(lista, Sistema.ROOT + "/users.xr");
    }

    public static SystemUser login(String user, String pass){
        return null;
    }

}
