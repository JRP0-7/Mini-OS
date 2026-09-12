package mini.os.insta.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import mini.os.core.Sistema;
import mini.os.model.ListaEnlazada;
import mini.os.io.ArchivoUtil;

public class InstaServer{

    public static final String IROOT = pathRaiz();
    public static final int PORT = 1500;


    public static String pathRaiz(){
        String cd = Sistema.ROOT + "/insta";
        return cd;
    }

    public static void iniciar(){
        File raizInsta = new File(pathRaiz());

        if(!raizInsta.exists()){
            raizInsta.mkdir();
        }

        File datosUsers =  new File(raizInsta, "users.xr");
        if(!datosUsers.exists()){
            ListaEnlazada<String> lista = new ListaEnlazada<>();
            ArchivoUtil.guardar(lista, datosUsers.getPath());
        }
    }
    
    public static void main(String[] args) throws IOException{
        iniciar();
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Servidor INSTA+ escuchando en puerto " + PORT);

        while(true){
            Socket cliente = server.accept();
            ManejoConexion manejo = new ManejoConexion(cliente);
            new Thread(manejo).start();
        }
    }

}