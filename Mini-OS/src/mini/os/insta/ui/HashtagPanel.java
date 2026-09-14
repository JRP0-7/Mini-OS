package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mini.os.insta.core.InstaClient;
import mini.os.insta.model.Publicacion;
import mini.os.model.ListaEnlazada;

public class HashtagPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private JTextField txtHashtag;
    private JPanel lista;

    public HashtagPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());

        JPanel barra = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        txtHashtag = new JTextField(18);
        JButton btnBuscar = new JButton("Buscar publicado");
        btnBuscar.addActionListener(e -> buscar());
        barra.add(new JLabel("Hashtag:"));
        barra.add(txtHashtag);
        barra.add(btnBuscar);
        add(barra, BorderLayout.NORTH);

        lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        add(new JScrollPane(lista), BorderLayout.CENTER);
    }

    public void refrescar() {
        lista.removeAll();
        lista.add(new JLabel("Escribe un hashtag para buscar publicaciones con #etiqueta."));
        lista.revalidate();
        lista.repaint();
    }

    private void buscar() {
        String texto = txtHashtag.getText().trim();
        lista.removeAll();
        if (texto.isEmpty()) {
            lista.add(new JLabel("Escribe un hashtag para buscar publicaciones con #etiqueta."));
            lista.revalidate();
            lista.repaint();
            return;
        }
        try {
            ListaEnlazada<Publicacion> pubs = cliente.buscarHashtag(texto);
            if (pubs.getSize() == 0) {
                lista.add(new JLabel("No hay publicaciones que contengan #" + texto));
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