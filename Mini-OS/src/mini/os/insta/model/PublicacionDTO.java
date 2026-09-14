package mini.os.insta.model;

import java.io.Serializable;

public class PublicacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String autor;
    private String texto;
    private String rutaImagen;
    private String carpeta;

    public PublicacionDTO(String autor, String texto, String rutaImagen) {
        this(autor, texto, rutaImagen, null);
    }

    public PublicacionDTO(String autor, String texto, String rutaImagen, String carpeta) {
        this.autor = autor;
        this.texto = texto;
        this.rutaImagen = rutaImagen;
        this.carpeta = carpeta;
    }

    public String getAutor() {
        return autor;
    }

    public String getTexto() {
        return texto;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public String getCarpeta() {
        return carpeta;
    }

}