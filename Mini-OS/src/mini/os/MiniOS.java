package mini.os;

import javax.swing.UIManager;

import mini.os.core.Sistema;
import mini.os.ui.VentanaMain;

public class MiniOS {

    public static void main(String[] args) {
        Sistema.iniciar();
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        VentanaMain ventana = new VentanaMain();
        ventana.setVisible(true);

    }

}
