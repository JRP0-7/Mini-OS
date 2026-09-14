package mini.os.insta.model;

import java.io.Serializable;
import java.util.Date;

public class MensajesDirectos implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String emisor;
    private String receptor;
    private String contenido;
    private Date fechaE;
    private boolean leido;
    private String tipo;

    public MensajesDirectos(int id, String em, String rec, String msg, String tipo){
        this.id=id;
        this.emisor=em;
        this.receptor=rec;
        this.contenido=msg;
        this.fechaE= new Date();
        this.leido=false;
        this.tipo=tipo;
    }

    public int getId() {
        return id;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getContenido() {
        return contenido;
    }

    public Date getFechaE() {
        return fechaE;
    }

    public boolean isLeido() {
        return leido;
    }

    public String getTipo() {
        return tipo;
    }

    public void fueLeido(){
        leido=true;
    }

}