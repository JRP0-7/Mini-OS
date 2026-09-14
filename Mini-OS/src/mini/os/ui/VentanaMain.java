package mini.os.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextFieldUI;

import mini.os.core.GestorUser;
import mini.os.design.AccentButton;
import mini.os.design.GradientPanel;
import mini.os.error.ArchivoCorruptoException;
import mini.os.model.SystemUser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.security.NoSuchAlgorithmException;

// Ventana de inicio donde el usuario pone sus datos para entrar
public class VentanaMain extends JFrame {
    public VentanaMain() {
        setTitle("Mini-Windows");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        Font fuenteTitulo = new Font("Century Gothic", Font.BOLD, 50);
        Font fuenteTexto = new Font("Segoe UI", Font.PLAIN, 14);

        GradientPanel fondo = new GradientPanel("#b9dae2", "#508e63");
        fondo.setLayout(new GridBagLayout());
        setContentPane(fondo);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        fondo.add(contenido);

        JLabel lblBienvenida1 = new JLabel("Inicio de ");
        JLabel lblBienvenida2 = new JLabel("Sesión");
        lblBienvenida1.setFont(fuenteTitulo);
        lblBienvenida2.setFont(fuenteTitulo);

        // Campos para el usuario y la contraseña
        JLabel lblUser = new JLabel("Usuario:");
        JTextField txtUser = new JTextField(20);
        txtUser.setUI(new BasicTextFieldUI());
        txtUser.setOpaque(false);
        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JLabel lblContra = new JLabel("Contraseña:");
        JPasswordField txtContra = new JPasswordField(15);
        txtContra.setUI(new BasicTextFieldUI());
        txtContra.setOpaque(false);
        txtContra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#E4E0D6")),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        // Checkbox para ver la contraseña en texto plano
        JCheckBox verContra = new JCheckBox("Mostrar");
        verContra.setOpaque(false);
        verContra.setForeground(Color.WHITE);
        verContra.addActionListener(e -> {
            if (verContra.isSelected()) {
                txtContra.setEchoChar((char) 0);
            } else {
                txtContra.setEchoChar('•');
            }
        });

        // Botón para validar el login y pasar al Escritorio
        AccentButton login = new AccentButton("Iniciar Sesion");
        login.addActionListener(e -> {
            String usuario = txtUser.getText();
            String contra = new String(txtContra.getPassword());

            try {
                SystemUser su = GestorUser.login(usuario, contra);
                if (su != null) {
                    Escritorio vEscritorio = new Escritorio(su);
                    vEscritorio.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario o Contraseña incorrectos");
                }
            } catch (NoSuchAlgorithmException | ArchivoCorruptoException e1) {
                JOptionPane.showMessageDialog(this, "Error al iniciar sesion: " + e1.getMessage());
            }
        });

        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtUser.getPreferredSize().height));
        txtContra.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtContra.getPreferredSize().height));
        lblBienvenida1.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBienvenida2.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblContra.setAlignmentX(Component.CENTER_ALIGNMENT);
        verContra.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUser.setFont(fuenteTexto);
        lblContra.setFont(fuenteTexto);
        verContra.setFont(fuenteTexto.deriveFont(Font.BOLD));
        login.setFont(fuenteTexto.deriveFont(Font.BOLD));

        contenido.add(Box.createVerticalGlue());
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(lblBienvenida1);
        contenido.add(lblBienvenida2);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(lblUser);
        contenido.add(txtUser);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(lblContra);
        contenido.add(txtContra);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(verContra);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(login);
        contenido.add(Box.createVerticalGlue());
    }
}
