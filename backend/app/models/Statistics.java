package models;

import java.util.*;

public class Statistics {


    private String followerName;
    private int numberOfPapers;
    private int numberOfKeywords;

    // public Statistics() {}

    public Statistics(String followerName, int numberOfPapers, int numberOfKeywords) {
        this.followerName = followerName;
        this.numberOfPapers = numberOfPapers;
        this.numberOfKeywords = numberOfKeywords;
    }

    // public Paper(String title) {
    //     this.title = title;
    // }

    public String getfollowerName() {
        return followerName;
    }

    public int getnumberOfPapers() {
        return numberOfPapers;
    }

    public int getnumberOfKeywords() {
        return numberOfKeywords;
    }

    public void setfollowerName(String followerName) {
        this.followerName = followerName;
    }

    public void setnumberOfPapers(int numberOfPapers) {
        this.numberOfPapers = numberOfPapers;
    }

    public void setnumberOfKeywords(int numberOfKeywords) {
        this.numberOfKeywords = numberOfKeywords;
    }
}
