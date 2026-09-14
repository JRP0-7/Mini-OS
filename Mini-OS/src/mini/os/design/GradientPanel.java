package mini.os.design;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GradientPanel extends JPanel {

    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint degradado = new GradientPaint(0, 0, Color.decode("#b9dae2"), getWidth(), getHeight(), Color.decode("#508e63"),false);
        super.paintComponent(g);
        g2d.setPaint(degradado);
        g2d.fillRect(0,0,getWidth(), getHeight());

    }   
}
