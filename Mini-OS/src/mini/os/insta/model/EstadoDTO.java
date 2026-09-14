package mini.os.insta.model;

import java.io.Serializable;

public class EstadoDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String user;
    private boolean activo;

    public EstadoDTO(String user, boolean activo){
        this.user = user;
        this.activo = activo;
    }

    public String getUser() { return user; }
    public boolean isActivo() { return activo; }
}