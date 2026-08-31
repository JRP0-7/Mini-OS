package mini.os;

import mini.os.core.GestorUser;
import mini.os.core.Sistema;
import mini.os.ui.VentanaMain;

public class MiniOS {

    public static void main(String[] args) {
        Sistema.iniciar();

        try {
            GestorUser.crearUser("alex", "1234");
        } catch (Exception e) {
            e.printStackTrace();
        }

        VentanaMain ventana = new VentanaMain();
        ventana.setVisible(true);

    }

}
