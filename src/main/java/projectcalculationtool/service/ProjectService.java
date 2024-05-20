package projectcalculationtool.service;
import projectcalculationtool.model.User;
import org.springframework.stereotype.Service;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.Task;
import projectcalculationtool.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    public ProjectService(ProjectRepository projectRepository) {this.projectRepository = projectRepository;}


    public void addProject(Project project) {
        projectRepository.addNewProject(project);}
    public void addSubProject(SubProject subProject, int parent) {
        projectRepository.addNewSubProject(subProject, parent);}

    public void addTask(Task task, int parent) {
        projectRepository.addNewTask(task, parent);}

//    Call getProjects
    public List<Project> getProjects(int userId) {return projectRepository.getProjects(userId);}
//     Call getProject
    public Project getProject (int projectId) {return projectRepository.getProject(projectId);}
//   Call getSubprojects
    public List<SubProject> getSubProjects(int projectId, String role) {return projectRepository.getSubProjects(projectId, role);}
// Call getSubProject
    public SubProject getSubProject(int subProjectId){return (SubProject) projectRepository.getSubProject(subProjectId);}
    public void updateSubProject(SubProject subProject) {projectRepository.updateSubProject(subProject);}
//  Call verifyUser/login
    public void login(String username, String password) {projectRepository.login(username, password);}
    public User getLoggedInUser() {return projectRepository.getLoggedInUser();}
//  Call getTasks
    public List<Task> getTasks(int projectId, String role) {return projectRepository.getTasks(projectId, role);}
//  Call getTask
    public Task getTask (int taskId) {return projectRepository.getTask(taskId);}
    public void updateTask (Task task) {projectRepository.updateTask(task);}
    //  Call getUsers
    public List<User> getUsers() {return projectRepository.getUsers();}
    // Call getUser
    public User getUser(int userId) {return projectRepository.getUser(userId);}


//    TODO - Call getUserSitePermissions
//    TODO - Call createProject
//    TODO - Call setRole
//    TODO - Call getRole
//    TODO - Call getCollaborators
//    TODO - Call editUser
}

