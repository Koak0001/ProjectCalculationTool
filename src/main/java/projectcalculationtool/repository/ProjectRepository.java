package projectcalculationtool.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.Task;
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

    private User loggedInUser = new User();
//    TODO  - logout
//    TODO  - getUsers

    public void login(String username, String password) {
    User user = checkUser(username, password);
    if (user != null) {
       this.loggedInUser = user;}
    }

    public User getLoggedInUser() {
    return loggedInUser;
}

    public List<Project> getProjects(int userId) {
        List<Project> projects = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT P.description, P.Deadline, P.projectId, P.projectName, R.RoleTitle, SUM(Temp.Total) AS TotalHours " +
                    "FROM User_Project_Role UPR " +
                    "JOIN Project P ON UPR.ProjectId = P.ProjectId " +
                    "JOIN UserRole R ON UPR.RoleId = R.RoleId " +
                    "LEFT JOIN (SELECT PS.ProjectId, ST.SubProjectId, SUM(T.Hours) AS Total " +
                    "           FROM Subproject_Task ST " +
                    "           JOIN Task T ON ST.TaskId = T.TaskId " +
                    "           JOIN Project_Subproject PS ON ST.SubProjectId = PS.SubProjectId " +
                    "           GROUP BY PS.ProjectId, ST.SubProjectId) AS Temp ON P.ProjectId = Temp.ProjectId " +
                    "WHERE UPR.UserId = ? " +
                    "GROUP BY P.ProjectId";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, userId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                String description = resultSet.getString("Description");
                Date deadline = resultSet.getDate("Deadline");
                int projectId = resultSet.getInt("projectId");
                String projectName = resultSet.getString("projectName");
                String roleTitle = resultSet.getString("RoleTitle");
                int totalHours = resultSet.getInt("TotalHours");

                Project project = new Project(projectName);

                project.setDescription(description);
                project.setDeadline(deadline);
                project.setProjectId(projectId);
                project.setUserRole(roleTitle);
                project.setHours(totalHours);
                projects.add(project);
            }
        } catch (SQLException e) {
            System.out.println("Database not connected");
            e.printStackTrace();
        }
        return projects;
    }

    public List<SubProject> getSubProjects(int projectId, String userRole) {
        List<SubProject> subprojects = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT P.SubprojectId, P.SubprojectName, Temp.Total AS Hours " +
                    "FROM Project_Subproject PS " +
                    "JOIN Subproject P ON PS.SubprojectId = P.SubprojectId " +
                    "LEFT JOIN (SELECT ST.SubProjectId, SUM(T.Hours) AS Total " +
                    "           FROM Subproject_Task ST " +
                    "           JOIN Task T ON ST.TaskId = T.TaskId " +
                    "           GROUP BY ST.SubProjectId) AS Temp ON PS.SubprojectId = Temp.SubProjectId " +
                    "WHERE PS.ProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, projectId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                int subprojectId = resultSet.getInt("SubprojectId");
                String subprojectName = resultSet.getString("SubprojectName");
                int hours = resultSet.getInt("Hours");

                SubProject subproject = new SubProject(subprojectName);

                subproject.setProjectId(subprojectId);
                subproject.setHours(hours);
                subproject.setUserRole(userRole);
                subprojects.add(subproject);
            }
        } catch (SQLException e) {
            System.out.println("Database not connected");
            e.printStackTrace();
        }
        return subprojects;
    }

    public void addNewProject(Project newProject, int projectLeadId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            con.setAutoCommit(false);

            String sql = "INSERT INTO Project (ProjectName, description, Deadline) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, newProject.getProjectName());
                pstmt.setString(2, newProject.getDescription());
                pstmt.setDate(3, new java.sql.Date(newProject.getDeadline().getTime()));
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int projectId = rs.getInt(1);
                        newProject.setProjectId(projectId);
                    }
                }
                insertUserProjectRole(con, projectLeadId, newProject.getProjectId(), 2);
                insertUserProjectRole(con, 1, newProject.getProjectId(), 1);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error adding new project");
            e.printStackTrace();
        }
    }

    private void insertUserProjectRole(Connection con, int userId, int projectId, int roleId) throws SQLException {
        String junctionSql = "INSERT INTO User_Project_Role (UserId, ProjectId, RoleId) VALUES (?, ?, ?)";
        try (PreparedStatement junctionPstmt = con.prepareStatement(junctionSql)) {
            junctionPstmt.setInt(1, userId);
            junctionPstmt.setInt(2, projectId);
            junctionPstmt.setInt(3, roleId);
            junctionPstmt.executeUpdate();
        }
    }
    public Project getProject(int projectId) {
        Project project = null;
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM Project WHERE ProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, projectId);
            ResultSet resultSet = psts.executeQuery();

            if (resultSet.next()) {
                String description = resultSet.getString("description");
                Date deadline = resultSet.getDate("Deadline");
                String projectName = resultSet.getString("ProjectName");

                project = new Project(projectName);

                project.setDescription(description);
                project.setDeadline(deadline);
                project.setProjectId(projectId);

                System.out.println("Project found: " + projectName);
            } else {
                System.out.println("No project found with ID: " + projectId);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving project");
            e.printStackTrace();
        }
        return project;
    }

    public void updateProject(Project updatedProject) {
        System.out.println("Updating Project: " + updatedProject.getProjectId() + ", " + updatedProject.getProjectName());
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String updateProjectSql = "UPDATE Project SET ProjectName = ?, Description = ?, Deadline = ? WHERE ProjectId = ?";
            PreparedStatement pstmt = con.prepareStatement(updateProjectSql);
            pstmt.setString(1, updatedProject.getProjectName());
            pstmt.setString(2, updatedProject.getDescription());
            pstmt.setDate(3, new java.sql.Date(updatedProject.getDeadline().getTime()));
            pstmt.setInt(4, updatedProject.getProjectId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating project");
            e.printStackTrace();
        }
    }

    public SubProject getSubProject(int subProjectId) {
        SubProject subProject = null;
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM SubProject WHERE subProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, subProjectId);
            ResultSet resultSet = psts.executeQuery();
            if (resultSet.next()) {
                String subProjectName = resultSet.getString("SubProjectName");
                subProject = new SubProject(subProjectName);
                subProject.setProjectId(subProjectId);

            }
        } catch (SQLException e) {
            System.out.println("SubProject not located");
            e.printStackTrace();
        }
        return subProject;
    }


    public void addNewSubProject(SubProject newSubProject, int parentId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String insertSubprojectSql = "INSERT INTO Subproject (SubprojectName) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(insertSubprojectSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, newSubProject.getProjectName());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int subProjectId = rs.getInt(1);
                newSubProject.setProjectId(subProjectId);
            }
            String junctionSql = "INSERT INTO Project_Subproject (ProjectId, SubprojectId) VALUES (?, ?)";
            PreparedStatement junctionPstmt = con.prepareStatement(junctionSql);
            junctionPstmt.setInt(1, parentId);
            junctionPstmt.setInt(2, newSubProject.getProjectId());
            junctionPstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding new subproject");
            e.printStackTrace();
        }
    }
    public void updateSubProject(SubProject updatedSubProject) {
        System.out.println("Updating SubProject: " + updatedSubProject.getProjectId() + ", " + updatedSubProject.getProjectName());
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String updateSubProjectSql = "UPDATE Subproject SET SubprojectName = ? WHERE SubprojectId = ?";
            PreparedStatement pstmt = con.prepareStatement(updateSubProjectSql);
            pstmt.setString(1, updatedSubProject.getProjectName());
            pstmt.setInt(2, updatedSubProject.getProjectId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating subproject");
            e.printStackTrace();
        }
    }


    public void addNewTask(Task newTask, int parentId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String insertTaskSql = "INSERT INTO Task (TaskName, Hours) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertTaskSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, newTask.getTaskName());
            pstmt.setInt(2, newTask.getHours());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int taskId = rs.getInt(1);
                newTask.setTaskId(taskId);
            }
            String junctionSql = "INSERT INTO Subproject_Task (SubprojectId, TaskId ) VALUES (?, ?)";
            PreparedStatement junctionPstmt = con.prepareStatement(junctionSql);
            junctionPstmt.setInt(1, parentId);
            junctionPstmt.setInt(2, newTask.getTaskId());
            junctionPstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding new task");
            e.printStackTrace();
        }
    }


    public User checkUser(String username, String password) {
        User userLoggedIn = null;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM User WHERE username=? AND UserPassword=?";
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
                Task task = new Task(taskName);
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

    public Task getTask(int taskId) {
        Task task = null;
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM Task WHERE TaskId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, taskId);
            ResultSet resultSet = psts.executeQuery();
            if (resultSet.next()) {
                String taskName = resultSet.getString("TaskName");
                int hours = resultSet.getInt("Hours");
                task = new Task(taskName);
                task.setTaskId(taskId);
                task.setHours(hours);

            }
        } catch (SQLException e) {
            System.out.println("Task not located");
            e.printStackTrace();
    }
        return task;
}


    public void updateTask(Task updatedTask) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String updateTaskSql = "UPDATE Task SET TaskName = ?, Hours = ? WHERE TaskId = ?";
            PreparedStatement pstmt = con.prepareStatement(updateTaskSql);
            pstmt.setString(1, updatedTask.getTaskName());
            pstmt.setInt(2, updatedTask.getHours());
            pstmt.setInt(3, updatedTask.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating task");
            e.printStackTrace();
        }
    }
}