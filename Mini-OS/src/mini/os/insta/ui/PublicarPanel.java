package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import mini.os.design.AccentButton;
import mini.os.insta.core.InstaClient;
import mini.os.model.ListaEnlazada;

public class PublicarPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private JTextArea txtDesc;
    private JLabel lblContador;
    private JLabel lblImagen;
    private JTextField txtCarpeta;
    private JPanel panelStickers;
    private String rutaImagen;

    public PublicarPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 10));
        form.setBackground(Color.WHITE);

        Font fuenteTexto = new Font("Segoe UI", Font.PLAIN, 13);

        txtDesc = new JTextArea(5, 40);
        txtDesc.setLineWrap(true);
        txtDesc.setFont(fuenteTexto);
        lblContador = new JLabel("0 / 220");
        lblContador.setFont(fuenteTexto);

        txtCarpeta = new JTextField();
        txtCarpeta.setFont(fuenteTexto);
        txtCarpeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        AccentButton btnImagen = new AccentButton("Adjuntar imagen");
        btnImagen.addActionListener(e -> elegirImagen());

        lblImagen = new JLabel("Sin imagen adjunta");
        lblImagen.setFont(fuenteTexto);
        lblImagen.setForeground(Color.decode("#8A867C"));
        lblImagen.setBorder(BorderFactory.createLineBorder(Color.decode("#E4E0D6")));

        AccentButton btnPublicar = new AccentButton("Publicar");
        btnPublicar.addActionListener(e -> publicar());

        JLabel lblDescripcion = new JLabel("Descripción (hashtags # y menciones @ permitidos):");
        JLabel lblCarpeta = new JLabel("Carpeta personal (opcional):");
        lblDescripcion.setFont(fuenteTexto);
        lblCarpeta.setFont(fuenteTexto);

        form.add(lblDescripcion);
        form.add(lblContador);
        form.add(new JScrollPane(txtDesc));
        form.add(relleno());
        form.add(lblCarpeta);
        form.add(txtCarpeta);
        form.add(btnImagen);
        form.add(new JScrollPane(lblImagen));
        form.add(btnPublicar);
        form.add(relleno());

        txtDesc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { contar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { contar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { contar(); }
        });

        add(form, BorderLayout.NORTH);

        panelStickers = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelStickers.setBackground(Color.WHITE);
        panelStickers.setBorder(BorderFactory.createTitledBorder("Adjuntar un sticker como imagen"));
        JScrollPane scrollStickers = new JScrollPane(panelStickers);
        scrollStickers.setBorder(BorderFactory.createEmptyBorder());
        add(scrollStickers, BorderLayout.CENTER);
    }

    // Celda vacia para completar la grilla, sin el gris default de JPanel
    private JPanel relleno() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        return p;
    }

    private void contar() {
        lblContador.setText(txtDesc.getText().length() + " / 220");
    }

    public void refrescar() {
        panelStickers.removeAll();
        try {
            ListaEnlazada<String> stickers = cliente.stickersDisponibles(user);
            for (int i = 0; i < stickers.getSize(); i++) {
                String ruta = stickers.get(i);
                JButton btn = new JButton(UtilInstaUI.cargarImagenCuadrada(ruta, 55));
                btn.setToolTipText(ruta);
                String r = ruta;
                btn.addActionListener(e -> {
                    rutaImagen = r;
                    lblImagen.setText("Sticker adjuntado");
                    lblImagen.setIcon(UtilInstaUI.cargarImagen(r, 80));
                });
                panelStickers.add(btn);
            }
        } catch (IOException ex) {
            panelStickers.add(new JLabel("Sin stickers disponibles"));
        }
        panelStickers.revalidate();
        panelStickers.repaint();
    }

    private void elegirImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            rutaImagen = f.getAbsolutePath();
            lblImagen.setIcon(UtilInstaUI.cargarImagen(f.getAbsolutePath(), 80));
            lblImagen.setText(f.getName());
        }
    }

    private void publicar() {
        String texto = txtDesc.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe una descripción para la publicación");
            return;
        }
        if (texto.length() > 220) {
            JOptionPane.showMessageDialog(this, "La descripción supera el máximo de 220 caracteres");
            return;
        }
        try {
            boolean ok = cliente.publicar(user, texto, rutaImagen, txtCarpeta.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Publicación creada correctamente");
                txtDesc.setText("");
                txtCarpeta.setText("");
                rutaImagen = null;
                lblImagen.setIcon(null);
                lblImagen.setText("Sin imagen adjunta");
                contar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo publicar. Revisa tu conexión.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }
}