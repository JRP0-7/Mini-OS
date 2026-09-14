package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import mini.os.insta.model.MensajesDirectos;
import mini.os.model.ListaEnlazada;

public class InboxPanel extends JPanel {

    private InstaClient cliente;
    private String user;
    private DefaultListModel<String> modeloConvs;
    private JList<String> listaConvs;
    private JPanel chatBox;
    private JTextField txtMsj;
    private JPanel panelStickers;
    private String conversacionActual;
    private JLabel lblEstado;

    public InboxPanel(InstaClient c, String user) {
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());

        JPanel barraNorte = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        JButton btnRefrescar = new JButton("Refrescar conversaciones");
        btnRefrescar.addActionListener(e -> refrescar());
        JButton btnEliminar = new JButton("Eliminar conversación");
        btnEliminar.addActionListener(e -> eliminarConversacion());
        barraNorte.add(btnRefrescar);
        barraNorte.add(btnEliminar);
        add(barraNorte, BorderLayout.NORTH);

        modeloConvs = new DefaultListModel<>();
        listaConvs = new JList<>(modeloConvs);
        listaConvs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaConvs.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaConvs.getSelectedValue() != null) {
                abrirConversacion(listaConvs.getSelectedValue().split("\\(")[0].trim());
            }
        });

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.add(new JScrollPane(listaConvs), BorderLayout.CENTER);
        izquierda.setPreferredSize(new java.awt.Dimension(240, 0));
        add(izquierda, BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout());

        chatBox = new JPanel();
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        derecha.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        JPanel entrada = new JPanel(new BorderLayout());
        txtMsj = new JTextField();
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(e -> enviarTexto());

        JButton btnSticker = new JButton("Enviar un sticker");
        btnSticker.addActionListener(e -> panelStickers.setVisible(!panelStickers.isVisible()));

        JPanel entradaSuperior = new JPanel(new BorderLayout());
        panelStickers = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelStickers.setBorder(BorderFactory.createTitledBorder("Mis stickers"));
        panelStickers.setVisible(false);
        entradaSuperior.add(panelStickers, BorderLayout.NORTH);

        JPanel entradaTexto = new JPanel(new BorderLayout());
        entradaTexto.add(txtMsj, BorderLayout.CENTER);
        JPanel btns = new JPanel(new java.awt.FlowLayout());
        btns.add(btnEnviar);
        btns.add(btnSticker);
        entradaTexto.add(btns, BorderLayout.EAST);

        entrada.add(entradaSuperior, BorderLayout.NORTH);
        entrada.add(entradaTexto, BorderLayout.SOUTH);

        derecha.add(entrada, BorderLayout.SOUTH);

        lblEstado = new JLabel(" ");
        derecha.add(lblEstado, BorderLayout.NORTH);

        add(derecha, BorderLayout.CENTER);
    }

    public void refrescar() {
        modeloConvs.clear();
        conversacionActual = null;
        chatBox.removeAll();
        panelStickers.removeAll();
        panelStickers.setVisible(false);
        lblEstado.setText(" ");
        try {
            ListaEnlazada<String> conversaciones = cliente.conversaciones(user);
            for (int i = 0; i < conversaciones.getSize(); i++) {
                modeloConvs.addElement(conversaciones.get(i));
            }
            if (modeloConvs.getSize() > 0) {
                listaConvs.setSelectedIndex(0);
                abrirConversacion(modeloConvs.getElementAt(0).split("\\(")[0].trim());
            } else {
                chatBox.add(new JLabel("Sin conversaciones todavía."));
                chatBox.revalidate();
                chatBox.repaint();
            }
            cargarStickers();
        } catch (IOException ex) {
            chatBox.add(new JLabel("Error de conexión: " + ex.getMessage()));
            chatBox.revalidate();
            chatBox.repaint();
        }
    }

    private void cargarStickers() throws IOException {
        ListaEnlazada<String> stickers = cliente.stickersDisponibles(user);
        for (int i = 0; i < stickers.getSize(); i++) {
            String ruta = stickers.get(i);
            JButton btn = new JButton(UtilInstaUI.cargarImagenCuadrada(ruta, 45));
            btn.setToolTipText(ruta);
            String r = ruta;
            btn.addActionListener(e -> enviarSticker(r));
            panelStickers.add(btn);
        }
        panelStickers.revalidate();
        panelStickers.repaint();
    }

    private void abrirConversacion(String otro) {
        conversacionActual = otro;
        chatBox.removeAll();
        lblEstado.setText("Conversación con @" + otro);
        try {
            ListaEnlazada<MensajesDirectos> mensajes = cliente.mensajesEntre(user, otro);
            for (int i = 0; i < mensajes.getSize(); i++) {
                MensajesDirectos m = mensajes.get(i);
                JLabel lbl;
                String quien = m.getEmisor().equals(user) ? "Yo" : m.getEmisor();
                if ("STICKER".equals(m.getTipo())) {
                    lbl = new JLabel();
                    lbl.setIcon(UtilInstaUI.cargarImagen(m.getContenido(), 110));
                    lbl.setText(quien + " (" + UtilInstaUI.fechaSolo(m.getFechaE()) + ")");
                    lbl.setHorizontalTextPosition(JLabel.CENTER);
                    lbl.setVerticalTextPosition(JLabel.BOTTOM);
                } else {
                    String nuevo = m.isLeido() ? "" : "  [NUEVO]";
                    lbl = new JLabel("<html><b>" + quien + "</b> (" + UtilInstaUI.fechaSolo(m.getFechaE())
                            + "): " + UtilInstaUI.escapar(m.getContenido()) + nuevo + "</html>");
                }
                lbl.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
                lbl.setOpaque(true);
                lbl.setBackground(m.getEmisor().equals(user) ? new Color(224, 224, 224) : new Color(217, 236, 255));
                lbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);
                chatBox.add(lbl);
                chatBox.add(Box.createVerticalStrut(4));
            }
            cliente.marcarLeidos(user, otro);
        } catch (IOException ex) {
            chatBox.add(new JLabel("Error de conexión: " + ex.getMessage()));
        }
        chatBox.revalidate();
        chatBox.repaint();
    }

    private void enviarTexto() {
        String texto = txtMsj.getText().trim();
        if (texto.isEmpty() || conversacionActual == null) return;
        try {
            cliente.enviarMensaje(user, conversacionActual, texto, "TEXTO");
            txtMsj.setText("");
            abrirConversacion(conversacionActual);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }

    private void enviarSticker(String ruta) {
        if (conversacionActual == null) return;
        try {
            cliente.enviarMensaje(user, conversacionActual, ruta, "STICKER");
            abrirConversacion(conversacionActual);
            refrescar();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }

    private void eliminarConversacion() {
        if (conversacionActual == null) return;
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la conversación con @" + conversacionActual + "?",
                "Eliminar conversación", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            try {
                cliente.eliminarConversacion(user, conversacionActual);
                refrescar();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
            }
        }
    }
}