package mini.os.design;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;

public class GradientPanel extends JPanel {
    private Color colorInicio;
    private Color colorFin;
    private Color[] colores; // si viene seteado (3+ paradas), tiene prioridad sobre colorInicio/colorFin

    public GradientPanel(String colorI, String colorF) {
        this.colorInicio = Color.decode(colorI);
        this.colorFin = Color.decode(colorF);
    }

    // Gradiente con 3 o mas paradas, repartidas parejo a lo largo de la diagonal
    public GradientPanel(String... colores) {
        this.colores = new Color[colores.length];
        for (int i = 0; i < colores.length; i++) {
            this.colores[i] = Color.decode(colores[i]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        if (colores != null && colores.length >= 2) {
            float[] paradas = new float[colores.length];
            for (int i = 0; i < colores.length; i++) {
                paradas[i] = i / (float) (colores.length - 1);
            }
            LinearGradientPaint degradado = new LinearGradientPaint(
                    new Point(0, 0), new Point(Math.max(1, getWidth()), Math.max(1, getHeight())),
                    paradas, colores);
            g2d.setPaint(degradado);
        } else {
            GradientPaint degradado = new GradientPaint(0, 0, colorInicio, getWidth(), getHeight(), colorFin, false);
            g2d.setPaint(degradado);
        }
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
