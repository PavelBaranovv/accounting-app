package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.entity.Employee;
import spbpu.accountingapp.repository.EmployeeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllByOrderByLastName();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
}