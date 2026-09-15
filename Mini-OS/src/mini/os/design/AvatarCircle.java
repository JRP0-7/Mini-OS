package mini.os.design;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

// Circulo de perfil: muestra la foto si hay, si no una inicial sobre fondo de color solido
public class AvatarCircle extends JPanel {
    private Image foto;
    private String inicial = "?";
    private Color color = Color.decode("#4E7C59");
    private boolean circular = true;

    public AvatarCircle(int diametro) {
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(diametro, diametro));
    }

    // false = cuadrado con esquinas suaves, en vez de circulo completo
    public void setCircular(boolean circular) {
        this.circular = circular;
        repaint();
    }

    public void setFoto(Image foto) {
        this.foto = foto;
        repaint();
    }

    public void setInicial(String nombre) {
        this.inicial = (nombre == null || nombre.isEmpty()) ? "?" : nombre.substring(0, 1).toUpperCase();
        repaint();
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int lado = Math.min(getWidth(), getHeight());
        java.awt.Shape forma = circular
                ? new Ellipse2D.Float(0, 0, lado, lado)
                : new RoundRectangle2D.Float(0, 0, lado, lado, 12, 12);

        if (foto != null) {
            g2d.setClip(forma);
            g2d.drawImage(foto, 0, 0, lado, lado, this);
            g2d.setClip(null);
        } else {
            g2d.setColor(color);
            g2d.fill(forma);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, (int) (lado * 0.4)));
            java.awt.FontMetrics fm = g2d.getFontMetrics();
            int x = (lado - fm.stringWidth(inicial)) / 2;
            int y = (lado - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(inicial, x, y);
        }
        super.paintComponent(g);
    }
}
