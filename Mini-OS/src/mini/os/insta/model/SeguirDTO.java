package mini.os.insta.model;

import java.io.Serializable;

public class SeguirDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String seguidor;
    private String seguido;
    public SeguirDTO(String seguidor, String seguido) {
        this.seguidor = seguidor;
        this.seguido = seguido;
    }
    public String getSeguidor() {
        return seguidor;
    }
    public String getSeguido() {
        return seguido;
    }

    
}
