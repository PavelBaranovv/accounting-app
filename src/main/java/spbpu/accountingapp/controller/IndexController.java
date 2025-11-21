package spbpu.accountingapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spbpu.accountingapp.service.DepartmentService;
import spbpu.accountingapp.service.EmployeeService;
import spbpu.accountingapp.service.ProjectService;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

    private final ProjectService projectService;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @GetMapping
    public String index() {
        return "redirect:/projects";
    }
}