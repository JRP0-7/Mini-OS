package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import mini.os.insta.model.Publicacion;

public class UtilInstaUI {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String fecha(Date d) {
        if (d == null) return "";
        return FMT.format(d);
    }

    public static String fechaSolo(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(d);
    }

    public static ImageIcon cargarImagen(String ruta, int alto) {
        if (ruta == null) return null;
        File f = new File(ruta);
        if (!f.exists()) return null;
        ImageIcon icono = new ImageIcon(ruta);
        int w = icono.getIconWidth();
        int h = icono.getIconHeight();
        int ancho = (w <= 0 || h <= 0) ? alto : (int) Math.round(w * (alto / (double) h));
        return new ImageIcon(icono.getImage().getScaledInstance(Math.max(1, ancho), alto, Image.SCALE_SMOOTH));
    }

    // Recorta al centro en cuadrado antes de escalar — si no, las imagenes que no son
    // cuadradas (la mayoria de stickers/fotos) salen estiradas/deformadas
    public static ImageIcon cargarImagenCuadrada(String ruta, int lado) {
        if (ruta == null) return null;
        File f = new File(ruta);
        if (!f.exists()) return null;
        try {
            BufferedImage original = ImageIO.read(f);
            if (original == null) return null;

            int w = original.getWidth();
            int h = original.getHeight();
            int ladoRecorte = Math.min(w, h);
            int recorteX = (w - ladoRecorte) / 2;
            int recorteY = (h - ladoRecorte) / 2;

            BufferedImage recortada = original.getSubimage(recorteX, recorteY, ladoRecorte, ladoRecorte);
            return new ImageIcon(recortada.getScaledInstance(lado, lado, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            return null;
        }
    }

    // Recorta al centro en proporcion vertical 4:5 (1080x1350, como pide el doc de specs)
    // y despues escala — asi todas las publicaciones se ven parejas en el feed
    public static ImageIcon cargarImagenVertical(String ruta, int ancho) {
        if (ruta == null) return null;
        File f = new File(ruta);
        if (!f.exists()) return null;
        try {
            BufferedImage original = ImageIO.read(f);
            if (original == null) return null;

            int alto = (int) Math.round(ancho * 5.0 / 4.0);
            double relacionDeseada = ancho / (double) alto;

            int w = original.getWidth();
            int h = original.getHeight();
            double relacionActual = w / (double) h;

            int recorteX = 0, recorteY = 0, recorteW = w, recorteH = h;
            if (relacionActual > relacionDeseada) {
                // imagen mas ancha de lo necesario: recorta los costados
                recorteW = (int) Math.round(h * relacionDeseada);
                recorteX = (w - recorteW) / 2;
            } else {
                // imagen mas alta de lo necesario: recorta arriba/abajo
                recorteH = (int) Math.round(w / relacionDeseada);
                recorteY = (h - recorteH) / 2;
            }
            BufferedImage recortada = original.getSubimage(recorteX, recorteY, recorteW, recorteH);
            return new ImageIcon(recortada.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            return null;
        }
    }

    public static JPanel panelPublicacion(Publicacion p) {
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 219, 219)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        caja.setBackground(Color.WHITE);

        String texto = p.getTexto() == null || p.getTexto().isEmpty()
                ? "" : " &quot;" + escapar(p.getTexto()) + "&quot;";
        JLabel encabezado = new JLabel("<html><b>" + escapar(p.getAutor()) + "</b> escribio:" + texto
                + "<br><font color='gray'>— " + fecha(p.getFecha()) + "</font></html>");
        caja.add(encabezado, BorderLayout.NORTH);

        if (p.getrImagen() != null) {
            ImageIcon icono = cargarImagenVertical(p.getrImagen(), 280);
            if (icono != null) {
                JLabel imagen = new JLabel(icono);
                imagen.setHorizontalAlignment(JLabel.CENTER);
                JPanel centro = new JPanel(new BorderLayout());
                centro.setBackground(Color.WHITE);
                centro.add(imagen, BorderLayout.CENTER);
                caja.add(centro, BorderLayout.CENTER);
            }
        }
        return caja;
    }

    // Tarjeta con borde redondeado, una fila de texto por linea, separadas por una linea fina
    public static JPanel panelInfoCard(String... filas) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.decode("#E4E0D6"), 1, true));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < filas.length; i++) {
            JLabel fila = new JLabel(filas[i]);
            fila.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fila.setForeground(Color.decode("#2E2C28"));
            fila.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(fila);
            if (i < filas.length - 1) {
                JSeparator sep = new JSeparator();
                sep.setForeground(Color.decode("#E4E0D6"));
                card.add(sep);
            }
        }
        return card;
    }

    public static String escapar(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}