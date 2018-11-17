package models;

public class PaperLocation {

    private String title;
    private String year;
    private String address;

    public PaperLocation(String title, String year, String address) {
        this.title = title;
        this.year = year;
        this.address = address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

}