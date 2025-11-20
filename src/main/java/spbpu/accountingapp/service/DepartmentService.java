package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.entity.Department;
import spbpu.accountingapp.repository.DepartmentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAllByOrderByName();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }
}