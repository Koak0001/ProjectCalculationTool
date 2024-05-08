package projectcalculationtool.service;

import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.repository.Repository;

import java.util.List;

@Service
public class Service {

    private final Repository repository;
    public Service(Repository repository) {this.repository = repository;}



    public void addProject(Project project) {repository.addNewProject(project);}
    public void addSubProject(SubProject subProject, int parent) {repository.addNewSubProject(subProject, parent);}

//    Call getProjects
    public List<Project> getProjects(int userId) {return repository.getProjects(userId);}
//     Call getProject
    public Project getProject (int projectId, boolean isSubProject) {return repository.getProject(projectId, isSubProject);}
//   Call getSubprojects
    public List<SubProject> getSubProjects(int projectId) {return repository.getSubProjects(projectId);}


//    TODO - Call verifyUser/login
//    TODO - Call getUserSitePermissions
//    TODO - Call createProject



//    TODO - Call getSubproject
//    TODO - Call createTask
//    TODO - Call getTasks
//    TODO - Call getTask
//    TODO - Call getUsers
//    TODO - Call setRole
//    TODO - Call getRole
//    TODO - Call getCollaborators
//    TODO - Call editUser
}

