package mini.os.error;

public class ArchivoCorruptoException extends Exception{
    public ArchivoCorruptoException(String msj){
        super(msj);
    }
}