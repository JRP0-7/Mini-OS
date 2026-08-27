package mini.os;

import mini.os.core.GestorUser;
import mini.os.core.Sistema;
import mini.os.error.UsuarioDuplicadoException;

public class MiniOS {

    public static void main(String[] args) {
        Sistema.iniciar();
        try{
            GestorUser.crearUser("Luis", "1234");
        }
        catch(UsuarioDuplicadoException e){
            System.out.println(e.getMessage());
        }
    }

}
