package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.dto.ProjectProfitInfo;
import spbpu.accountingapp.entity.Employee;
import spbpu.accountingapp.entity.Project;
import spbpu.accountingapp.enums.ProfitStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateProjectsReport() throws IOException {
        List<Project> projects = projectService.getAllProjects();
        Map<Long, ProjectProfitInfo> profitInfo = projectService.calculateProfitInfo(projects);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Проекты");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle negativeStyle = createNegativeStyle(workbook);
            CellStyle projectedStyle = createProjectedStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Название", "Стоимость", "Отдел", "Дата начала", "План окончания", 
                              "Факт окончания", "Прибыль", "Статус"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Project project : projects) {
                Row row = sheet.createRow(rowNum++);
                ProjectProfitInfo info = profitInfo.get(project.getId());

                int colNum = 0;
                row.createCell(colNum++).setCellValue(project.getName());
                
                Cell costCell = row.createCell(colNum++);
                costCell.setCellValue(project.getCost() != null ? project.getCost().doubleValue() : 0);
                costCell.setCellStyle(numberStyle);

                row.createCell(colNum++).setCellValue(
                    project.getDepartment() != null ? project.getDepartment().getName() : "—");

                Cell dateBegCell = row.createCell(colNum++);
                if (project.getDateBeg() != null) {
                    dateBegCell.setCellValue(project.getDateBeg().format(DATE_FORMATTER));
                    dateBegCell.setCellStyle(dateStyle);
                } else {
                    dateBegCell.setCellValue("—");
                }

                Cell dateEndCell = row.createCell(colNum++);
                if (project.getDateEnd() != null) {
                    dateEndCell.setCellValue(project.getDateEnd().format(DATE_FORMATTER));
                    dateEndCell.setCellStyle(dateStyle);
                } else {
                    dateEndCell.setCellValue("—");
                }

                Cell dateEndRealCell = row.createCell(colNum++);
                if (project.getDateEndReal() != null) {
                    dateEndRealCell.setCellValue(project.getDateEndReal().format(DATE_FORMATTER));
                    dateEndRealCell.setCellStyle(dateStyle);
                } else {
                    dateEndRealCell.setCellValue("—");
                }

                Cell profitCell = row.createCell(colNum++);
                if (info != null) {
                    profitCell.setCellValue(info.profit().doubleValue());
                    if (info.status() == ProfitStatus.NEGATIVE) {
                        profitCell.setCellStyle(negativeStyle);
                    } else if (info.status() == ProfitStatus.PROJECTED) {
                        profitCell.setCellStyle(projectedStyle);
                    } else {
                        profitCell.setCellStyle(numberStyle);
                    }
                } else {
                    profitCell.setCellValue("—");
                }

                row.createCell(colNum++).setCellValue(
                    project.getDateEndReal() != null ? "Завершен" : "В работе");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1000, 15000));
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateEmployeesReport() throws IOException {
        List<Employee> employees = employeeService.getAllEmployees();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Сотрудники");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Фамилия", "Имя", "Отчество", "Должность", "Зарплата", "Отделы"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Employee employee : employees) {
                Row row = sheet.createRow(rowNum++);

                int colNum = 0;
                row.createCell(colNum++).setCellValue(employee.getLastName());
                row.createCell(colNum++).setCellValue(employee.getFirstName());
                row.createCell(colNum++).setCellValue(
                    employee.getFatherName() != null ? employee.getFatherName() : "");
                row.createCell(colNum++).setCellValue(employee.getPosition());

                Cell salaryCell = row.createCell(colNum++);
                salaryCell.setCellValue(employee.getSalary() != null ? employee.getSalary().doubleValue() : 0);
                salaryCell.setCellStyle(numberStyle);

                String departments = employee.getDepartments() != null && !employee.getDepartments().isEmpty()
                    ? employee.getDepartments().stream()
                        .map(d -> d.getName())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")
                    : "—";
                row.createCell(colNum++).setCellValue(departments);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1000, 15000));
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd.mm.yyyy"));
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }

    private CellStyle createNegativeStyle(Workbook workbook) {
        CellStyle style = createNumberStyle(workbook);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createProjectedStyle(Workbook workbook) {
        CellStyle style = createNumberStyle(workbook);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        return style;
    }
}

