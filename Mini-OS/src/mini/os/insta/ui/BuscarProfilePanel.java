package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import mini.os.design.AccentButton;
import mini.os.insta.core.InstaClient;
import mini.os.insta.model.Publicacion;
import mini.os.model.InstaUser;
import mini.os.model.ListaEnlazada;

public class BuscarProfilePanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private JTextField txtBuscar;
    private JList<String> resultados;
    private DefaultListModel<String> modelo;
    private JPanel detalle;
    private String perfilActual;

    public BuscarProfilePanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        Font fuenteTexto = new Font("Segoe UI", Font.PLAIN, 13);

        JPanel barra = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        barra.setBackground(Color.decode("#4E7C59"));
        barra.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        txtBuscar = new JTextField(18);
        txtBuscar.setFont(fuenteTexto);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        txtBuscar.setOpaque(false);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setCaretColor(Color.WHITE);
        AccentButton btnBuscar = new AccentButton("Buscar personas");
        btnBuscar.addActionListener(e -> buscar());
        JLabel lblTexto = new JLabel("Texto:");
        lblTexto.setFont(fuenteTexto);
        lblTexto.setForeground(Color.WHITE);
        barra.add(lblTexto);
        barra.add(txtBuscar);
        barra.add(btnBuscar);
        add(barra, BorderLayout.NORTH);

        modelo = new DefaultListModel<>();
        resultados = new JList<>(modelo);
        resultados.setFont(fuenteTexto);
        resultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && resultados.getSelectedValue() != null) {
                cargarPerfil(resultados.getSelectedValue());
            }
        });

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBackground(Color.WHITE);
        JScrollPane scrollResultados = new JScrollPane(resultados);
        scrollResultados.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#E4E0D6")));
        izquierda.add(scrollResultados, BorderLayout.CENTER);
        izquierda.setPreferredSize(new java.awt.Dimension(260, 0));

        detalle = new JPanel();
        detalle.setBackground(Color.WHITE);
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));
        detalle.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        JScrollPane scrollDetalle = new JScrollPane(detalle);
        scrollDetalle.setBorder(BorderFactory.createEmptyBorder());
        centro.add(scrollDetalle, BorderLayout.CENTER);

        add(izquierda, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);
    }

    public void refrescar() {
        perfilActual = null;
        modelo.clear();
        detalle.removeAll();
        detalle.add(mensajeVacio("Busca un username para explorar perfiles."));
        detalle.revalidate();
        detalle.repaint();
    }

    // Label estilo "estado vacio", reusado en los mensajes de esta pantalla
    private JLabel mensajeVacio(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.decode("#8A867C"));
        return l;
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) return;
        try {
            ListaEnlazada<String> sigo = cliente.siguiendoDe(user);
            ListaEnlazada<String> personas = cliente.buscarPersonas(texto);
            modelo.clear();
            if (personas.getSize() == 0) {
                detalle.removeAll();
                detalle.add(mensajeVacio("No se encontraron usuarios que contengan \"" + texto + "\""));
                detalle.revalidate();
                detalle.repaint();
            }
            for (int i = 0; i < personas.getSize(); i++) {
                String p = personas.get(i);
                String estado = sigo.contiene(p) ? "Lo sigo" : "No lo sigues";
                modelo.addElement(p + "  —  " + estado);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }

    private void cargarPerfil(String valor) {
        String target = valor.split("—")[0].trim();
        perfilActual = target;
        detalle.removeAll();
        try {
            InstaUser u = cliente.perfil(target);
            if (u == null) {
                detalle.add(mensajeVacio("Usuario no encontrado o cuenta desactivada."));
                detalle.revalidate();
                detalle.repaint();
                return;
            }
            ListaEnlazada<String> sigo = cliente.siguiendoDe(user);
            boolean loSigo = sigo.contiene(target);
            int seguidores = cliente.seguidoresDe(target).getSize();
            int siguiendo = cliente.siguiendoDe(target).getSize();

            JLabel info = new JLabel("<html><body style='font-family:Segoe UI;font-size:13px;'>"
                    + "<h3>@" + UtilInstaUI.escapar(target) + "</h3>"
                    + "<b>Nombre completo:</b> " + UtilInstaUI.escapar(u.getNombre()) + "<br>"
                    + "<b>Género:</b> " + u.getGenero() + "<br>"
                    + "<b>Edad:</b> " + u.getEdad() + "<br>"
                    + "<b>Fecha de ingreso:</b> " + UtilInstaUI.fechaSolo(u.getFechaRegistro()) + "<br>"
                    + "<b>Seguidores:</b> " + seguidores + "<br>"
                    + "<b>Siguiendo:</b> " + siguiendo + "<br>"
                    + "<b>Estado:</b> " + (u.isActivo() ? "Activa" : "Inactiva") + "<br>"
                    + "<b>¿Lo sigo?</b> " + (loSigo ? "Sí" : "No")
                    + "</html>");
            detalle.add(info);

            JPanel botones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
            botones.setBackground(Color.WHITE);
            AccentButton btnSeguir = new AccentButton("Seguir");
            AccentButton btnDejar = new AccentButton("Dejar de seguir");

            btnSeguir.setVisible(!loSigo);
            btnDejar.setVisible(loSigo);

            btnSeguir.addActionListener(e -> {
                try {
                    cliente.seguir(user, target);
                    buscar();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            btnDejar.addActionListener(e -> {
                int op = JOptionPane.showConfirmDialog(this,
                        "¿Dejar de seguir a @" + target + "?", "Dejar de seguir",
                        JOptionPane.YES_NO_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    try {
                        cliente.dejarSeguir(user, target);
                        buscar();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            botones.add(btnSeguir);
            botones.add(btnDejar);
            detalle.add(botones);

            detalle.add(Box.createVerticalStrut(10));
            JLabel tituloPubs = new JLabel("Publicaciones de @" + target + ":");
            tituloPubs.setFont(new Font("Segoe UI", Font.BOLD, 13));
            tituloPubs.setForeground(Color.decode("#2E2C28"));
            tituloPubs.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
            detalle.add(tituloPubs);

            ListaEnlazada<Publicacion> pubs = cliente.publicacionesDe(target);
            if (pubs.getSize() == 0) {
                detalle.add(mensajeVacio("Sin publicaciones."));
            }
            for (int i = 0; i < pubs.getSize(); i++) {
                JPanel p = UtilInstaUI.panelPublicacion(pubs.get(i));
                p.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
                detalle.add(p);
                detalle.add(Box.createVerticalStrut(8));
            }
            detalle.revalidate();
            detalle.repaint();
        } catch (IOException ex) {
            detalle.add(new JLabel("Error de conexión: " + ex.getMessage()));
            detalle.revalidate();
            detalle.repaint();
        }
    }
}