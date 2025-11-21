package spbpu.accountingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spbpu.accountingapp.entity.Employee;
import spbpu.accountingapp.service.DepartmentService;
import spbpu.accountingapp.service.EmployeeService;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping
    public String employees(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("pageTitle", "Сотрудники");
        model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return "view/employees";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentService.getAllDepartmentsWithEmplAndProj());
        model.addAttribute("pageTitle", "Создать сотрудника");
        return "form/employee-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createEmployee(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        try {
            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании сотрудника");
        }
        return "redirect:/employees";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return "redirect:/employees";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentService.getAllDepartmentsWithEmplAndProj());
        model.addAttribute("pageTitle", "Редактировать сотрудника");
        return "form/employee-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable Long id, @ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        try {
            employee.setId(id);
            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении сотрудника");
        }
        return "redirect:/employees";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("success", "Сотрудник успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении сотрудника");
        }
        return "redirect:/employees";
    }
}