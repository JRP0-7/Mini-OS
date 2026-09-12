package mini.os.insta.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import mini.os.insta.model.Solicitud;
import mini.os.error.UsuarioDuplicadoException;
import mini.os.insta.model.Respuesta;
import mini.os.insta.model.UserDTO;

public class ManejoConexion implements Runnable {
    private Socket socket;


    public ManejoConexion(Socket cliente) {
        this.socket=cliente;
    }

    public void run() {

        ObjectOutputStream salida=null;
        ObjectInputStream entrada;

        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Solicitud soli = (Solicitud) entrada.readObject();
                UserDTO dto = (UserDTO) soli.getDato();
                switch (soli.getTipo()) {
                    case LOGIN:
                        try {
                            if (GestorInstaUser.login(dto.getUser(), dto.getPass())!=null){
                                salida.writeObject(new Respuesta(true, "Login exitoso", null));
                            }else{
                                salida.writeObject(new Respuesta(false, "Usuario o contraseña incorrectos", null));
                            }
                        } catch (NoSuchAlgorithmException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    case REGISTRAR:
                        try {
                            GestorInstaUser.registrar(dto.getUser(), dto.getPass(), dto.getNombre(), dto.getGenero(), dto.getEdad(), dto.getRutaI());
                            salida.writeObject(new Respuesta(true, "Registro exitoso", null));
                        } catch (UsuarioDuplicadoException | NoSuchAlgorithmException e) {
                            salida.writeObject(new Respuesta(false, e.getMessage(), null));
                        }
                        salida.flush();
                        break;
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException | IOException  e) {
            try {
                salida.writeObject(new Respuesta(false, e.getMessage(), null));
                salida.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
