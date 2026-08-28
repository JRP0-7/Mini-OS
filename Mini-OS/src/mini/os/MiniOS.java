package mini.os;

import mini.os.core.Sistema;
import mini.os.ui.VentanaMain;

public class MiniOS {

    public static void main(String[] args) {
        Sistema.iniciar();

        VentanaMain ventana = new VentanaMain();
        ventana.setVisible(true);


        
    }

}
