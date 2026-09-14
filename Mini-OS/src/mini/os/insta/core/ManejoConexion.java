package mini.os.insta.core;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import mini.os.insta.model.Solicitud;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.model.MensajesDirectos;
import mini.os.insta.model.Publicacion;
import mini.os.insta.model.PublicacionDTO;
import mini.os.insta.model.Respuesta;
import mini.os.insta.model.SeguirDTO;
import mini.os.insta.model.UserDTO;
import mini.os.insta.model.chatDTO;
import mini.os.insta.model.gestorMensajes;
import mini.os.io.ArchivoUtil;
import mini.os.model.InstaUser;
import mini.os.model.ListaEnlazada;

public class ManejoConexion implements Runnable {
    private Socket socket;
    private String userActual;

    public ManejoConexion(Socket cliente) {
        this.socket = cliente;
    }

    ObjectOutputStream salida = null;
    ObjectInputStream entrada;
    public void run() {


        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Solicitud soli = (Solicitud) entrada.readObject();
                switch (soli.getTipo()) {
                    case LOGIN:
                        UserDTO dto = (UserDTO) soli.getDato();
                        try {
                            if (GestorInstaUser.login(dto.getUser(), dto.getPass()) != null) {
                                userActual = dto.getUser();
                                InstaServer.conexionesActivas.put(userActual, this);
                                salida.writeObject(new Respuesta(true, "Login exitoso", null));
                            } else {
                                salida.writeObject(new Respuesta(false, "Usuario o contraseña incorrectos", null));
                            }
                        } catch (NoSuchAlgorithmException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    case REGISTRAR:
                        UserDTO dto2 = (UserDTO) soli.getDato();
                        try {
                            GestorInstaUser.registrar(dto2.getUser(), dto2.getPass(), dto2.getNombre(),
                                    dto2.getGenero(),
                                    dto2.getEdad(), dto2.getRutaI());
                            salida.writeObject(new Respuesta(true, "Registro exitoso", null));
                        } catch (UsuarioDuplicadoException | NoSuchAlgorithmException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    case PUBLICAR:
                        try {
                            PublicacionDTO pDTO = (PublicacionDTO) soli.getDato();

                            int id = InstaServer.getGestorPub().getCode();
                            Publicacion nueva = new Publicacion(id, pDTO.getAutor(), pDTO.getTexto(),
                                    pDTO.getRutaImagen());
                            ManagePublicaciones.guardar(nueva);
                            salida.writeObject(new Respuesta(true, "Publicacion creada", null));
                        } catch (IOException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    case VER_TIMELINE:
                        ListaEnlazada<Publicacion> linea = ManagePublicaciones.obtenerTimeLine();
                        salida.writeObject(new Respuesta(true, "TimeLine recuperado", linea));
                        salida.flush();
                        break;
                    case BUSCAR_USUARIO:
                        String busqueda = (String) soli.getDato();
                        InstaUser encontrado = GestorInstaUser.buscar(busqueda);
                        if (encontrado == null) {
                            salida.writeObject(new Respuesta(false, "Usuario no encontrado", null));
                        } else {
                            salida.writeObject(new Respuesta(true, "Usuario encontrado", encontrado));
                        }
                        salida.flush();
                        break;
                    case SEGUIR:
                        SeguirDTO sDto = (SeguirDTO) soli.getDato();
                        GestorInstaUser.seguir(sDto.getSeguidor(), sDto.getSeguido());
                        salida.writeObject(new Respuesta(true, "Ahora sigues a " + sDto.getSeguido(), null));
                        salida.flush();
                        break;
                    case VER_PERFIL:
                        String nombrePerfil = (String) soli.getDato();
                        InstaUser perfil = GestorInstaUser.buscar(nombrePerfil);
                        if (perfil == null) {
                            salida.writeObject(new Respuesta(false, "Usuario no encontrado", null));
                        } else {
                            salida.writeObject(new Respuesta(true, "Perfil encontrado", perfil));
                        }
                        salida.flush();
                        break;
                    case ENVIAR_MENSAJE:
                        chatDTO chatDTO = (chatDTO) soli.getDato();
                        int id = InstaServer.getGestorMen().getCode();
                        MensajesDirectos msg = new MensajesDirectos(id, chatDTO.getEmisor(), chatDTO.getReceptor(),
                                chatDTO.getMsg(), chatDTO.isSticker());
                        GestorMD.guardar(msg);
                        ManejoConexion destino = InstaServer.conexionesActivas.get(chatDTO.getReceptor());
                        if(destino!=null){
                            destino.recibirMensaje(msg);
                        }
                        salida.writeObject(new Respuesta(true, "Mensaje Enviado", msg));
                        salida.flush();
                        break;
                    case VER_MENSAJES:
                        String userx = (String) soli.getDato();
                        ListaEnlazada<MensajesDirectos> chat = GestorMD.obtenerChat(userActual, userx);
                        salida.writeObject(new Respuesta(true, "Chat recuperado", chat));
                        salida.flush();
                        break;
                    default:
                        salida.writeObject(new Respuesta(false, "Tipo de peticion no soportado", null)); 
                        salida.flush();
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            try {
                salida.writeObject(new Respuesta(false, e.getMessage(), null));
                salida.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        finally{
            if (userActual != null) {
                InstaServer.conexionesActivas.remove(userActual);
            }
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void recibirMensaje(MensajesDirectos msg) throws IOException{
        synchronized(salida){
            salida.writeObject(new RecibirPush(msg));
            salida.flush();
        }
    }




}
