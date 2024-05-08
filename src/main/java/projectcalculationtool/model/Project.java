package projectcalculationtool.model;

import java.util.Date;

public class Project {
    private String projectName;
    private String userRole;
    private int projectId;
    private Date deadline;
    private int totalHours;
    boolean isArchived;

    public Project(String projectName) {
        this.projectName = projectName;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    public int getProjectId() {
        return projectId;
    }

    public Date getDeadline() {
        return deadline;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public boolean isArchived() {
        return isArchived;
    }
    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
