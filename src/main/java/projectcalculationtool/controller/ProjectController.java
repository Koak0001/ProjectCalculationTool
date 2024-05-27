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


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("oversigt")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping(value = "/check-login")
    public String checkLogin(@RequestParam("login") String userLogin, @RequestParam("password") String password, HttpServletRequest request, Model model) {
        User user = projectService.login(userLogin, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user);
            return "redirect:/oversigt/projekter";
        } else {
            model.addAttribute("loginError", "Forkert brugernavn eller kodeord!");
            return "index";
        }
    }
    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("loggedInUser");
        }
        return null;
    }

    @GetMapping(value = "/forside")
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return "redirect:/oversigt/projekter";
        } else {
            return "redirect:/";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Session invalidated");
        } else {
            System.out.println("No session found");
        }
        return "redirect:/";
    }

    // view projects with roles, deadlines, and hours
    @GetMapping("projekter")
    public String getAllProjects(Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else {
            int userId = user.getUserId();
            List<Project> projects = projectService.getProjects(userId, false);
            model.addAttribute("projects", projects);
            model.addAttribute("user", user);
            return "projekter";
        }
    }

    @GetMapping("arkiv")
    public String getArchivedProjects(Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {return "redirect:/";}
        else {
        int userId = user.getUserId();
        List<Project> projects = projectService.getProjects(userId, true);
        model.addAttribute("projects", projects);
        return "archive";}
    }

    @PostMapping("/arkiver_projekt")
    public String archiveProject(@RequestParam int projectId, @RequestParam boolean isArchived) {
        projectService.archiveProject(projectId, isArchived);
        return "redirect:/oversigt/arkiv";
    }

    @PostMapping("/slet_projekt/{projectName}")
    public String deleteProject(@RequestParam int projectId) {
        projectService.deleteProject(projectId);
        return "redirect:/oversigt/arkiv";
    }

    //   View project with subprojects
    @GetMapping("/{projectName}")
    public String getProject(@RequestParam int projectId,
                             @RequestParam String userRole,
                             Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {return "redirect:/";}
        else {
            Project project = projectService.getProject(projectId);
            List<SubProject> subProjects = projectService.getSubProjects(projectId, userRole);
            model.addAttribute("project", project);
            model.addAttribute("projectId", projectId);
            model.addAttribute("role", userRole);
            model.addAttribute("subprojects", subProjects);
            return "projekt";
        }
    }

    // View subproject with tasks
    @GetMapping("/{projectName}/opgaver")
    public String getSubProject(@PathVariable String projectName,
                                @RequestParam int subProjectId,
                                @RequestParam String userRole, @RequestParam boolean archived,
                                Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null){return "redirect:/";}
        else {
            List<Task> tasks = projectService.getTasks(subProjectId, userRole);
            model.addAttribute("tasks", tasks);
            model.addAttribute("subProjectName", projectName);
            model.addAttribute("role", userRole);
            model.addAttribute("archived", archived);
            model.addAttribute("subProjectId", subProjectId);
            return "opgaver";
        }
    }

    // View task
    @GetMapping("/{projectName}/{taskName}")
    public String getTask(@PathVariable String taskName, @RequestParam int subProjectId,
                          @RequestParam String userRole, @RequestParam int taskId, @RequestParam boolean archived,
                          Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null){return "redirect:/";}
        else {
        Task task = projectService.getTask(taskId);
        model.addAttribute("task", task);
        model.addAttribute("taskName", taskName);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("role", userRole);
        model.addAttribute("archived", archived);
        return "opgave";}
    }

    @GetMapping("/nytprojekt")
    public String showProjectForm(Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null){
            return "redirect:/";
        } else {
            model.addAttribute("project", new Project(""));
            if (projectService.getLoggedInUser(request).isProjectLead()) {
                return "new_project";
            } else return "no_permission";}
    }

    @PostMapping("/nytprojekt")
    public String addProject(@ModelAttribute("project") Project project, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null){return "redirect:/";}
        else {
        int projectLeadId = user.getUserId();
        projectService.addProject(project, projectLeadId);
        return "redirect:/oversigt/projekter";}
        }

    @GetMapping("/{projectName}/rediger_projekt/{projectId}")
    public String showUpdateProjectForm(@PathVariable int projectId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else {
            Project project = projectService.getProject(projectId);
            model.addAttribute("project", project);
            return "updateProject"; }
    }

    @PostMapping("/{projectName}/rediger_projekt")
    public String updateProject(@ModelAttribute("project") Project project) {

        projectService.updateProject(project);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/{projectName}/opret_delprojekt")
    public String showSubProjectForm(@RequestParam int parentProjectId, @PathVariable String projectName, Model
            model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {return "redirect:/";}
        else {
            model.addAttribute("projectName", projectName);
            model.addAttribute("projectId", parentProjectId);
            model.addAttribute("subproject", new SubProject(""));
            return "opret_delprojekt";
        }
    }

    @PostMapping("/{projectName}/opret_delprojekt")
    public String addSubProject(@ModelAttribute("subproject") SubProject newSubProject,
                                @RequestParam int parentProjectId) {
        projectService.addSubProject(newSubProject, parentProjectId);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/{subProjectName}/rediger_delprojekt/{subProjectId}")
    public String showUpdateSubProjectForm(@PathVariable int subProjectId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {return "redirect:/";}
        else {
        SubProject subProject = projectService.getSubProject(subProjectId);
        model.addAttribute("subProject", subProject);
        return "updateSubProject";}
    }

    @PostMapping("/{subProjectName}/rediger_delprojekt")
    public String updateSubProject(@ModelAttribute("subProject") SubProject subProject) {
        projectService.updateSubProject(subProject);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/{subProjectName}/opret_opgave")
    public String showTaskForm(@RequestParam int parentProjectId,
                               @PathVariable String subProjectName, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {return "redirect:/";}
        else {
            model.addAttribute("subProjectName", subProjectName);
            model.addAttribute("projectId", parentProjectId);
            model.addAttribute("task", new Task(""));
            return "opret_opgave";}
    }

    @PostMapping("/{subProjectName}/opret_opgave")
    public String addTask(@ModelAttribute("task") Task task, @RequestParam int parentProjectId) {
        projectService.addTask(task, parentProjectId);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/{taskName}/rediger_opgave/{taskId}")
    public String showUpdateTaskForm(@PathVariable int taskId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {return "redirect:/";}
        else {
            Task task = projectService.getTask(taskId);
            model.addAttribute("task", task);
            return "updateTask";
        }
    }
    @PostMapping("/{taskName}/rediger_opgave")
    public String updateTask(@ModelAttribute("task") Task task) {
        projectService.updateTask(task);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("/alle_brugere")
    public String getAllUsers(Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else {
            List<User> users = projectService.getUsers();
            model.addAttribute("users", users);
            if (user.isAdmin()) {
                return "users";
            } else return "no_permission";
        }
    }
    @GetMapping("/bruger/{userId}")
    public String getUser(@PathVariable("userId") int userId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else {
            User userToView = projectService.getUser(userId);
            model.addAttribute("user", userToView);
            return "user";
        }
    }

    @GetMapping("{ProjectName}/tilfoej_medlem")
    public String getAvailableUsers(@RequestParam int projectId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else {
            List<User> users = projectService.getAvailableUsers(projectId);
            Project project = projectService.getProject(projectId);
            model.addAttribute("project", project);
            model.addAttribute("users", users);
            return "addMemberToProject";
        }
    }

    @PostMapping("{ProjectName}/tilfoej_medlem")
    public String addCollaborator(@RequestParam int projectId, @RequestParam int userId, @RequestParam int roleValue) {
        projectService.addUserToProject(userId, projectId, roleValue);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("{ProjectName}/rediger_projektgruppe")
    public String getCollaborators(@RequestParam int projectId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else {
            List<User> users = projectService.getAssociatedUsers(projectId);
            Project project = projectService.getProject(projectId);

            List<User> sortedUsers = users.stream()
                    .sorted(Comparator.comparingInt(this::getRolePriority))
                    .collect(Collectors.toList());
            model.addAttribute("project", project);
            model.addAttribute("users", sortedUsers);
            return "editProjectGroup";
        }
    }
    private int getRolePriority(User user) {
        return switch (user.getProjectRole()) {
            case "Projektleder" -> 1;
            case "Udvikler" -> 2;
            case "Observatør" -> 3;
            default -> 4;
        };
    }

    @GetMapping("{ProjectName}/rediger_projektgruppe/{userName}")
    public String editCollaborator(@RequestParam int projectId, @RequestParam int userId, Model model, HttpServletRequest request) {
        if (projectService.getLoggedInUser(request) == null) {
            return "redirect:/";
        } else {
            User user = projectService.getUser(userId);
            Project project = projectService.getProject(projectId);
            model.addAttribute("user", user);
            model.addAttribute("project", project);
            return "editCollaborator";
        }
    }

    @PostMapping("{projectName}/rediger_projektgruppe/{userName}")
    public String updateUserRole(@RequestParam int projectId,
                                 @RequestParam int userId,
                                 @RequestParam int roleValue) {
        projectService.updateCollaboratorRole(projectId, userId, roleValue);
        return "redirect:/oversigt/projekter";
    }

    @PostMapping("{projectName}/fjern_bruger")
    public String removeCollaborator(@RequestParam int userId, @RequestParam int projectId, @RequestParam int roleId) {
        projectService.removeCollaborator(userId, projectId, roleId);
        return "redirect:/oversigt/projekter";
    }

    @GetMapping("administrator")
    public String adminPage(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else if (!user.isAdmin()) {
            return "no_permission";
        } else {
            return "administrator";
        }
    }

    @GetMapping("/rediger_bruger/{userId}")
    public String showUpdateUserForm(@PathVariable int userId, Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else if (!user.isAdmin()) {
            return "no_permission";
        } else {
            User userToEdit = projectService.getUser(userId);
            model.addAttribute("user", userToEdit);
            return "editUser";}
    }

    @PostMapping("/rediger_bruger/{userId}")
    public String updateUser(@ModelAttribute("user") User user) {
        projectService.updateUser(user);
        return "redirect:/oversigt/administrator";
    }

    @PostMapping("/slet_bruger/{userName}")
    public String deleteUser(@RequestParam int userId, HttpServletRequest request) {
        if (projectService.getLoggedInUser(request).isAdmin()) {
            projectService.deleteUser(userId);
            return "redirect:/oversigt/administrator";
        } else {
            return "no_permission";
        }
    }
    @GetMapping("/administrator/opret_bruger")
    public String showCreateUserForm(Model model, HttpServletRequest request) {
            User user = getLoggedInUser(request);
            if (user == null) {
                return "redirect:/";
            } else if (!user.isAdmin()) {
                return "no_permission";
            } else {
                model.addAttribute("user", new User());
                return "createUser";}
    }

    @PostMapping("/administrator/opret_bruger")
    public String createUser(@ModelAttribute User user) {
        projectService.createUser(user);
        return "redirect:/oversigt/administrator";
    }
    @GetMapping("/administrator/projekter")
    public String showProjects(Model model, HttpServletRequest request) {
        User user = getLoggedInUser(request);
        if (user == null) {
            return "redirect:/";
        } else if (!user.isAdmin()) {
            return "no_permission";
        } else {
            int adminId = user.getUserId();
            List<Project> projects =projectService.adminGetProjects(adminId);
            model.addAttribute("projects", projects);
            model.addAttribute("userId", adminId);
            return "adminProjects";}
    }

    @PostMapping("/administrator/overtag_projekt")
    public String insertAdminInProject(@RequestParam int projectId, @RequestParam int userId) {
        projectService.adminInsertIntoProject(projectId, userId);
        return "redirect:/oversigt/administrator";
    }

    @PostMapping("/slet_delprojekt/{subProjectName}")
    public String deleteSubProject(@RequestParam int subProjectId) {
        projectService.deleteProject(subProjectId);
        return "redirect:/oversigt/projekter";
    }

    @PostMapping("/slet_opgave/{taskName}")
    public String deleteTask(@RequestParam int taskId) {
        projectService.deleteTask(taskId);
        return "redirect:/oversigt/projekter";
    }

}


