package forms;

public class LoginForm {
    protected String username;
    protected String password;
    protected String RI;
    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}

    public String getPassword(){return password;}
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRI() {
        return RI;
    }

    public void setRI(String RI) {
        this.RI = RI;
    }
}
