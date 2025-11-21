package spbpu.accountingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spbpu.accountingapp.entity.Department;
import spbpu.accountingapp.service.DepartmentService;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String departments(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("departments", departmentService.getAllDepartmentsWithEmplAndProj());
        model.addAttribute("pageTitle", "Отделы");
        model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return "view/departments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("pageTitle", "Создать отдел");
        return "form/department-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String createDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        try {
            departmentService.saveDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Отдел успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании отдела");
        }
        return "redirect:/departments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        if (department == null) {
            return "redirect:/departments";
        }
        model.addAttribute("department", department);
        model.addAttribute("pageTitle", "Редактировать отдел");
        return "form/department-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        try {
            department.setId(id);
            departmentService.saveDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Отдел успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении отдела");
        }
        return "redirect:/departments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Отдел успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении отдела. Проверьте связанные проекты и сотрудников.");
        }
        return "redirect:/departments";
    }
}