package models;

public class PaperMeta {

    private String allData;
    private String journal;
    private String key;
    private String mdate;
    private String number;
    private String page;
    private String title;
    private String url;
    private String ee;
    private String volume;
    private String year;

    public PaperMeta(String allData)
    {
        this.allData = allData;
    }

    public String getJournal() {
        return journal;
    }
    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getMdate() {
        return mdate;
    }
    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) { this.number = number; }

    public String getPage() {
        return page;
    }
    public void setPage(String page) { this.page = page; }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getEe() {
        return ee;
    }
    public void setEe(String ee) { this.ee = ee; }

    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
}
