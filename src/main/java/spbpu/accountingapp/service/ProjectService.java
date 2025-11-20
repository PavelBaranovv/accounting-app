package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.dto.ProjectProfitInfo;
import spbpu.accountingapp.entity.Department;
import spbpu.accountingapp.entity.Employee;
import spbpu.accountingapp.entity.Project;
import spbpu.accountingapp.enums.ProfitStatus;
import spbpu.accountingapp.repository.ProjectRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByDepartmentNameAscDateBegAsc();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public List<Project> getProjectsByDepartment(Long departmentId) {
        return projectRepository.findByDepartmentIdOrderByDateBeg(departmentId);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public Map<Long, ProjectProfitInfo> calculateProfitInfo(List<Project> projects) {
        if (projects == null) {
            return Collections.emptyMap();
        }
        return projects.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Project::getId, this::calculateProfit));
    }

    private ProjectProfitInfo calculateProfit(Project project) {
        BigDecimal salarySum = getDepartmentSalarySum(project.getDepartment());
        BigDecimal durationMonths = calculateDurationInMonths(project);
        BigDecimal expenses = salarySum.multiply(durationMonths);
        BigDecimal profit = project.getCost() != null
                ? project.getCost().subtract(expenses)
                : BigDecimal.ZERO.subtract(expenses);
        boolean projected = project.getDateEndReal() == null;
        ProfitStatus status = determineStatus(profit, projected);
        return new ProjectProfitInfo(profit, status);
    }

    private ProfitStatus determineStatus(BigDecimal profit, boolean projected) {
        if (profit.signum() < 0) {
            return ProfitStatus.NEGATIVE;
        }
        if (projected) {
            return ProfitStatus.PROJECTED;
        }
        return ProfitStatus.POSITIVE;
    }

    private BigDecimal getDepartmentSalarySum(Department department) {
        if (department == null || department.getEmployees() == null) {
            return BigDecimal.ZERO;
        }
        return department.getEmployees().stream()
                .map(Employee::getSalary)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDurationInMonths(Project project) {
        LocalDate start = project.getDateBeg();
        if (start == null) {
            return BigDecimal.ZERO;
        }
        LocalDate end = project.getDateEndReal();
        if (end == null) {
            end = project.getDateEnd();
        }
        if (end == null) {
            end = LocalDate.now();
        }
        if (end.isBefore(start)) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
    }
}