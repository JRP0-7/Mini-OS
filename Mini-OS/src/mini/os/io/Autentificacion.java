package mini.os.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.nio.charset.StandardCharsets;

public class Autentificacion {
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
