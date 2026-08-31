package mini.os.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.nio.charset.StandardCharsets;

// Maneja la seguridad de las contraseñas
public class Autentificacion {
    // Convierte la contraseña en un hash SHA-256 para que no esté en texto plano
    public static String hash(String pass) throws NoSuchAlgorithmException{
        try {
            MessageDigest dig = MessageDigest.getInstance("SHA-256");
            byte[] hashcodificado = dig.digest(pass.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashcodificado);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(e);
        }
    }

}
