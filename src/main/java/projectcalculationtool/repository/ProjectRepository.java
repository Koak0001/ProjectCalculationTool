package projectcalculationtool.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.User;

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
//    TODO  - logout
//    TODO  - getUsers

    private User loggedInUser = new User();

    public void login(String username, String password) {
    User user = checkUser(username, password);
    if (user != null) {
       this.loggedInUser = user;
    }
}

public User getLoggedInUser() {
    return loggedInUser;
}

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

            // Insert into Subproject table
            insertSubprojectStmt.setString(1, newProject.getProjectName());
            insertSubprojectStmt.executeUpdate();

            // Get the auto-generated subproject ID
            int newSubProjectId = getLastInsertedId(con);

            // Insert into Project_Subproject table
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

    public User checkUser(String username, String password) {
        User userLoggedIn = null;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM User WHERE username=? AND password=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userId");
                userLoggedIn = new User(username, password);
                userLoggedIn.setUserId(userId);
            }
        } catch (SQLException e) {
            System.out.println("Checking user failed");
            throw new RuntimeException(e);
        }
        return userLoggedIn;
    }
    //TODO udvid metode





}