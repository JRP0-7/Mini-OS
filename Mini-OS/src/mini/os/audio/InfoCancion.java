package mini.os.audio;

public class InfoCancion {
    private String nombre;
    private String descripcion;
    private String rutaImagen;

    public InfoCancion(String nombre, String descripcion, String ruta){
        this.nombre = nombre;
        this.descripcion=descripcion;
        this.rutaImagen = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    
}
