package mini.os.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class VisorImagenes extends JFrame {
    String[] clasificacion = { "jpg", "png", "jpeg" };
    ArrayList<File> imagenes = new ArrayList<>();
    int indiceActual = 0;

    public VisorImagenes(File raiz) {
        setTitle("Visualizador de Imagenes");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        

        filtrar(raiz);
        if (imagenes.isEmpty()) {
            add(new JLabel("No hay imagenes para mostrar", SwingConstants.CENTER));
            return;
        }
        try {
            JLabel NImagen = new JLabel(imagenes.get(indiceActual).getName(),
                    new ImageIcon(imagenes.get(indiceActual).getCanonicalPath()), SwingConstants.CENTER);
            add(NImagen);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void filtrar(File folder) {
        File[] hijos = folder.listFiles();
        if (hijos != null) {
            for (File hijo : hijos) {
                String ext1 = hijo.getName().substring(hijo.getName().lastIndexOf(".") + 1);
                for (String i : clasificacion) {
                    if (ext1.toLowerCase().equals(i)) {
                        imagenes.add(hijo);
                    }
                }
            }
        }
    }
}
