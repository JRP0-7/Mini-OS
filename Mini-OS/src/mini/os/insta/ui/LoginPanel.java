package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import mini.os.error.CuentaDesactivadaException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.core.InstaClient;
import mini.os.model.InstaUser;

public class LoginPanel extends JPanel {

    private InstaFrame frame;
    private CardLayout layout;
    private JPanel contenedor;

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JTextField txtNombre;
    private JComboBox<String> cmbGenero;
    private JTextField txtEdad;
    private JTextField txtUserN;
    private JPasswordField txtPassN;
    private JPasswordField txtPassN2;
    private JTextField txtFoto;
    private String rutaFoto;

    public LoginPanel(InstaFrame frame) {
        this.frame = frame;
        this.layout = new CardLayout();
        this.contenedor = new JPanel(layout);

        contenedor.add(panelLogin(), "login");
        contenedor.add(panelCrear(), "crear");

        setLayout(new BorderLayout());
        add(contenedor, BorderLayout.CENTER);
        layout.show(contenedor, "login");
    }

    private JPanel panelLogin() {
        JPanel p = new JPanel(new GridLayout(3, 2, 6, 6));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(120, 320, 0, 320));
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        p.add(new JLabel("Username:"));
        p.add(txtUser);
        p.add(new JLabel("Password:"));
        p.add(txtPass);

        JButton btnEntrar = new JButton("Log In");
        btnEntrar.addActionListener(e -> intentarLogin());
        JButton btnCrear = new JButton("Crear cuenta");
        btnCrear.addActionListener(e -> layout.show(contenedor, "crear"));
        p.add(btnEntrar);
        p.add(btnCrear);
        return p;
    }

    private JPanel panelCrear() {
        JPanel p = new JPanel(new GridLayout(9, 2, 6, 6));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(60, 260, 0, 260));

        txtNombre = new JTextField();
        cmbGenero = new JComboBox<>(new String[]{"M", "F"});
        txtEdad = new JTextField();
        txtUserN = new JTextField();
        txtPassN = new JPasswordField();
        txtPassN2 = new JPasswordField();
        txtFoto = new JTextField();
        txtFoto.setEditable(false);

        JButton btnBuscarFoto = new JButton("Elegir foto de perfil");
        btnBuscarFoto.addActionListener(e -> elegirFoto());

        JButton btnRegistrar = new JButton("Crear cuenta");
        btnRegistrar.addActionListener(e -> crearCuenta());

        JButton btnVolver = new JButton("Volver al login");
        btnVolver.addActionListener(e -> layout.show(contenedor, "login"));

        p.add(new JLabel("Nombre completo:"));
        p.add(txtNombre);
        p.add(new JLabel("Género (M/F):"));
        p.add(cmbGenero);
        p.add(new JLabel("Edad:"));
        p.add(txtEdad);
        p.add(new JLabel("Username:"));
        p.add(txtUserN);
        p.add(new JLabel("Password:"));
        p.add(txtPassN);
        p.add(new JLabel("Repetir password:"));
        p.add(txtPassN2);
        p.add(btnBuscarFoto);
        p.add(txtFoto);
        p.add(btnVolver);
        p.add(btnRegistrar);
        return p;
    }

    private void intentarLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa tu usuario y contraseña");
            return;
        }
        try {
            InstaClient cliente = frame.obtenerCliente();
            InstaUser u = cliente.login(user, pass);
            if (u == null) {
                int op = JOptionPane.showConfirmDialog(this,
                        "Usuario o contraseña incorrectos. ¿Deseas crear una cuenta nueva?",
                        "Error de acceso", JOptionPane.YES_NO_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    layout.show(contenedor, "crear");
                }
            } else {
                frame.abrirSesion(cliente, u.getUser());
            }
        } catch (CuentaDesactivadaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con el servidor INSTA+. Abre INSTA+ desde el escritorio.");
        }
    }

    private void crearCuenta() {
        String nombre = txtNombre.getText().trim();
        String user = txtUserN.getText().trim();
        String pass = new String(txtPassN.getPassword());
        String pass2 = new String(txtPassN2.getPassword());
        char genero = cmbGenero.getSelectedIndex() == 0 ? 'M' : 'F';
        int edad = 0;

        if (nombre.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios");
            return;
        }
        if (!pass.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
            return;
        }
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero");
            return;
        }
        if (edad < 1) {
            JOptionPane.showMessageDialog(this, "Ingresa una edad válida");
            return;
        }

        InstaClient cliente;
        try {
            cliente = frame.obtenerCliente();
            InstaUser nuevo = cliente.registrar(user, pass, nombre, genero, edad, rutaFoto);
            if (nuevo == null) {
                JOptionPane.showMessageDialog(this, "No se pudo crear la cuenta");
                return;
            }
            JOptionPane.showMessageDialog(this, "Cuenta creada correctamente. Bienvenido/a, " + user);
            frame.abrirSesion(cliente, nuevo.getUser());
        } catch (UsuarioDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor INSTA+.");
        }
    }

    private void elegirFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            rutaFoto = f.getAbsolutePath();
            txtFoto.setText(f.getName());
        }
    }

    public void limpiar() {
        txtUser.setText("");
        txtPass.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtUserN.setText("");
        txtPassN.setText("");
        txtPassN2.setText("");
        txtFoto.setText("");
        rutaFoto = null;
        layout.show(contenedor, "login");
    }
}