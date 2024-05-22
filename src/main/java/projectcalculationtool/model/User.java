package projectcalculationtool.model;

public class User {
    String name;
    String email;
    String country;
    String projectRole;
    private String password;
    private String userName;
    boolean isAdmin;
    boolean isProjectLead;
    int userId;


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User() {

    }

    public String getName() {return name;}
    public String getUserName() {return userName;}
    public String getEmail() {return email;}
    public String getCountry() {return country;}
    public String getLogin() {return projectRole;}
    public String getPassword() {return password;}
    public boolean isAdmin() {return isAdmin;}
    public boolean isProjectLead() {return isProjectLead;}
    public int getUserId() {return userId;}
    public void setName(String name) {this.name = name;}
    public void setUserName(String userName) {this.userName = userName;}
    public void setEmail(String email) {this.email = email;}
    public void setCountry(String country) {this.country = country;}
    public void setLogin(String login) {this.projectRole = login;}
    public void setPassword(String password) {this.password = password;}
    public void setAdmin(boolean isAdmin) {this.isAdmin = isAdmin;}
    public void setProjectLead(boolean projectLead) {this.isProjectLead = projectLead;}
    public void setUserId(int userId) {this.userId = userId;}
    public String getProjectRole() {return projectRole;}
    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }
}

