package mini.os.ui;

import javax.swing.*;

import mini.os.core.GestorUser;
import mini.os.model.SystemUser;

import java.awt.FlowLayout;
import java.security.NoSuchAlgorithmException;

// Ventana de inicio donde el usuario pone sus datos para entrar
public class VentanaMain extends JFrame{
    public VentanaMain(){
        setTitle("Mini-Windows");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        // Campos para el usuario y la contraseña
        JLabel lblUser = new JLabel("Usuario:");
        JTextField txtUser= new JTextField(20);

        JLabel lblContra = new JLabel("Contraseña:");
        JPasswordField txtContra= new JPasswordField(15);

        // Checkbox para ver la contraseña en texto plano
        JCheckBox verContra = new JCheckBox("Mostrar");
        verContra.addActionListener(e ->{
            if(verContra.isSelected()){
                txtContra.setEchoChar((char) 0);
            }
            else{
                txtContra.setEchoChar('•');
            }
        });

        
        // Botón para validar el login y pasar al Escritorio
        JButton login = new JButton("Iniciar Sesion");
        login.addActionListener(e ->{
            String usuario = txtUser.getText();
            String contra=new String(txtContra.getPassword());
            
            try {
                SystemUser su = GestorUser.login(usuario, contra);
                if(su !=null){
                    Escritorio vEscritorio = new Escritorio(su);
                    vEscritorio.setVisible(true);
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(this, "Usuario o Contraseña incorrectos");
                }
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
        });

        setLayout(new FlowLayout());
        add(lblUser);
        add(txtUser);
        add(lblContra);
        add(txtContra);
        add(verContra);
        add(login);
    }
}
