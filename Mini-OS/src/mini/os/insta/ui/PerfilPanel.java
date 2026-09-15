package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mini.os.design.AvatarCircle;
import mini.os.design.PillBadge;
import mini.os.insta.core.InstaClient;
import mini.os.model.InstaUser;

public class PerfilPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private AvatarCircle avatar;
    private JLabel lblNombre;
    private JLabel lblUsername;
    private JPanel panelDatos;
    private JLabel lblPublicaciones;
    private JPanel panelBadge;

    public PerfilPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Columna izquierda: gris muy claro, avatar circular + "Mi perfil"
        JPanel izquierda = new JPanel();
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));
        izquierda.setBackground(Color.decode("#FAFAF8"));
        izquierda.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));
        izquierda.setPreferredSize(new java.awt.Dimension(220, 0));

        avatar = new AvatarCircle(120);
        avatar.setCircular(false);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tituloMi = new JLabel("Mi perfil");
        tituloMi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tituloMi.setForeground(Color.decode("#2E2C28"));
        tituloMi.setAlignmentX(Component.CENTER_ALIGNMENT);
        tituloMi.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        izquierda.add(avatar);
        izquierda.add(tituloMi);
        add(izquierda, BorderLayout.WEST);

        // Columna derecha: nombre grande, @username, tarjeta de datos, publicaciones, badge de estado
        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(Color.WHITE);
        derecha.setBorder(BorderFactory.createEmptyBorder(40, 32, 40, 32));

        lblNombre = new JLabel(" ");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNombre.setForeground(Color.decode("#2E2C28"));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblUsername = new JLabel(" ");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblUsername.setForeground(Color.decode("#8A867C"));
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUsername.setBorder(BorderFactory.createEmptyBorder(2, 0, 20, 0));

        panelDatos = new JPanel();
        panelDatos.setLayout(new BorderLayout());
        panelDatos.setOpaque(false);
        panelDatos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDatos.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 220));

        lblPublicaciones = new JLabel(" ");
        lblPublicaciones.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblPublicaciones.setForeground(Color.decode("#2E2C28"));
        lblPublicaciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPublicaciones.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        panelBadge = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        panelBadge.setOpaque(false);
        panelBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        derecha.add(lblNombre);
        derecha.add(lblUsername);
        derecha.add(panelDatos);
        derecha.add(lblPublicaciones);
        derecha.add(panelBadge);
        derecha.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(derecha);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    public void refrescar() {
        try {
            InstaUser u = cliente.perfil(user);
            if (u == null) {
                lblNombre.setText("Usuario no encontrado.");
                lblUsername.setText(" ");
                return;
            }
            int seguidores = cliente.seguidoresDe(user).getSize();
            int siguiendo = cliente.siguiendoDe(user).getSize();
            int publicaciones = cliente.publicacionesDe(user).getSize();

            avatar.setFoto(UtilInstaUI.cargarImagenCuadrada(u.getRutaI(), 120) == null ? null
                    : UtilInstaUI.cargarImagenCuadrada(u.getRutaI(), 120).getImage());
            avatar.setInicial(u.getNombre());

            lblNombre.setText(u.getNombre());
            lblUsername.setText("@" + u.getUser());

            panelDatos.removeAll();
            panelDatos.add(UtilInstaUI.panelInfoCard(
                    "Edad: " + u.getEdad(),
                    "Género: " + u.getGenero(),
                    "Miembro desde: " + UtilInstaUI.fechaSolo(u.getFechaRegistro()),
                    "Seguidores: " + seguidores + "        Siguiendo: " + siguiendo
            ), BorderLayout.NORTH);
            panelDatos.revalidate();

            lblPublicaciones.setText("Publicaciones: " + publicaciones);

            boolean activa = u.isActivo();
            panelBadge.removeAll();
            panelBadge.add(new PillBadge(activa ? "Activa" : "Inactiva",
                    activa ? Color.decode("#4E7C59") : Color.decode("#B0463C")));
            panelBadge.revalidate();
            panelBadge.repaint();
        } catch (IOException ex) {
            lblNombre.setText("Error de conexión: " + ex.getMessage());
        }
    }
}
