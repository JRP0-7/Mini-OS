package mini.os.console;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Raiz {
    
    public static void find(File ubicacion, String nombreBuscado, ConsoleGUI gui){
    File carpetaActual = ubicacion;
    
    gui.imprimirTexto("\n> Find "+nombreBuscado + "\n");
        boolean encontrado = buscarEnCarpeta(carpetaActual,
                nombreBuscado.toLowerCase(), gui);

        if (!encontrado) {
            gui.imprimirTexto("No se encontro ningun archivo o carpeta con ese nombre.");
        }
    }
    
    private static boolean buscarEnCarpeta(File carpeta, String nombreBuscado,
            ConsoleGUI gui) {

    File[] elementos = carpeta.listFiles();

    if (elementos == null) {
        return false;
    }

    boolean encontrado = false;

    for (File elemento : elementos) {
        if (elemento.getName().toLowerCase().contains(nombreBuscado)) {
            String tipo = elemento.isDirectory() ? "[Carpeta] " : "[Archivo] ";
            gui.imprimirTexto(tipo + elemento.getAbsolutePath() + "\n");
            encontrado = true;
        }

        if (elemento.isDirectory()) {
            if (buscarEnCarpeta(elemento, nombreBuscado, gui)) {
                encontrado = true;
            }
        }
    }

    return encontrado;
}

    public static void tree(File carpetaActual, ConsoleGUI gui) {
        gui.imprimirTexto("\n> Tree");
        gui.imprimirTexto(carpetaActual.getName());
        mostrarArbol(carpetaActual, "", gui);
    }

    private static void mostrarArbol(File carpeta, String sangria,
            ConsoleGUI gui) {
        File[] elementos = carpeta.listFiles();

        if (elementos == null) {
            return;
        }

        for (int i = 0; i < elementos.length; i++) {
            File elemento = elementos[i];
            boolean esUltimo = i == elementos.length - 1;
            String conector = esUltimo ? "`-- " : "|-- ";

            gui.imprimirTexto(sangria + conector + elemento.getName());

            if (elemento.isDirectory()) {
                String nuevaSangria = esUltimo
                        ? sangria + "    "
                        : sangria + "|   ";
                mostrarArbol(elemento, nuevaSangria, gui);
            }
        }
    }
    
    public static void info(File ubicacion, String nombre, ConsoleGUI gui){
    File carpetaActual = ubicacion;
    File elemento = new File(carpetaActual, nombre);
    gui.imprimirTexto("\n> Info "+nombre);
    
        if (!elemento.exists()) {
            gui.imprimirTexto("No se encontro el archivo o carpeta.");
            return;
        }
        
        String tipo = elemento.isDirectory() ? "Carpeta":"Archivo";
        long tamanio = elemento.length();
        Date fechaModificacion = new Date(elemento.lastModified());
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        gui.imprimirTexto("Tipo: "+tipo);
        gui.imprimirTexto("Ruta: "+elemento.getAbsolutePath());
        gui.imprimirTexto("Tamano: "+tamanio+" bytes");
        gui.imprimirTexto("Ultima Modificacion: "+formato.format(fechaModificacion));
    
    
    }
}
