package mini.os.error;

// Error para cuando un archivo no se puede leer porque está dañado o corrupto
public class ArchivoCorruptoException extends Exception{
    public ArchivoCorruptoException(String msj){
        super(msj);
    }
}