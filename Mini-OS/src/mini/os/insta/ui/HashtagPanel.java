package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mini.os.design.AccentButton;
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
        setBackground(Color.WHITE);

        Font fuenteTexto = new Font("Segoe UI", Font.PLAIN, 13);

        JPanel barra = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));
        barra.setBackground(Color.decode("#4E7C59"));
        barra.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        txtHashtag = new JTextField(18);
        txtHashtag.setFont(fuenteTexto);
        txtHashtag.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        txtHashtag.setOpaque(false);
        txtHashtag.setForeground(Color.WHITE);
        txtHashtag.setCaretColor(Color.WHITE);
        AccentButton btnBuscar = new AccentButton("Buscar publicado");
        btnBuscar.addActionListener(e -> buscar());
        JLabel lblHashtag = new JLabel("Hashtag:");
        lblHashtag.setFont(fuenteTexto);
        lblHashtag.setForeground(Color.WHITE);
        barra.add(lblHashtag);
        barra.add(txtHashtag);
        barra.add(btnBuscar);
        add(barra, BorderLayout.NORTH);

        lista = new JPanel();
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    // Label estilo "estado vacio", reusado en los distintos mensajes de esta pantalla
    private JLabel mensajeVacio(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.decode("#8A867C"));
        return l;
    }

    public void refrescar() {
        lista.removeAll();
        lista.add(mensajeVacio("Escribe un hashtag para buscar publicaciones con #etiqueta."));
        lista.revalidate();
        lista.repaint();
    }

    private void buscar() {
        String texto = txtHashtag.getText().trim();
        lista.removeAll();
        if (texto.isEmpty()) {
            lista.add(mensajeVacio("Escribe un hashtag para buscar publicaciones con #etiqueta."));
            lista.revalidate();
            lista.repaint();
            return;
        }
        try {
            ListaEnlazada<Publicacion> pubs = cliente.buscarHashtag(texto);
            if (pubs.getSize() == 0) {
                lista.add(mensajeVacio("No hay publicaciones que contengan #" + texto));
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