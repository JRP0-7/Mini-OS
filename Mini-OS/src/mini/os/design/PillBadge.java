package mini.os.design;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

// Etiqueta tipo "pildora": borde redondeado completo, sin relleno, texto y borde del mismo color
public class PillBadge extends JLabel {
    private Color color;

    public PillBadge(String texto, Color color) {
        super(texto, SwingConstants.CENTER);
        this.color = color;
        setOpaque(false);
        setForeground(color);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 24, 10, 24));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new java.awt.BasicStroke(2));
        int arco = getHeight();
        g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arco, arco);
        super.paintComponent(g);
    }
}
