package mini.os.insta.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import mini.os.design.AccentButton;
import mini.os.design.GradientPanel;
import mini.os.error.CuentaDesactivadaException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.core.InstaClient;
import mini.os.model.InstaUser;

// Panel de inicio de sesión y registro de usuarios de INSTA+.
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
        contenedor.setOpaque(false);

        contenedor.add(panelLogin(), "login");
        contenedor.add(panelCrear(), "crear");

        GradientPanel fondo = new GradientPanel("#054C76", "#0C192A", "#471C3A");
        fondo.setLayout(new BorderLayout());

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new javax.swing.BoxLayout(encabezado, javax.swing.BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("INSTA+", SwingConstants.CENTER);
        titulo.setFont(new Font("Century Gothic", Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JLabel subtitulo = new JLabel("Comparte tu momento", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(Color.decode("#C7CDD3"));
        subtitulo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(contenedor, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(fondo, BorderLayout.CENTER);
        layout.show(contenedor, "login");
    }

    // Le da a un campo el look flat del resto de Mini-Windows: linea inferior, sin caja
    private void estiloCampo(JTextField campo) {
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void estiloLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
    }

    private JPanel panelLogin() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(30, 280, 0, 280));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.weightx = 0;
        gbc.gridx = 0;

        txtUser = new JTextField();
        txtPass = new JPasswordField();
        estiloCampo(txtUser);
        estiloCampo(txtPass);
        char echoDefault = txtPass.getEchoChar();

        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        estiloLabel(lblUser);
        estiloLabel(lblPass);

        gbc.gridy = 0;
        p.add(lblUser, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(txtUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        p.add(lblPass, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        p.add(txtPass, gbc);

        JCheckBox chkVerPass = new JCheckBox("Mostrar contraseña");
        chkVerPass.setOpaque(false);
        chkVerPass.setForeground(Color.WHITE);
        chkVerPass.addActionListener(e ->
                txtPass.setEchoChar(chkVerPass.isSelected() ? (char) 0 : echoDefault));
        gbc.gridx = 1;
        gbc.gridy = 2;
        p.add(chkVerPass, gbc);

        AccentButton btnEntrar = new AccentButton("Log In");
        btnEntrar.addActionListener(e -> intentarLogin());
        AccentButton btnCrear = new AccentButton("Crear cuenta");
        btnCrear.addActionListener(e -> layout.show(contenedor, "crear"));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 4, 6, 4);
        p.add(btnEntrar, gbc);
        gbc.gridx = 1;
        p.add(btnCrear, gbc);
        return p;
    }

    private JPanel panelCrear() {
        JPanel p = new JPanel(new GridLayout(10, 2, 6, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 200, 0, 200));

        txtNombre = new JTextField();
        cmbGenero = new JComboBox<>(new String[]{"M", "F"});
        txtEdad = new JTextField();
        txtUserN = new JTextField();
        txtPassN = new JPasswordField();
        txtPassN2 = new JPasswordField();
        txtFoto = new JTextField();
        txtFoto.setEditable(false);
        estiloCampo(txtNombre);
        estiloCampo(txtEdad);
        estiloCampo(txtUserN);
        estiloCampo(txtPassN);
        estiloCampo(txtPassN2);
        estiloCampo(txtFoto);
        char echoDefaultN = txtPassN.getEchoChar();

        AccentButton btnBuscarFoto = new AccentButton("Elegir foto de perfil");
        btnBuscarFoto.addActionListener(e -> elegirFoto());

        AccentButton btnRegistrar = new AccentButton("Crear cuenta");
        btnRegistrar.addActionListener(e -> crearCuenta());

        AccentButton btnVolver = new AccentButton("Volver al login");
        btnVolver.addActionListener(e -> layout.show(contenedor, "login"));

        JCheckBox chkVerPassN = new JCheckBox("Mostrar contraseñas");
        chkVerPassN.setOpaque(false);
        chkVerPassN.setForeground(Color.WHITE);
        chkVerPassN.addActionListener(e -> {
            char echo = chkVerPassN.isSelected() ? (char) 0 : echoDefaultN;
            txtPassN.setEchoChar(echo);
            txtPassN2.setEchoChar(echo);
        });

        JLabel lblNombre = new JLabel("Nombre completo:");
        JLabel lblGenero = new JLabel("Género (M/F):");
        JLabel lblEdad = new JLabel("Edad:");
        JLabel lblUserN = new JLabel("Username:");
        JLabel lblPassN = new JLabel("Password:");
        JLabel lblPassN2 = new JLabel("Repetir password:");
        estiloLabel(lblNombre);
        estiloLabel(lblGenero);
        estiloLabel(lblEdad);
        estiloLabel(lblUserN);
        estiloLabel(lblPassN);
        estiloLabel(lblPassN2);

        p.add(lblNombre);
        p.add(txtNombre);
        p.add(lblGenero);
        p.add(cmbGenero);
        p.add(lblEdad);
        p.add(txtEdad);
        p.add(lblUserN);
        p.add(txtUserN);
        p.add(lblPassN);
        p.add(txtPassN);
        p.add(lblPassN2);
        p.add(txtPassN2);
        p.add(new JLabel());
        p.add(chkVerPassN);
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
            int op = JOptionPane.showConfirmDialog(this,
                    "Tu cuenta esta desactivada. ¿Deseas reactivarla e iniciar sesión?",
                    "Cuenta desactivada", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                try {
                    InstaClient cliente = frame.obtenerCliente();
                    InstaUser u = cliente.reactivar(user, pass);
                    if (u == null) {
                        JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
                    } else {
                        JOptionPane.showMessageDialog(this, "Cuenta reactivada correctamente");
                        frame.abrirSesion(cliente, u.getUser());
                    }
                } catch (IOException ex2) {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo conectar con el servidor INSTA+. Abre INSTA+ desde el escritorio.");
                }
            }
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