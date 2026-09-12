package mini.os.insta.model;

import java.io.Serializable;

public class Respuesta implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean connected;
    private String msg;
    private Object dato;

    public Respuesta(boolean exito, String mensaje, Object obj){
        this.connected =exito;
        this.msg = mensaje;
        this.dato = obj;
    }

    public boolean isConnected(){
        return connected;
    }

    public String getMsg(){
        return msg;
    }

    public Object getDato(){
        return dato;
    }
}
