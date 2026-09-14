package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import mini.os.insta.core.InstaClient;

public class MainPanel extends JPanel {

    private InstaFrame frame;
    private InstaClient cliente;
    private String user;
    private CardLayout layout;
    private JPanel contenido;
    private JLabel lblNoti;
    private JButton btnInbox;
    private volatile boolean detener;

    private PerfilPanel perfil;
    private PublicarPanel publicar;
    private TimelinePanel timeline;
    private InteraccionesPanel interacciones;
    private BuscarProfilePanel buscar;
    private HashtagPanel hashtag;
    private InboxPanel inbox;
    private EditarPerfilPanel editar;

    public MainPanel(InstaFrame frame, InstaClient c, String user) {
        this.frame = frame;
        this.cliente = c;
        this.user = user;

        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        lblNoti = new JLabel("Mensajes nuevos: 0");
        lblNoti.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        top.add(lblNoti);
        add(top, BorderLayout.NORTH);

        String[] nombres = {"Perfil", "Cargar imagenes", "Comentarios", "Interacciones",
                "Buscar Profile", "Buscar Hashtag", "Inbox", "Editar perfil", "Cerrar sesion"};
        String[] claves = {"perfil", "publicar", "timeline", "interacciones",
                "buscar", "hashtag", "inbox", "editar", "cerrar"};

        JPanel lado = new JPanel(new GridLayout(0, 1, 4, 4));
        lado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < nombres.length; i++) {
            String clave = claves[i];
            String nombre = nombres[i];
            JButton btn = new JButton(nombre);
            if (clave.equals("cerrar")) {
                btn.addActionListener(e -> confirmarCerrarSesion());
            } else {
                btn.addActionListener(e -> mostrar(clave));
            }
            if (clave.equals("inbox")) {
                btnInbox = btn;
            }
            btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            lado.add(btn);
        }
        add(lado, BorderLayout.WEST);

        layout = new CardLayout();
        contenido = new JPanel(layout);

        perfil = new PerfilPanel(c, user);
        publicar = new PublicarPanel(c, user);
        timeline = new TimelinePanel(c, user);
        interacciones = new InteraccionesPanel(c, user);
        buscar = new BuscarProfilePanel(c, user);
        hashtag = new HashtagPanel(c, user);
        inbox = new InboxPanel(c, user);
        editar = new EditarPerfilPanel(c, user);

        contenido.add(perfil, "perfil");
        contenido.add(publicar, "publicar");
        contenido.add(timeline, "timeline");
        contenido.add(interacciones, "interacciones");
        contenido.add(buscar, "buscar");
        contenido.add(hashtag, "hashtag");
        contenido.add(inbox, "inbox");
        contenido.add(editar, "editar");

        add(contenido, BorderLayout.CENTER);
        layout.show(contenido, "perfil");

        iniciarNotificador();
    }

    public void mostrar(String clave) {
        switch (clave) {
            case "perfil": perfil.refrescar(); break;
            case "publicar": publicar.refrescar(); break;
            case "timeline": timeline.refrescar(); break;
            case "interacciones": interacciones.refrescar(); break;
            case "buscar": buscar.refrescar(); break;
            case "hashtag": hashtag.refrescar(); break;
            case "inbox": inbox.refrescar(); break;
            case "editar": editar.refrescar(); break;
            default: break;
        }
        layout.show(contenido, clave);
    }

    private void confirmarCerrarSesion() {
        int op = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas cerrar la sesión?",
                "Cerrar sesion", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            detener = true;
            frame.cerrarSesion();
        }
    }

    private void iniciarNotificador() {
        Thread hilo = new Thread(() -> {
            int ultimo = -1;
            while (!detener) {
                try {
                    Thread.sleep(5000);
                    int n = cliente.noLeidos(user);
                    boolean llego = (ultimo >= 0 && n > ultimo);
                    ultimo = n;
                    SwingUtilities.invokeLater(() -> {
                        lblNoti.setText("Mensajes nuevos: " + n);
                        if (btnInbox != null) {
                            btnInbox.setText(n > 0 ? "Inbox (" + n + " nuevos)" : "Inbox");
                        }
                    });
                    if (llego) {
                        SwingUtilities.invokeLater(() -> lblNoti.setText("Mensajes nuevos: " + n + "  — revisa tu Inbox"));
                    }
                } catch (Exception e) {
                    detener = true;
                }
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }
}