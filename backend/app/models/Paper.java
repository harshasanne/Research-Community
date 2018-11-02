package models;

import java.util.*;

public class Paper {

    private Long index;

    private String title;
    private String abstact;
    private String journal;
    private String year;

    private List<String> authors;

    public Paper(String title) {
        this.title = title;
    }

    public Paper(String title, String _abstract, String journal, String year, String... _authors) {
        this.title = title;
        this.abstact = _abstract;
        this.journal = journal;
        this.year = year;
        this.authors = new ArrayList<String>();
        for (String author : _authors) {
            authors.add(author);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getAbstact() {
        return abstact;
    }

    public String getJournal() {
        return journal;
    }

    public String getYear() {
        return year;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
