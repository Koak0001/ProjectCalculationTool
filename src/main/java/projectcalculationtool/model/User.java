package projectcalculationtool.model;

public class User {
    String name;
    String surName;
    String email;
    String Country;
    String login;
    String password;
    boolean isAdmin;
    boolean isCTO;
    boolean isProjectLead;
    int userId;


    public User(String name, String surName, String login, String password) {
        this.name = name;
        this.surName = surName;
        this.login = login;
        this.password = password;
    }
    public String getName() {return name;}
    public String getSurName() {return surName;}
    public String getEmail() {return email;}
    public String getCountry() {return Country;}
    public String getLogin() {return login;}
    public String getPassword() {return password;}
    public boolean isAdmin() {return isAdmin;}
    public boolean isCTO() {return isCTO;}
    public boolean isProjectLead() {return isProjectLead;}
    public int getUserId() {return userId;}
    public void setName(String name) {this.name = name;}
    public void setSurName(String surName) {this.surName = surName;}
    public void setEmail(String email) {this.email = email;}
    public void setCountry(String Country) {this.Country = Country;}
    public void setLogin(String login) {this.login = login;}
    public void setPassword(String password) {this.password = password;}
    public void setAdmin(boolean isAdmin) {this.isAdmin = isAdmin;}
    public void setCTO(boolean isCTO) {this.isCTO = isCTO;}
    public void setProjectLead(boolean projectLead) {this.isProjectLead = projectLead;}
    public void setUserId(int userId) {this.userId = userId;}

}

