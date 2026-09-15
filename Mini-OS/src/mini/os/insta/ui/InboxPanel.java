package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import mini.os.design.AccentButton;
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
        setBackground(Color.WHITE);
        Font fuenteTexto = new Font("Segoe UI", Font.PLAIN, 13);

        JPanel barraNorte = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        barraNorte.setBackground(Color.WHITE);
        barraNorte.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        AccentButton btnRefrescar = new AccentButton("Refrescar conversaciones");
        btnRefrescar.addActionListener(e -> refrescar());
        AccentButton btnEliminar = new AccentButton("Eliminar conversación");
        btnEliminar.addActionListener(e -> eliminarConversacion());

        JTextField txtNuevo = new JTextField(12);
        txtNuevo.setFont(fuenteTexto);
        txtNuevo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        AccentButton btnNuevo = new AccentButton("Nueva conversación");
        btnNuevo.addActionListener(e -> {
            String destino = txtNuevo.getText().trim();
            if (destino.isEmpty()) return;
            iniciarConversacion(destino);
            txtNuevo.setText("");
        });

        barraNorte.add(btnRefrescar);
        barraNorte.add(btnEliminar);
        barraNorte.add(txtNuevo);
        barraNorte.add(btnNuevo);
        add(barraNorte, BorderLayout.NORTH);

        modeloConvs = new DefaultListModel<>();
        listaConvs = new JList<>(modeloConvs);
        listaConvs.setFont(fuenteTexto);
        listaConvs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaConvs.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaConvs.getSelectedValue() != null) {
                abrirConversacion(listaConvs.getSelectedValue().split("\\(")[0].trim());
            }
        });

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBackground(Color.WHITE);
        JScrollPane scrollConvs = new JScrollPane(listaConvs);
        scrollConvs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#E4E0D6")));
        izquierda.add(scrollConvs, BorderLayout.CENTER);
        izquierda.setPreferredSize(new java.awt.Dimension(240, 0));
        add(izquierda, BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBackground(Color.WHITE);

        chatBox = new JPanel();
        chatBox.setBackground(Color.WHITE);
        chatBox.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        JScrollPane scrollChat = new JScrollPane(chatBox);
        scrollChat.setBorder(BorderFactory.createEmptyBorder());
        derecha.add(scrollChat, BorderLayout.CENTER);

        JPanel entrada = new JPanel(new BorderLayout());
        entrada.setBackground(Color.WHITE);
        txtMsj = new JTextField();
        txtMsj.setFont(fuenteTexto);
        txtMsj.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        AccentButton btnEnviar = new AccentButton("Enviar");
        btnEnviar.addActionListener(e -> enviarTexto());

        AccentButton btnSticker = new AccentButton("Enviar un sticker");
        btnSticker.addActionListener(e -> panelStickers.setVisible(!panelStickers.isVisible()));

        AccentButton btnAgregarSticker = new AccentButton("Agregar sticker");
        btnAgregarSticker.addActionListener(e -> agregarSticker());

        JPanel entradaSuperior = new JPanel(new BorderLayout());
        entradaSuperior.setBackground(Color.WHITE);
        panelStickers = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelStickers.setBackground(Color.WHITE);
        panelStickers.setBorder(BorderFactory.createTitledBorder("Mis stickers"));
        panelStickers.setVisible(false);
        entradaSuperior.add(panelStickers, BorderLayout.NORTH);

        JPanel entradaTexto = new JPanel(new BorderLayout());
        entradaTexto.setBackground(Color.WHITE);
        entradaTexto.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        entradaTexto.add(txtMsj, BorderLayout.CENTER);
        JPanel btns = new JPanel(new java.awt.FlowLayout());
        btns.setBackground(Color.WHITE);
        btns.add(btnEnviar);
        btns.add(btnSticker);
        btns.add(btnAgregarSticker);
        entradaTexto.add(btns, BorderLayout.EAST);

        entrada.add(entradaSuperior, BorderLayout.NORTH);
        entrada.add(entradaTexto, BorderLayout.SOUTH);

        derecha.add(entrada, BorderLayout.SOUTH);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstado.setForeground(Color.decode("#2E2C28"));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
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
                chatBox.add(mensajeVacio("Sin conversaciones todavía."));
                chatBox.revalidate();
                chatBox.repaint();
            }
            cargarStickers();
        } catch (IOException ex) {
            chatBox.add(mensajeVacio("Error de conexión: " + ex.getMessage()));
            chatBox.revalidate();
            chatBox.repaint();
        }
    }

    // Label estilo "estado vacio", reusado en los mensajes de esta pantalla
    private JLabel mensajeVacio(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.decode("#8A867C"));
        return l;
    }

    private void cargarStickers() throws IOException {
        panelStickers.removeAll();
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

    // Convierte una imagen del disco en un sticker nuevo disponible para enviar
    private void agregarSticker() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        String nombre = JOptionPane.showInputDialog(this,
                "Nombre para el sticker (opcional, sin extension):", f.getName().replaceFirst("\\.[^.]+$", ""));
        if (nombre == null) return;
        try {
            String ruta = cliente.importarSticker(user, f.getAbsolutePath(), nombre);
            if (ruta == null) {
                JOptionPane.showMessageDialog(this, "No se pudo importar el sticker");
                return;
            }
            JOptionPane.showMessageDialog(this, "Sticker agregado correctamente");
            cargarStickers();
            panelStickers.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
        }
    }

    // Arranca una conversacion con alguien a quien todavia no le escribiste
    private void iniciarConversacion(String destino) {
        if (destino.equals(user)) {
            JOptionPane.showMessageDialog(this, "No podes enviarte mensajes a vos mismo");
            return;
        }
        try {
            if (cliente.perfil(destino) == null) {
                JOptionPane.showMessageDialog(this, "El usuario @" + destino + " no existe");
                return;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage());
            return;
        }
        abrirConversacion(destino);
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
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode("#E4E0D6")),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                lbl.setOpaque(true);
                lbl.setBackground(m.getEmisor().equals(user) ? Color.decode("#EFEBE2") : Color.decode("#E7EEE9"));
                lbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);
                chatBox.add(lbl);
                chatBox.add(Box.createVerticalStrut(4));
            }
            cliente.marcarLeidos(user, otro);
        } catch (IOException ex) {
            chatBox.add(mensajeVacio("Error de conexión: " + ex.getMessage()));
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