package mini.os.insta.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import mini.os.core.Sistema;

public class gestorPublicacion {
    private RandomAccessFile rPub;

    public gestorPublicacion() {
        try {
            File raiz = new File(Sistema.ROOT, "/insta");
            raiz.mkdir();

            rPub = new RandomAccessFile(new File(raiz, "idPublicacion.xr"), "rw");
            iniciar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniciar() throws IOException{
        if(rPub.length()==0){
            rPub.writeInt(1);
        }
    }

    public int getCode() throws IOException{
        rPub.seek(0);
        int xnum = rPub.readInt();
        rPub.seek(0);
        rPub.writeInt(xnum+1);
        return xnum;
    }

    
}
