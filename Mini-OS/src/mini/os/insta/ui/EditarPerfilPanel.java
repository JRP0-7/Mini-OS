package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import mini.os.design.AccentButton;
import mini.os.design.AvatarCircle;
import mini.os.insta.core.InstaClient;
import mini.os.model.InstaUser;

public class EditarPerfilPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private AvatarCircle avatar;
    private JLabel lblInfo;
    private AccentButton btnEstado;

    public EditarPerfilPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Columna izquierda: bloque de color solido, avatar cuadrado centrado
        JPanel izquierda = new JPanel();
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));
        izquierda.setBackground(Color.decode("#7FA898"));
        izquierda.setPreferredSize(new java.awt.Dimension(220, 0));

        avatar = new AvatarCircle(140);
        avatar.setCircular(false);
        avatar.setColor(Color.decode("#5C8574"));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatar.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        izquierda.add(avatar);
        add(izquierda, BorderLayout.WEST);

        // Columna derecha: info arriba + botones, seccion "Editar perfil" abajo, separadas por linea
        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(Color.WHITE);

        JPanel seccionInfo = new JPanel();
        seccionInfo.setLayout(new BoxLayout(seccionInfo, BoxLayout.Y_AXIS));
        seccionInfo.setBackground(Color.WHITE);
        seccionInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(28, 28, 24, 28)));
        seccionInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setForeground(Color.decode("#2E2C28"));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setBackground(Color.WHITE);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        AccentButton btnFoto = new AccentButton("Cambiar foto de perfil");
        btnFoto.addActionListener(e -> cambiarFoto());

        btnEstado = new AccentButton("");
        btnEstado.addActionListener(e -> cambiarEstado());

        AccentButton btnSticker = new AccentButton("Importar sticker");
        btnSticker.addActionListener(e -> importarSticker());

        botones.add(btnFoto);
        botones.add(btnEstado);
        botones.add(btnSticker);

        seccionInfo.add(lblInfo);
        seccionInfo.add(botones);

        JPanel seccionEditar = new JPanel(new BorderLayout());
        seccionEditar.setBackground(Color.WHITE);
        seccionEditar.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        seccionEditar.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tituloEditar = new JLabel("Editar perfil");
        tituloEditar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloEditar.setForeground(Color.decode("#2E2C28"));
        seccionEditar.add(tituloEditar, BorderLayout.WEST);

        derecha.add(seccionInfo);
        derecha.add(seccionEditar);

        JScrollPane scroll = new JScrollPane(derecha);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    public void refrescar() {
        try {
            InstaUser u = cliente.perfil(user);
            if (u == null) {
                lblInfo.setText("Usuario no encontrado.");
                return;
            }
            avatar.setInicial(u.getNombre());
            var foto = UtilInstaUI.cargarImagenCuadrada(u.getRutaI(), 140);
            avatar.setFoto(foto == null ? null : foto.getImage());

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
