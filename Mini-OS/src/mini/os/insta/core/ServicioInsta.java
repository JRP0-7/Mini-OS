package mini.os.insta.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import mini.os.error.ArchivoCorruptoException;
import mini.os.error.CuentaDesactivadaException;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.model.MensajesDirectos;
import mini.os.insta.model.Publicacion;
import mini.os.insta.model.UserDTO;
import mini.os.insta.model.gestorMensajes;
import mini.os.insta.model.gestorPublicacion;
import mini.os.io.ArchivoUtil;
import mini.os.io.Autentificacion;
import mini.os.model.InstaUser;
import mini.os.model.ListaEnlazada;

public class ServicioInsta {

    private static final String[] STICKERS_DEFECTO = {"Feliz.png", "Triste.png", "Corazon.png", "Risa.png", "Aplauso.png"};
    private static final gestorPublicacion GP = new gestorPublicacion();
    private static final gestorMensajes GM = new gestorMensajes();

    private static File carpeta(String user) {
        return new File(InstaServer.IROOT, user);
    }

    private static File archivo(File carpeta, String nombre) {
        return new File(carpeta, nombre);
    }

    private static ListaEnlazada<String> leerNombres(File f) throws ArchivoCorruptoException {
        if (!f.exists()) return new ListaEnlazada<>();
        ListaEnlazada<String> l = ArchivoUtil.leer(f.getPath());
        return l == null ? new ListaEnlazada<>() : l;
    }

    private static void guardarNombres(File f, ListaEnlazada<String> lista) {
        ArchivoUtil.guardar(lista, f.getPath());
    }

    private static ListaEnlazada<InstaUser> leerUsuarios() throws ArchivoCorruptoException {
        File f = new File(InstaServer.IROOT, "users.ins");
        if (!f.exists()) return new ListaEnlazada<>();
        ListaEnlazada<InstaUser> l = ArchivoUtil.leer(f.getPath());
        return l == null ? new ListaEnlazada<>() : l;
    }

    private static void guardarUsuarios(ListaEnlazada<InstaUser> lista) {
        ArchivoUtil.guardar(lista, new File(InstaServer.IROOT, "users.ins").getPath());
    }

    private static ListaEnlazada<Publicacion> leerPublicaciones(String user) throws ArchivoCorruptoException {
        return leerPublicaciones(new File(carpeta(user), "insta.ins"));
    }

    private static ListaEnlazada<Publicacion> leerPublicaciones(File f) throws ArchivoCorruptoException {
        if (!f.exists()) return new ListaEnlazada<>();
        ListaEnlazada<Publicacion> l = ArchivoUtil.leer(f.getPath());
        return l == null ? new ListaEnlazada<>() : l;
    }

    private static void guardarPublicaciones(String user, ListaEnlazada<Publicacion> lista) {
        ArchivoUtil.guardar(lista, new File(carpeta(user), "insta.ins").getPath());
    }

    private static ListaEnlazada<MensajesDirectos> leerMensajes(String user) throws ArchivoCorruptoException {
        File f = new File(carpeta(user), "inbox.ins");
        if (!f.exists()) return new ListaEnlazada<>();
        ListaEnlazada<MensajesDirectos> l = ArchivoUtil.leer(f.getPath());
        return l == null ? new ListaEnlazada<>() : l;
    }

    private static void guardarMensajes(String user, ListaEnlazada<MensajesDirectos> lista) {
        ArchivoUtil.guardar(lista, new File(carpeta(user), "inbox.ins").getPath());
    }

    private static ListaEnlazada<String> leerStickers(String user) throws ArchivoCorruptoException {
        return leerNombres(new File(carpeta(user), "stickers.ins"));
    }

    private static void guardarStickers(String user, ListaEnlazada<String> lista) {
        guardarNombres(new File(carpeta(user), "stickers.ins"), lista);
    }

    public static synchronized void inicializarDatos() throws ArchivoCorruptoException {
        File raiz = new File(InstaServer.IROOT);
        raiz.mkdirs();
        File globales = new File(raiz, "stickers_globales");
        if (!globales.exists()) globales.mkdirs();
        generarStickersGlobales();

        File uf = new File(raiz, "users.ins");
        ListaEnlazada<InstaUser> lista = leerUsuarios();
        if (!uf.exists()) guardarUsuarios(lista);

        if (lista.getSize() == 0) {
            sembrarDatos();
        }
    }

    private static void sembrarDatos() {
        try {
            registrar(new UserDTO("admin", "1234", "Administrador INSTA+", 'M', 25, null));
            registrar(new UserDTO("noticias", "noticias", "El Noticiero", 'M', 30, null));
            registrar(new UserDTO("moda", "moda", "Moda y Deporte", 'F', 26, null));
            registrar(new UserDTO("entretenimiento", "entretenimiento", "Zona Entretenimiento", 'M', 22, null));

            seguir("admin", "noticias");
            seguir("admin", "moda");
            seguir("admin", "entretenimiento");
            seguir("moda", "noticias");

            publicar("noticias", "Gran estreno: nueva inteligencia artificial para estudiantes #tecnologia", null);
            publicar("noticias", "Resultados deportivos de la semana #deporte", null);
            publicar("moda", "Las tendencias que amaras esta temporada #moda", null);
            publicar("moda", "Rutina fitness de 5 minutos al dia #deporte #salud", null);
            publicar("entretenimiento", "La pelicula del mes llega hoy, no te la pierdas", null);
            publicar("entretenimiento", "Top 10 videos virales #entretenimiento", null);
            publicar("noticias", "Entrevista exclusiva al mejor usuario de INSTA+ @admin", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized InstaUser registrar(UserDTO dto)
            throws UsuarioDuplicadoException, NoSuchAlgorithmException, ArchivoCorruptoException {
        String user = dto.getUser();
        if (existe(user)) {
            throw new UsuarioDuplicadoException("El usuario " + user + " ya existe");
        }

        File dir = carpeta(user);
        dir.mkdirs();
        new File(dir, "imagenes").mkdirs();
        new File(dir, "folders_personales").mkdirs();
        new File(dir, "stickers_personales").mkdirs();

        String rutaFoto = guardarFoto(dto.getRutaI(), user, null);

        InstaUser nuevo = new InstaUser(user, Autentificacion.hash(dto.getPass()),
                dto.getNombre(), dto.getGenero(), dto.getEdad(), new Date(), rutaFoto, true);

        ListaEnlazada<InstaUser> lista = leerUsuarios();
        lista.agregar(nuevo);
        guardarUsuarios(lista);

        guardarNombres(new File(dir, "following.ins"), new ListaEnlazada<>());
        guardarNombres(new File(dir, "followers.ins"), new ListaEnlazada<>());
        guardarNombres(new File(dir, "insta.ins"), new ListaEnlazada<>());
        guardarNombres(new File(dir, "inbox.ins"), new ListaEnlazada<>());
        ListaEnlazada<String> stickers = new ListaEnlazada<>();
        for (String s : STICKERS_DEFECTO) {
            stickers.agregar(s);
        }
        guardarStickers(user, stickers);

        return nuevo;
    }

    public static synchronized InstaUser login(String user, String pass)
            throws NoSuchAlgorithmException, CuentaDesactivadaException, ArchivoCorruptoException {
        InstaUser u = buscar(user);
        if (u == null) return null;
        if (!u.getPassword().equals(Autentificacion.hash(pass))) return null;
        if(!u.isActivo()) 
            throw new CuentaDesactivadaException("La cuenta esta desactivada");
        return u;
    }

    public static synchronized InstaUser buscar(String user) throws ArchivoCorruptoException {
        ListaEnlazada<InstaUser> lista = leerUsuarios();
        for (int i = 0; i < lista.getSize(); i++) {
            InstaUser u = lista.get(i);
            if (u.getUser().equals(user)) return u;
        }
        return null;
    }

    public static synchronized boolean existe(String user) throws ArchivoCorruptoException {
        return buscar(user) != null;
    }

    public static synchronized boolean esActiva(String user) throws ArchivoCorruptoException {
        InstaUser u = buscar(user);
        return u != null && u.isActivo();
    }

    public static synchronized InstaUser perfil(String user) throws ArchivoCorruptoException {
        return buscar(user);
    }

    public static synchronized ListaEnlazada<String> buscarPersonas(String texto) throws ArchivoCorruptoException {
        ListaEnlazada<String> resultado = new ListaEnlazada<>();
        if (texto == null || texto.trim().isEmpty()) return resultado;
        String t = texto.trim().toLowerCase();
        ListaEnlazada<InstaUser> lista = leerUsuarios();
        for (int i = 0; i < lista.getSize(); i++) {
            InstaUser u = lista.get(i);
            if (u.isActivo() && u.getUser().toLowerCase().contains(t)) {
                resultado.agregar(u.getUser());
            }
        }
        return resultado;
    }

    public static synchronized void seguir(String seguidor, String seguido) throws ArchivoCorruptoException {
        if (seguidor.equals(seguido)) return;
        if (!existe(seguido)) return;

        ListaEnlazada<String> sig = leerNombres(new File(carpeta(seguidor), "following.ins"));
        if (!sig.contiene(seguido)) {
            sig.agregar(seguido);
            guardarNombres(new File(carpeta(seguidor), "following.ins"), sig);
        }

        ListaEnlazada<String> seg = leerNombres(new File(carpeta(seguido), "followers.ins"));
        if (!seg.contiene(seguidor)) {
            seg.agregar(seguidor);
            guardarNombres(new File(carpeta(seguido), "followers.ins"), seg);
        }
    }

    public static synchronized void dejarSeguir(String seguidor, String seguido) throws ArchivoCorruptoException {
        ListaEnlazada<String> sig = leerNombres(new File(carpeta(seguidor), "following.ins"));
        sig.eliminar(seguido);
        guardarNombres(new File(carpeta(seguidor), "following.ins"), sig);

        ListaEnlazada<String> seg = leerNombres(new File(carpeta(seguido), "followers.ins"));
        seg.eliminar(seguidor);
        guardarNombres(new File(carpeta(seguido), "followers.ins"), seg);
    }

    public static synchronized ListaEnlazada<String> seguidoresDe(String user) throws ArchivoCorruptoException {
        return leerNombres(new File(carpeta(user), "followers.ins"));
    }

    public static synchronized ListaEnlazada<String> siguiendoDe(String user) throws ArchivoCorruptoException {
        return leerNombres(new File(carpeta(user), "following.ins"));
    }

    public static synchronized boolean loSigo(String yo, String otro) throws ArchivoCorruptoException {
        return siguiendoDe(yo).contiene(otro);
    }

    public static synchronized Publicacion publicar(String autor, String texto, String rutaImagen) throws ArchivoCorruptoException {
        return publicar(autor, texto, rutaImagen, null);
    }

    public static synchronized Publicacion publicar(String autor, String texto, String rutaImagen, String carpetaPersonal) throws ArchivoCorruptoException {
        String rutaFinal = guardarImagenPublicacion(autor, rutaImagen, carpetaPersonal);
        int id = siguienteIdPublicacion();
        Publicacion p = new Publicacion(id, autor, texto, rutaFinal);
        ListaEnlazada<Publicacion> lista = leerPublicaciones(autor);
        lista.agregar(p);
        guardarPublicaciones(autor, lista);
        return p;
    }

    public static synchronized ListaEnlazada<Publicacion> timelineDe(String user) throws ArchivoCorruptoException {
        ListaEnlazada<Publicacion> resultado = new ListaEnlazada<>();
        agregarPublicacionesDe(resultado, user);
        ListaEnlazada<String> siguiendo = siguiendoDe(user);
        for (int i = 0; i < siguiendo.getSize(); i++) {
            agregarPublicacionesDe(resultado, siguiendo.get(i));
        }
        return ordenarDesc(resultado);
    }

    public static synchronized ListaEnlazada<Publicacion> publicacionesDe(String user) throws ArchivoCorruptoException {
        if (!esActiva(user)) return new ListaEnlazada<>();
        return ordenarDesc(leerPublicaciones(user));
    }

    public static synchronized ListaEnlazada<Publicacion> buscarHashtag(String tag) throws ArchivoCorruptoException {
        ListaEnlazada<Publicacion> resultado = new ListaEnlazada<>();
        String t = tag.trim().toLowerCase();
        if (t.isEmpty()) return resultado;
        ListaEnlazada<InstaUser> usuarios = leerUsuarios();
        for (int i = 0; i < usuarios.getSize(); i++) {
            InstaUser u = usuarios.get(i);
            if (!u.isActivo()) continue;
            ListaEnlazada<Publicacion> pubs = leerPublicaciones(u.getUser());
            for (int j = 0; j < pubs.getSize(); j++) {
                Publicacion p = pubs.get(j);
                if (p.getTexto() != null && p.getTexto().toLowerCase().contains("#" + t)) {
                    agregarSinDuplicar(resultado, p);
                }
            }
        }
        return resultado;
    }

    public static synchronized ListaEnlazada<Publicacion> interacciones(String user) throws ArchivoCorruptoException {
        ListaEnlazada<Publicacion> resultado = new ListaEnlazada<>();
        ListaEnlazada<InstaUser> usuarios = leerUsuarios();
        for (int i = 0; i < usuarios.getSize(); i++) {
            InstaUser u = usuarios.get(i);
            if (u.getUser().equals(user) || !u.isActivo()) continue;
            ListaEnlazada<Publicacion> pubs = leerPublicaciones(u.getUser());
            for (int j = 0; j < pubs.getSize(); j++) {
                Publicacion p = pubs.get(j);
                if (mencionaA(p.getTexto(), user)) {
                    agregarSinDuplicar(resultado, p);
                }
            }
        }
        return ordenarDesc(resultado);
    }

    private static void agregarPublicacionesDe(ListaEnlazada<Publicacion> resultado, String autor) throws ArchivoCorruptoException {
        if (!esActiva(autor)) return;
        ListaEnlazada<Publicacion> pubs = leerPublicaciones(autor);
        for (int i = 0; i < pubs.getSize(); i++) {
            agregarSinDuplicar(resultado, pubs.get(i));
        }
    }

    private static void agregarSinDuplicar(ListaEnlazada<Publicacion> resultado, Publicacion p) {
        for (int i = 0; i < resultado.getSize(); i++) {
            if (resultado.get(i).getId() == p.getId()) return;
        }
        resultado.agregar(p);
    }

    private static ListaEnlazada<Publicacion> ordenarDesc(ListaEnlazada<Publicacion> lista) {
        ListaEnlazada<Publicacion> ordenada = new ListaEnlazada<>();
        for (int i = 0; i < lista.getSize(); i++) {
            Publicacion p = lista.get(i);
            int pos = 0;
            while (pos < ordenada.getSize() && ordenada.get(pos).getFecha().after(p.getFecha())) {
                pos++;
            }
            ordenada.agregarEn(pos, p);
        }
        return ordenada;
    }

    private static boolean mencionaA(String texto, String user) {
        if (texto == null) return false;
        return texto.toLowerCase().contains("@" + user.toLowerCase());
    }

    public static synchronized void enviarMensaje(String emisor, String receptor, String contenido, String tipo) throws ArchivoCorruptoException {
        if (!existe(receptor)) return;
        int id = siguienteIdMensaje();

        MensajesDirectos mio = new MensajesDirectos(id, emisor, receptor, contenido, tipo);
        mio.fueLeido();
        ListaEnlazada<MensajesDirectos> mios = leerMensajes(emisor);
        mios.agregar(mio);
        guardarMensajes(emisor, mios);

        MensajesDirectos otro = new MensajesDirectos(id, emisor, receptor, contenido, tipo);
        ListaEnlazada<MensajesDirectos> otros = leerMensajes(receptor);
        otros.agregar(otro);
        guardarMensajes(receptor, otros);
    }

    public static synchronized ListaEnlazada<String> conversaciones(String user) throws ArchivoCorruptoException {
        ListaEnlazada<String> resultado = new ListaEnlazada<>();
        ListaEnlazada<MensajesDirectos> msj = leerMensajes(user);
        for (int i = 0; i < msj.getSize(); i++) {
            MensajesDirectos m = msj.get(i);
            String otro = m.getEmisor().equals(user) ? m.getReceptor() : m.getEmisor();
            if (!otro.equals(user) && !resultado.contiene(otro)) {
                resultado.agregar(otro);
            }
        }
        return resultado;
    }

    public static synchronized ListaEnlazada<MensajesDirectos> mensajesEntre(String user, String otro) throws ArchivoCorruptoException {
        ListaEnlazada<MensajesDirectos> resultado = new ListaEnlazada<>();
        ListaEnlazada<MensajesDirectos> msj = leerMensajes(user);
        for (int i = 0; i < msj.getSize(); i++) {
            MensajesDirectos m = msj.get(i);
            if ((m.getEmisor().equals(user) && m.getReceptor().equals(otro))
                    || (m.getEmisor().equals(otro) && m.getReceptor().equals(user))) {
                resultado.agregar(m);
            }
        }
        return resultado;
    }

    public static synchronized void marcarLeidos(String user, String otro) throws ArchivoCorruptoException {
        ListaEnlazada<MensajesDirectos> msj = leerMensajes(user);
        boolean cambio = false;
        for (int i = 0; i < msj.getSize(); i++) {
            MensajesDirectos m = msj.get(i);
            if (m.getEmisor().equals(otro) && m.getReceptor().equals(user) && !m.isLeido()) {
                m.fueLeido();
                cambio = true;
            }
        }
        if (cambio) guardarMensajes(user, msj);
    }

    public static synchronized void eliminarConversacion(String user, String otro) throws ArchivoCorruptoException {
        ListaEnlazada<MensajesDirectos> msj = leerMensajes(user);
        ListaEnlazada<MensajesDirectos> nuevo = new ListaEnlazada<>();
        for (int i = 0; i < msj.getSize(); i++) {
            MensajesDirectos m = msj.get(i);
            if (!m.getEmisor().equals(otro) && !m.getReceptor().equals(otro)) {
                nuevo.agregar(m);
            }
        }
        guardarMensajes(user, nuevo);
    }

    public static synchronized int noLeidos(String user) throws ArchivoCorruptoException {
        int cont = 0;
        ListaEnlazada<MensajesDirectos> msj = leerMensajes(user);
        for (int i = 0; i < msj.getSize(); i++) {
            MensajesDirectos m = msj.get(i);
            if (m.getReceptor().equals(user) && !m.isLeido()) cont++;
        }
        return cont;
    }

    public static synchronized void activarDesactivar(String user, boolean activo) throws ArchivoCorruptoException {
        ListaEnlazada<InstaUser> lista = leerUsuarios();
        for (int i = 0; i < lista.getSize(); i++) {
            InstaUser u = lista.get(i);
            if (u.getUser().equals(user)) {
                u.setActivo(activo);
                break;
            }
        }
        guardarUsuarios(lista);
    }

    public static synchronized String cambiarFoto(String user, String nuevaRuta) throws ArchivoCorruptoException {
        String ruta = guardarFoto(nuevaRuta, user, "foto_perfil" + extension(nuevaRuta));
        ListaEnlazada<InstaUser> lista = leerUsuarios();
        for (int i = 0; i < lista.getSize(); i++) {
            InstaUser u = lista.get(i);
            if (u.getUser().equals(user)) {
                u.setRutaI(ruta);
                break;
            }
        }
        guardarUsuarios(lista);
        return ruta;
    }

    public static synchronized ListaEnlazada<String> stickersDisponibles(String user) throws ArchivoCorruptoException {
        ListaEnlazada<String> resultado = new ListaEnlazada<>();
        ListaEnlazada<String> nombres = leerStickers(user);
        File globales = new File(InstaServer.IROOT, "stickers_globales");
        File personales = new File(carpeta(user), "stickers_personales");
        for (int i = 0; i < nombres.getSize(); i++) {
            String nombre = nombres.get(i);
            File f = new File(globales, nombre);
            if (!f.exists()) f = new File(personales, nombre);
            if (f.exists()) {
                resultado.agregar(f.getAbsolutePath());
            }
        }
        return resultado;
    }

    public static synchronized String importarSticker(String user, String ruta, String nombre) throws ArchivoCorruptoException {
        if (ruta == null) return null;
        File src = new File(ruta);
        if (!src.exists()) return null;
        String ext = extension(ruta);
        if (!ext.equalsIgnoreCase(".png") && !ext.equalsIgnoreCase(".jpg") && !ext.equalsIgnoreCase(".jpeg")) {
            return null;
        }
        String nombreFinal = nombre == null || nombre.trim().isEmpty() ? "sticker" + System.currentTimeMillis() : nombre.trim();
        if (!nombreFinal.endsWith(".png") && !nombreFinal.endsWith(".jpg") && !nombreFinal.endsWith(".jpeg")) {
            nombreFinal = nombreFinal + ext;
        }
        File destino = new File(new File(carpeta(user), "stickers_personales"), nombreFinal);
        try {
            Files.copy(src.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ListaEnlazada<String> nombres = leerStickers(user);
        if (!nombres.contiene(nombreFinal)) {
            nombres.agregar(nombreFinal);
            guardarStickers(user, nombres);
        }
        return destino.getAbsolutePath();
    }

    public static String formatearFecha(Date fecha) {
        if (fecha == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
    }

    private static int siguienteIdPublicacion() {
        try {
            return GP.getCode();
        } catch (IOException e) {
            return (int) System.currentTimeMillis();
        }
    }

    private static int siguienteIdMensaje() {
        try {
            return GM.getCode();
        } catch (IOException e) {
            return (int) System.currentTimeMillis();
        }
    }

    private static String guardarImagenPublicacion(String autor, String rutaImagen, String carpetaPersonal) {
        if (rutaImagen == null) return null;
        File src = new File(rutaImagen);
        if (!src.exists()) return null;
        String nombre = System.currentTimeMillis() + "_" + src.getName();
        File destino;
        File carpetas = new File(carpeta(autor), "folders_personales");
        if (carpetaPersonal != null && !carpetaPersonal.trim().isEmpty()) {
            File sub = new File(carpetas, carpetaPersonal.trim());
            if (!sub.exists()) sub.mkdirs();
            destino = new File(sub, nombre);
        } else {
            destino = new File(new File(carpeta(autor), "imagenes"), nombre);
        }
        try {
            Files.copy(src.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destino.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String guardarFoto(String origen, String user, String nombreDestino) {
        if (origen == null) return null;
        File src = new File(origen);
        if (!src.exists()) return null;
        File imagenes = new File(carpeta(user), "imagenes");
        if (!imagenes.exists()) imagenes.mkdirs();
        String nombre = nombreDestino != null ? nombreDestino : ("foto_perfil" + extension(origen));
        File destino = new File(imagenes, nombre);
        try {
            Files.copy(src.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destino.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extension(String ruta) {
        if (ruta == null) return ".jpg";
        int i = ruta.lastIndexOf('.');
        if (i < 0) return ".jpg";
        String e = ruta.substring(i);
        return e.length() > 5 ? ".jpg" : e;
    }

    private static void generarStickersGlobales() {
        File dir = new File(InstaServer.IROOT, "stickers_globales");
        if (!dir.exists()) dir.mkdirs();
        for (String nombre : STICKERS_DEFECTO) {
            File f = new File(dir, nombre);
            if (f.exists()) continue;
            try {
                BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                dibujarSticker(g, nombre);
                g.dispose();
                ImageIO.write(img, "png", f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void dibujarSticker(Graphics2D g, String nombre) {
        if (nombre.startsWith("Feliz")) {
            g.setColor(new Color(255, 214, 10));
            g.fillOval(10, 10, 100, 100);
            g.setColor(Color.BLACK);
            g.fillOval(35, 40, 12, 16);
            g.fillOval(73, 40, 12, 16);
            g.setStroke(new java.awt.BasicStroke(5f));
            g.drawArc(32, 55, 56, 40, 20, 140);
        } else if (nombre.startsWith("Triste")) {
            g.setColor(new Color(64, 156, 255));
            g.fillOval(10, 10, 100, 100);
            g.setColor(Color.BLACK);
            g.fillOval(35, 40, 12, 16);
            g.fillOval(73, 40, 12, 16);
            g.setStroke(new java.awt.BasicStroke(5f));
            g.drawArc(32, 68, 56, 40, 200, 140);
        } else if (nombre.startsWith("Corazon")) {
            g.setColor(new Color(231, 76, 60));
            int[] xs = {60, 20, 100};
            int[] ys = {42, 100, 100};
            g.fillPolygon(xs, ys, 3);
            g.fillOval(18, 8, 44, 52);
            g.fillOval(58, 8, 44, 52);
        } else if (nombre.startsWith("Risa")) {
            g.setColor(new Color(255, 214, 10));
            g.fillOval(10, 10, 100, 100);
            g.setColor(Color.BLACK);
            g.fillOval(35, 40, 12, 16);
            g.fillOval(73, 40, 12, 16);
            g.setColor(new Color(120, 60, 0));
            g.fillRoundRect(35, 62, 50, 28, 16, 16);
            g.setColor(Color.WHITE);
            g.fillRect(45, 68, 30, 7);
        } else {
            g.setColor(new Color(255, 200, 87));
            for (int i = 0; i < 4; i++) {
                g.fillRoundRect(15 + i * 12, 20, 9, 22, 6, 6);
            }
            g.fillOval(12, 30, 45, 45);
            for (int i = 0; i < 4; i++) {
                g.fillRoundRect(65 + i * 12, 20, 9, 22, 6, 6);
            }
            g.fillOval(62, 30, 45, 45);
        }
    }
}