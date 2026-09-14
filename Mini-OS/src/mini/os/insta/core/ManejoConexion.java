package mini.os.insta.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import mini.os.error.ArchivoCorruptoException;
import mini.os.error.CuentaDesactivadaException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.model.EstadoDTO;
import mini.os.insta.model.MensajeDTO;
import mini.os.insta.model.ParejaDTO;
import mini.os.insta.model.Publicacion;
import mini.os.insta.model.PublicacionDTO;
import mini.os.insta.model.Respuesta;
import mini.os.insta.model.SeguirDTO;
import mini.os.insta.model.Solicitud;
import mini.os.insta.model.StickerDTO;
import mini.os.insta.model.TipoPeticion;
import mini.os.insta.model.UserDTO;
import mini.os.model.InstaUser;
import mini.os.model.ListaEnlazada;

public class ManejoConexion implements Runnable {
    private Socket socket;
    private String userActual;
    private ObjectOutputStream salida = null;

    public ManejoConexion(Socket cliente) {
        this.socket = cliente;
    }

    public void run() {


        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Solicitud soli = (Solicitud) entrada.readObject();
                TipoPeticion tipo = soli.getTipo();
                switch (tipo) {
                    case LOGIN:
                        UserDTO DTO = (UserDTO) soli.getDato();
                        try {
                            InstaUser u = ServicioInsta.login(DTO.getUser(), DTO.getPass());
                            if (u == null) {
                                salida.writeObject(new Respuesta(false, "Usuario o contraseña incorrectos", null));
                            } else {
                                salida.writeObject(new Respuesta(true, "Login exitoso", u));
                                userActual= DTO.getUser();
                            }
                        } catch (NoSuchAlgorithmException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        } catch (CuentaDesactivadaException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    case REGISTRAR:
                        UserDTO DTO2 = (UserDTO) soli.getDato();
                        try {
                            InstaUser nuevo = ServicioInsta.registrar(DTO2);
                            userActual = DTO2.getUser();
                            salida.writeObject(new Respuesta(true, "Cuenta creada correctamente", nuevo));
                        } catch (UsuarioDuplicadoException | NoSuchAlgorithmException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    case PUBLICAR:
                        PublicacionDTO pDTO = (PublicacionDTO) soli.getDato();
                        if(!esLogeado(pDTO.getAutor()))
                            break;
                        Publicacion nueva = ServicioInsta.publicar(pDTO.getAutor(), pDTO.getTexto(),
                                pDTO.getRutaImagen(), pDTO.getCarpeta());
                        salida.writeObject(new Respuesta(true, "Publicacion creada", nueva));
                        salida.flush();
                        break;
                    case VER_TIMELINE:
                        String yoTimeline = (String) soli.getDato();
                        ListaEnlazada<Publicacion> linea = ServicioInsta.timelineDe(yoTimeline);
                        salida.writeObject(new Respuesta(true, "TimeLine recuperado", linea));
                        salida.flush();
                        break;
                    case VER_PUBLICACIONES:
                        String autorPub = (String) soli.getDato();
                        ListaEnlazada<Publicacion> pubsAutor = ServicioInsta.publicacionesDe(autorPub);
                        salida.writeObject(new Respuesta(true, "Publicaciones recuperadas", pubsAutor));
                        salida.flush();
                        break;
                    case BUSCAR_PERSONAS:
                        String busqueda = (String) soli.getDato();
                        ListaEnlazada<String> personas = ServicioInsta.buscarPersonas(busqueda);
                        salida.writeObject(new Respuesta(true, "Resultados de busqueda", personas));
                        salida.flush();
                        break;
                    case BUSCAR_HASHTAG:
                        String tag = (String) soli.getDato();
                        ListaEnlazada<Publicacion> porTag = ServicioInsta.buscarHashtag(tag);
                        salida.writeObject(new Respuesta(true, "Publicaciones con el hashtag", porTag));
                        salida.flush();
                        break;
                    case INTERACCIONES:
                        String yoInter = (String) soli.getDato();
                        ListaEnlazada<Publicacion> inter = ServicioInsta.interacciones(yoInter);
                        salida.writeObject(new Respuesta(true, "Menciones recuperadas", inter));
                        salida.flush();
                        break;
                    case VER_PERFIL:
                        String nombrePerfil = (String) soli.getDato();
                        InstaUser perfil = ServicioInsta.perfil(nombrePerfil);
                        if (perfil == null) {
                            salida.writeObject(new Respuesta(false, "Usuario no encontrado o cuenta desactivada", null));
                        } else {
                            salida.writeObject(new Respuesta(true, "Perfil encontrado", perfil));
                        }
                        salida.flush();
                        break;
                    case SEGUIR:
                        SeguirDTO sDTO = (SeguirDTO) soli.getDato();
                        if(!esLogeado(sDTO.getSeguidor()))
                            break;
                        ServicioInsta.seguir(sDTO.getSeguidor(), sDTO.getSeguido());
                        salida.writeObject(new Respuesta(true, "Ahora sigues a " + sDTO.getSeguido(), null));
                        salida.flush();
                        break;
                    case DEJAR_SEGUIR:
                        SeguirDTO uDTO = (SeguirDTO) soli.getDato();
                        if(!esLogeado(uDTO.getSeguidor()))
                            break;
                        ServicioInsta.dejarSeguir(uDTO.getSeguidor(), uDTO.getSeguido());
                        salida.writeObject(new Respuesta(true, "Dejaste de seguir a " + uDTO.getSeguido(), null));
                        salida.flush();
                        break;
                    case VER_SEGUIDORES:
                        String userSeg = (String) soli.getDato();
                        salida.writeObject(new Respuesta(true, "Seguidores", ServicioInsta.seguidoresDe(userSeg)));
                        salida.flush();
                        break;
                    case VER_SIGUIENDO:
                        String userSig = (String) soli.getDato();
                        salida.writeObject(new Respuesta(true, "Siguiendo", ServicioInsta.siguiendoDe(userSig)));
                        salida.flush();
                        break;
                    case ACTIVAR_DESACTIVAR:
                        EstadoDTO estado = (EstadoDTO) soli.getDato();
                        if(!esLogeado(estado.getUser()))
                            break;
                        ServicioInsta.activarDesactivar(estado.getUser(), estado.isActivo());
                        salida.writeObject(new Respuesta(true,
                                estado.isActivo() ? "Cuenta activada correctamente" : "Cuenta desactivada, ya no apareces en busquedas",
                                null));
                        salida.flush();
                        break;
                    case CAMBIAR_FOTO:
                        UserDTO fotoDTO = (UserDTO) soli.getDato();
                        if(!esLogeado(fotoDTO.getUser()))
                            break;
                        String ruta = ServicioInsta.cambiarFoto(fotoDTO.getUser(), fotoDTO.getRutaI());
                        salida.writeObject(new Respuesta(true, "Foto de perfil actualizada", ruta));
                        salida.flush();
                        break;
                    case ENVIAR_MENSAJE:
                        MensajeDTO mDTO = (MensajeDTO) soli.getDato();
                        if(!esLogeado(mDTO.getEmisor()))
                            break;
                        ServicioInsta.enviarMensaje(mDTO.getEmisor(), mDTO.getReceptor(), mDTO.getContenido(), mDTO.getTipo());
                        salida.writeObject(new Respuesta(true, "Mensaje enviado", null));
                        salida.flush();
                        break;
                    case VER_MENSAJES:
                        ParejaDTO pareja = (ParejaDTO) soli.getDato();
                        if(!esLogeado(pareja.getUserA()))
                            break;
                        salida.writeObject(new Respuesta(true, "Historial de conversacion",
                                ServicioInsta.mensajesEntre(pareja.getUserA(), pareja.getUserB())));
                        salida.flush();
                        break;
                    case CONVERSACIONES:
                        String userConv = (String) soli.getDato();
                        if(!esLogeado(userConv))
                            break;
                        salida.writeObject(new Respuesta(true, "Conversaciones", ServicioInsta.conversaciones(userConv)));
                        salida.flush();
                        break;
                    case MARCAR_LEIDOS:
                        ParejaDTO pareja2 = (ParejaDTO) soli.getDato();
                        if(!esLogeado(pareja2.getUserA()))
                            break;
                        ServicioInsta.marcarLeidos(pareja2.getUserA(), pareja2.getUserB());
                        salida.writeObject(new Respuesta(true, "Marcados como leidos", null));
                        salida.flush();
                        break;
                    case ELIMINAR_CONVERSACION:
                        ParejaDTO pareja3 = (ParejaDTO) soli.getDato();
                        if(!esLogeado(pareja3.getUserA()))
                            break;
                        ServicioInsta.eliminarConversacion(pareja3.getUserA(), pareja3.getUserB());
                        salida.writeObject(new Respuesta(true, "Conversacion eliminada", null));
                        salida.flush();
                        break;
                    case NO_LEIDOS:
                        String userNo = (String) soli.getDato();
                        if(!esLogeado(userNo))
                            break;
                        int cantidad = ServicioInsta.noLeidos(userNo);
                        salida.writeObject(new Respuesta(true, "No leidos", cantidad));
                        salida.flush();
                        break;
                    case STICKERS_DISPONIBLES:
                        String userSticky = (String) soli.getDato();
                        if(!esLogeado(userSticky))
                            break;
                        salida.writeObject(new Respuesta(true, "Stickers disponibles", ServicioInsta.stickersDisponibles(userSticky)));
                        salida.flush();
                        break;
                    case IMPORTAR_STICKER:
                        StickerDTO stDTO = (StickerDTO) soli.getDato();
                        if(!esLogeado(stDTO.getUser()))
                            break;
                        String rutaSticker = ServicioInsta.importarSticker(stDTO.getUser(), stDTO.getRutaArchivo(), stDTO.getNombre());
                        if (rutaSticker == null) {
                            salida.writeObject(new Respuesta(false, "No se pudo importar el sticker (formato o archivo invalido)", null));
                        } else {
                            salida.writeObject(new Respuesta(true, "Sticker importado", rutaSticker));
                        }
                        salida.flush();
                        break;
                    default:
                        salida.writeObject(new Respuesta(false, "Tipo de peticion no soportado", null)); 
                        salida.flush();
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException | ArchivoCorruptoException e) {
            try {
                salida.writeObject(new Respuesta(false, "Error de conexion: " + e.getMessage(), null));
                salida.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally{
            try{
                socket.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

    }

    private boolean esLogeado(String usuario) throws IOException{
        if(userActual == null || !userActual.equals(usuario)){
            salida.writeObject(new Respuesta(false, "Accion no Autorizada", null));
            salida.flush();
            return false;
        }
        return true;
    }

}