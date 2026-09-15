package mini.os.core;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import mini.os.io.ArchivoUtil;
import mini.os.io.Autentificacion;
import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;

// Configuración general del sistema y la carpeta raíz
public class Sistema {
    
    // Mira dónde estamos ejecutando el programa para poner la ruta bien
    public static String pathOrigen(){
        String cd = System.getProperty("user.dir");
        if(cd.endsWith("Mini-OS")){
            return "Z";
        }
        else{
            return "Mini-OS/Z";
        }
    }
    
    // La carpeta raíz donde se guarda todo
    public static final String ROOT = pathOrigen();

    // Crea las carpetas básicas y el admin si es la primera vez que corre
    public static void iniciar(){
        File raiz = new File(ROOT);
        if(!raiz.exists()){
            raiz.mkdirs();
        }
        
        File adminF = new File(ROOT + "/admin");
        File adminXr = new File(adminF, "admin.xr");
        if(!adminF.exists() || !adminXr.exists()){
            adminF.mkdirs();
            new File(adminF, "Mis Documentos").mkdirs();
            new File(adminF, "Música").mkdirs();
            new File(adminF, "Mis Imágenes/Portadas").mkdirs();
            SystemUser su=null;
            try {
                su = new SystemUser("admin", Autentificacion.hash("admin"), true);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if (su != null) {
                ArchivoUtil.guardar(su, adminXr.getPath());
            }
        }

        File userF= new File(ROOT + "/users.xr");
        if(!userF.exists()){
            ListaEnlazada<String> lista = new ListaEnlazada<>();
            lista.agregar("admin");
            ArchivoUtil.guardar(lista, userF.getPath());
        }
    }
}
