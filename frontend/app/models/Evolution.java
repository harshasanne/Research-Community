package models;

import java.util.List;

public class Evolution  {

    private String year;

    private List<Keyword> keywords;


    public Evolution(String y,List<Keyword> k){
        this.year=y;

        this.keywords=k;

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
