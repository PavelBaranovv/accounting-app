package spbpu.accountingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spbpu.accountingapp.entity.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e LEFT JOIN e.departments d GROUP BY e.id ORDER BY COALESCE(MIN(d.name), 'zzzzzzzzzz'), e.lastName, e.firstName")
    List<Employee> findAllByOrderByDepartmentNameAscLastNameAsc();

    List<Employee> findAllByOrderByLastName();
}