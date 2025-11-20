package spbpu.accountingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spbpu.accountingapp.entity.Project;
import spbpu.accountingapp.service.DepartmentService;
import spbpu.accountingapp.service.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final DepartmentService departmentService;

    @GetMapping
    public String projects(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        model.addAttribute("profitInfo", projectService.calculateProfitInfo(projects));
        model.addAttribute("pageTitle", "Проекты");
        model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("isUser", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        return "view/projects";
    }

    @GetMapping("/{id}")
    public String projectDetails(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
        model.addAttribute("project", project);
        model.addAttribute("pageTitle", "Проект: " + project.getName());
        return "project-details";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "Создать проект");
        return "form/project-form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public String createProject(@ModelAttribute Project project, RedirectAttributes redirectAttributes) {
        try {
            projectService.saveProject(project);
            redirectAttributes.addFlashAttribute("success", "Проект успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании проекта");
        }
        return "redirect:/projects";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
        model.addAttribute("project", project);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "Редактировать проект");
        return "form/project-form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute Project project, RedirectAttributes redirectAttributes) {
        try {
            project.setId(id);
            projectService.saveProject(project);
            redirectAttributes.addFlashAttribute("success", "Проект успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении проекта");
        }
        return "redirect:/projects";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteProject(id);
            redirectAttributes.addFlashAttribute("success", "Проект успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении проекта");
        }
        return "redirect:/projects";
    }
}