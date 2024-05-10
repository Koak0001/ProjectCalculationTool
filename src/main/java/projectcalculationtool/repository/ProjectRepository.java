package projectcalculationtool.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectRepository {
    @Value("${spring.datasource.url}")
    String dbUrl;
    @Value("${spring.datasource.username}")
    String dbUsername;
    @Value("${spring.datasource.password}")
    String dbPassword;



//    TODO - login/verifyUser
//    TODO  - logout

//    getProjects
public List<Project> getProjects(int userId) {
    List<Project> projects = new ArrayList<>();
    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        String sql = "SELECT P.projectId, P.projectName, R.RoleTitle " +
                "FROM User_Project_Role UPR " +
                "JOIN Project P ON UPR.ProjectID = P.ProjectID " +
                "JOIN Role R ON UPR.RoleID = R.RoleID " +
                "WHERE UPR.UserID = ?";
        PreparedStatement psts = con.prepareStatement(sql);
        psts.setInt(1, userId);
        ResultSet resultSet = psts.executeQuery();
        while (resultSet.next()) {
            int projectId = resultSet.getInt("projectId");
            String projectName = resultSet.getString("projectName");
            String roleTitle = resultSet.getString("RoleTitle");
            Project project = new Project(projectName);
            project.setProjectId(projectId);
            project.setUserRole(roleTitle);
            projects.add(project);
        }
    } catch (SQLException e) {
        System.out.println("Database not connected");
        e.printStackTrace();
    }
    return projects;
}
// Get subProjects
public List<SubProject> getSubProjects(int projectId, String userRole) {
    List<SubProject> subprojects = new ArrayList<>();
    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        String sql = "SELECT P.SubprojectId, P.SubprojectName, P.Hours, P.Deadline " +
                "FROM Project_Subproject PS " +
                "JOIN Subproject P ON PS.SubprojectId = P.SubprojectId " +
                "WHERE PS.ProjectId = ?";
        PreparedStatement psts = con.prepareStatement(sql);
        psts.setInt(1, projectId);
        ResultSet resultSet = psts.executeQuery();
        while (resultSet.next()) {
            int subprojectId = resultSet.getInt("SubprojectId");
            String subprojectName = resultSet.getString("SubprojectName");
            int hours = resultSet.getInt("Hours");

            SubProject subproject = new SubProject(subprojectName, projectId);
            Date deadline = resultSet.getDate("Deadline");
            subproject.setDeadline(deadline);

            subproject.setProjectId(subprojectId);
            subproject.setHours(hours);

            subproject.setParentProjectId(projectId);
            subproject.setUserRole(userRole);

            subprojects.add(subproject);
        }
    } catch (SQLException e) {
        System.out.println("Database not connected");
        e.printStackTrace();
    }
    return subprojects;
}
//    Create Project
public void addNewProject(Project newProject) {
    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        String sql = "INSERT INTO Project (ProjectName) VALUES (?)";
        PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, newProject.getProjectName());
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            int projectId = rs.getInt(1);
            newProject.setProjectId(projectId);
        }
    } catch (SQLException e) {
        System.out.println("Error adding new project");
        e.printStackTrace();
    }
}

//    getProject
public Project getProject(int projectId) {
    Project project = null;
    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        String sql = "SELECT * FROM Project WHERE projectId = ?";
        PreparedStatement psts = con.prepareStatement(sql);
        psts.setInt(1, projectId);
        ResultSet resultSet = psts.executeQuery();
        if (resultSet.next()) {
            String projectName = resultSet.getString("ProjectName");
            project = new Project(projectName);
            project.setProjectId(projectId);
        } else {
            // If project not found in Project table, check SubProject table
            sql = "SELECT * FROM SubProject WHERE subProjectId = ?";
            psts = con.prepareStatement(sql);
            psts.setInt(1, projectId);
            resultSet = psts.executeQuery();
            if (resultSet.next()) {
                String projectName = resultSet.getString("SubProjectName");
                project = new Project(projectName);
                project.setProjectId(projectId);
            }
        }
    } catch (SQLException e) {
        System.out.println("Project not located");
        e.printStackTrace();
    }
    return project;
}

//add Subproject:

    public void addNewSubProject(SubProject newProject, int parentId) {
        String insertSubprojectSql = "INSERT INTO Subproject (SubprojectName) VALUES (?)";
        String insertProjectSubprojectSql = "INSERT INTO Project_Subproject (ProjectID, SubprojectID) VALUES (?, ?)";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement insertSubprojectStmt = con.prepareStatement(insertSubprojectSql);
             PreparedStatement insertProjectSubprojectStmt = con.prepareStatement(insertProjectSubprojectSql)) {

            // Insert into Subproject
            insertSubprojectStmt.setString(1, newProject.getProjectName());
            insertSubprojectStmt.executeUpdate();

            // Get subproject ID
            int newSubProjectId = getLastInsertedId(con);

            // Insert into Project_Subproject
            insertProjectSubprojectStmt.setInt(1, parentId);
            insertProjectSubprojectStmt.setInt(2, newSubProjectId);
            insertProjectSubprojectStmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding new subproject");
            e.printStackTrace();
        }
    }

    private int getLastInsertedId(Connection con) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT LAST_INSERT_ID()")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve last inserted ID");
    }
//    TODO  - getTasks

    public List<Task> getTasks(int subProjectId, String userRole) {
        List<Task> tasks = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT T.TaskId, T.TaskName, T.Hours " +
                    "FROM Subproject_Task ST " +
                    "JOIN Task T ON ST.TaskId = T.TaskId " +
                    "WHERE ST.SubProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, subProjectId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                int taskId = resultSet.getInt("TaskId");
                String taskName = resultSet.getString("TaskName");
                int hours = resultSet.getInt("Hours");
                Task task = new Task(taskName, subProjectId);

                task.setTaskId(taskId);
                task.setHours(hours);
                task.setProjectId(subProjectId);
                task.setRole(userRole);
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Database not connected");
            e.printStackTrace();
        }
        return tasks;
    }

//    TODO  - getTask
//    TODO  - getUsers
//    TODO  - getHoursTask
//    TODO  - getHoursSubProject
//    TODO  - getHoursTotal
//    TODO  - getHoursPerDay
}