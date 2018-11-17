package models;

public class PaperLocation {

    private String title;
    private String address;

    public PaperLocation(String title, String address) {
        this.title = title;
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }
}