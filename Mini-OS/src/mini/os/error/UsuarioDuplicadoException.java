package mini.os.error;

// Error que salta cuando intentas crear un usuario que ya existe en el sistema
public class UsuarioDuplicadoException extends Exception{
    public UsuarioDuplicadoException(String msj){
        super(msj);
    }
}