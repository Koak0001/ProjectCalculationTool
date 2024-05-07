package projectcalculationtool.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectcalculationtool.model.Project;
import projectcalculationtool.model.Role;
import projectcalculationtool.model.SubProject;
import projectcalculationtool.model.Task;
import projectcalculationtool.service.Service;

import java.util.List;

public class Controller {
    private final Service service;

    @RequestMapping("oversigt")
    public Controller(Service service) {
        this.service = service;
    }

//    TODO index, with login option.
//    TODO index, logged in.
//    TODO log out

//   View Projects
@GetMapping("projekter")
public String getAllProjects(Model model){
    List<Project> projects = service.getProjects();
    model.addAttribute("projects", projects);
    return "projekter";}

//   View Project
@GetMapping("/{projectName}")
public String getProject(@PathVariable int projectId, @PathVariable int userId, Model model){
        List<SubProject> subProjects = service.getSubProjects(projectId);
        List<Task> tasks = service.getTasks(projectId);
        Role role = service.getRole(projectId, userId);
        model.addAttribute("role", role);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subprojects", subProjects);
        return "projekt";
}
// Create project

@GetMapping("nytprojekt")
public String showProjectForm(Model model) {
    model.addAttribute("project", new Project(""));
    return "nytprojekt";
}

    @PostMapping("nytprojekt")
    public String addProject(@PathVariable int projectLeadId, @ModelAttribute("project") Project project) {
        return "redirect:/projekter";
    }

//    TODO Create subproject
//    TODO Create Task
//    TODO View task

//    TODO Add collaborator/Set role
//    TODO View collaborators
//    TODO Edit collaborator
//    TODO View site users
//    TODO Edit site users
}
