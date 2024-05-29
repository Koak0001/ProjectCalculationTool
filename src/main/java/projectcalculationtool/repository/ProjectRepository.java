package projectcalculationtool.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    public List<Project> getProjects(int userId, boolean archived) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT P.*, R.RoleTitle " +
                "FROM User_Project_Role UPR " +
                "JOIN Project P ON UPR.ProjectId = P.ProjectId " +
                "JOIN UserRole R ON UPR.RoleId = R.RoleId " +
                "WHERE UPR.UserId = ?";
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String description = resultSet.getString("Description");
                Date deadline = resultSet.getDate("Deadline");
                int projectId = resultSet.getInt("projectId");
                String projectName = resultSet.getString("projectName");
                String roleTitle = resultSet.getString("RoleTitle");
                int totalHours = getTotalHoursForProject(projectId);

                Project project = new Project(projectName);
                project.setDescription(description);
                project.setDeadline(deadline);
                project.setProjectId(projectId);
                project.setUserRole(roleTitle);
                project.setHours(totalHours);

                boolean isArchived = resultSet.getBoolean("isArchived");
                project.setArchived(isArchived);

                if (project.isArchived() == archived) {
                    projects.add(project);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database not connected");
            e.printStackTrace();
        }
        return projects;
    }

    private int getTotalHoursForProject(int projectId) {
        int totalHours = 0;
        String sql = "SELECT SUM(T.Hours) AS TotalHours " +
                "FROM Subproject_Task ST " +
                "JOIN Task T ON ST.TaskId = T.TaskId " +
                "JOIN Project_Subproject PS ON ST.SubProjectId = PS.SubProjectId " +
                "WHERE PS.ProjectId = ?";
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                totalHours = resultSet.getInt("TotalHours");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total hours for project");
            e.printStackTrace();
        }
        return totalHours;
    }



    public List<SubProject> getSubProjects(int projectId, String userRole) {
        List<SubProject> subprojects = new ArrayList<>();
        String sql = "SELECT P.* " +
                "FROM Project_Subproject PS " +
                "JOIN Subproject P ON PS.SubprojectId = P.SubprojectId " +
                "WHERE PS.ProjectId = ?";
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int subprojectId = resultSet.getInt("SubprojectId");
                String subprojectName = resultSet.getString("SubprojectName");
                String description = resultSet.getString("Description");

                int hours = getTotalHoursForSubproject(subprojectId);

                SubProject subproject = new SubProject(subprojectName);
                subproject.setProjectId(subprojectId);
                subproject.setDescription(description);
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

    private int getTotalHoursForSubproject(int subprojectId) {
        int totalHours = 0;
        String sql = "SELECT SUM(T.Hours) AS TotalHours " +
                "FROM Subproject_Task ST " +
                "JOIN Task T ON ST.TaskId = T.TaskId " +
                "WHERE ST.SubProjectId = ?";
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, subprojectId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                totalHours = resultSet.getInt("TotalHours");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total hours for subproject");
            e.printStackTrace();
        }
        return totalHours;
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
                if (projectLeadId != 1){
                insertUserProjectRole(con, 1, newProject.getProjectId(), 1);
                }
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

    public void addUserToProject(int userId, int projectId, int roleId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            insertUserProjectRole(con, userId, projectId, roleId);

        } catch (SQLException e) {
            System.out.println("Error adding user to project");
            e.printStackTrace();
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
                boolean isArchived = resultSet.getBoolean("isArchived");

                project = new Project(projectName);

                project.setDescription(description);
                project.setDeadline(deadline);
                project.setArchived(isArchived);
                project.setProjectId(projectId);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving project");
            e.printStackTrace();
        }
        return project;
    }

    public void updateProject(Project updatedProject) {
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

    public void archiveProject(int projectId, boolean isArchived) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "UPDATE Project SET isArchived = ? WHERE ProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setBoolean(1, isArchived);
            psts.setInt(2, projectId);
            psts.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int taskId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            String stJunctionSql = "DELETE FROM Subproject_Task WHERE TaskId = ?";
            PreparedStatement stJunctionPs = con.prepareStatement(stJunctionSql);
            stJunctionPs.setInt(1, taskId);
            stJunctionPs.executeUpdate();

            String taskSql = "DELETE FROM Task WHERE TaskId = ?";
            PreparedStatement taskPs = con.prepareStatement(taskSql);
            taskPs.setInt(1, taskId);
            taskPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting task with ID: " + taskId);
            e.printStackTrace();
        }
    }


    public void deleteTasksForSubproject(int subprojectId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            String selectTasksSql = "SELECT TaskId FROM Subproject_Task WHERE SubprojectId = ?";
            PreparedStatement selectTasksPs = con.prepareStatement(selectTasksSql);
            selectTasksPs.setInt(1, subprojectId);
            ResultSet rs = selectTasksPs.executeQuery();

            // Delete tasks associated with Subproject
            while (rs.next()) {
                int taskId = rs.getInt("TaskId");
                deleteTask(taskId);
            }

            String psJunctionSql = "DELETE FROM Project_Subproject WHERE SubprojectId = ?";
            PreparedStatement psJunctionPs = con.prepareStatement(psJunctionSql);
            psJunctionPs.setInt(1, subprojectId);
            psJunctionPs.executeUpdate();

            String spSql = "DELETE FROM Subproject WHERE SubprojectId = ?";
            PreparedStatement subProjectPs = con.prepareStatement(spSql);
            subProjectPs.setInt(1, subprojectId);
            subProjectPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting subproject with ID: " + subprojectId);
            e.printStackTrace();
        }
    }

    public void deleteProject(int projectId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            String selectSubprojectsSql = "SELECT SubprojectId FROM Project_Subproject WHERE ProjectId = ?";
            PreparedStatement selectSubprojectsPs = con.prepareStatement(selectSubprojectsSql);
            selectSubprojectsPs.setInt(1, projectId);
            ResultSet rs = selectSubprojectsPs.executeQuery();

            // Delete subprojects associated with Project
            while (rs.next()) {
                int subprojectId = rs.getInt("SubprojectId");
                deleteTasksForSubproject(subprojectId);
            }

            String uprJunctionSql = "DELETE FROM User_Project_Role WHERE ProjectId = ?";
            PreparedStatement uprJunctionPs = con.prepareStatement(uprJunctionSql);
            uprJunctionPs.setInt(1, projectId);
            uprJunctionPs.executeUpdate();

            String pSql = "DELETE FROM Project WHERE ProjectId = ?";
            PreparedStatement projectPs = con.prepareStatement(pSql);
            projectPs.setInt(1, projectId);
            projectPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting project with ID: " + projectId);
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
                String description = resultSet.getString("Description");
                subProject = new SubProject(subProjectName);
                subProject.setDescription(description);
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
            String insertSubprojectSql = "INSERT INTO Subproject (SubprojectName, Description) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertSubprojectSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, newSubProject.getProjectName());
            pstmt.setString(2, newSubProject.getDescription());
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
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String updateSubProjectSql = "UPDATE Subproject SET SubprojectName = ?, Description = ? WHERE SubprojectId = ?";
            PreparedStatement pstmt = con.prepareStatement(updateSubProjectSql);
            pstmt.setString(1, updatedSubProject.getProjectName());
            pstmt.setString(2, updatedSubProject.getDescription());
            pstmt.setInt(3, updatedSubProject.getProjectId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating subproject");
            e.printStackTrace();
        }
    }


    public void addNewTask(Task newTask, int parentId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String insertTaskSql = "INSERT INTO Task (TaskName, Description, Hours) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertTaskSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, newTask.getTaskName());
            pstmt.setString(2, newTask.getDescription());
            pstmt.setInt(3, newTask.getHours());
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


    public User checkUser(String userLogin, String password) {
        User userLoggedIn = null;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM User WHERE UserLogin=? AND UserPassword=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userLogin);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("userId");
                boolean isAdmin = rs.getBoolean("isAdmin");
                boolean isProjectLead = rs.getBoolean("isProjectLead");
                userLoggedIn = new User(userLogin, password);
                userLoggedIn.setUserId(userId);
                userLoggedIn.setAdmin(isAdmin);
                userLoggedIn.setProjectLead(isProjectLead);
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
            String sql = "SELECT T.* " +
                    "FROM Subproject_Task ST " +
                    "JOIN Task T ON ST.TaskId = T.TaskId " +
                    "WHERE ST.SubProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, subProjectId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                int taskId = resultSet.getInt("TaskId");
                String taskName = resultSet.getString("TaskName");
                String description = resultSet.getString("Description");
                int hours = resultSet.getInt("Hours");
                Task task = new Task(taskName);
                task.setTaskId(taskId);
                task.setHours(hours);
                task.setDescription(description);
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
                String description = resultSet.getString("Description");
                int hours = resultSet.getInt("Hours");
                task = new Task(taskName);
                task.setTaskId(taskId);
                task.setDescription(description);
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
            String updateTaskSql = "UPDATE Task SET TaskName = ?, Hours = ?, Description = ? WHERE TaskId = ?";
            PreparedStatement pstmt = con.prepareStatement(updateTaskSql);
            pstmt.setString(1, updatedTask.getTaskName());
            pstmt.setInt(2, updatedTask.getHours());
            pstmt.setString(3, updatedTask.getDescription());
            pstmt.setInt(4, updatedTask.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating task");
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM User";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("UserId");
                String email = resultSet.getString("Email");
                String userLogin = resultSet.getString("UserLogin");
                String userPassword = resultSet.getString("UserPassword");
                String location = resultSet.getString("Location");
                String userName = resultSet.getString("UserName");
                boolean admin = resultSet.getBoolean("isAdmin");
                boolean projectLead = resultSet.getBoolean("isProjectLead");

                User newUser = new User();
                newUser.setUserId(userId);
                newUser.setEmail(email);
                newUser.setLogin(userLogin);
                newUser.setPassword(userPassword);
                newUser.setUserName(userName);
                newUser.setLocation(location);
                newUser.setAdmin(admin);
                newUser.setProjectLead(projectLead);

                users.add(newUser);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error");
            e.printStackTrace();
        }
        return users;
    }

    public User getUser(int userId) {
        User user = null;
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM User WHERE UserId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String email = resultSet.getString("Email");
                String login = resultSet.getString("UserLogin");
                String location = resultSet.getString("Location");
                String password = resultSet.getString("UserPassword");
                String userName = resultSet.getString("UserName");
                boolean isAdmin = resultSet.getBoolean("isAdmin");
                boolean isProjectLead = resultSet.getBoolean("isProjectLead");

                user = new User();
                user.setUserId(userId);
                user.setEmail(email);
                user.setLogin(login);
                user.setLocation(location);
                user.setPassword(password);
                user.setUserName(userName);
                user.setAdmin(isAdmin);
                user.setProjectLead(isProjectLead);
            }
        } catch (SQLException e) {
            System.out.println("Database connection error");
            e.printStackTrace();
        }
        return user;
    }


    public List<User> getAvailableUsers(int projectId) {
        List<User> availableUsers = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT * FROM User u WHERE u.UserName " +
                    "<> 'admin' AND NOT EXISTS (SELECT 1 FROM User_Project_Role upr " +
                    "WHERE upr.UserId = u.UserId AND upr.ProjectId = ?)";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, projectId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("UserId");
                String userName = resultSet.getString("UserName");
                String email = resultSet.getString("Email");
                String location = resultSet.getString("Location");

                User user = new User();

                user.setUserId(userId);
                user.setUserName(userName);
                user.setEmail(email);
                user.setLocation(location);
                availableUsers.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Available users not located");
            e.printStackTrace();
        }
        return availableUsers;
    }

    public List<User> getAssociatedUsers(int projectId) {
        List<User> associatedUsers = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT u.*, ur.* FROM User u " +
                    "JOIN User_Project_Role upr ON u.UserId = upr.UserId " +
                    "JOIN UserRole ur ON upr.RoleId = ur.RoleId " +
                    "WHERE upr.ProjectId = ?";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, projectId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("UserId");
                String userName = resultSet.getString("UserName");
                String email = resultSet.getString("Email");
                String location = resultSet.getString("Location");
                String roleTitle = resultSet.getString("RoleTitle");
                int roleId = resultSet.getInt("RoleId");

                User user = new User();

                user.setUserId(userId);
                user.setUserName(userName);
                user.setEmail(email);
                user.setLocation(location);
                user.setProjectRole(roleTitle);
                user.setRoleId(roleId);
                associatedUsers.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Users not located");
            e.printStackTrace();
        }
        return associatedUsers;
    }
    public void updateCollaboratorRole(int projectId, int userId, int roleId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String updateRoleSql = "UPDATE User_Project_Role SET RoleId = ? WHERE ProjectId = ? AND UserId = ?";
            PreparedStatement psts = con.prepareStatement(updateRoleSql);
            psts.setInt(1, roleId);
            psts.setInt(2, projectId);
            psts.setInt(3, userId);
        } catch (SQLException e) {
            System.out.println("Error updating user role");
            e.printStackTrace();
        }
    }

    public void removeCollaborator(int userId, int projectId, int roleId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            String deleteUprSql = "DELETE FROM User_Project_Role WHERE userId = ? AND projectId = ? AND RoleId = ?";
            PreparedStatement deleteUprPs = con.prepareStatement(deleteUprSql);
            deleteUprPs.setInt(1, userId);
            deleteUprPs.setInt(2, projectId);
            deleteUprPs.setInt(3, roleId);

            deleteUprPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting set from UPR");
            e.printStackTrace();
        }
    }
    public void updateUser(User updatedUser) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String getCurrentPasswordSql = "SELECT UserPassword FROM User WHERE UserId = ?";
            PreparedStatement getCurrentPasswordStmt = con.prepareStatement(getCurrentPasswordSql);
            getCurrentPasswordStmt.setInt(1, updatedUser.getUserId());
            ResultSet rs = getCurrentPasswordStmt.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("UserPassword");

                String updateUserSql = "UPDATE User SET UserLogin = ?, UserName = ?, UserPassword = ?, isAdmin = ?, isProjectLead = ?, Email = ?, Location = ? WHERE UserId = ?";
                PreparedStatement pstmt = con.prepareStatement(updateUserSql);
                pstmt.setString(1, updatedUser.getLogin());
                pstmt.setString(2, updatedUser.getUserName());
                if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
                    pstmt.setString(3, currentPassword);
                } else {
                    pstmt.setString(3, updatedUser.getPassword());
                }
                pstmt.setBoolean(4, updatedUser.isAdmin());
                pstmt.setBoolean(5, updatedUser.isProjectLead());
                pstmt.setString(6, updatedUser.getEmail());
                pstmt.setString(7, updatedUser.getLocation());
                pstmt.setInt(8, updatedUser.getUserId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error updating user");
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String junctionSql = "DELETE FROM User_Project_Role WHERE userId = ?";
            PreparedStatement junctionPs = con.prepareStatement(junctionSql);
            junctionPs.setInt(1, userId);
            junctionPs.executeUpdate();

            String userSql = "DELETE FROM User WHERE userId = ?";
            PreparedStatement userPs = con.prepareStatement(userSql);
            userPs.setInt(1, userId);
            userPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error user from database");
            e.printStackTrace();
        }
    }
    public void createUser(User newUser) {
        if (newUser.getLogin() == null || newUser.getLogin().isEmpty()) {
            newUser.setLogin(generateUniqueUserLogin(newUser.getUserName()));
        }

        String insertUserSql = "INSERT INTO User (UserLogin, UserName, UserPassword, isAdmin, isProjectLead, Email, Location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement pstmt = con.prepareStatement(insertUserSql)) {
            pstmt.setString(1, newUser.getLogin());
            pstmt.setString(2, newUser.getUserName());
            pstmt.setString(3, newUser.getPassword());
            pstmt.setBoolean(4, newUser.isAdmin());
            pstmt.setBoolean(5, newUser.isProjectLead());
            pstmt.setString(6, newUser.getEmail());
            pstmt.setString(7, newUser.getLocation());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating user");
            e.printStackTrace();
        }
    }

    public String generateUniqueUserLogin(String userName) {
        String[] nameParts = userName.split(" ");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full name must consist of at least a first and last name.");
        }

        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        String loginPrefix = (firstName.substring(0, 2) + lastName.substring(0, 2)).toLowerCase();
        int suffix = 1;
        String userLogin;

        do {
            userLogin = loginPrefix + String.format("%02d", suffix);
            suffix++;
        } while (userLoginExists(userLogin));

        return userLogin;
    }

    public boolean userLoginExists(String userLogin) {
        String checkUserLoginSql = "SELECT COUNT(*) FROM User WHERE UserLogin = ?";
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement pstmt = con.prepareStatement(checkUserLoginSql)) {
            pstmt.setString(1, userLogin);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<Project> adminGetProjects(int adminUserId) {
        List<Project> projects = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT ProjectId, ProjectName FROM Project " +
                    "WHERE ProjectId NOT IN (SELECT ProjectId FROM User_Project_Role WHERE UserId = ?)";
            PreparedStatement psts = con.prepareStatement(sql);
            psts.setInt(1, adminUserId);
            ResultSet resultSet = psts.executeQuery();
            while (resultSet.next()) {
                int projectId = resultSet.getInt("ProjectId");
                String projectName = resultSet.getString("ProjectName");
                Project project = new Project(projectName);
                project.setProjectId(projectId);
                projects.add(project);
            }
        } catch (SQLException e) {
            System.out.println("Retrieving projects for admin failed");
            e.printStackTrace();
        }
        return projects;
    }


    public void adminInsertIntoProject(int projectId, int userId){
        try (Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            insertUserProjectRole(con, userId, projectId, 2);
        } catch (SQLException e) {
            System.out.println("Failed to insert admin");
            throw new RuntimeException(e);
        }
    }
}
