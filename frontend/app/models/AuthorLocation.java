package models;

public class AuthorLocation {

    private String name;
    private String address;

    public AuthorLocation (String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}