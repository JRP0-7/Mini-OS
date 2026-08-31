package mini.os.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// Clase con métodos útiles para guardar y leer objetos de archivos
public class ArchivoUtil {
    // Guarda cualquier objeto en un archivo usando serialización
    public static <T> void guardar(T objeto, String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lee un objeto desde un archivo y lo devuelve
    public static <T> T leer(String ruta){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))){
            T objeto = (T) ois.readObject();
            return objeto;
        }
        catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

}
