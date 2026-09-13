package mini.os.insta.core;

import java.io.File;
import java.io.IOException;

import mini.os.insta.model.Publicacion;
import mini.os.io.ArchivoUtil;
import mini.os.model.ListaEnlazada;

public class ManagePublicaciones {
    public static void guardar(Publicacion publi){
        File rPublicaciones = new File(InstaServer.IROOT + "/publicaciones.xr");
        ListaEnlazada<Publicacion> lista;
        if(!rPublicaciones.exists()){
            lista=new ListaEnlazada<>();
        }
        else{
            lista = ArchivoUtil.leer(rPublicaciones.getPath());
        }
        lista.agregar(publi);
        ArchivoUtil.guardar(lista, rPublicaciones.getPath());
    }

    public static ListaEnlazada<Publicacion> obtenerTimeLine(){
        File archivo = new File(InstaServer.IROOT + "/publicaciones.xr");
        if(!archivo.exists()){
            return new ListaEnlazada<>();
        }
        return ArchivoUtil.leer(archivo.getPath());
    }
}
