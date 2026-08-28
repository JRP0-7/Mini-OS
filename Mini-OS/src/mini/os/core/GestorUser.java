package mini.os.core;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import mini.os.error.UsuarioDuplicadoException;
import mini.os.io.ArchivoUtil;
import mini.os.io.Autentificacion;
import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;

public class GestorUser {
    public static void crearUser(String user, String pass) throws UsuarioDuplicadoException, NoSuchAlgorithmException {
        ListaEnlazada<String> lista = ArchivoUtil.leer(Sistema.ROOT + "/users.xr");
        if (lista.contiene(user)) {
            throw new UsuarioDuplicadoException("El usuario " + user + " ya existe");
        }
        File UserFolder = new File(Sistema.ROOT + "/" + user);
        UserFolder.mkdirs();

        new File(UserFolder, "Documents").mkdirs();
        new File(UserFolder, "Music").mkdirs();
        new File(UserFolder, "Images").mkdirs();
        
        String hashPass= Autentificacion.hash(pass);

        SystemUser nuevo = new SystemUser(user, hashPass , false);
        ArchivoUtil.guardar(nuevo, Sistema.ROOT + "/" + user + "/" + user + ".xr");

        lista.agregar(user);
        ArchivoUtil.guardar(lista, Sistema.ROOT + "/users.xr");
    }

    public static SystemUser login(String user, String pass) throws NoSuchAlgorithmException {
        File folder = new File(Sistema.ROOT + "/" + user);
        
        if(!folder.exists()){
            return null;
        }
        
        SystemUser su = ArchivoUtil.leer(folder.getPath() + "/" + user +".xr");
        if(su.getPassword().equals(Autentificacion.hash(pass)))
        {
            System.out.println("Inicio Sesion correcto");
            return su;
        }
        else{
            System.out.println("Error");
            return null;
        }
    }

}
