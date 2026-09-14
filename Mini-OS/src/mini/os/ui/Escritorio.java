package mini.os.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javazoom.jlgui.basicplayer.BasicPlayerException;
import mini.os.audio.ReproductorMusica;
import mini.os.console.Consola;
import mini.os.core.Sistema;
import mini.os.design.DockButton;
import mini.os.docs.Editor;
import mini.os.error.ArchivoCorruptoException;
import mini.os.insta.core.InstaServer;
import mini.os.insta.ui.InstaFrame;
import mini.os.model.SystemUser;

// El escritorio principal que ve el usuario después de loguearse
public class Escritorio extends JFrame{
    private JDesktopPane escritorio = new JDesktopPane();
    public Escritorio(SystemUser usuario){
        setTitle("Escritorio -" + usuario.getUser());
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel dock = new JPanel();
        dock.setOpaque(false);


        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(escritorio, BorderLayout.CENTER);
        contenedor.add(dock, BorderLayout.SOUTH);
        setContentPane(contenedor);
        

        // Botón para abrir el explorador, chequeando si es admin para darle acceso a todo o solo a su carpeta
        DockButton btnExplorador = new DockButton("", "icons/explorer.png");
        btnExplorador.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser());
            abrir(new Explorador(FolderUser, escritorio));
        });


        DockButton btnEditor = new DockButton("ET", "icons/editor.png");
        btnEditor.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser() + "/Mis Documentos");
            abrir(Editor.abrir(FolderUser, null));
        });

        DockButton btnVisualizador = new DockButton("VI", "icons/visualizador.png");
        btnVisualizador.addActionListener(e->{
            File FolderUser = new File(Sistema.ROOT + "/" + usuario.getUser()+ "/Mis Imágenes");
            abrir(new VisorImagenes(FolderUser));
        });

        DockButton btnReproductor = new DockButton("RM", "icons/reproductor.png");
        btnReproductor.addActionListener(e->{
            File FolderUser = new File(Sistema.ROOT + "/" + usuario.getUser()+ "/Música");
            try {
                abrir(new ReproductorMusica(FolderUser));
            } catch (IOException | BasicPlayerException e1) {
                e1.printStackTrace();
            }
        });

        DockButton btnConsola = new DockButton("C", "icons/cmd.png");
        btnConsola.addActionListener(e->{
            File FolderUser = usuario.isAdmin() ? new File(Sistema.ROOT) : new File(Sistema.ROOT + "/" + usuario.getUser() );
            abrir(Consola.abrir(FolderUser));
        });

        DockButton btnInsta = new DockButton("I+", "icons/insta+.png");
        btnInsta.addActionListener(e->{
            try{
                InstaServer.iniciar();
                InstaServer.iniciarServidorEnSegundoPlano();
                abrir(new InstaFrame());
            }catch (ArchivoCorruptoException ex){
                JOptionPane.showMessageDialog(this, "No se pudo iniciar Insta+ " + ex.getMessage());
            }
        });

        DockButton btnGestion  = new DockButton("GU", "icons/gestor.png");
        btnGestion.addActionListener(e->{
            abrir(new UserManager());
        });

        DockButton btnCerrarSesion = new DockButton("X", "icons/cerrarsesion.png");
        btnCerrarSesion.addActionListener(e->{
            dispose();
            new VentanaMain().setVisible(true);
        });
    
        dock.add(btnCerrarSesion);
        dock.add(btnExplorador);
        dock.add(btnEditor);
        dock.add(btnVisualizador);
        dock.add(btnReproductor);
        dock.add(btnConsola);
        dock.add(btnInsta);

        if(usuario.isAdmin()){
            dock.add(btnGestion);
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
