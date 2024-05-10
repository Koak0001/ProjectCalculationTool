package projectcalculationtool.service;

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

//    Call getProjects
    public List<Project> getProjects(int userId) {return projectRepository.getProjects(userId);}
//     Call getProject
    public Project getProject (int projectId) {return projectRepository.getProject(projectId);}
//   Call getSubprojects
    public List<SubProject> getSubProjects(int projectId, String role) {return projectRepository.getSubProjects(projectId, role);}
//    TODO - Call getTasks
    public List<Task> getTasks(int projectId, String role) {return projectRepository.getTasks(projectId, role);}

//    TODO - Call verifyUser/login
//    TODO - Call getUserSitePermissions
//    TODO - Call createProject



//    TODO - Call getSubproject
//    TODO - Call createTask

//    TODO - Call getTask
//    TODO - Call getUsers
//    TODO - Call setRole
//    TODO - Call getRole
//    TODO - Call getCollaborators
//    TODO - Call editUser
}

