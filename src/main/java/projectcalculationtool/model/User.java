package projectcalculationtool.model;

public class User {
    String name;
    String surName;
    String email;
    String country;
    String login;
    private String password;
    private String username;
    boolean isAdmin;
    boolean isCTO;
    boolean isProjectLead;
    int userId;


    public User(int userId, String name, String surName, String login, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.surName = surName;
        this.login = login;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public String getName() {return name;}
    public String getSurName() {return surName;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getCountry() {return country;}
    public String getLogin() {return login;}
    public String getPassword() {return password;}
    public boolean isAdmin() {return isAdmin;}
    public boolean isCTO() {return isCTO;}
    public boolean isProjectLead() {return isProjectLead;}
    public int getUserId() {return userId;}
    public void setName(String name) {this.name = name;}
    public void setSurName(String surName) {this.surName = surName;}
    public void setUsername(String username) {this.username = username;}
    public void setEmail(String email) {this.email = email;}
    public void setCountry(String Country) {this.country = country;}
    public void setLogin(String login) {this.login = login;}
    public void setPassword(String password) {this.password = password;}
    public void setAdmin(boolean isAdmin) {this.isAdmin = isAdmin;}
    public void setCTO(boolean isCTO) {this.isCTO = isCTO;}
    public void setProjectLead(boolean projectLead) {this.isProjectLead = projectLead;}
    public void setUserId(int userId) {this.userId = userId;}

}

