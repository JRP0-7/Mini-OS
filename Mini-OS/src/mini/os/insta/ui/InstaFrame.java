package mini.os.insta.ui;

import java.awt.CardLayout;
import java.io.IOException;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import mini.os.insta.core.InstaClient;

public class InstaFrame extends JInternalFrame {

    private InstaClient cliente;
    private CardLayout layout;
    private JPanel contenedor;
    private LoginPanel login;
    private MainPanel principal;

    public InstaFrame() {
        super("INSTA+", true, true, true, true);
        setSize(900, 650);
        setLocation(50,50);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                if (cliente != null) {
                    cliente.cerrar();
                }
            }
        });

        layout = new CardLayout();
        contenedor = new JPanel(layout);
        login = new LoginPanel(this);
        contenedor.add(login, "login");
        setContentPane(contenedor);
        layout.show(contenedor, "login");
    }

    public InstaClient obtenerCliente() throws IOException {
        if (cliente == null) {
            cliente = new InstaClient();
        }
        return cliente;
    }

    public void abrirSesion(InstaClient c, String user) {
        this.cliente = c;
        if (principal != null) {
            contenedor.remove(principal);
        }
        principal = new MainPanel(this, c, user);
        contenedor.add(principal, "principal");
        layout.show(contenedor, "principal");
    }

    public void cerrarSesion() {
        if (cliente != null) {
            cliente.cerrar();
        }
        cliente = null;
        principal = null;
        login.limpiar();
        layout.show(contenedor, "login");
    }
}