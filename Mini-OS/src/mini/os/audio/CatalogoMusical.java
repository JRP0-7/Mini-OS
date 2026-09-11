package mini.os.audio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class CatalogoMusical {
    int LNombre = 150;
    int LDescripcion =200;
    int LRuta = 100;
    int tamañoR =(LNombre + LDescripcion + LRuta)*2;
    private RandomAccessFile mReg;

    public CatalogoMusical(File carpeta){
        try{
            
            mReg = new RandomAccessFile(carpeta.getCanonicalPath()+ "/catalogo.msc", "rw"); 
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void guardar(int indice, String nombre, String descripcion, String RImagen) throws IOException{
        mReg.seek(indice*tamañoR);

        mReg.writeChars(ajustarTamaño(nombre, LNombre));
        mReg.writeChars(ajustarTamaño(descripcion, LDescripcion));
        mReg.writeChars(ajustarTamaño(RImagen, LRuta));
    }

    public InfoCancion leer(int indice) throws IOException{
        String nombre = "";
        String descripcion ="";
        String rimagen="";

        mReg.seek(indice*tamañoR);
        for (int i = 0; i < LNombre; i++) {
            nombre +=  mReg.readChar();
        }
        
        for (int i = 0; i < LDescripcion; i++) {
            descripcion +=  mReg.readChar();
        }
        
        for (int i = 0; i < LRuta; i++) {
            rimagen +=  mReg.readChar();
        }
        InfoCancion info = new InfoCancion(nombre.trim(), descripcion.trim(), rimagen.trim());
        return info;
    }

    public boolean existeReg(int indice) throws IOException{
        long pos = (indice+1) * tamañoR;
        return mReg.length()>=pos;

    }

    public String ajustarTamaño(String texto, int largo) {
        if (texto.length() == largo) {
            return texto;
        }
        if (texto.length() < largo) {
            return ajustarTamaño(texto + " ", largo);
        }
        if (texto.length() > largo) {
            return texto.substring(0, largo);
        }
        return null;
    }

    public InfoCancion buscarNombre(String nombre) throws IOException{
        long tamaño = mReg.length()/tamañoR;
        for (int i = 0; i < tamaño; i++) {
            InfoCancion valor = leer(i);
            if(valor.getNombre().equals(nombre)){
                return valor;   
            }
        }
        return null;
    }

    public int tRegistros() throws IOException{
        return (int) (mReg.length()/tamañoR);
    }


    
}
