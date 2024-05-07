package projectcalculationtool.model;

public class Task {
    private String taskName;
    private String taskDescription;
    private int taskId;
    private int hours;
    public Task(String taskName, String taskDescription, int hours) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.hours = hours;
    }
}
