package mini.os.error;

// Para avisar que el usuario intenta entrar pero su cuenta está desactivada
public class CuentaDesactivadaException extends Exception{
    public CuentaDesactivadaException(String msj){
        super(msj);
    }
}
