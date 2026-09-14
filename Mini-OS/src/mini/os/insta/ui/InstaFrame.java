package mini.os.insta.ui;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mini.os.insta.core.InstaClient;

public class InstaFrame extends JFrame {

    private InstaClient cliente;
    private CardLayout layout;
    private JPanel contenedor;
    private LoginPanel login;
    private MainPanel principal;

    public InstaFrame() {
        setTitle("INSTA+");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
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