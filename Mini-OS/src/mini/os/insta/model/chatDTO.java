package mini.os.insta.model;

import java.io.Serializable;

public class chatDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String emisor;
    private String receptor;
    private String msg;
    private boolean sticker=false;
    
    public chatDTO(String emisor, String receptor, String msg) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.msg = msg;
    }

    public chatDTO(String emisor, String receptor, String msg, boolean sticker){
        this(emisor, receptor, msg);
        this.sticker = sticker;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSticker(){
        return sticker;
    }
    
}
