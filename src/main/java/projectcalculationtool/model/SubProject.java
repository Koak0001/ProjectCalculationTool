package projectcalculationtool.model;

import java.util.Date;

public class SubProject extends Project {

    private int parentProjectId;

    public SubProject(String projectName, Date deadline, int parentProjectId) {
        super(projectName, deadline);
        this.parentProjectId = parentProjectId;
    }
    public int getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

}
