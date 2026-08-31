package mini.os.model;

import java.util.Date;

// Usuario con perfil tipo "Instagram" con más datos personales y seguidores
public class InstaUser extends Users{
    private String nombre;
    private char genero;
    private int edad;
    private Date fechaRegistro;
    private String rutaI;
    private boolean activo;
    private ListaEnlazada<String> seguidores;
    private ListaEnlazada<String> siguiendo;

    public InstaUser(String user, String pass, String nombre, char genero, int age, Date fechaR, String rutaI, boolean activo){
        super(user, pass);
        this.nombre=nombre;
        this.genero=genero;
        this.edad=age;
        this.fechaRegistro=fechaR;
        this.rutaI=rutaI;
        this.activo=activo;
        this.seguidores=new ListaEnlazada<>();
        this.siguiendo=new ListaEnlazada<>();
    }

    public String getNombre(){
        return nombre;
    }

    public char getGenero() {
        return genero;
    }

    public int getEdad() {
        return edad;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public String getRutaI() {
        return rutaI;
    }

    public boolean isActivo() {
        return activo;
    }

    public ListaEnlazada<String> getSeguidores() {
        return seguidores;
    }

    public ListaEnlazada<String> getSiguiendo() {
        return siguiendo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
