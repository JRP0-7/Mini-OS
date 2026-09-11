package mini.os.core;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import mini.os.error.UsuarioDuplicadoException;
import mini.os.io.ArchivoUtil;
import mini.os.io.Autentificacion;
import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;

// Esta clase se encarga de crear usuarios y manejar el login
public class GestorUser {
    // Crea un usuario nuevo, le hace sus carpetas y guarda los datos
    public static void crearUser(String user, String pass, boolean admin) throws UsuarioDuplicadoException, NoSuchAlgorithmException {
        ListaEnlazada<String> lista = ArchivoUtil.leer(Sistema.ROOT + "/users.xr");
        if (lista.contiene(user)) {
            throw new UsuarioDuplicadoException("El usuario " + user + " ya existe");
        }
        File UserFolder = new File(Sistema.ROOT + "/" + user);
        UserFolder.mkdirs();

        new File(UserFolder, "Documents").mkdirs();
        new File(UserFolder, "Music").mkdirs();
        new File(UserFolder, "Images/Portadas").mkdirs();
        
        String hashPass= Autentificacion.hash(pass);

        SystemUser nuevo = new SystemUser(user, hashPass , admin);
        ArchivoUtil.guardar(nuevo, Sistema.ROOT + "/" + user + "/" + user + ".xr");

        lista.agregar(user);
        ArchivoUtil.guardar(lista, Sistema.ROOT + "/users.xr");
    }

    // Verifica si el usuario y la contraseña son correctos para entrar
    public static SystemUser login(String user, String pass) throws NoSuchAlgorithmException {
        File folder = new File(Sistema.ROOT + "/" + user);
        String[] carpetas = {"Documents", "Music", "Images", "Images/Portadas"};
        
        if(!folder.exists()){
            return null;
        }
        for (String string : carpetas) {
            new File(folder, string).mkdirs();
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

    public static ListaEnlazada<String> listarUsuarios(){
        return ArchivoUtil.leer(Sistema.ROOT + "/users.xr");
    }

    public static void eliminarUsuario(String user){
        if(user.equals("admin")){
            return;
        }

        File folder = new File(Sistema.ROOT + "/" + user);

        if(!folder.exists())
            return;

        borrarTodo(folder);
        ListaEnlazada<String> lista = ArchivoUtil.leer(Sistema.ROOT+"/users.xr");
        lista.eliminar(user);
        ArchivoUtil.guardar(lista, Sistema.ROOT+ "/users.xr");
    }

    private static boolean borrarTodo(File f){
        if (f.isDirectory()) {
            File hijos[] = f.listFiles();
            if (hijos != null) {
                for (File child : hijos) {
                    borrarTodo(child);
                }
            }
        }
        return f.delete();
    }
}
