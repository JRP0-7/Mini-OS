package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mini.os.insta.core.InstaClient;
import mini.os.model.InstaUser;

public class PerfilPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private JLabel lblFoto;
    private JLabel lblInfo;

    public PerfilPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());

        JPanel izquierda = new JPanel(new GridLayout(2, 1));
        izquierda.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblFoto = new JLabel("Sin foto", JLabel.CENTER);
        lblFoto.setHorizontalAlignment(JLabel.CENTER);
        lblFoto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblFoto.setPreferredSize(new java.awt.Dimension(180, 200));
        izquierda.add(lblFoto);

        JLabel titulo = new JLabel("Mi perfil", JLabel.CENTER);
        titulo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
        izquierda.add(titulo);

        add(izquierda, BorderLayout.WEST);

        lblInfo = new JLabel(" ");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(new JScrollPane(lblInfo), BorderLayout.CENTER);
    }

    public void refrescar() {
        try {
            InstaUser u = cliente.perfil(user);
            if (u == null) {
                lblInfo.setText("Usuario no encontrado.");
                lblFoto.setText("Sin foto");
                return;
            }
            int seguidores = cliente.seguidoresDe(user).getSize();
            int siguiendo = cliente.siguiendoDe(user).getSize();
            int publicaciones = cliente.publicacionesDe(user).getSize();

            lblFoto.setIcon(UtilInstaUI.cargarImagenCuadrada(u.getRutaI(), 160));
            lblFoto.setText(u.getRutaI() == null ? "Sin foto" : "");
            lblFoto.setHorizontalTextPosition(JLabel.CENTER);
            lblFoto.setVerticalTextPosition(JLabel.BOTTOM);

            String estado = u.isActivo() ? "Activa" : "Inactiva";
            lblInfo.setText("<html><body style='font-size:13px;'>"
                    + "<h2>" + UtilInstaUI.escapar(u.getNombre()) + "</h2>"
                    + "<b>Username:</b> @" + UtilInstaUI.escapar(u.getUser()) + "<br>"
                    + "<b>Edad:</b> " + u.getEdad() + "<br>"
                    + "<b>Género:</b> " + u.getGenero() + "<br>"
                    + "<b>Fecha de registro:</b> " + UtilInstaUI.fechaSolo(u.getFechaRegistro()) + "<br>"
                    + "<b>Seguidores:</b> " + seguidores + "<br>"
                    + "<b>Siguiendo:</b> " + siguiendo + "<br>"
                    + "<b>Publicaciones:</b> " + publicaciones + "<br>"
                    + "<b>Estado de la cuenta:</b> " + estado + "<br>"
                    + "</body></html>");
        } catch (IOException ex) {
            lblInfo.setText("Error de conexión: " + ex.getMessage());
        }
    }
}