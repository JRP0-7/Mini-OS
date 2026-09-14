package mini.os.design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class DockButton extends JButton {
    private Image icono;

    public DockButton(String texto) {
        super(texto);
        setContentAreaFilled(false); // apaga el relleno gris default
        setFocusPainted(false); // apaga el rectángulo de foco default
        setBorderPainted(false); // apaga el borde 3D default
        setForeground(Color.WHITE);
        setPreferredSize(new Dimension(55, 55));
        setOpaque(false);
    }

    public DockButton(String texto, String rutaI) {
        this(texto);
        URL link = getClass().getResource(rutaI);
        if(link!=null){
            icono = new ImageIcon(link).getImage();
            setText("");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.decode("#4E7C59"));
        int diametro = Math.min(getWidth(), getHeight());
        g2d.fillOval(0, 0, diametro, diametro);

        if (icono != null) {
            int margen = diametro / 4;
            g2d.drawImage(icono, margen, margen, diametro - margen * 2, diametro - margen * 2, this);
        }
        super.paintComponent(g);
    }

}
