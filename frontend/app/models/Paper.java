package models;

public class Paper {
    public String title;
    public String journalName;

    public int startYear;
    public int endYear;

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

