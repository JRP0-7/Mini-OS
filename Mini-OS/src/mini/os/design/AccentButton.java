package mini.os.design;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

public class AccentButton extends JButton {
    public AccentButton(String texto) {
        super(texto);
        setContentAreaFilled(false); // apaga el relleno gris default
        setFocusPainted(false); // apaga el rectángulo de foco default
        setBorderPainted(false); // apaga el borde 3D default
        setForeground(Color.WHITE);
        setOpaque(false);
    }

    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.decode("#4E7C59"));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g);
    }
}
