package models;

public class Paper {
    private String title;
    public int startYear;
    public int endYear;
    public String journalName;



    public Paper() {
        
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getstartYear() {
        return startYear;
    }

    public void setstartYear(int startYear) {
        this.startYear = startYear;
    }
    
    public int getendYear() {
        return endYear;
    }

    public void setendYear(int endYear) {
        this.endYear = endYear;
    }
     public String getjournalName() {
        return journalName;
    }

    public void setjournalName(String journalName) {
        this.journalName = journalName;
    }
}

