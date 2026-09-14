package mini.os.insta.core;

import java.io.Serializable;
import mini.os.insta.model.MensajesDirectos;

public class RecibirPush implements Serializable{
    private static final long serialVersionUID = 1L;
    MensajesDirectos mensaje;
    public RecibirPush(MensajesDirectos msg){
        this.mensaje= msg;
    }

    public MensajesDirectos getMensaje(){
        return mensaje;
    }
}
