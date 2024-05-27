package projectcalculationtool.model;

public class Task {
    private String taskName;
    private String role;
    private String description = "Ingen beskrivelse";
    private int projectId;
    private int taskId;
    private int hours;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {this.taskName = taskName;}
    public int getProjectId() {return projectId;}


    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
    public String getRole() {
    return role;
}

    public void setRole(String role) {
    this.role = role;
}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}