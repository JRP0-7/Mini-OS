package mini.os.ui;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;

import mini.os.console.Consola;
import mini.os.core.Sistema;
import mini.os.model.SystemUser;
import mini.os.ui.VisorImagenes;

// El escritorio principal que ve el usuario después de loguearse
public class Escritorio extends JFrame{
    public Escritorio(SystemUser usuario){
        setTitle("Escritorio -" + usuario.getUser());
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new FlowLayout());
        // Botón para abrir el explorador, chequeando si es admin para darle acceso a todo o solo a su carpeta
        JButton btnExplorador = new JButton("Explorador de Archivos");
        btnExplorador.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser());
            Explorador explorer= new Explorador(FolderUser);
            explorer.setVisible(true);
        });

        add(btnExplorador);
        add(new JButton("Editor de Texto"));

        JButton btnVisualizador = new JButton("Visor de Imagenes");
        btnVisualizador.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser()+ "/Images");
            VisorImagenes view = new VisorImagenes(FolderUser);
            view.setVisible(true);
        });

        add(btnVisualizador);

        JButton btnConsola = new JButton("Consola");
        btnConsola.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser() );
            Consola.abrir(FolderUser);
        });
        add(btnConsola);
        add(new JButton("Reproductor"));
    }
}
