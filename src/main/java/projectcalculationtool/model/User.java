package projectcalculationtool.model;

public class User {
    private String userLogin;
    private String email;
    private String location;
    private String projectRole;
    private String password;
    private String userName;
    private int roleId;
    private boolean isAdmin;
    private boolean isProjectLead;
    private int userId;


    public User(String userLogin, String password) {
        this.userLogin = userLogin;
        this.password = password;
    }

    public User() {

    }

    public String getUserName() {return userName;}
    public String getEmail() {return email;}
    public String getLocation() {return location;}
    public String getLogin() {return projectRole;}
    public String getPassword() {return password;}
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    public boolean isProjectLead() { return isProjectLead; }
    public void setProjectLead(boolean projectLead) { this.isProjectLead = projectLead; }


    public int getUserId() {return userId;}

    public void setUserName(String userName) {this.userName = userName;}
    public void setEmail(String email) {this.email = email;}
    public void setLocation(String location) {this.location = location;}
    public void setLogin(String login) {this.projectRole = login;}
    public void setPassword(String password) {this.password = password;}

    public void setUserId(int userId) {this.userId = userId;}
    public String getProjectRole() {return projectRole;}
    public void setProjectRole(String projectRole) {this.projectRole = projectRole;}
    public int getRoleId() {return roleId;}
    public void setRoleId(int roleId) {this.roleId = roleId;}

    public String getAdminStatus() {
        return isAdmin ? " admin " : " nej ";
    }

    public String getProjectLeadStatus() {
        return isProjectLead ? " projektleder " : " nej ";
    }
}

