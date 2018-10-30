package models;

public class LoginUser {
    protected String userId;
    protected String password;
    protected String ri;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRI() {
        return ri;
    }

    public void setRI(String ri) {
        this.ri = ri;
    }
}