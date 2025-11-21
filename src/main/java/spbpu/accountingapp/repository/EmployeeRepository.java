package spbpu.accountingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spbpu.accountingapp.entity.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            SELECT DISTINCT e
            FROM Employee e
            LEFT JOIN FETCH e.departments d
            ORDER BY e.lastName, e.firstName, e.id
            """)
    List<Employee> getAllWithDepartments();
}