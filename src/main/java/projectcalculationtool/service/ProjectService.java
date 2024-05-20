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


    //  Project calls
    public void addProject(Project project, int projectLeadId) {projectRepository.addNewProject(project, projectLeadId);}
    public void updateProject(Project project) {projectRepository.updateProject(project);}
    public void archiveProject(int projectId, boolean isArchived) {projectRepository.archiveProject(projectId,isArchived);}
    public Project getProject (int projectId) {return projectRepository.getProject(projectId);}
    public List<Project> getProjects(int userId, boolean archived) {return projectRepository.getProjects(userId, archived);}

    //  SubProject calls
    public void addSubProject(SubProject subProject, int parent) {projectRepository.addNewSubProject(subProject, parent);}
    public void updateSubProject(SubProject subProject) {projectRepository.updateSubProject(subProject);}
    public SubProject getSubProject(int subProjectId){return (SubProject) projectRepository.getSubProject(subProjectId);}
    public List<SubProject> getSubProjects(int projectId, String role) {return projectRepository.getSubProjects(projectId, role);}

    // Task calls
    public void addTask(Task task, int parent) {projectRepository.addNewTask(task, parent);}
    public void updateTask (Task task) {projectRepository.updateTask(task);}
    public Task getTask (int taskId) {return projectRepository.getTask(taskId);}
    public List<Task> getTasks(int projectId, String role) {return projectRepository.getTasks(projectId, role);}

    // User calls and functionality
    public void login(String username, String password) {projectRepository.login(username, password);}
    public User getLoggedInUser() {return projectRepository.getLoggedInUser();}
    public List<User> getUsers() {return projectRepository.getUsers();}
    public User getUser(int userId) {return projectRepository.getUser(userId);}


//    TODO - Call editUser
//    TODO - Call getUserSitePermissions
//    TODO - Call getProjectCollaborators

}

