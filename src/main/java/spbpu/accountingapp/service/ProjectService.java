package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.dto.ProjectProfitInfo;
import spbpu.accountingapp.entity.Department;
import spbpu.accountingapp.entity.Project;
import spbpu.accountingapp.enums.ProfitStatus;
import spbpu.accountingapp.repository.DepartmentRepository;
import spbpu.accountingapp.repository.ProjectRepository;
import spbpu.accountingapp.repository.projection.DepartmentSalary;

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
    private final DepartmentRepository departmentRepository;

    public List<Project> getAllProjects() {
        return projectRepository.getAllWithDepartment();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project saveProject(Project project) {
        validateProjectDates(project);
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public Map<Long, ProjectProfitInfo> calculateProfitInfo(List<Project> projects) {
        if (projects == null) {
            return Collections.emptyMap();
        }
        Map<Long, BigDecimal> departmentSalaryTotals = getDepartmentSalaryTotals(projects);
        return projects.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Project::getId, project -> calculateProfit(project, departmentSalaryTotals)));
    }

    public BigDecimal calculateTotalCompletedProfit(Map<Long, ProjectProfitInfo> profitInfo) {
        if (profitInfo == null) {
            return BigDecimal.ZERO;
        }
        return profitInfo.values().stream()
                .filter(info -> info.status() == ProfitStatus.POSITIVE || info.status() == ProfitStatus.NEGATIVE)
                .map(ProjectProfitInfo::profit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalProjectedProfit(Map<Long, ProjectProfitInfo> profitInfo) {
        if (profitInfo == null) {
            return BigDecimal.ZERO;
        }
        return profitInfo.values().stream()
                .filter(info -> info.status() == ProfitStatus.PROJECTED)
                .map(ProjectProfitInfo::profit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ProjectProfitInfo calculateProfit(Project project, Map<Long, BigDecimal> salaryTotals) {
        BigDecimal salarySum = getDepartmentSalarySum(project.getDepartment(), salaryTotals);
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

    private BigDecimal getDepartmentSalarySum(Department department, Map<Long, BigDecimal> salaryTotals) {
        if (department == null || department.getId() == null) {
            return BigDecimal.ZERO;
        }
        return salaryTotals.getOrDefault(department.getId(), BigDecimal.ZERO);
    }

    private Map<Long, BigDecimal> getDepartmentSalaryTotals(List<Project> projects) {
        var departmentIds = projects.stream()
                .map(Project::getDepartment)
                .filter(Objects::nonNull)
                .map(Department::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (departmentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return departmentRepository.sumEmployeeSalaries(departmentIds).stream()
                .collect(Collectors.toMap(DepartmentSalary::getDepartmentId, DepartmentSalary::getTotalSalary));
    }

    private void validateProjectDates(Project project) {
        LocalDate start = project.getDateBeg();
        LocalDate plannedEnd = project.getDateEnd();
        LocalDate actualEnd = project.getDateEndReal();

        if (start != null && plannedEnd != null && plannedEnd.isBefore(start)) {
            throw new IllegalArgumentException("Плановая дата окончания не может быть раньше даты начала");
        }
        if (start != null && actualEnd != null && actualEnd.isBefore(start)) {
            throw new IllegalArgumentException("Фактическая дата окончания не может быть раньше даты начала");
        }
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