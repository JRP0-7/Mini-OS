package mini.os.core;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import mini.os.error.ArchivoCorruptoException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.io.ArchivoUtil;
import mini.os.io.Autentificacion;
import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;

// Esta clase se encarga de crear usuarios y manejar el login
public class GestorUser {
    public static boolean nombreValido(String user) {
        if (user == null || user.isEmpty()) {
            return false;
        }
        if (user.equals(".") || user.equals("..")) {
            return false;
        }
        for (char c : user.toCharArray()) {
            if (c == '/' || c == '\\' || c == ':' || c == '*' || c == '?' || c == '"' || c == '<' || c == '>'
                    || c == '|') {
                return false;
            }
        }
        return true;
    }

    private static ListaEnlazada<String> usuariosRegistrados() throws ArchivoCorruptoException {
        ListaEnlazada<String> lista = ArchivoUtil.leer(Sistema.ROOT + "/users.xr");
        return lista == null ? new ListaEnlazada<>() : lista;
    }

    // Crea un usuario nuevo, le hace sus carpetas y guarda los datos
public static void crearUser(String user, String pass, boolean admin)
            throws UsuarioDuplicadoException, NoSuchAlgorithmException, ArchivoCorruptoException {
        user = user == null ? "" : user.trim();
        if (!nombreValido(user)) {
            throw new UsuarioDuplicadoException("El nombre de usuario es invalido o contiene caracteres prohibidos");
        }
        ListaEnlazada<String> lista = usuariosRegistrados();
        if (lista.contiene(user)) {
            throw new UsuarioDuplicadoException("El usuario " + user + " ya existe");
        }
        File UserFolder = new File(Sistema.ROOT + "/" + user);
        if (!UserFolder.exists() && !UserFolder.mkdirs()) {
            throw new ArchivoCorruptoException("No se pudo crear la carpeta del usuario");
        }

        new File(UserFolder, "Mis Documentos").mkdirs();
        new File(UserFolder, "Música").mkdirs();
        new File(UserFolder, "Mis Imágenes/Portadas").mkdirs();

        String hashPass = Autentificacion.hash(pass);

        SystemUser nuevo = new SystemUser(user, hashPass, admin);
        ArchivoUtil.guardar(nuevo, Sistema.ROOT + "/" + user + "/" + user + ".xr");

        lista.agregar(user);
        ArchivoUtil.guardar(lista, Sistema.ROOT + "/users.xr");
    }

    // Verifica si el usuario y la contraseña son correctos para entrar
    public static SystemUser login(String user, String pass) throws NoSuchAlgorithmException, ArchivoCorruptoException {
        user = user == null ? "" : user.trim();
        if (!nombreValido(user)) {
            return null;
        }
        File folder = new File(Sistema.ROOT + "/" + user);
        String[] carpetas = { "Mis Documentos", "Música", "Mis Imágenes", "Mis Imágenes/Portadas" };

        if (!folder.exists()) {
            return null;
        }
        for (String string : carpetas) {
            new File(folder, string).mkdirs();
        }

SystemUser su = ArchivoUtil.leer(folder.getPath() + "/" + user + ".xr");
        if (su == null) {
            return null;
        }
        if (su.getPassword().equals(Autentificacion.hash(pass))) {
            System.out.println("Inicio Sesion correcto");
            return su;
        } else {
            System.out.println("Error");
            return null;
        }
    }

public static ListaEnlazada<String> listarUsuarios() throws ArchivoCorruptoException {
        return usuariosRegistrados();
    }

    public static SystemUser buscarUsuario(String user) throws ArchivoCorruptoException{
        return ArchivoUtil.leer(Sistema.ROOT+"/"+user+"/"+user+".xr");
    }

    public static void eliminarUsuario(String user) throws ArchivoCorruptoException {
        if (user.equals("admin")) {
            return;
        }

        File folder = new File(Sistema.ROOT + "/" + user);

        if (!folder.exists())
            return;

        borrarTodo(folder);
        ListaEnlazada<String> lista = usuariosRegistrados();
        lista.eliminar(user);
        ArchivoUtil.guardar(lista, Sistema.ROOT + "/users.xr");
    }

    private static boolean borrarTodo(File f) {
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

    public static void editarUser(String user, String npass, boolean admin) throws ArchivoCorruptoException, NoSuchAlgorithmException {
        File folder = new File(Sistema.ROOT + "/" + user + "/" + user + ".xr");
        SystemUser su = ArchivoUtil.leer(folder.getPath());
        if (!npass.isEmpty()) {
            String hashP = Autentificacion.hash(npass);
            if (!hashP.equals(su.getPassword())) {
                su.setPassword(hashP);
            }
        }
        su.setAdmin(admin);
        ArchivoUtil.guardar(su, folder.getPath());
    }
}
