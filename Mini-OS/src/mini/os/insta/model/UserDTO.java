package mini.os.insta.model;

import java.io.Serializable;

public class UserDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String user;
    private String pass;
    private String nombre;
    private char genero;
    private int edad;
    private String rutaI;

    public UserDTO(){}

    public UserDTO(String usuario, String pass){
        this.user=usuario;
        this.pass=pass;
    }


    public UserDTO(String user, String pass, String nombre, char genero, int edad, String rutaI) {
        this.user = user;
        this.pass = pass;
        this.nombre = nombre;
        this.genero = genero;
        this.edad = edad;
        this.rutaI = rutaI;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getNombre() {
        return nombre;
    }

    public char getGenero() {
        return genero;
    }

    public int getEdad() {
        return edad;
    }

    public String getRutaI() {
        return rutaI;
    }

    
    
}
