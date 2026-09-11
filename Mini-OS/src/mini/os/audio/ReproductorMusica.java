package mini.os.audio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import javax.imageio.ImageIO;
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
        super("Reproductor de Music", true, true, true, true);
        setSize(800, 700);
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e){
                try {
                    player.stop();
                } catch (BasicPlayerException e1) {
                    e1.printStackTrace();
                }
            }
        });
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setBackground(Color.BLACK);
        this.raiz = raiz;

        cm = new CatalogoMusical(raiz);
        refresh();

        JLabel iCancion = new JLabel("Seleccione una cancion para comenzar a reproductir");
        add(iCancion, BorderLayout.NORTH);

        lista.setModel(modelo);
        lista.addListSelectionListener(e -> {
            indiceA = lista.getSelectedIndex();
            if (indiceA != -1) {
                iCancion.setText("Informacion de Cancion: " + informacion.get(indiceA).getNombre());
                String rutaImagen = informacion.get(indiceA).getRutaImagen();
                if (!rutaImagen.isEmpty()) {
                    File careta = new File(raiz.getParentFile(), "Images/Portadas/"+rutaImagen);
                    try {
                        BufferedImage img = ImageIO.read(careta);
                        if (img != null)
                            iCancion.setIcon(new ImageIcon(img.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                iCancion.setIcon(null);
            }
        });

        JPanel pBotones = new JPanel();
        JButton btnPlay = new JButton("Reproductir");
        btnPlay.addActionListener(e -> {
            if (indiceA != -1 && indiceA < canciones.size()) {
                try {
                    player.open(canciones.get(indiceA));
                    player.play();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
            }
        });

        JButton btnPausar = new JButton("Pauar");
        btnPausar.addActionListener(e -> {
            try {
                player.pause();
            } catch (BasicPlayerException e1) {
                e1.printStackTrace();
            }
        });

        JButton btnDetener = new JButton("Detener");
        btnDetener.addActionListener(e -> {
            try {
                player.stop();
            } catch (BasicPlayerException e1) {
                e1.printStackTrace();
            }
        });

        JButton btnAdicionar = new JButton("Adicionar Nueva Cancion");
        btnAdicionar.addActionListener(e -> {
            try {
                agregarCancion();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        add(new JScrollPane(lista), BorderLayout.CENTER);

        pBotones.add(btnPlay);
        pBotones.add(btnPausar);
        pBotones.add(btnDetener);
        pBotones.add(btnAdicionar);
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
            if(info!=null){
                informacion.add(info);
            }else{
                File p= buscarPortadas(file.getName());
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

    private void agregarCancion() throws IOException {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Canciones (.mp3)", "mp3"));
        selector.setAcceptAllFileFilterUsed(false);
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File original = selector.getSelectedFile();
            File nSong = new File(raiz, original.getName());

            if(nSong.exists()){
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
        filtrarPortadas(new File(raiz.getParentFile(), "Images/Portadas"));
        cargarInfo();
        llenarLista();

    }

}
