package mini.os.model;

// Usuario básico del sistema (como el admin)
public class SystemUser extends Users{
    private boolean admin;

    public SystemUser(String user, String pass, boolean admin){
        super(user, pass);
        this.admin=admin;
    }

    public boolean isAdmin() {
        return admin;
    }
}
