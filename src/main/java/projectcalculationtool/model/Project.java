package projectcalculationtool.model;

import java.util.Date;

public class Project {
    private String projectName;
    private int projectID;
    private Date deadline;
    private int totalHours;
    boolean isArchived;

    public Project(String projectName, Date deadline) {
        this.projectName = projectName;
        this.deadline = deadline;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }
}
