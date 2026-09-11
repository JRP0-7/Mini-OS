
package mini.os.docs;

import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oscar
 */
public class Editor {

    public static EditorTexto abrir(File carpeta, File seleccion) {
        EditorTexto editor = new EditorTexto(carpeta);
        editor.abrirDirecto(seleccion);
        return editor;
    }


}
