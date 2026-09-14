package mini.os.insta.model;

import java.io.Serializable;

public class StickerDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String user;
    private String rutaArchivo;
    private String nombre;

    public StickerDTO(String user, String rutaArchivo, String nombre){
        this.user = user;
        this.rutaArchivo = rutaArchivo;
        this.nombre = nombre;
    }

    public String getUser() { return user; }
    public String getRutaArchivo() { return rutaArchivo; }
    public String getNombre() { return nombre; }
}