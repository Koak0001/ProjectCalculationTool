package projectcalculationtool.model;

public class Role {
    private String title;
    private int roleId;
    public Role(String title, int roleId) {
        this.title = title;
        this.roleId = roleId;
    }
    public String getTitle() {return title;}
    public int getRoleId() {return roleId;}
}
