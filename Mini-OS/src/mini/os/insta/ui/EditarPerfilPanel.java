package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import mini.os.insta.core.InstaClient;
import mini.os.model.InstaUser;

public class EditarPerfilPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private JLabel lblFoto;
    private JLabel lblInfo;
    private JButton btnEstado;

    public EditarPerfilPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());

        JPanel izquierda = new JPanel(new GridLayout(2, 1));
        izquierda.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblFoto = new JLabel("Sin foto", JLabel.CENTER);
        lblFoto.setHorizontalAlignment(JLabel.CENTER);
        lblFoto.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
        lblFoto.setPreferredSize(new java.awt.Dimension(180, 200));

        JLabel titulo = new JLabel("Editar perfil", JLabel.CENTER);
        titulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));

        izquierda.add(lblFoto);
        izquierda.add(titulo);
        add(izquierda, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        lblInfo = new JLabel(" ");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        centro.add(lblInfo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnFoto = new JButton("Cambiar foto de perfil");
        btnFoto.addActionListener(e -> cambiarFoto());

        btnEstado = new JButton();
        btnEstado.addActionListener(e -> cambiarEstado());

        JButton btnSticker = new JButton("Importar sticker");
        btnSticker.addActionListener(e -> importarSticker());

        botones.add(btnFoto);
        botones.add(btnEstado);
        botones.add(btnSticker);
        centro.add(botones, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    public void refrescar() {
        try {
            InstaUser u = cliente.perfil(user);
            if (u == null) {
                lblInfo.setText("Usuario no encontrado.");
                lblFoto.setText("Sin foto");
                return;
            }
            lblFoto.setIcon(UtilInstaUI.cargarImagenCuadrada(u.getRutaI(), 160));
            lblFoto.setText(u.getRutaI() == null ? "Sin foto" : "");
            lblFoto.setHorizontalTextPosition(JLabel.CENTER);
            lblFoto.setVerticalTextPosition(JLabel.BOTTOM);

            String estado = u.isActivo() ? "Activa" : "Inactiva";
            lblInfo.setText("<html><b>Usuario:</b> @" + UtilInstaUI.escapar(u.getUser()) + "<br>"
                    + "<b>Nombre completo:</b> " + UtilInstaUI.escapar(u.getNombre()) + "<br>"
                    + "<b>Estado de la cuenta:</b> " + estado + "<br>"
                    + "<b>Nota:</b> si desactivas tu cuenta dejaras de aparecer en las busquedas "
                    + "y tus publicaciones no se mostraran. Podras reactivarla desde aqui."
                    + "</html>");
            if (u.isActivo()) {
                btnEstado.setText("Desactivar cuenta");
            } else {
                btnEstado.setText("Reactivar cuenta");
            }
        } catch (IOException ex) {
            lblInfo.setText("Error de conexión: " + ex.getMessage());
        }
    }

    private void cambiarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                String nueva = cliente.cambiarFoto(user, f.getAbsolutePath());
                if (nueva == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo cambiar la foto");
                } else {
                    JOptionPane.showMessageDialog(this, "Foto de perfil actualizada");
                    refrescar();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
            }
        }
    }

    private void cambiarEstado() {
        try {
            InstaUser u = cliente.perfil(user);
            if (u == null) return;
            boolean nueva = !u.isActivo();
            if (!nueva) {
                int op = JOptionPane.showConfirmDialog(this,
                        "¿Desactivar tu cuenta? Ya no apareceras en las busquedas.",
                        "Desactivar cuenta", JOptionPane.YES_NO_OPTION);
                if (op != JOptionPane.YES_OPTION) return;
            }
            boolean ok = cliente.activarDesactivar(user, nueva);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        nueva ? "Cuenta reactivada correctamente" : "Cuenta desactivada");
                refrescar();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }

    private void importarSticker() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                String nombre = JOptionPane.showInputDialog(this,
                        "Nombre para el sticker (opcional, sin extension):", f.getName().replaceFirst("\\.[^.]+$", ""));
                String ruta = nombre == null ? null : cliente.importarSticker(user, f.getAbsolutePath(), nombre);
                if (ruta == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo importar el sticker");
                } else {
                    JOptionPane.showMessageDialog(this, "Sticker importado correctamente");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
            }
        }
    }
}