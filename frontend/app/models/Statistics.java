package models;

import java.util.*;

public class Statistics {


    private String followerName;
    private String numberOfPapers;
    private String numberOfKeywords;

    // public Statistics() {}

    public Statistics(String followerName, String numberOfPapers, String numberOfKeywords) {
        this.followerName = followerName;
        this.numberOfPapers = numberOfPapers;
        this.numberOfKeywords = numberOfKeywords;
    }

    public String getfollowerName() {
        return followerName;
    }

    public String getnumberOfPapers() {
        return numberOfPapers;
    }

    public String getnumberOfKeywords() {
        return numberOfKeywords;
    }

    public void setfollowerName(String followerName) {
        this.followerName = followerName;
    }

    public void setnumberOfPapers(String numberOfPapers) {
        this.numberOfPapers = numberOfPapers;
    }

    public void setnumberOfKeywords(String numberOfKeywords) {
        this.numberOfKeywords = numberOfKeywords;
    }
}
