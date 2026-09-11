package mini.os.ui;

import java.awt.BorderLayout;
import java.security.NoSuchAlgorithmException;

import mini.os.model.ListaEnlazada;
import mini.os.core.GestorUser;
import mini.os.error.UsuarioDuplicadoException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
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
    private JCheckBox chkAdmin = new JCheckBox("Admin");

    public UserManager() {
        super("Gestion de Usuarios", true, true, true, true);
        setSize(600, 700);
        JScrollPane mostrador = new JScrollPane(listaUsers);
        add(mostrador, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setSize(400, 400);

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
            } catch (NoSuchAlgorithmException | UsuarioDuplicadoException e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage());
                return;
            }
            recargar();
            limpiar();
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e->{
            String seleccion = listaUsers.getSelectedValue();
            if(seleccion==null){
                return;
            }
            if(seleccion.equals("admin")){
                JOptionPane.showMessageDialog(this, "No se puede borrar al administrador");
                return;
            }

            int res = JOptionPane.showConfirmDialog(this, "Esta seguro de que desea eliminar el usuario?");
            if(res==JOptionPane.YES_OPTION){
                GestorUser.eliminarUsuario(seleccion);
                recargar();
            }
            else{
                return;
            }
        });

        JButton btnRecargar = new JButton("Recargar");
        btnRecargar.addActionListener(e->{recargar();});

        panel.add(txtUser);
        panel.add(txtPass);
        panel.add(chkAdmin);
        panel.add(btnCrear);
        panel.add(btnEliminar);
        panel.add(btnRecargar);

        add(panel, BorderLayout.SOUTH);
        recargar();

    }

    private void recargar() {
        modelo.clear();
        ListaEnlazada<String> l = GestorUser.listarUsuarios();
        for (int i = 0; i < l.getSize(); i++) {
            modelo.addElement(l.get(i));
        }
    }

    private void limpiar() {
        txtUser.setText("");
        txtPass.setText("");
        chkAdmin.setSelected(false);
    }

}
