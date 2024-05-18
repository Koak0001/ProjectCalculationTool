package projectcalculationtool.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Project {
    private String projectName;
    private String userRole;
    private String description = "Ingen beskrivelse";
    private int projectId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
    private int hours;
    boolean isArchived;

    public Project(String projectName) {
        this.projectName = projectName;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {this.projectName = projectName;}
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }
    public int getProjectId() {
        return projectId;
    }

    public Date getDeadline() {
        return deadline;
    }
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    public int getHours() {
        return hours;
    }

    public String getDescription() {return description;}
    public void setDescription(String projectDescription) {this.description = projectDescription;}

    public boolean isArchived() {
        return isArchived;
    }
    public void setIsArchived(boolean isArchived) {this.isArchived = isArchived;}
}
