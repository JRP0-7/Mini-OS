package mini.os.console;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Consola {

    private static boolean modoEscritura = false;
    private static String archivoDestino = null;
    private static boolean modoAppend = false;
    private static StringBuilder buffer = new StringBuilder();

    public static void abrir(File raizUser) {
        ConsoleGUI gui = new ConsoleGUI();
        ManejoFile archivos = new ManejoFile(raizUser);
        gui.setRutaActual(archivos.ubicacionActual());
        gui.setComandoListener(comando -> procesarComando(comando, gui, archivos));
        gui.setVisible(true);
    }

    private static void procesarComando(String entrada, ConsoleGUI gui,
            ManejoFile archivos) {
        String comandoCompleto = entrada.trim();

        if (comandoCompleto.isEmpty()) {
            return;
        }

        if (modoEscritura) {
            if (comandoCompleto.equals("EXIT")) {
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
        String comando = partes[0];
        String argumento = partes.length > 1 ? partes[1].trim() : "";

        switch (comando) {
            case "Mkdir":
                gui.imprimirTexto(archivos.crearCarpeta(argumento));
                break;
            case "Mfile":
                gui.imprimirTexto(archivos.crearArchivo(argumento));
                break;
            case "Rm":
                gui.imprimirTexto(archivos.borrar(argumento));
                break;
            case "Cd":
                gui.imprimirTexto(archivos.Mover(argumento));
                break;
            case "..":
                gui.imprimirTexto(archivos.Subir());
                break;
            case "Dir":
                gui.imprimirTexto(archivos.listar());
                break;
            case "Date":
                gui.imprimirTexto(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                break;
            case "Time":
                gui.imprimirTexto(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                break;
            case "Wr":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Wr <archivo>");
                } else {
                    modoEscritura = true;
                    modoAppend = false;
                    archivoDestino = argumento;
                    buffer.setLength(0);
                    gui.imprimirTexto("Escribe el contenido. Termina con EXIT.");
                }
                break;
            case "Rd":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Rd <archivo>");
                } else {
                    gui.imprimirTexto(archivos.leer(argumento));
                }
                break;
            case "Ap":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Ap <archivo>");
                } else {
                    modoEscritura = true;
                    modoAppend = true;
                    archivoDestino = argumento;
                    buffer.setLength(0);
                    gui.imprimirTexto("Escribe el contenido. Termina con EXIT.");
                }
                break;
            case "Ren":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Ren <actual> <nuevo>");
                } else {
                    String[] d = argumento.split("\\s+", 2);
                    if (d.length == 2) {
                        gui.imprimirTexto(archivos.renombrar(d[0], d[1]));
                    } else {
                        gui.imprimirTexto("Uso: Ren <actual> <nuevo>");
                    }
                }
                break;
            case "Copy":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Copy <origen> <destino>");
                } else {
                    String[] d = argumento.split("\\s+", 2);
                    if (d.length == 2) {
                        gui.imprimirTexto(archivos.copiar(d[0], d[1]));
                    } else {
                        gui.imprimirTexto("Uso: Copy <origen> <destino>");
                    }
                }
                break;
            case "Find":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Find <nombre>");
                } else {
                    Raiz.find(archivos.getCarpetaActual(), argumento, gui);
                }
                break;
            case "Info":
                if (argumento.isEmpty()) {
                    gui.imprimirTexto("Uso: Info <nombre>");
                } else {
                    Raiz.info(archivos.getCarpetaActual(), argumento, gui);
                }
                break;
            case "Tree":
                Raiz.tree(archivos.getCarpetaActual(), gui);
                break;
            case "Help":
                gui.imprimirTexto(
                        "Comandos disponibles: Mkdir, Mfile, Rm, Cd, Dir, Date, Time, Wr, Rd, Ap, Ren, Copy, Find, Info, Tree, Cls, Help y Exit.");
                break;
            case "Exit":
                gui.dispose();
                break;
            default:
                gui.imprimirTexto("'" + comando
                        + "' no se reconoce como un comando interno o externo. Escribe Help para ver los comandos disponibles.");
                break;
        }
        gui.setRutaActual(archivos.ubicacionActual());
    }
}
