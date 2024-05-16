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

//   View Projects
@GetMapping("projekter")
public String getAllProjects(Model model){
    User user = projectService.getLoggedInUser();
    int userId = user.getUserId();
    List<Project> projects = projectService.getProjects(userId);
    model.addAttribute("projects", projects);
    return "projekter";}

//   View Project
@GetMapping("/{projectName}")
public String getProject(@PathVariable int projectId, @PathVariable int userId, @PathVariable boolean isSubProject, Model model){
        List<SubProject> subProjects = projectService.getSubProjects(projectId);
        Project parent  = projectService.getProject(projectId);
        model.addAttribute("parent", parent);
        model.addAttribute("subprojects", subProjects);
        return "projekt";
}
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

//    TODO Create subproject
//    TODO Create Task
//    TODO View task

//    TODO Add collaborator/Set role
//    TODO View collaborators
//    TODO Edit collaborator
//    TODO View site users
//    TODO Edit site users
}
