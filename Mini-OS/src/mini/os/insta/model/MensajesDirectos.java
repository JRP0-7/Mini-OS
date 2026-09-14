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
    private boolean sticker=false;

    public MensajesDirectos(int id, String em, String rec, String msg){
        this.id=id;
        this.emisor=em;
        this.receptor=rec;
        this.contenido=msg;
        this.fechaE= new Date();
        this.leido=false;
    }

    

    public MensajesDirectos(int id, String emisor, String receptor, String contenido, boolean sticker) {
        this(id, emisor, receptor, contenido);
        this.sticker = sticker;
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

    public void fueLeido(){
        leido=true;
    }



    public boolean isSticker() {
        return sticker;
    }

    
    
}
