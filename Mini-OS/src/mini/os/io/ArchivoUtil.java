package mini.os.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mini.os.error.ArchivoCorruptoException;

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
    public static <T> T leer(String ruta) throws ArchivoCorruptoException{
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))){
            @SuppressWarnings("unchecked") //supresor de advertencia, revisar despues
            T objeto = (T) ois.readObject();
            return objeto;
        }
        catch(FileNotFoundException e){
            return null;
        }
        catch(IOException | ClassNotFoundException e){
            throw new ArchivoCorruptoException("El archivo " + ruta + " esta dañado o tiene formato invalido " + e.getMessage());
        } 
    }

}
