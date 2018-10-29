package forms;

import play.data.validation.Constraints;



import play.data.validation.Constraints.Validatable;

public class LogInForm  {

    @Constraints.Required
    protected String userId;
    @Constraints.Required
    protected String password;

    protected String RI;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRI() {
        return RI;
    }

    public void setRI(String RI) {
        this.RI = RI;
    }
}