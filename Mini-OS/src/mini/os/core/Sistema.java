package mini.os.core;

import java.io.File;

import mini.os.io.ArchivoUtil;
import mini.os.model.ListaEnlazada;
import mini.os.model.SystemUser;

public class Sistema {
    
    public static String pathOrigen(){
        String cd = System.getProperty("user.dir");
        if(cd.endsWith("Mini-OS")){
            return "Z";
        }
        else{
            return "Mini-OS/Z";
        }
    }
    
    public static final String ROOT = pathOrigen();
    public static void iniciar(){
        File raiz = new File(ROOT);
        if(!raiz.exists()){
            raiz.mkdirs();
        }
        
        File adminF = new File(ROOT + "/admin");
        if(!adminF.exists()){
            adminF.mkdirs();
            new File(adminF, "Documents").mkdirs();
            new File(adminF, "Music").mkdirs();
            new File(adminF, "Images").mkdirs();
            SystemUser su= new SystemUser("admin", "admin", true);
            ArchivoUtil.guardar(su, adminF.getPath() + "/admin.xr");
        }

        File userF= new File(ROOT + "/users.xr");
        if(!userF.exists()){
            ListaEnlazada<String> lista = new ListaEnlazada<>();
            lista.agregar("admin");
            ArchivoUtil.guardar(lista, userF.getPath());
        }
    }
}
