package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

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

    public static ImageIcon cargarImagenCuadrada(String ruta, int lado) {
        if (ruta == null) return null;
        File f = new File(ruta);
        if (!f.exists()) return null;
        ImageIcon icono = new ImageIcon(ruta);
        return new ImageIcon(icono.getImage().getScaledInstance(lado, lado, Image.SCALE_SMOOTH));
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
            ImageIcon icono = cargarImagen(p.getrImagen(), 240);
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

    public static String escapar(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}