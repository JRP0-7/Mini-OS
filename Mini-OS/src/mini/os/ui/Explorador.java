package mini.os.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import mini.os.core.NodoArchivos;
import mini.os.core.Sistema;

// Ventana para navegar por las carpetas y archivos del sistema
public class Explorador extends JFrame {
    // Aquí guardamos qué extensiones van en cada carpeta (Images, Docs, etc)
    private Map<String, String[]> categorias = new HashMap<>();
    private File archivoCopiado;

    public Explorador(File raiz) {
        setTitle("Explorador de Archivos");
        setSize(800, 500);

        // Definimos las extensiones de cada categoría
        categorias.put("Images", new String[] { "jpg", "png", "jpeg" });
        categorias.put("Documents", new String[] { "txt", "docx", "pdf" });
        categorias.put("Music", new String[] { "mp3", "wav" });

        // Creamos el árbol de archivos y lo ponemos en el JTree
        DefaultMutableTreeNode NRaiz = crearNodo(raiz, 0);
        JTree arbol = new JTree(NRaiz);

        // JComboBox ordenar = new JCombo("Ordenar");
        // El combo para elegir cómo ordenar los archivos
        String opciones[] = { "Por Nombre", "Por fecha de Modificacion", "Tamaño", "Tipo" };
        JComboBox<String> ordenar = new JComboBox<String>(opciones);
        ordenar.addActionListener(e -> {
            // Usamos un hilo aparte para que la UI no se congele mientras ordena
            Thread t = new Thread(() -> {
                String seleccion = (String) ordenar.getSelectedItem();
                DefaultMutableTreeNode nNodo = null;
                switch (seleccion) {
                    case "Por Nombre":
                        nNodo = crearNodo(raiz, 1);
                        break;
                    case "Por fecha de Modificacion":
                        nNodo = crearNodo(raiz, 2);
                        break;
                    case "Tamaño":
                        nNodo = crearNodo(raiz, 3);
                        break;
                    case "Tipo":
                        nNodo = crearNodo(raiz, 4);
                        break;
                    default:
                        break;
                }
                final DefaultMutableTreeNode FNodo = nNodo;
                SwingUtilities.invokeLater(() -> {
                    arbol.setModel(new DefaultTreeModel(FNodo));
                });
            });
            t.start();
        });

        // Botón para mover automáticamente los archivos a sus carpetas según la extensión
        JButton surtir = new JButton("Organizar Archivos");
        surtir.addActionListener(e -> {
            Thread t = new Thread(() -> {
                organizar(raiz);
                recargarArbol(arbol, raiz);
            });
            t.start();
        });

        // Botón para cambiarle el nombre a un archivo seleccionado
        JButton renombrar = new JButton("Renombrar");
        renombrar.addActionListener(e -> {
            DefaultMutableTreeNode seleccion = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();

            if (seleccion == null) {
                return;
            }

            NodoArchivos nA = (NodoArchivos) seleccion.getUserObject();
            File destino = nA.getArchivo();
            String res = JOptionPane.showInputDialog(renombrar, "Ingrese el nuevo nombre del elemento");
            if (res == null || res.isEmpty()) {
                return;
            }

            destino.renameTo(new File(destino.getParent(), res));

            recargarArbol(arbol, raiz);
        });

        // Botón para copiar el archivo seleccionado en la memoria temporal
        JButton copiar = new JButton("Copiar");
        copiar.addActionListener(e -> {
            DefaultMutableTreeNode seleccion = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
            if (seleccion == null) {
                return;
            }

            // Guardamos el archivo en la variable archivoCopiado para usarlo después en el "Pegar"
            NodoArchivos nNodo = (NodoArchivos) seleccion.getUserObject();
            archivoCopiado = nNodo.getArchivo();

        });

        // Botón para pegar el archivo que copiamos antes
        JButton pegar = new JButton("Pegar");
        pegar.addActionListener(e -> {
            DefaultMutableTreeNode seleccion = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
            if (seleccion == null) {
                return;
            }

            // Si no hemos copiado nada antes, no hacemos nada
            if (archivoCopiado == null) {
                return;
            }

            NodoArchivos nNodo = (NodoArchivos) seleccion.getUserObject();
            File destino = nNodo.getArchivo();
            // Solo podemos pegar archivos dentro de carpetas
            if (!destino.isDirectory()) {
                JOptionPane.showMessageDialog(pegar, "Seleccion una carpeta para pegar", "Error al copiar",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                File hijos[] = destino.listFiles();
                boolean existe=false;
                if (hijos != null) {
                    // Chequeamos si ya hay un archivo con el mismo nombre en la carpeta destino
                    for (File hijo : hijos) {
                        if (archivoCopiado.getName().equals(hijo.getName())) {
                            existe = true;
                        }
                    }
                    if (existe) {
                        // Si existe, preguntamos si quiere reemplazarlo
                        int res = JOptionPane.showConfirmDialog(pegar, "Desea sobreescribir el archivo?");
                        if (res != JOptionPane.YES_OPTION) {
                            return;
                        } else {
                            Files.copy(archivoCopiado.toPath(),
                                    new File(destino, archivoCopiado.getName()).toPath(),
                                    StandardCopyOption.REPLACE_EXISTING);
                        }
                    } else {
                        // Si no existe, simplemente lo copiamos
                        Files.copy(archivoCopiado.toPath(), new File(destino, archivoCopiado.getName()).toPath());
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            recargarArbol(arbol, raiz);

        });

        JButton recargar = new JButton("Recargar");
        recargar.addActionListener(e->{ recargarArbol(arbol, raiz);});

        JScrollPane lista = new JScrollPane(arbol);

        JPanel pBotones = new JPanel();
        pBotones.add(ordenar);
        pBotones.add(surtir);
        pBotones.add(renombrar);
        pBotones.add(copiar);
        pBotones.add(pegar);
        pBotones.add(recargar);

        add(pBotones, BorderLayout.NORTH);
        add(lista, BorderLayout.CENTER);

    }

    // Método para armar el árbol de carpetas y archivos recursivamente
    private DefaultMutableTreeNode crearNodo(File archivo, int filtro) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(new NodoArchivos(archivo));

        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                // Dependiendo del filtro, ordenamos los archivos de forma distinta
                switch (filtro) {
                    case 1:
                        Arrays.sort(hijos, (d1, d2) -> d1.getName().compareTo(d2.getName()));
                        break;
                    case 2:
                        Arrays.sort(hijos, (d1, d2) -> Long.compare(d1.lastModified(), d2.lastModified()));
                        break;
                    case 3:
                        Arrays.sort(hijos, (d1, d2) -> Long.compare(d1.length(), d2.length()));
                        break;
                    case 4:
                        // Ordenar por extensión: sacamos la parte después del punto y comparamos
                        Arrays.sort(hijos, (d1, d2) -> {
                            String ext1 = d1.getName().substring(d1.getName().lastIndexOf(".") + 1);
                            String ext2 = d2.getName().substring(d2.getName().lastIndexOf(".") + 1);
                            return ext1.compareTo(ext2);
                        });
                        break;
                    default:
                        break;
                }
                for (File hijo2 : hijos) {
                    // Llamada recursiva para seguir bajando por las subcarpetas
                    nodo.add(crearNodo(hijo2, filtro));
                }
            }
        }
        return nodo;
    }

    // Este método recorre todo y mueve los archivos a sus carpetas correspondientes
    private void organizar(File folder) {
        // Si ya estamos dentro de una carpeta de categoría, no organizamos más aquí para no hacer lío
        for (String valor : categorias.keySet()) {
            if (valor.equalsIgnoreCase(folder.getName()))
                return;
        }

        File[] hijos = folder.listFiles();
        if (hijos != null) {
            for (File hijo2 : hijos) {
                if (hijo2.isDirectory()) {
                    // Si es carpeta, entramos en ella para organizar también lo de adentro
                    organizar(hijo2);
                }
                if (hijo2.isFile()) {
                    // Miramos la extensión del archivo
                    String extension = hijo2.getName().substring(hijo2.getName().lastIndexOf(".") + 1);
                    String cat = categoria(extension);
                    if (cat != null) {
                        // Si la extensión pertenece a una categoría, lo movemos a esa carpeta
                        File destino = new File(folder, cat);
                        if (!destino.exists()) {
                            destino.mkdir();
                        }
                        try {
                            Files.move(hijo2.toPath(), new File(destino, hijo2.getName()).toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    // Busca en el mapa a qué categoría pertenece una extensión
    private String categoria(String sufijo) {
        for (Entry<String, String[]> i : categorias.entrySet()) {
            for (String string : i.getValue()) {
                if (string.equals(sufijo.toLowerCase()))
                    return i.getKey();
            }
        }
        return null;
    }

    // Refresca el árbol visual para que se vean los cambios (como cuando mueves o renombras)
    private void recargarArbol(JTree arbol, File raiz) {
        DefaultMutableTreeNode nNodo = crearNodo(raiz, 0);
        SwingUtilities.invokeLater(() -> {
            arbol.setModel(new DefaultTreeModel(nNodo));
        });
    }

}
