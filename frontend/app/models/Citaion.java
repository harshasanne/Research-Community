package models;

import java.util.List;

public class Citaion  {
    
    private String title;

    private String year;

    private List<Keyword> keywords;


    public Citaion(String x,String y,List<Keyword> k){
        this.year=y;
        this.title = x;
        this.keywords=k;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String year) {
        this.title = title;
    }
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }
}
