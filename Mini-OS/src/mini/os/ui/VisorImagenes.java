package mini.os.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VisorImagenes extends JInternalFrame {

    private static final Color FONDO = new Color(0x111111);
    private static final Color BARRA = new Color(0x1E1E1E);
    private static final Color TEXTO = new Color(0xEDEDED);
    private static final int objAncho = 900;
    private static final int objAlto = 560;

    private final String[] clasificacion = { "jpg", "png", "jpeg" };
    private final ArrayList<File> imagenes = new ArrayList<>();
    private int indiceActual = 0;

    private final JLabel NImagen = new JLabel("", SwingConstants.CENTER);
    private final JButton btnAnterior = new JButton("< Anterior");
    private final JButton btnSiguiente = new JButton("Siguiente >");

    public VisorImagenes(File raiz) {
        super("Visualizador de Imagenes", true, true, true, true);
        setSize(1000, 720);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(FONDO);
        getContentPane().setLayout(new BorderLayout());

        filtrar(raiz);

        NImagen.setForeground(TEXTO);
        NImagen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        NImagen.setVerticalTextPosition(SwingConstants.BOTTOM);
        NImagen.setHorizontalTextPosition(SwingConstants.CENTER);
        NImagen.setIconTextGap(12);
        NImagen.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(NImagen, BorderLayout.CENTER);

        JPanel pbotones = new JPanel();
        pbotones.setBackground(BARRA);
        pbotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        estilizar(btnAnterior);
        estilizar(btnSiguiente);
        btnAnterior.addActionListener(e -> mover(-1));
        btnSiguiente.addActionListener(e -> mover(1));
        pbotones.add(btnAnterior);
        pbotones.add(btnSiguiente);
        add(pbotones, BorderLayout.SOUTH);

        refrescar();
    }

    private void estilizar(JButton b) {
        b.setPreferredSize(new Dimension(140, 36));
        b.setFocusPainted(false);
        b.setBackground(new Color(0x2563EB));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setBorder(BorderFactory.createEmptyBorder());
    }

    private void mover(int paso) {
        int nuevo = indiceActual + paso;
        if (nuevo < 0 || nuevo >= imagenes.size()) {
            return;
        }
        indiceActual = nuevo;
        refrescar();
    }

    // Abre una imagen concreta seleccionada desde el Explorador
    public void genImagen(File archivo) {
        for (int i = 0; i < imagenes.size(); i++) {
            if (imagenes.get(i).getAbsolutePath().equals(archivo.getAbsolutePath())) {
                indiceActual = i;
                break;
            }
        }
        refrescar();
    }

    // Unico punto que pinta la imagen actual y sincroniza los botones
    private void refrescar() {
        boolean hay = !imagenes.isEmpty();
        btnAnterior.setEnabled(hay && indiceActual > 0);
        btnSiguiente.setEnabled(hay && indiceActual < imagenes.size() - 1);

        if (!hay) {
            NImagen.setIcon(null);
            NImagen.setText("No se encontraron imagenes en la carpeta");
            return;
        }

        File actual = imagenes.get(indiceActual);
        try {
            ImageIcon original = new ImageIcon(actual.getCanonicalPath());
            NImagen.setIcon(escalar(original));
            NImagen.setText((indiceActual + 1) + " / " + imagenes.size() + "   -   " + actual.getName());
        } catch (IOException e) {
            NImagen.setIcon(null);
            NImagen.setText("No se pudo abrir: " + actual.getName());
            e.printStackTrace();
        }
    }

    // Escala respetando la proporcion original, sin agrandar imagenes pequeñas
    private ImageIcon escalar(ImageIcon icon) {
        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();
        if (iw <= 0 || ih <= 0) {
            return icon;
        }
        double factor = Math.min((double) objAncho / iw, (double) objAlto / ih);
        if (factor >= 1.0) {
            return icon;
        }
        int w = (int) Math.round(iw * factor);
        int h = (int) Math.round(ih * factor);
        return new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private void filtrar(File folder) {
        File[] hijos = folder.listFiles();
        if (hijos == null) {
            return;
        }
        for (File hijo : hijos) {
            if (!hijo.isFile()) {
                continue;
            }
            String nombre = hijo.getName();
            int punto = nombre.lastIndexOf('.');
            if (punto < 0) {
                continue;
            }
            String ext = nombre.substring(punto + 1).toLowerCase();
            for (String permitida : clasificacion) {
                if (ext.equals(permitida)) {
                    imagenes.add(hijo);
                    break;
                }
            }
        }
    }
}
