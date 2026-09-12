package mini.os.ui;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javazoom.jlgui.basicplayer.BasicPlayerException;
import mini.os.audio.ReproductorMusica;
import mini.os.console.Consola;
import mini.os.core.Sistema;
import mini.os.docs.Editor;
import mini.os.model.SystemUser;

// El escritorio principal que ve el usuario después de loguearse
public class Escritorio extends JFrame{
    private JDesktopPane escritorio = new JDesktopPane();
    public Escritorio(SystemUser usuario){
        setTitle("Escritorio -" + usuario.getUser());
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(escritorio);
        JMenuBar barraTareas = new JMenuBar();
        setJMenuBar(barraTareas);

        // Botón para abrir el explorador, chequeando si es admin para darle acceso a todo o solo a su carpeta
        JMenuItem btnExplorador = new JMenuItem("Explorador de Archivos");
        btnExplorador.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser());
            abrir(new Explorador(FolderUser, escritorio));
        });


        JMenuItem btnEditor = new JMenuItem("Editor de Texto");
        btnEditor.addActionListener(e->{
            File FolderUser = new File(Sistema.ROOT + "/" + usuario.getUser()+ "/Documents");
            abrir(Editor.abrir(FolderUser, null));
        });

        JMenuItem btnVisualizador = new JMenuItem("Visor de Imagenes");
        btnVisualizador.addActionListener(e->{
            File FolderUser = new File(Sistema.ROOT + "/" + usuario.getUser()+ "/Images");
            abrir(new VisorImagenes(FolderUser));
        });

        JMenuItem btnReproductor = new JMenuItem("Reproductor Musical");
        btnReproductor.addActionListener(e->{
            File FolderUser = new File(Sistema.ROOT + "/" + usuario.getUser()+ "/Music");
            try {
                abrir(new ReproductorMusica(FolderUser));
            } catch (IOException | BasicPlayerException e1) {
                e1.printStackTrace();
            }
        });

        JMenuItem btnConsola = new JMenuItem("Consola");
        btnConsola.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser() );
            abrir(Consola.abrir(FolderUser));
        });

        JMenuItem btnGestion  = new JMenuItem("Gestion de Usuarios");
        btnGestion.addActionListener(e->{
            abrir(new UserManager());
        });

        JMenuItem btnCerrarSesion = new JMenuItem("Cerrar Sesion");
        btnCerrarSesion.addActionListener(e->{
            dispose();
            new VentanaMain().setVisible(true);
        });
    
        barraTareas.add(btnCerrarSesion);
        barraTareas.add(btnExplorador);
        barraTareas.add(btnEditor);
        barraTareas.add(btnVisualizador);
        barraTareas.add(btnReproductor);
        barraTareas.add(btnConsola);

        if(usuario.isAdmin()){
            barraTareas.add(btnGestion);
        }

    
    }

    private void abrir(JInternalFrame jf){
        escritorio.add(jf);
        jf.setVisible(true);
        try{
            jf.setSelected(true);
        } catch (PropertyVetoException e){

        }
    }

    public JDesktopPane getEscritorio(){
        return escritorio;
    }
}
