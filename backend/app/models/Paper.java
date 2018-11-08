package models;

import java.util.*;

public class Paper {

    private Long index;

    private String author;
    private String title;
    private String abstract_;
    private String journal;
    private String year;

    public Paper() {}

    public Paper(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstract() {
        return abstract_;
    }

    public String getJournal() {
        return journal;
    }

    public String getYear() {
        return year;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAbstract(String abstract_) {
        this.abstract_ = abstract_;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
