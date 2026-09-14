package mini.os.console;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Consola {

    private static boolean modoEscritura = false;
    private static String archivoDestino = null;
    private static boolean modoAppend = false;
    private static StringBuilder buffer = new StringBuilder();

    public static ConsoleGUI abrir(File raizUser) {
        ConsoleGUI gui = new ConsoleGUI();
        ManejoFile archivos = new ManejoFile(raizUser);
        gui.setRutaActual(archivos.ubicacionActual());
        gui.setComandoListener(comando -> procesarComando(comando, gui, archivos));
        return gui;
    }

    private static void procesarComando(String entrada, ConsoleGUI gui,
            ManejoFile archivos) {
        String comandoCompleto = entrada.trim();

        if (comandoCompleto.isEmpty()) {
            return;
        }

        if (modoEscritura) {
            if (comandoCompleto.equalsIgnoreCase("EXIT")) {
                String res = archivos.escribir(archivoDestino, buffer.toString(), modoAppend);
                if (res.isEmpty()) {
                    gui.imprimirTexto("Guardado en " + archivoDestino);
                } else {
                    gui.imprimirTexto(res);
                }
                modoEscritura = false;
                buffer.setLength(0);
            } else {
                buffer.append(entrada).append("\n");
            }
            return;
        }

        // El segundo valor conserva el texto completo del nombre indicado.
        String[] partes = comandoCompleto.split("\\s+", 2);
        String comando = partes[0].toLowerCase();
        String argumento = partes.length > 1 ? partes[1].trim() : "";

        switch (comando) {
            case "mkdir":
                gui.imprimirTexto(archivos.crearCarpeta(argumento));
                break;
            case "mfile":
                gui.imprimirTexto(archivos.crearArchivo(argumento));
                break;
            case "rm":
                gui.imprimirTexto(archivos.borrar(argumento));
                break;
            case "cd":
                if (argumento.equals("..")) {
                    gui.imprimirTexto(archivos.Subir());
                } else {
                    gui.imprimirTexto(archivos.Mover(argumento));
                }
                break;
            case "cd..":
                gui.imprimirTexto(archivos.Subir());
                break;
            case "..":
                gui.imprimirTexto(archivos.Subir());
                break;
            case "dir":
                gui.imprimirTexto(archivos.listar());
                break;
            case "date":
                gui.imprimirTexto(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                break;
            case "time":
                gui.imprimirTexto(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                break;
            case "wr":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: wr <archivo>");
                } else {
                    modoEscritura = true;
                    modoAppend = false;
                    archivoDestino = argumento;
                    buffer.setLength(0);
                    gui.imprimirTexto("Escribe el contenido. Termina con EXIT.");
                }
                break;
            case "rd":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: rd <archivo>");
                } else {
                    gui.imprimirTexto(archivos.leer(argumento));
                }
                break;
            case "ap":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: ap <archivo>");
                } else {
                    modoEscritura = true;
                    modoAppend = true;
                    archivoDestino = argumento;
                    buffer.setLength(0);
                    gui.imprimirTexto("Escribe el contenido. Termina con EXIT.");
                }
                break;
            case "ren":
            case "rename":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: ren <actual> <nuevo>");
                } else {
                    String[] d = argumento.split("\\s+", 2);
                    if (d.length == 2) {
                        gui.imprimirTexto(archivos.renombrar(d[0], d[1]));
                    } else {
                        gui.imprimirTexto("Uso: ren <actual> <nuevo>");
                    }
                }
                break;
            case "copy":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: copy <origen> <destino>");
                } else {
                    String[] d = argumento.split("\\s+", 2);
                    if (d.length == 2) {
                        gui.imprimirTexto(archivos.copiar(d[0], d[1]));
                    } else {
                        gui.imprimirTexto("Uso: copy <origen> <destino>");
                    }
                }
                break;
            case "find":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: find <nombre>");
                } else {
                    Raiz.find(archivos.getCarpetaActual(), argumento, gui);
                }
                break;
            case "info":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: info <nombre>");
                } else {
                    Raiz.info(archivos.getCarpetaActual(), argumento, gui);
                }
                break;
            case "tree":
                Raiz.tree(archivos.getCarpetaActual(), gui);
                break;
            case "help":
                gui.imprimirTexto(
                        "Comandos disponibles: mkdir, mfile, rm, cd, cd.., dir, date, time, wr, rd, ap, ren, copy, find, info, tree, cls, help y exit. Los comandos pueden escribirse en mayusculas o minusculas.");
                break;
            case "exit":
                gui.dispose();
                break;
            default:
                gui.imprimirTexto("'" + comando
                        + "' no se reconoce como un comando interno o externo. Escribe help para ver los comandos disponibles.");
                break;
        }
        gui.setRutaActual(archivos.ubicacionActual());
    }
}
