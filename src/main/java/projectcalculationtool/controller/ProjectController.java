package projectcalculationtool.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import projectcalculationtool.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.Task;
import projectcalculationtool.service.ProjectService;


import java.util.List;
@Controller
@RequestMapping("oversigt")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


@PostMapping(value = "/check-login")
public String checkLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request, Model model) {
    System.out.println(username);
    System.out.println(password);
        projectService.login(username, password);
    if (projectService.getLoggedInUser() != null) {
        HttpSession session = request.getSession();
        session.setAttribute("userId", projectService.getLoggedInUser().getUserId());
        return "redirect:/oversigt/projekter";
    } else {
        model.addAttribute("loginError", "Username or password is incorrect");
        return "index";
    }
}

    @GetMapping(value = "/forside")
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return "index.html";
        } else {
            return "redirect:/";
        }
    }

//    TODO log out

//   View Projects for user
@GetMapping("projekter")
public String getAllProjects(Model model){
    User user = projectService.getLoggedInUser();
    int userId = user.getUserId();
    List<Project> projects = projectService.getProjects(userId);
    model.addAttribute("projects", projects);
    return "projekter";}

//   View Project's subprojects
@GetMapping("/{projectName}")
public String getProject(@PathVariable String projectName,
                         @RequestParam int projectId,
                         @RequestParam String userRole,
                         Model model){
    List<SubProject> subProjects = projectService.getSubProjects(projectId, userRole);
    model.addAttribute("projectName", projectName);
    model.addAttribute("projectId", projectId);
    model.addAttribute("role", userRole);
    model.addAttribute("subprojects", subProjects);
    return "projekt";
}
// View Subproject's tasks
@GetMapping("/{projectName}/opgaver")
public String getSubProject(@PathVariable String projectName,
                            @RequestParam int subProjectId,
                            @RequestParam String userRole,
                            Model model){
        List<Task> tasks = projectService.getTasks(subProjectId, userRole);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subProjectName", projectName);
        model.addAttribute("role", userRole);
        model.addAttribute("subProjectId", subProjectId);
        return "opgaver";
}
// View task
@GetMapping("/{projectName}/{taskName}")
public String getTask(@PathVariable String taskName, @RequestParam int subProjectId,
                      @RequestParam String userRole, @RequestParam int taskId,
                      Model model){
        Task task = projectService.getTask(taskId);
        model.addAttribute("task", task);
        model.addAttribute("taskName", taskName);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("role", userRole);
        return "opgave";

public String getProject(@PathVariable int projectId, @PathVariable int userId, @PathVariable boolean isSubProject, Model model){
        List<SubProject> subProjects = projectService.getSubProjects(projectId);
        Project parent  = projectService.getProject(projectId);
        model.addAttribute("parent", parent);
        model.addAttribute("subprojects", subProjects);
        return "projekt";

// Create project
@GetMapping("/nytprojekt")
public String showProjectForm(Model model) {
    model.addAttribute("project", new Project(""));
    return "nytprojekt";
}

@PostMapping("/nytprojekt")
public String addProject(@PathVariable int projectLeadId, @ModelAttribute("project") Project project) {
     return "redirect:/projekter";
    }
    // Create subproject
    @GetMapping("/{projectName}/opret_delprojekt")
    public String showSubProjectForm(@RequestParam int parentProjectId, @PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        model.addAttribute("projectId", parentProjectId);
        model.addAttribute("subproject", new SubProject(""));
        return "opret_delprojekt";
    }

    @PostMapping("/{projectName}/opret_delprojekt")
    public String addSubProject(@ModelAttribute("subproject") SubProject newSubProject, @RequestParam int parentProjectId) {
        projectService.addSubProject(newSubProject, parentProjectId);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/{subProjectName}/rediger_delprojekt/{subProjectId}")
    public String showUpdateSubProjectForm(@PathVariable int subProjectId, Model model) {
        SubProject subProject = projectService.getSubProject(subProjectId);
        model.addAttribute("subProject", subProject);
        return "updateSubProject";
    }

    @PostMapping("/{subProjectName}/rediger_delprojekt")
    public String updateSubProject(@ModelAttribute("subProject") SubProject subProject) {
        System.out.println("SubProject ID: " + subProject.getProjectId());
        System.out.println("SubProject Name: " + subProject.getProjectName());
        projectService.updateSubProject(subProject);
        return "redirect:/oversigt/projekter";
    }

@GetMapping("/{subProjectName}/opret_opgave")
public String showTaskForm(@RequestParam int parentProjectId, @PathVariable String subProjectName, Model model) {
    model.addAttribute("subProjectName", subProjectName);
    model.addAttribute("projectId", parentProjectId);
    model.addAttribute("task", new Task(""));
    return "opret_opgave";
}

    @PostMapping("/{subProjectName}/opret_opgave")
    public String addTask(@ModelAttribute("task") Task task, @RequestParam int parentProjectId) {
        projectService.addTask(task, parentProjectId);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/{taskName}/rediger_opgave/{taskId}")
    public String showUpdateTaskForm(@PathVariable int taskId, Model model) {
        Task task = projectService.getTask(taskId);
        model.addAttribute("task", task);
        return "updateTask";
    }
    @PostMapping("/{taskName}/rediger_opgave")
    public String updateTask(@ModelAttribute("task") Task task) {
        projectService.updateTask(task);
        return "redirect:/oversigt/projekter";
    }

//    TODO Add collaborator/Set role
//    TODO View collaborators
//    TODO Edit collaborator
//    TODO View site users
//    TODO Edit site users
}
