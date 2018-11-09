package forms;

public class UserProfileForm {
    private String username;
    private String title;
    private String affliation;
    private String email;
    private String RI;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRI() {
        return RI;
    }

    public void setRI(String RI) {
        this.RI = RI;
    }

    public String getAffliation() {
        return affliation;
    }

    public void setAffliation(String affliation) {
        this.affliation = affliation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
