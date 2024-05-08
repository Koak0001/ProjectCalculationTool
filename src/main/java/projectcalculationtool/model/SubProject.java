package projectcalculationtool.model;

import java.util.Date;

public class SubProject extends Project {

    private int parentProjectId;

    public SubProject(String projectName, int parentProjectId) {
        super(projectName);
        this.parentProjectId = parentProjectId;
    }
    public int getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

}
