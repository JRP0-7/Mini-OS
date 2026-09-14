package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mini.os.insta.core.InstaClient;
import mini.os.insta.model.Publicacion;
import mini.os.model.ListaEnlazada;

public class TimelinePanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private JPanel lista;

    public TimelinePanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());

        JPanel barra = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refrescar());
        barra.add(btnRefrescar);
        add(barra, BorderLayout.NORTH);

        lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        add(new JScrollPane(lista), BorderLayout.CENTER);
    }

    public void refrescar() {
        lista.removeAll();
        try {
            ListaEnlazada<Publicacion> pubs = cliente.timeline(user);
            if (pubs.getSize() == 0) {
                lista.add(new JLabel("Aún no hay publicaciones en tu timeline. ¡Sigue a más personas!"));
            }
            for (int i = 0; i < pubs.getSize(); i++) {
                JPanel p = UtilInstaUI.panelPublicacion(pubs.get(i));
                p.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
                lista.add(p);
                lista.add(Box.createVerticalStrut(8));
            }
        } catch (IOException ex) {
            lista.add(new JLabel("Error de conexión: " + ex.getMessage()));
        }
        lista.revalidate();
        lista.repaint();
    }
}