package mini.os.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ArchivoUtil {
    public static <T> void guardar(T objeto, String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
