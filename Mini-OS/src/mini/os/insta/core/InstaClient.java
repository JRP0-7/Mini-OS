package mini.os.insta.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mini.os.error.CuentaDesactivadaException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.model.EstadoDTO;
import mini.os.insta.model.MensajeDTO;
import mini.os.insta.model.MensajesDirectos;
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

public class InstaClient {

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    public InstaClient() throws IOException {
        socket = new Socket("localhost", InstaServer.PORT);
        salida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Respuesta enviar(TipoPeticion tipo, Object dato) throws IOException {
        salida.writeObject(new Solicitud(tipo, dato));
        salida.flush();
        try {
            return (Respuesta) entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Protocolo invalido", e);
        }
    }

    public void cerrar() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public InstaUser login(String user, String pass) throws IOException, CuentaDesactivadaException {
        Respuesta r = enviar(TipoPeticion.LOGIN, new UserDTO(user, pass));
        if (!r.isConnected()) {
            String msg = r.getMsg();
            if (msg != null && msg.contains("desactivada")) {
                throw new CuentaDesactivadaException(msg);
            }
            return null;
        }
        return (InstaUser) r.getDato();
    }

    public InstaUser registrar(String user, String pass, String nombre, char genero, int edad, String rutaFoto)
            throws IOException, UsuarioDuplicadoException {
        Respuesta r = enviar(TipoPeticion.REGISTRAR, new UserDTO(user, pass, nombre, genero, edad, rutaFoto));
        if (!r.isConnected()) {
            throw new UsuarioDuplicadoException(r.getMsg());
        }
        return (InstaUser) r.getDato();
    }

    public boolean publicar(String autor, String texto, String rutaImagen, String carpeta) throws IOException {
        Respuesta r = enviar(TipoPeticion.PUBLICAR, new PublicacionDTO(autor, texto, rutaImagen, carpeta));
        return r.isConnected();
    }

    public ListaEnlazada<Publicacion> timeline(String user) throws IOException {
        Respuesta r = enviar(TipoPeticion.VER_TIMELINE, user);
        return listaPublicaciones(r);
    }

    public ListaEnlazada<Publicacion> publicacionesDe(String user) throws IOException {
        Respuesta r = enviar(TipoPeticion.VER_PUBLICACIONES, user);
        return listaPublicaciones(r);
    }

    public ListaEnlazada<String> buscarPersonas(String texto) throws IOException {
        Respuesta r = enviar(TipoPeticion.BUSCAR_PERSONAS, texto);
        return listaNombres(r);
    }

    public ListaEnlazada<Publicacion> buscarHashtag(String tag) throws IOException {
        Respuesta r = enviar(TipoPeticion.BUSCAR_HASHTAG, tag);
        return listaPublicaciones(r);
    }

    public ListaEnlazada<Publicacion> interacciones(String user) throws IOException {
        Respuesta r = enviar(TipoPeticion.INTERACCIONES, user);
        return listaPublicaciones(r);
    }

    public InstaUser perfil(String user) throws IOException {
        Respuesta r = enviar(TipoPeticion.VER_PERFIL, user);
        if (!r.isConnected()) return null;
        return (InstaUser) r.getDato();
    }

    public boolean seguir(String yo, String otro) throws IOException {
        return enviar(TipoPeticion.SEGUIR, new SeguirDTO(yo, otro)).isConnected();
    }

    public boolean dejarSeguir(String yo, String otro) throws IOException {
        return enviar(TipoPeticion.DEJAR_SEGUIR, new SeguirDTO(yo, otro)).isConnected();
    }

    public ListaEnlazada<String> seguidoresDe(String user) throws IOException {
        return listaNombres(enviar(TipoPeticion.VER_SEGUIDORES, user));
    }

    public ListaEnlazada<String> siguiendoDe(String user) throws IOException {
        return listaNombres(enviar(TipoPeticion.VER_SIGUIENDO, user));
    }

    public boolean activarDesactivar(String user, boolean activo) throws IOException {
        return enviar(TipoPeticion.ACTIVAR_DESACTIVAR, new EstadoDTO(user, activo)).isConnected();
    }

    public String cambiarFoto(String user, String ruta) throws IOException {
        Respuesta r = enviar(TipoPeticion.CAMBIAR_FOTO, new UserDTO(user, null, null, '0', 0, ruta));
        return r.isConnected() ? (String) r.getDato() : null;
    }

    public boolean enviarMensaje(String emisor, String receptor, String contenido, String tipo) throws IOException {
        return enviar(TipoPeticion.ENVIAR_MENSAJE, new MensajeDTO(emisor, receptor, contenido, tipo)).isConnected();
    }

    public ListaEnlazada<String> conversaciones(String user) throws IOException {
        return listaNombres(enviar(TipoPeticion.CONVERSACIONES, user));
    }

    public ListaEnlazada<MensajesDirectos> mensajesEntre(String user, String otro) throws IOException {
        Respuesta r = enviar(TipoPeticion.VER_MENSAJES, new ParejaDTO(user, otro));
        if (!r.isConnected()) return new ListaEnlazada<>();
        return (ListaEnlazada<MensajesDirectos>) r.getDato();
    }

    public boolean marcarLeidos(String user, String otro) throws IOException {
        return enviar(TipoPeticion.MARCAR_LEIDOS, new ParejaDTO(user, otro)).isConnected();
    }

    public boolean eliminarConversacion(String user, String otro) throws IOException {
        return enviar(TipoPeticion.ELIMINAR_CONVERSACION, new ParejaDTO(user, otro)).isConnected();
    }

    public int noLeidos(String user) throws IOException {
        Respuesta r = enviar(TipoPeticion.NO_LEIDOS, user);
        if (!r.isConnected()) return 0;
        return ((Integer) r.getDato());
    }

    public ListaEnlazada<String> stickersDisponibles(String user) throws IOException {
        return listaNombres(enviar(TipoPeticion.STICKERS_DISPONIBLES, user));
    }

    public String importarSticker(String user, String ruta, String nombre) throws IOException {
        Respuesta r = enviar(TipoPeticion.IMPORTAR_STICKER, new StickerDTO(user, ruta, nombre));
        return r.isConnected() ? (String) r.getDato() : null;
    }

    @SuppressWarnings("unchecked")
    private ListaEnlazada<Publicacion> listaPublicaciones(Respuesta r) {
        if (!r.isConnected() || r.getDato() == null) return new ListaEnlazada<>();
        return (ListaEnlazada<Publicacion>) r.getDato();
    }

    @SuppressWarnings("unchecked")
    private ListaEnlazada<String> listaNombres(Respuesta r) {
        if (!r.isConnected() || r.getDato() == null) return new ListaEnlazada<>();
        return (ListaEnlazada<String>) r.getDato();
    }

}