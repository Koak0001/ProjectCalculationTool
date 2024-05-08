package projectcalculationtool.repository;

import org.springframework.beans.factory.annotation.Value;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.Role;
import projectcalculationtool.model.SubProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Repository
public class Repository {
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
public List<SubProject> getSubProjects(int projectId) {
    List<SubProject> subprojects = new ArrayList<>();
    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        String sql = "SELECT P.SubprojectId, P.SubprojectName, P.Hours " +
                "FROM Project_Subproject P " +
                "WHERE projectId = ?";
        PreparedStatement psts = con.prepareStatement(sql);
        psts.setInt(1, projectId);
        ResultSet resultSet = psts.executeQuery();
        while (resultSet.next()) {
            int subprojectId = resultSet.getInt("subprojectId");
            String subprojectName = resultSet.getString("subprojectName");
            SubProject subproject = new SubProject(subprojectName, projectId);
            subproject.setProjectId(projectId);
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
public Project getProject(int projectId, boolean isSubProject) {
    Project project = null;
    try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        String tableName = isSubProject ? "SubProject" : "Project";
        String columnName = isSubProject ? "SubProjectName" : "ProjectName";

        String sql = "SELECT * FROM " + tableName + " WHERE " + (isSubProject ? "subProjectId" : "projectId") + " = ?";
        PreparedStatement psts = con.prepareStatement(sql);
        psts.setInt(1, projectId);
        ResultSet resultSet = psts.executeQuery();
        if (resultSet.next()) {
            String projectName = resultSet.getString(columnName);
            project = new Project(projectName);
            project.setProjectId(projectId);
        }
    } catch (SQLException e) {
        System.out.println("Project not located");
        e.printStackTrace();
    }
    return project;
}
//add Subproject:

    public void addNewSubProject(Project newProject, int parentId) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            con.setAutoCommit(false); // Start a transaction

            // Insert into Subproject table
            String sql = "INSERT INTO Subproject (ProjectName) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, newProject.getProjectName());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            int subProjectId = -1;
            if (rs.next()) {
                subProjectId = rs.getInt(1);
                newProject.setProjectId(subProjectId);
            }

            // Insert into Project_Subproject table
            if (subProjectId != -1) {
                sql = "INSERT INTO Project_Subproject (ProjectID, SubProjectID) VALUES (?, ?)";
                pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, parentId);
                pstmt.setInt(2, subProjectId);
                pstmt.executeUpdate();
            }

            con.commit(); // Commit the transaction
            con.setAutoCommit(true); // Reset auto-commit mode
        } catch (SQLException e) {
            System.out.println("Error adding new subproject");
            e.printStackTrace();
            // Rollback the transaction if an error occurs
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction");
                ex.printStackTrace();
            }
        } finally {
            // Close the connection in the finally block to ensure it's always closed
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing connection");
                ex.printStackTrace();
            }
        }
    }




//    TODO  - getTasks
//    TODO  - getTask
//    TODO  - getUsers
//    TODO  - getHoursTask
//    TODO  - getHoursSubProject
//    TODO  - getHoursTotal
//    TODO  - getHoursPerDay
}