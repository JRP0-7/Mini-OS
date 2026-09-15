package mini.os.design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

// Boton plano para menus laterales (sidebar), estilo app moderna: sin borde 3D,
// se resalta con un fondo suave cuando esta seleccionado (pestaña activa)
public class SidebarButton extends JToggleButton {
    private Color colorActivo;

    public SidebarButton(String texto) {
        this(texto, Color.decode("#4E7C59"));
    }

    public SidebarButton(String texto, Color colorActivo) {
        super(texto);
        this.colorActivo = colorActivo;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 16, 10, 16));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(Color.decode("#D8DDD9"));
        setPreferredSize(new Dimension(180, 40));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected()) {
            g2d.setColor(colorActivo);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            setForeground(Color.WHITE);
        } else {
            setForeground(Color.decode("#D8DDD9"));
        }
        super.paintComponent(g);
    }
}
