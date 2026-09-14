package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

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

        JPanel barra = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        txtBuscar = new JTextField(18);
        JButton btnBuscar = new JButton("Buscar personas");
        btnBuscar.addActionListener(e -> buscar());
        barra.add(new JLabel("Texto:"));
        barra.add(txtBuscar);
        barra.add(btnBuscar);
        add(barra, BorderLayout.NORTH);

        modelo = new DefaultListModel<>();
        resultados = new JList<>(modelo);
        resultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && resultados.getSelectedValue() != null) {
                cargarPerfil(resultados.getSelectedValue());
            }
        });

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.add(new JScrollPane(resultados), BorderLayout.CENTER);
        izquierda.setPreferredSize(new java.awt.Dimension(260, 0));

        detalle = new JPanel();
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));
        detalle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(new JScrollPane(detalle), BorderLayout.CENTER);

        add(izquierda, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);
    }

    public void refrescar() {
        perfilActual = null;
        modelo.clear();
        detalle.removeAll();
        detalle.add(new JLabel("Busca un username para explorar perfiles."));
        detalle.revalidate();
        detalle.repaint();
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
                detalle.add(new JLabel("No se encontraron usuarios que contengan \"" + texto + "\""));
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
                detalle.add(new JLabel("Usuario no encontrado o cuenta desactivada."));
                detalle.revalidate();
                detalle.repaint();
                return;
            }
            ListaEnlazada<String> sigo = cliente.siguiendoDe(user);
            boolean loSigo = sigo.contiene(target);
            int seguidores = cliente.seguidoresDe(target).getSize();
            int siguiendo = cliente.siguiendoDe(target).getSize();

            JLabel info = new JLabel("<html>"
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

            JPanel botones = new JPanel(new java.awt.FlowLayout());
            JButton btnSeguir = new JButton("Seguir");
            JButton btnDejar = new JButton("Dejar de seguir");

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
            tituloPubs.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
            detalle.add(tituloPubs);

            ListaEnlazada<Publicacion> pubs = cliente.publicacionesDe(target);
            if (pubs.getSize() == 0) {
                detalle.add(new JLabel("Sin publicaciones."));
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