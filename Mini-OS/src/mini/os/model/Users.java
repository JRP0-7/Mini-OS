package mini.os.model;

import java.io.Serializable;

// Clase base para cualquier tipo de usuario del sistema
public abstract class Users implements Serializable {
    private String user;
    private String password;

    public Users(String user, String pass){
        this.user=user;
        this.password = pass;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
