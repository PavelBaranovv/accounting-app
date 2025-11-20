package spbpu.accountingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spbpu.accountingapp.service.DepartmentService;
import spbpu.accountingapp.service.EmployeeService;
import spbpu.accountingapp.service.ProjectService;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class DashboardController {

    private final ProjectService projectService;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @GetMapping
    public String index() {
        return "redirect:/projects";
    }

    @GetMapping("/projects")
    public String projects(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("pageTitle", "Проекты");
        return "projects";
    }

    @GetMapping("/departments")
    public String departments(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle", "Отделы");
        return "departments";
    }

    @GetMapping("/employees")
    public String employees(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("pageTitle", "Сотрудники");
        return "employees";
    }
}