package mini.os.insta.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import mini.os.insta.model.MensajesDirectos;
import mini.os.insta.model.Publicacion;
import mini.os.insta.model.PublicacionDTO;
import mini.os.insta.model.Respuesta;
import mini.os.insta.model.SeguirDTO;
import mini.os.insta.model.Solicitud;
import mini.os.insta.model.TipoPeticion;
import mini.os.insta.model.UserDTO;
import mini.os.insta.model.chatDTO;
import mini.os.model.ListaEnlazada;
import mini.os.model.InstaUser;


//SERVIDOR DE PRUEBAS, INGORAR PARA LOGICA Y FUNCIONAMIENTO
public class InstaClient implements Serializable {
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost", InstaServer.PORT);
        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        String users = args.length > 0 ? args[0] : "admin";

        UserDTO dto = new UserDTO(users, "1234");
        Solicitud soli = new Solicitud(TipoPeticion.LOGIN, dto);
        salida.writeObject((soli));
        salida.flush();
        Respuesta resp = (Respuesta) entrada.readObject();
        if (users.equals("alex")) {
            // Paso 1: solicitud normal, respuesta esperada tipo Respuesta
            Solicitud solTL = new Solicitud(TipoPeticion.VER_TIMELINE, null);
            salida.writeObject(solTL);
            salida.flush();
            Object recibido1 = entrada.readObject();
            if (recibido1 instanceof Respuesta) {
                System.out.println("Es Respuesta: " + ((Respuesta) recibido1).getMsg());
            } else {
                System.out.println("Tipo inesperado: " + recibido1);
            }

            // Paso 2: esperar push
            System.out.println("Esperando push...");
            Object recibido2 = entrada.readObject();
            if (recibido2 instanceof RecibirPush) {
                RecibirPush push = (RecibirPush) recibido2;
                System.out.println("Es RecibirPush: " + push.getMensaje().getContenido());
            } else if (recibido2 instanceof Respuesta) {
                System.out.println("Es Respuesta (no push): " + ((Respuesta) recibido2).getMsg());
            } else {
                System.out.println("Tipo inesperado: " + recibido2);
            }
            return;
        }
        System.out.println(resp.getMsg());

        Solicitud sol = new Solicitud(TipoPeticion.PUBLICAR, new PublicacionDTO("admin", "gato", null));
        salida.writeObject(sol);
        salida.flush();
        Respuesta resp1 = (Respuesta) entrada.readObject();

        Solicitud sol2 = new Solicitud(TipoPeticion.VER_TIMELINE, null);
        salida.writeObject(sol2);
        salida.flush();
        Respuesta resp2 = (Respuesta) entrada.readObject();
        ListaEnlazada<Publicacion> lista = (ListaEnlazada<Publicacion>) resp2.getDato();
        System.out.println("Publicaciones:" + lista.getSize());
        for (int i = 0; i < lista.getSize(); i++) {
            System.out.println(lista.get(i).getTexto());
        }
        System.out.println(resp2.getMsg());

        UserDTO dtoN = new UserDTO("alex", "1234", "Alex", 'M', 18, null);
        Solicitud sol3 = new Solicitud(TipoPeticion.REGISTRAR, dtoN);
        salida.writeObject((sol3));
        salida.flush();
        Respuesta rep3 = (Respuesta) entrada.readObject();
        System.out.println(rep3.getMsg());

        SeguirDTO nSeguir = new SeguirDTO("admin", "alex");
        Solicitud soli4 = new Solicitud(TipoPeticion.SEGUIR, nSeguir);
        salida.writeObject(soli4);
        salida.flush();
        Respuesta resp4 = (Respuesta) entrada.readObject();
        System.out.println(resp4.getMsg());

        Solicitud sol5 = new Solicitud(TipoPeticion.VER_PERFIL, "admin");
        salida.writeObject(sol5);
        salida.flush();
        Respuesta rep5 = (Respuesta) entrada.readObject();
        InstaUser perfilAdmin = (InstaUser) rep5.getDato();
        System.out.println("Siguiendo: " + perfilAdmin.getSiguiendo().getSize());

        chatDTO chat = new chatDTO("admin", "alex", "hola");
        Solicitud sol6 = new Solicitud(TipoPeticion.ENVIAR_MENSAJE, chat);
        salida.writeObject(sol6);
        salida.flush();
        Respuesta resp6 = (Respuesta) entrada.readObject();
        System.out.println("Mensaje enviado: " + resp6.getMsg());

        Solicitud sol7 = new Solicitud(TipoPeticion.VER_MENSAJES, "alex");
        salida.writeObject(sol7);
        salida.flush();
        Respuesta resp7 = (Respuesta) entrada.readObject();
        ListaEnlazada<MensajesDirectos> dm = (ListaEnlazada<MensajesDirectos>) resp7.getDato();
        System.out.println("Cantidad de mensajes: " + dm.getSize());
        for (int i = 0; i < dm.getSize(); i++) {
            System.out.println(dm.get(i).getContenido());
        }

    }
}
