package projectcalculationtool.model;

public class Task {
    private String taskName;
    private String taskDescription;
    private int taskId;
    private int projectId;
    private int hours;
    public Task(String taskName, String taskDescription, int projectId, int hours) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.projectId = projectId;
        this.hours = hours;
    }
}
