package mini.os.insta.model;

import java.io.Serializable;

public class PublicacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String autor;
    private String texto;
    private String rutaImagen;

    public PublicacionDTO(String autor, String texto, String rutaImagen) {
        this.autor = autor;
        this.texto = texto;
        this.rutaImagen = rutaImagen;
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

}
