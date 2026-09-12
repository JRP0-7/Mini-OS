package mini.os.insta.model;

import java.io.Serializable;
import java.util.Date;

import mini.os.model.ListaEnlazada;

public class Publicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String autor;
    String texto;
    String rImagen;
    Date fecha;
    ListaEnlazada<String> meGusta;


    public Publicacion(int id, String autor, String texto, String ruta){
        this.id = id;
        this.autor = autor;
        this.texto=texto;
        this.rImagen=ruta;
        this.fecha = new Date();
        meGusta = new ListaEnlazada<>();
    }

    public void darLike(String user){
        if(meGusta.contiene(user)){
            return;
        }
        meGusta.agregar(user);
    }

    public int getId() {
        return id;
    }

    public String getAutor() {
        return autor;
    }

    public String getTexto() {
        return texto;
    }

    public String getrImagen() {
        return rImagen;
    }

    public Date getFecha() {
        return fecha;
    }

    public ListaEnlazada<String> getMeGusta() {
        return meGusta;
    }

    
    
}
