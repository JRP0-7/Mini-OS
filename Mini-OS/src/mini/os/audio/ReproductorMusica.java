package mini.os.audio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import mini.os.design.DockButton;
import mini.os.design.GradientPanel;

import com.mpatric.mp3agic.Mp3File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ReproductorMusica extends JInternalFrame {
    String clasificacion = "mp3";
    ArrayList<File> canciones = new ArrayList<>();
    CatalogoMusical cm = null;
    int indiceA = 0;
    ArrayList<InfoCancion> informacion = new ArrayList<>();
    private BasicPlayer player = new BasicPlayer();
    DefaultListModel<String> modelo = new DefaultListModel<>();
    JList<String> lista = new JList<>();
    private File raiz = null;

    String[] clasificacionImg = { "jpg", "jpeg", "png" };
    ArrayList<File> portadas = new ArrayList<>();

    public ReproductorMusica(File raiz) throws IOException, BasicPlayerException {
        super("Reproductor de Música", true, true, true, true);
        setSize(800, 700);
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                try {
                    player.stop();
                } catch (BasicPlayerException e1) {
                    e1.printStackTrace();
                }
            }
        });
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        this.raiz = raiz;

        cm = new CatalogoMusical(raiz);
        refresh();

        GradientPanel fondo = new GradientPanel("#E4EAF0", "#B7C6D6");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        JLabel iCancion = new JLabel("Seleccione una canción para comenzar a reproducir");
        JLabel imgPortada = new JLabel();
        JLabel iDescripcion = new JLabel(" ");
        JPanel pInfo = new JPanel();
        pInfo.setLayout(new BoxLayout(pInfo, BoxLayout.Y_AXIS));
        imgPortada.setHorizontalAlignment(SwingConstants.CENTER);
        imgPortada.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgPortada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        iCancion.setHorizontalAlignment(SwingConstants.CENTER);
        iCancion.setAlignmentX(Component.CENTER_ALIGNMENT);
        iCancion.setMaximumSize(new Dimension(Integer.MAX_VALUE, iCancion.getPreferredSize().height));
        iDescripcion.setHorizontalAlignment(SwingConstants.CENTER);
        iDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        iDescripcion.setMaximumSize(new Dimension(Integer.MAX_VALUE, iDescripcion.getPreferredSize().height));
        pInfo.add(imgPortada);
        pInfo.add(iCancion);
        pInfo.add(iDescripcion);
        add(pInfo, BorderLayout.NORTH);

        lista.setModel(modelo);
        lista.addListSelectionListener(e -> {
            indiceA = lista.getSelectedIndex();
            if (indiceA != -1) {
                iCancion.setText("Información de canción: " + informacion.get(indiceA).getNombre());
                String descripcion = informacion.get(indiceA).getDescripcion();
                iDescripcion.setText(descripcion.isEmpty() ? "(Sin descripción)" : "Descripción: " + descripcion);
                ImageIcon img = obtenerPortada(canciones.get(indiceA));
                if (img != null) {
                    imgPortada.setIcon(new ImageIcon(img.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH)));
                } else {
                    imgPortada.setIcon(null);
                }
            } else {
                iCancion.setText("");
                imgPortada.setIcon(null);
                iDescripcion.setText(" ");
            }
        });
        lista.setOpaque(true);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16)); 
                if (isSelected) {
                    label.setBackground(Color.decode("#5B7C99"));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.decode("#F0F4F8")); 
                    label.setForeground(Color.decode("#2C333A"));
                }
                return label;
            }
        });

        JPanel pBotones = new JPanel();
        DockButton btnPlay = new DockButton("Reproducir", Color.decode("#5B7C99"), "icons/reproducir.png");
        btnPlay.addActionListener(e -> {
            if (indiceA != -1 && indiceA < canciones.size()) {
                int indice = indiceA;
                Thread t = new Thread(() -> {
                    try {
                        player.open(canciones.get(indice));
                        player.play();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });

        DockButton btnPausar = new DockButton("Pausar", Color.decode("#5B7C99"), "icons/pausar.png");
        btnPausar.addActionListener(e -> {
            try {
                player.pause();
            } catch (BasicPlayerException e1) {
                e1.printStackTrace();
            }
        });

        DockButton btnDetener = new DockButton("Detener", Color.decode("#5B7C99"), "icons/detener.png");
        btnDetener.addActionListener(e -> {
            try {
                player.stop();
            } catch (BasicPlayerException e1) {
                e1.printStackTrace();
            }
        });

        DockButton btnAdicionar = new DockButton("Adicionar Nueva Canción", Color.decode("#5B7C99"), "icons/subir.png");
        btnAdicionar.addActionListener(e -> {
            try {
                agregarCancion();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        DockButton btnDescripcion = new DockButton("Editar Descripción", Color.decode("#5B7C99"), "icons/editar.png");
        btnDescripcion.addActionListener(e -> {
            if (indiceA == -1 || indiceA >= informacion.size()) {
                JOptionPane.showMessageDialog(this, "Seleccione una canción primero");
                return;
            }
            InfoCancion cancion = informacion.get(indiceA);
            String nueva = JOptionPane.showInputDialog(this, "Descripción de la canción:", cancion.getDescripcion());
            if (nueva == null || nueva.trim().isEmpty()) {
                return;
            }
            nueva = nueva.trim();
            cancion.setDescripcion(nueva);
            try {
                int reg = cm.buscarRegistro(cancion.getNombre());
                if (reg >= 0) {
                    cm.guardar(reg, cancion.getNombre(), nueva, cancion.getRutaImagen());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            iDescripcion.setText("Descripción: " + nueva);
        });

        DockButton btnAgregarPortada = new DockButton("Agregar Portada", Color.decode("#5B7C99"),
                "icons/subirImagen.png");
        btnAgregarPortada.addActionListener(e -> {
            if (indiceA == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una canción primero");
                return;
            }
            JFileChooser selector = new JFileChooser();
            selector.setFileFilter(new FileNameExtensionFilter("Imágenes (.jpg, .png)", "jpg", "jpeg", "png"));
            int resultado = selector.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File original = selector.getSelectedFile();
                String cancionNombre = canciones.get(indiceA).getName();
                String nombreSinExt = cancionNombre.substring(0, cancionNombre.lastIndexOf("."));
                String extImagen = original.getName().substring(original.getName().lastIndexOf("."));
                File destino = new File(raiz.getParentFile(), "Mis Imágenes/Portadas/" + nombreSinExt + extImagen);
                try {
                    Files.copy(original.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    portadas.add(destino);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al copiar la imagen: " + ex.getMessage());
                    return;
                }
                ImageIcon img = obtenerPortada(canciones.get(indiceA));
                if (img != null) {
                    imgPortada.setIcon(new ImageIcon(img.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH)));
                } else {
                    imgPortada.setIcon(null);
                }
            }
        });

        DockButton btnAnterior = new DockButton("Anterior", Color.decode("#5B7C99"), "icons/anterior.png");
        btnAnterior.addActionListener(e -> {
            int nuevo = indiceA - 1;
            if (nuevo >= 0) {
                lista.setSelectedIndex(nuevo);
            }
        });

        DockButton btnSiguiente = new DockButton("Siguiente", Color.decode("#5B7C99"), "icons/siguiente.png");
        btnSiguiente.addActionListener(e -> {
            int nuevo = indiceA + 1;
            if (nuevo < modelo.getSize()) {
                lista.setSelectedIndex(nuevo);
            }
        });

        add(new JScrollPane(lista), BorderLayout.CENTER);

        pBotones.add(btnAnterior);
        pBotones.add(btnPlay);
        pBotones.add(btnPausar);
        pBotones.add(btnDetener);
        pBotones.add(btnSiguiente);
        pBotones.add(btnAdicionar);
        pBotones.add(btnDescripcion);
        add(pBotones, BorderLayout.SOUTH);

    }

    private void filtrar(File folder) {
        File[] hijos = folder.listFiles();
        if (hijos != null) {
            for (File hijo : hijos) {
                String ext1 = hijo.getName().substring(hijo.getName().lastIndexOf(".") + 1);
                if (ext1.toLowerCase().equals(clasificacion)) {
                    canciones.add(hijo);
                }
            }
        }
    }

    private void cargarInfo() throws IOException {
        for (File file : canciones) {
            InfoCancion info = cm.buscarNombre(file.getName());
            if (info != null) {
                informacion.add(info);
            } else {
                File p = buscarPortadas(file.getName());
                String ruta = (p != null) ? p.getName() : "";
                int t = cm.tRegistros();
                cm.guardar(t, file.getName(), "", ruta);
                informacion.add(new InfoCancion(file.getName(), "", ruta));
            }
        }
    }

    private void llenarLista() {
        for (InfoCancion i : informacion) {
            modelo.addElement(i.getNombre());
        }
    }

    private void filtrarPortadas(File folder) {
        File[] hijos = folder.listFiles();
        if (hijos != null) {
            for (File hijo : hijos) {
                String ext1 = hijo.getName().substring(hijo.getName().lastIndexOf(".") + 1);
                for (String i : clasificacionImg) {
                    if (ext1.toLowerCase().equals(i)) {
                        portadas.add(hijo);
                    }
                }
            }
        }
    }

    private File buscarPortadas(String nombre) {
        String nameFile = nombre.substring(0, nombre.lastIndexOf(".") + 1);
        for (File portada : portadas) {
            String nombreP = portada.getName();
            String next = nombreP.substring(0, nombreP.lastIndexOf(".") + 1);

            if (next.equalsIgnoreCase(nameFile))
                return portada;

        }
        return null;
    }

    private ImageIcon obtenerPortada(File cancion) {
        try {
            Mp3File mp3 = new Mp3File(cancion);
            if (mp3.hasId3v2Tag()) {
                byte[] datos = mp3.getId3v2Tag().getAlbumImage();
                if (datos != null) {
                    return new ImageIcon(datos);
                }
            }
        } catch (Exception e) {
        }

        File p = buscarPortadas(cancion.getName());
        if (p != null) {
            return new ImageIcon(p.getPath());
        }
        return null;
    }

    private void agregarCancion() throws IOException {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Canciones (.mp3)", "mp3"));
        selector.setAcceptAllFileFilterUsed(false);
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File original = selector.getSelectedFile();
            File nSong = new File(raiz, original.getName());

            if (nSong.exists()) {
                JOptionPane.showMessageDialog(this, "Esa cancion ya existe");
                return;
            }
            Files.copy(original.toPath(), nSong.toPath());
            refresh();
        }
    }

    private void refresh() throws IOException {
        canciones.clear();
        informacion.clear();
        modelo.clear();
        portadas.clear();

        filtrar(raiz);
        filtrarPortadas(new File(raiz.getParentFile(), "Mis Imágenes/Portadas"));
        cargarInfo();
        llenarLista();

    }

}
