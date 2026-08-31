package mini.os.core;

import java.io.File;

// Nodo simple para guardar un archivo y usarlo en el explorador
public class NodoArchivos {
    private File archivo;
    
    // Constructor básico
    public NodoArchivos(File file){
        this.archivo= file;
    }

    public File getArchivo(){
        return archivo;
    }

    @Override
    public String toString(){
        return archivo.getName();
    }
}
