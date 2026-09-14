package mini.os.insta.core;

import java.io.File;

import mini.os.insta.model.MensajesDirectos;
import mini.os.io.ArchivoUtil;
import mini.os.model.ListaEnlazada;

public class GestorMD {
    static synchronized void guardar(MensajesDirectos msg) {
        File rMensajes = new File(InstaServer.IROOT + "/mensajes.xr");
        ListaEnlazada<MensajesDirectos> lista;
        if (!rMensajes.exists()) {
            lista = new ListaEnlazada<>();
        } else {
            lista = ArchivoUtil.leer(rMensajes.getPath());
        }
        lista.agregar(msg);
        ArchivoUtil.guardar(lista, rMensajes.getPath());
    }

    static synchronized ListaEnlazada<MensajesDirectos> obtenerChat(String UserA, String UserB) {
        File rChat = new File(InstaServer.IROOT + "/mensajes.xr");
        if (rChat.exists()) {
            ListaEnlazada<MensajesDirectos> lista = ArchivoUtil.leer(rChat.getPath());
            ListaEnlazada<MensajesDirectos> chat = new ListaEnlazada<>();
            for (int i = 0; i < lista.getSize(); i++) {
                if ((lista.get(i).getEmisor().equals(UserA) && lista.get(i).getReceptor().equals(UserB))
                        || (lista.get(i).getEmisor().equals(UserB) && lista.get(i).getReceptor().equals(UserA))) {
                    chat.agregar(lista.get(i));
                }
            }
            return chat;
        }
        return new ListaEnlazada<>();
    }
}
