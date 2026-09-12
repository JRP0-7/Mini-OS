package mini.os.insta.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import mini.os.core.Sistema;

public class gestorMensajes {
    private RandomAccessFile rMen;
    
    public gestorMensajes(){
        try {
            File raiz = new File(Sistema.ROOT, "/insta");
            raiz.mkdir();

            rMen = new RandomAccessFile(new File(raiz, "idMensajes.xr"), "rw");
            iniciar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniciar() throws IOException{
        if(rMen.length()==0){
            rMen.writeInt(1);
        }
    }

    public int getCode() throws IOException{
        rMen.seek(0);
        int xnum = rMen.readInt();
        rMen.seek(0);
        rMen.writeInt(xnum+1);
        return xnum;
    }

}
