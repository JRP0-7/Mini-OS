package mini.os.insta.model;

import java.io.Serializable;

public class Solicitud implements Serializable {

    private static final long serialVersionUID = 1L;
    private TipoPeticion tipo;
    private Object dato;

    public Solicitud(TipoPeticion type, Object obj) {
        this.tipo = type;
        this.dato = obj;
    }

    public TipoPeticion getTipo() {
        return tipo;
    }

    public Object getDato() {
        return dato;
    }

}
