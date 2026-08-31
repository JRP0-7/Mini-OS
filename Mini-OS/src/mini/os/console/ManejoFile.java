package mini.os.console;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ManejoFile {
    private File carpetaRaiz;
    private File carpetaActual;

    public ManejoFile(File raiz) {
        carpetaRaiz = raiz;
        carpetaActual = carpetaRaiz;
    }

    public File URLValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            return null;

        File destino = new File(carpetaActual, nombre);

        String rutaDestino;
        String rutaRaiz;
        try {
            rutaDestino = destino.getCanonicalPath();
            rutaRaiz = carpetaRaiz.getCanonicalPath();
        } catch (IOException e) {
            return null;
        }

        if (rutaDestino.equals(rutaRaiz))
            return destino;
        if (rutaDestino.startsWith(rutaRaiz + File.separator))
            return destino;

        return null;
    }

    public String crearArchivo(String nombre) {
        File destino = URLValido(nombre);
        if (destino == null) {
            return "Ruta Invalida";
        }

        if (destino.exists()) {
            return "El archivo " + nombre + " ya existe";
        }

        try {
            if (destino.createNewFile()) {
                return "Archivo " + nombre + " creado";
            } else {
                return "No se pudo crear el archivo " + nombre;
            }
        } catch (IOException e) {
            return "Error " + e.getMessage();
        }
    }

    public String crearCarpeta(String nombre) {
        File destino = URLValido(nombre);
        if (destino == null) {
            return "Ruta Invalida";
        }

        if (destino.exists()) {
            return "La carpeta " + nombre + " ya existe";
        }

        if (destino.mkdir()) {
            return "Carpeta " + nombre + " creada";
        } else {
            return "No se pudo crear la carpeta " + nombre;
        }
    }

    public String borrar(String nombre) {
        File destino = URLValido(nombre);
        if (destino == null || !destino.exists()) {
            return "Ruta Invalida";
        }

        try {
            if (destino.getCanonicalPath().equals(carpetaRaiz.getCanonicalPath())) {
                return "No se puede borrar la carpeta raiz";
            }
        } catch (IOException e) {
            return "Error " + e.getMessage();
        }

        try{
            if(destino.getCanonicalPath().equals(carpetaActual.getCanonicalPath())){
                return "No se puede eliminar la carpeta actual";
            }
        }
        catch(IOException e){
            return "Error con la ruta brindada";
        }

        if (borrarTodo(destino)) {
            return nombre + " eliminado correctamente";
        } else {
            return "No se pudo eliminar " + nombre;
        }
    }

    private boolean borrarTodo(File f) {
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

    public String Mover(String url) {
        File destino = URLValido(url);
        if (destino == null || !destino.exists()) {
            return "No existe la carpeta " + url;
        }

        if (!destino.isDirectory()) {
            return "No es una carpeta";
        }

        carpetaActual = destino;
        return "";
    }

    public String Subir() {
        try {
            if (carpetaActual.getCanonicalPath().equals(carpetaRaiz.getCanonicalPath())) {
                return "Ya se esta en la carpeta raiz";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }

        carpetaActual = carpetaActual.getParentFile();
        return "";
    }

    public String ubicacionActual() {
        try {
            String raiz = carpetaRaiz.getCanonicalPath();
            String actual = carpetaActual.getCanonicalPath();
            String root = "Z:\\" + carpetaRaiz.getName();
            if (actual.equals(raiz)) {
                return root;
            }
            return root + actual.substring(raiz.length());
        } catch (IOException e) {
            return "Z:\\" + carpetaRaiz.getName();
        }
    }

    public File getCarpetaActual() {
        return carpetaActual;
    }

    public String escribir(String origen, String contenido, boolean listo) {
        File destino = URLValido(origen);
        if (destino == null) {
            return "Ruta invalida";
        }
        try (FileWriter f = new FileWriter(destino, listo)) {
            f.write(contenido);
            return "";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String leer(String origen) {
        File destino = URLValido(origen);
        if (destino == null || !destino.isFile()) {
            return "Error: No existe el archivo " + origen;
        }

        StringBuilder Sb = new StringBuilder();
        try (FileReader fr = new FileReader(destino)) {
            int c;
            while ((c = fr.read()) != -1) {
                Sb.append((char) c);
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
        return Sb.toString();
    }

    public String copiar(String origen, String destino) {
        File origin = URLValido(origen);
        File end = URLValido(destino);

        if (origin == null || !origin.isFile()) {
            return "No existe el origen";
        }
        if (end == null) {
            return "Ruta invalida";
        }

        if (end.exists()) {
            return "El destino " + destino + " ya existe";
        }

        try (FileReader fr = new FileReader(origin);
                FileWriter fw = new FileWriter(end)) {
            int c;
            while ((c = fr.read()) != -1) {
                fw.write(c);
            }
            return "Copiado exitosamente";
        } catch (IOException e) {
            return "Error al copiar: " + e.getMessage();
        }
    }

    public String renombrar(String origen, String nNombre) {
        File destino = URLValido(origen);
        if (destino == null || !destino.exists()) {
            return "No existe " + origen;
        }

        File Ndestino = URLValido(nNombre);
        if (Ndestino == null) {
            return "Nombre Invalido";
        }
        if (Ndestino.exists()) {
            return "Ya existe " + nNombre;
        }

        if (destino.renameTo(Ndestino)) {
            return origen + " se ha renombrado a " + nNombre;
        } else {
            return "Error al renombrar";
        }
    }

    public String listar() {
        File hijos[] = carpetaActual.listFiles();
        if (hijos == null) {
            return "No se pudo leer la carpeta";
        }
        if (hijos.length == 0) {
            return "La carpeta esta vacia";
        }

        StringBuilder sb = new StringBuilder();
        for (File child : hijos) {
            if (child.isDirectory()) {
                sb.append("<DIR>    " + child.getName() + " \n");
            } else {
                sb.append(child.length() + " bytes  " + child.getName() + "\n");
            }
        }
        return sb.toString();
    }

}
