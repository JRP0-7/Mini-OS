package mini.os.insta.model;

import java.io.Serializable;

public class ParejaDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    private String userA;
    private String userB;

    public ParejaDTO(String a, String b){
        this.userA = a;
        this.userB = b;
    }

    public String getUserA() { return userA; }
    public String getUserB() { return userB; }
}