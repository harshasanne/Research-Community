package models;

public class User {
    public String username;
    public String password;

    public Long getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(Long lastVisited) {
        this.lastVisited = lastVisited;
    }

    public Long lastVisited;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
