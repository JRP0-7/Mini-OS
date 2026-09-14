package mini.os.insta.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import mini.os.insta.model.Respuesta;
import mini.os.insta.model.Solicitud;
import mini.os.model.ListaEnlazada;

public class InstaCliente {
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private BlockingQueue<Respuesta> colaRespuesta;
    private BlockingQueue<RecibirPush> colaPushes;

    public static InstaCliente conectar(String host) throws UnknownHostException, IOException {
        Socket socket = new Socket(host, InstaServer.PORT);
        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        return new InstaCliente(socket, salida, entrada);
    }

    public InstaCliente(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada) {
        this.socket = socket;
        this.entrada = entrada;
        this.salida = salida;
        this.colaRespuesta = new LinkedBlockingQueue<>();
        this.colaPushes = new LinkedBlockingQueue<>();
        iniciarProceso();
    }

    private void iniciarProceso() {

        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Object obj = entrada.readObject();
                    if (obj instanceof Respuesta) {
                        colaRespuesta.put((Respuesta) obj);
                        ;
                    }
                    if (obj instanceof RecibirPush) {
                        colaPushes.put((RecibirPush) obj);
                    }
                } catch (ClassNotFoundException | IOException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return;
                }

            }
        });

        t.start();
    }

    public synchronized Respuesta enviar(Solicitud sol) throws IOException, InterruptedException {
        salida.writeObject(sol);
        salida.flush();
        return colaRespuesta.take();
    }
}
