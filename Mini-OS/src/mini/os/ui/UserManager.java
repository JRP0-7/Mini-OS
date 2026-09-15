package mini.os.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.security.NoSuchAlgorithmException;

import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;
import mini.os.core.GestorUser;
import mini.os.error.ArchivoCorruptoException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.io.Autentificacion;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

import javax.swing.JTextField;

public class UserManager extends JInternalFrame {
    private DefaultListModel<String> modelo = new DefaultListModel<>();
    private JList<String> listaUsers = new JList<>(modelo);
    private JTextField txtUser = new JTextField(15);
    private JPasswordField txtPass = new JPasswordField(20);
    SystemUser userA;

    private JCheckBox chkAdmin = new JCheckBox("Admin");

    public UserManager() {
        super("Gestion de Usuarios", true, true, true, true);
        setSize(600, 500);
        JScrollPane mostrador = new JScrollPane(listaUsers);
        add(mostrador, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setSize(400, 400);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();



        listaUsers.addListSelectionListener(e -> {
            String seleccion = listaUsers.getSelectedValue();
            if (seleccion == null) {
                return;
            }
            try {
                userA = GestorUser.buscarUsuario(seleccion);
                txtUser.setText(userA.getUser());
                chkAdmin.setSelected(userA.isAdmin());

            } catch (ArchivoCorruptoException e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage());
            }
        });

        JCheckBox verContra = new JCheckBox("Mostrar");
        verContra.addActionListener(e -> {
            if (verContra.isSelected()) {
                txtPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('•');
            }
        });

        JButton btnCrear = new JButton("Crear Nuevo");
        btnCrear.addActionListener(e -> {
            String usuario = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            boolean admin = chkAdmin.isSelected();

            if (usuario.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Error: Campos vacios");
                return;
            }

            try {
                GestorUser.crearUser(usuario, pass, admin);
            } catch (NoSuchAlgorithmException | UsuarioDuplicadoException | ArchivoCorruptoException e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage());
                return;
            }
            recargar();
            limpiar();
        });

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> {
            if (userA == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario primero");
                return;
            }
            try {
                GestorUser.editarUser(userA.getUser(), new String(txtPass.getPassword()), chkAdmin.isSelected());
                recargar();
                limpiar();
            } catch (NoSuchAlgorithmException | ArchivoCorruptoException e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage());
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            String seleccion = listaUsers.getSelectedValue();
            if (seleccion == null) {
                return;
            }
            if (seleccion.equals("admin")) {
                JOptionPane.showMessageDialog(this, "No se puede borrar al administrador");
                return;
            }

            int res = JOptionPane.showConfirmDialog(this, "Esta seguro de que desea eliminar el usuario?");
            if (res == JOptionPane.YES_OPTION) {
                try {
                    GestorUser.eliminarUsuario(seleccion);
                } catch (ArchivoCorruptoException e1) {
                    JOptionPane.showMessageDialog(this, e1.getMessage());
                    return;
                }
                recargar();
            } else {
                return;
            }
        });

        JButton btnRecargar = new JButton("Recargar");
        btnRecargar.addActionListener(e -> {
            recargar();
        });

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx=1;
        gbc.gridy=0;
        gbc.gridwidth=2;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(txtUser, gbc) ;

        gbc.gridx=3;
        gbc.gridy=0;
        gbc.gridwidth=2;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(chkAdmin, gbc);

        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx=1;
        gbc.gridy=1;
        gbc.gridwidth=2;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        JPanel contra = new JPanel();
        contra.setLayout(new FlowLayout());
        contra.add(txtPass);
        contra.add(verContra);
        panel.add(contra, gbc);

        

        gbc.gridx=5;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(btnCrear, gbc);

        gbc.gridx=6;
        gbc.gridy=0;
        panel.add(btnEditar, gbc);

        gbc.gridx=5;
        gbc.gridy=1;
        panel.add(btnEliminar, gbc);

        gbc.gridx=6;
        gbc.gridy=1;
        panel.add(btnRecargar, gbc);

        add(panel, BorderLayout.SOUTH);
        recargar();

    }

    private void recargar() {
        modelo.clear();
        ListaEnlazada<String> l;
        try {
            l = GestorUser.listarUsuarios();
            for (int i = 0; i < l.getSize(); i++) {
                modelo.addElement(l.get(i));
            }
        } catch (ArchivoCorruptoException e) {
            JOptionPane.showMessageDialog(this, "Error en la carga de los usuarios " + e.getMessage());

        }
    }

    private void limpiar() {
        txtUser.setText("");
        txtPass.setText("");
        chkAdmin.setSelected(false);
    }

}
