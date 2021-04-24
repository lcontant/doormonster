package API.Model;

public class HashedPassword {
    public String password;
    public String salt;

    public HashedPassword(String password, String salt) {
        this.password = password;
        this.salt = salt;
    }
}
