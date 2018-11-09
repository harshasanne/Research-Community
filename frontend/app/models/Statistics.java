package models;

import java.util.*;

public class Statistics {


    private String followerName;
    private String numberOfFollowers;
    private String numberOfPapers;
    private String numberOfKeywords;
    private String Keywords;

    // public Statistics() {}

    public Statistics(String followerName, String numberOfFollowers, String numberOfPapers, String numberOfKeywords, String Keywords) {
        this.followerName = followerName;
        this.numberOfFollowers = numberOfFollowers;
        this.numberOfPapers = numberOfPapers;
        this.numberOfKeywords = numberOfKeywords;
        this.Keywords = Keywords;
    }

    // public Paper(String title) {
    //     this.title = title;
    // }

    public String getfollowerName() {
        return followerName;
    }

    public String getnumberOfFollowers() {
        return numberOfFollowers;
    }

    public String getnumberOfPapers() {
        return numberOfPapers;
    }

    public String getnumberOfKeywords() {
        return numberOfKeywords;
    }

    public String getKeywords() {
        return Keywords;
    }

    public void setfollowerName(String followerName) {
        this.followerName = followerName;
    }

    public void setnumberOfFollowers(String numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    public void setnumberOfPapers(String numberOfPapers) {
        this.numberOfPapers = numberOfPapers;
    }

    public void setnumberOfKeywords(String numberOfKeywords) {
        this.numberOfKeywords = numberOfKeywords;
    }

    public void setKeywords(String Keywords) {
        this.Keywords = Keywords;
    }
}
